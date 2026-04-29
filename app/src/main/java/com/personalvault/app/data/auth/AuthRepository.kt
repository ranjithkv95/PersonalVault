package com.personalvault.app.data.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Wraps Firebase Auth + Credential Manager (Google Sign-In).
 *
 * Call [signInWithGoogle] from a composable/activity context to launch
 * the Google credential picker. The token is exchanged with Firebase Auth
 * and the resulting [FirebaseUser] is emitted through [currentUser].
 */
class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /** Emits the signed-in user or null when signed out. */
    val currentUser: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    /** Returns the currently signed-in user (snapshot, not flow). */
    fun user(): FirebaseUser? = auth.currentUser

    /**
     * Launches the Credential Manager Google picker, exchanges the ID-token
     * with Firebase Auth, and returns the signed-in [FirebaseUser].
     *
     * @param context       An Activity (or valid UI) context.
     * @param webClientId   The OAuth 2.0 Web client ID from your Firebase console.
     */
    suspend fun signInWithGoogle(context: Context, webClientId: String): FirebaseUser {
        val credentialManager = CredentialManager.create(context)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result: GetCredentialResponse =
            credentialManager.getCredential(context, request)

        val credential = result.credential
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleIdTokenCredential =
                GoogleIdTokenCredential.createFrom(credential.data)
            val firebaseCredential =
                GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
            val authResult = auth.signInWithCredential(firebaseCredential).await()
            return authResult.user
                ?: throw IllegalStateException("Firebase sign-in succeeded but user is null")
        } else {
            throw IllegalStateException("Unexpected credential type: ${credential.type}")
        }
    }

    fun signOut() {
        auth.signOut()
    }
}
