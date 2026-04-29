package com.personalvault.app.data.family

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Firestore data model for family sharing:
 *
 *   user_profiles/{uid}
 *     - displayName: String
 *     - email: String
 *     - uniqueCode: String (6-char, uppercase alphanumeric)
 *     - familyGroupId: String? (null if not in a group)
 *     - createdAt: Long
 *
 *   family_groups/{groupId}
 *     - ownerUid: String
 *     - members: List<Map> [{uid, name, email, joinedAt}]
 *     - createdAt: Long
 *
 *   family_groups/{groupId}/shared_expenses/{expenseId}
 *     - (same fields as Expense, plus addedByUid, addedByName)
 *
 *   code_index/{uniqueCode}   (for fast code → uid lookup)
 *     - uid: String
 */
data class UserProfile(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val uniqueCode: String = "",
    val familyGroupId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

data class FamilyMember(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val joinedAt: Long = System.currentTimeMillis()
)

data class FamilyGroup(
    val id: String = "",
    val ownerUid: String = "",
    val members: List<FamilyMember> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

data class SharedExpense(
    val id: String = "",
    val amount: Double = 0.0,
    val categoryId: String = "",
    val subcategoryId: String? = null,
    val merchant: String = "",
    val note: String = "",
    val dateEpochMillis: Long = System.currentTimeMillis(),
    val addedByUid: String = "",
    val addedByName: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

class FamilyRepository {

    private val TAG = "FamilyRepo"
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun uid(): String? = auth.currentUser?.uid

    private fun requireUid(): String =
        auth.currentUser?.uid ?: throw IllegalStateException("Not signed in")

    // ─── User Profile ────────────────────────────────────────────────

    /**
     * Ensures a user profile exists in Firestore. Called once after login.
     * Generates a unique 6-char code if one doesn't exist yet.
     * Returns null if user is not signed in or on Firestore error.
     */
    suspend fun ensureProfile(): UserProfile? {
        return try {
            val u = auth.currentUser ?: return null
            val docRef = firestore.collection("user_profiles").document(u.uid)
            val snap = docRef.get().await()

            if (snap.exists()) {
                snap.toObject(UserProfile::class.java)
                    ?.copy(uid = u.uid)
                    ?: createNewProfile(u.uid, u.displayName ?: "", u.email ?: "")
            } else {
                createNewProfile(u.uid, u.displayName ?: "", u.email ?: "")
            }
        } catch (e: Exception) {
            Log.e(TAG, "ensureProfile failed", e)
            null
        }
    }

    private suspend fun createNewProfile(uid: String, name: String, email: String): UserProfile {
        val code = generateUniqueCode()
        val profile = UserProfile(
            uid = uid,
            displayName = name,
            email = email,
            uniqueCode = code,
            createdAt = System.currentTimeMillis()
        )
        firestore.collection("user_profiles").document(uid).set(profile).await()
        // Index the code for lookup
        firestore.collection("code_index").document(code)
            .set(mapOf("uid" to uid)).await()
        return profile
    }

    private suspend fun generateUniqueCode(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789" // no O/0/1/I to avoid confusion
        repeat(20) { // try up to 20 times
            val code = (1..6).map { chars.random() }.joinToString("")
            val existing = firestore.collection("code_index").document(code).get().await()
            if (!existing.exists()) return code
        }
        // Extremely unlikely fallback
        return (1..8).map { chars.random() }.joinToString("")
    }

    fun observeProfile(): Flow<UserProfile?> {
        val currentUid = uid() ?: return kotlinx.coroutines.flow.flowOf(null)
        return callbackFlow {
            val reg = firestore.collection("user_profiles").document(currentUid)
                .addSnapshotListener { snap, err ->
                    if (err != null) {
                        Log.e(TAG, "observeProfile listener error", err)
                        trySend(null)
                        return@addSnapshotListener
                    }
                    val profile = snap?.toObject(UserProfile::class.java)?.copy(uid = currentUid)
                    trySend(profile)
                }
            awaitClose { reg.remove() }
        }
    }

    // ─── Family Group ────────────────────────────────────────────────

    /**
     * Creates a new family group with this user as owner.
     * Returns the group ID.
     */
    suspend fun createFamilyGroup(): String {
        val u = auth.currentUser ?: throw IllegalStateException("Not signed in")
        val groupRef = firestore.collection("family_groups").document()
        val groupId = groupRef.id

        val member = mapOf(
            "uid" to u.uid,
            "name" to (u.displayName ?: ""),
            "email" to (u.email ?: ""),
            "joinedAt" to System.currentTimeMillis()
        )

        val group = mapOf(
            "ownerUid" to u.uid,
            "members" to listOf(member),
            "createdAt" to System.currentTimeMillis()
        )
        groupRef.set(group).await()

        // Update user profile with group id
        firestore.collection("user_profiles").document(u.uid)
            .update("familyGroupId", groupId).await()

        return groupId
    }

    /**
     * Connect to another user by their unique code.
     * If the target has a family group, join it. Otherwise create a new one.
     */
    suspend fun connectByCode(code: String): Result<String> {
        return try {
            val codeDoc = firestore.collection("code_index").document(code.uppercase()).get().await()
            if (!codeDoc.exists()) return Result.failure(Exception("No user found with code: $code"))

            val targetUid = codeDoc.getString("uid") ?: return Result.failure(Exception("Invalid code"))
            if (targetUid == requireUid()) return Result.failure(Exception("That's your own code!"))

            // Get target's profile
            val targetProfile = firestore.collection("user_profiles").document(targetUid).get().await()
            val targetGroupId = targetProfile.getString("familyGroupId")

            val u = auth.currentUser!!
            val myMember = mapOf(
                "uid" to u.uid,
                "name" to (u.displayName ?: ""),
                "email" to (u.email ?: ""),
                "joinedAt" to System.currentTimeMillis()
            )

            val groupId: String
            if (targetGroupId != null) {
                // Join existing group
                groupId = targetGroupId
                firestore.collection("family_groups").document(groupId)
                    .update("members", FieldValue.arrayUnion(myMember)).await()
            } else {
                // Create new group with both users
                val targetMember = mapOf(
                    "uid" to targetUid,
                    "name" to (targetProfile.getString("displayName") ?: ""),
                    "email" to (targetProfile.getString("email") ?: ""),
                    "joinedAt" to System.currentTimeMillis()
                )
                groupId = firestore.collection("family_groups").document().id
                val group = mapOf(
                    "ownerUid" to targetUid,
                    "members" to listOf(targetMember, myMember),
                    "createdAt" to System.currentTimeMillis()
                )
                firestore.collection("family_groups").document(groupId).set(group).await()
                // Update target's profile
                firestore.collection("user_profiles").document(targetUid)
                    .update("familyGroupId", groupId).await()
            }

            // Update my profile
            firestore.collection("user_profiles").document(requireUid())
                .update("familyGroupId", groupId).await()

            Result.success(groupId)
        } catch (e: Exception) {
            Log.e(TAG, "connectByCode failed", e)
            Result.failure(e)
        }
    }

    fun observeFamilyGroup(groupId: String): Flow<FamilyGroup?> = callbackFlow {
        val reg = firestore.collection("family_groups").document(groupId)
            .addSnapshotListener { snap, err ->
                if (err != null) { trySend(null); return@addSnapshotListener }
                if (snap == null || !snap.exists()) { trySend(null); return@addSnapshotListener }
                try {
                    @Suppress("UNCHECKED_CAST")
                    val membersList = (snap.get("members") as? List<Map<String, Any?>>)
                        ?.map { m ->
                            FamilyMember(
                                uid = m["uid"] as? String ?: "",
                                name = m["name"] as? String ?: "",
                                email = m["email"] as? String ?: "",
                                joinedAt = (m["joinedAt"] as? Number)?.toLong() ?: 0
                            )
                        } ?: emptyList()
                    trySend(
                        FamilyGroup(
                            id = snap.id,
                            ownerUid = snap.getString("ownerUid") ?: "",
                            members = membersList,
                            createdAt = snap.getLong("createdAt") ?: 0
                        )
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "parse group failed", e)
                    trySend(null)
                }
            }
        awaitClose { reg.remove() }
    }

    // ─── Shared Expenses ─────────────────────────────────────────────

    suspend fun addSharedExpense(groupId: String, expense: SharedExpense) {
        val docRef = firestore.collection("family_groups").document(groupId)
            .collection("shared_expenses").document()
        val data = mapOf(
            "amount" to expense.amount,
            "categoryId" to expense.categoryId,
            "subcategoryId" to expense.subcategoryId,
            "merchant" to expense.merchant,
            "note" to expense.note,
            "dateEpochMillis" to expense.dateEpochMillis,
            "addedByUid" to expense.addedByUid,
            "addedByName" to expense.addedByName,
            "createdAt" to expense.createdAt
        )
        docRef.set(data).await()
    }

    fun observeSharedExpenses(groupId: String): Flow<List<SharedExpense>> = callbackFlow {
        val reg = firestore.collection("family_groups").document(groupId)
            .collection("shared_expenses")
            .orderBy("dateEpochMillis", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) { trySend(emptyList()); return@addSnapshotListener }
                val list = snap?.documents?.mapNotNull { doc ->
                    try {
                        SharedExpense(
                            id = doc.id,
                            amount = doc.getDouble("amount") ?: 0.0,
                            categoryId = doc.getString("categoryId") ?: "",
                            subcategoryId = doc.getString("subcategoryId"),
                            merchant = doc.getString("merchant") ?: "",
                            note = doc.getString("note") ?: "",
                            dateEpochMillis = doc.getLong("dateEpochMillis") ?: 0,
                            addedByUid = doc.getString("addedByUid") ?: "",
                            addedByName = doc.getString("addedByName") ?: "",
                            createdAt = doc.getLong("createdAt") ?: 0
                        )
                    } catch (e: Exception) { null }
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { reg.remove() }
    }

    /**
     * Remove a member from the family group (owner action).
     * Also clears the removed member's familyGroupId.
     */
    suspend fun removeMember(groupId: String, memberUid: String): Result<Unit> {
        return try {
            val groupSnap = firestore.collection("family_groups").document(groupId).get().await()
            @Suppress("UNCHECKED_CAST")
            val members = (groupSnap.get("members") as? List<Map<String, Any?>>)?.toMutableList() ?: return Result.failure(Exception("No members"))
            val target = members.find { it["uid"] == memberUid }
            if (target != null) {
                firestore.collection("family_groups").document(groupId)
                    .update("members", FieldValue.arrayRemove(target)).await()
            }
            // Clear the removed member's familyGroupId
            firestore.collection("user_profiles").document(memberUid)
                .update("familyGroupId", null).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "removeMember failed", e)
            Result.failure(e)
        }
    }

    suspend fun leaveGroup() {
        val u = auth.currentUser ?: return
        val profileDoc = firestore.collection("user_profiles").document(u.uid).get().await()
        val groupId = profileDoc.getString("familyGroupId") ?: return

        // Remove self from group members
        val groupSnap = firestore.collection("family_groups").document(groupId).get().await()
        @Suppress("UNCHECKED_CAST")
        val members = (groupSnap.get("members") as? List<Map<String, Any?>>)?.toMutableList() ?: return
        val me = members.find { it["uid"] == u.uid }
        if (me != null) {
            firestore.collection("family_groups").document(groupId)
                .update("members", FieldValue.arrayRemove(me)).await()
        }

        // Clear group from profile
        firestore.collection("user_profiles").document(u.uid)
            .update("familyGroupId", null).await()
    }
}
