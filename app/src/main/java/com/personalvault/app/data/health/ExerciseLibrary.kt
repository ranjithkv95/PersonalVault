package com.personalvault.app.data.health

/**
 * Curated exercise library with difficulty levels, body targets, and instructions.
 */
data class Exercise(
    val id: String,
    val name: String,
    val category: String,       // strength, cardio, yoga, stretching
    val difficulty: String,     // beginner, intermediate, advanced
    val muscleGroups: List<String>,
    val durationMin: Int,
    val caloriePer10Min: Int,
    val equipment: String,      // none, dumbbells, resistance-band, mat
    val instructions: List<String>,
    val tips: List<String>
)

object ExerciseLibrary {

    val ALL: List<Exercise> = listOf(
        // ── Strength ───────────────────────────────────────────────
        Exercise("pushups", "Push-Ups", "strength", "beginner",
            listOf("chest", "triceps", "shoulders"), 10, 40, "none",
            listOf("Start in plank position, hands shoulder-width apart", "Lower chest to floor, elbows at 45°", "Push back up to start"),
            listOf("Keep core tight throughout", "Modify on knees if needed")
        ),
        Exercise("squats", "Bodyweight Squats", "strength", "beginner",
            listOf("quads", "glutes", "hamstrings"), 10, 45, "none",
            listOf("Stand feet shoulder-width, toes slightly out", "Lower hips back and down as if sitting", "Go until thighs are parallel to floor", "Drive through heels to stand"),
            listOf("Keep chest up, knees tracking over toes", "Go deeper as mobility improves")
        ),
        Exercise("lunges", "Walking Lunges", "strength", "beginner",
            listOf("quads", "glutes", "hamstrings"), 10, 50, "none",
            listOf("Step forward into a lunge", "Both knees at 90°", "Push off front foot, step next leg forward"),
            listOf("Keep torso upright", "Add dumbbells for progression")
        ),
        Exercise("plank", "Plank Hold", "strength", "beginner",
            listOf("core", "shoulders"), 5, 30, "mat",
            listOf("Forearms on floor, elbows under shoulders", "Body straight from head to heels", "Hold position without sagging"),
            listOf("Breathe steadily", "Aim for 30s → 60s → 90s progression")
        ),
        Exercise("deadlift", "Dumbbell Deadlift", "strength", "intermediate",
            listOf("hamstrings", "glutes", "back"), 15, 55, "dumbbells",
            listOf("Hold dumbbells in front of thighs", "Hinge at hips, lower weights past knees", "Keep back flat, drive hips forward to stand"),
            listOf("Squeeze glutes at top", "Start light — focus on form")
        ),
        Exercise("dumbbell_press", "Dumbbell Shoulder Press", "strength", "intermediate",
            listOf("shoulders", "triceps"), 10, 40, "dumbbells",
            listOf("Hold dumbbells at shoulder height", "Press overhead until arms are extended", "Lower slowly back to shoulders"),
            listOf("Don't arch your back", "Engage core throughout")
        ),

        // ── Cardio ─────────────────────────────────────────────────
        Exercise("brisk_walk", "Brisk Walk", "cardio", "beginner",
            listOf("full-body"), 30, 35, "none",
            listOf("Walk at a pace where talking is slightly difficult", "Swing arms naturally", "Maintain for 30 minutes"),
            listOf("Great for recovery days", "Aim for 7000+ steps daily")
        ),
        Exercise("jumping_jacks", "Jumping Jacks", "cardio", "beginner",
            listOf("full-body"), 10, 60, "none",
            listOf("Stand with feet together, arms at sides", "Jump feet apart while raising arms overhead", "Jump back to start"),
            listOf("Land softly on balls of feet", "Do 3 sets of 30 reps")
        ),
        Exercise("burpees", "Burpees", "cardio", "advanced",
            listOf("full-body"), 10, 80, "none",
            listOf("Stand → squat down, hands on floor", "Jump feet back to plank", "Do a push-up", "Jump feet to hands, jump up with arms overhead"),
            listOf("Go at your own pace", "Modify by skipping push-up or jump")
        ),
        Exercise("spot_jogging", "Spot Jogging", "cardio", "beginner",
            listOf("full-body"), 15, 50, "none",
            listOf("Jog in place lifting knees", "Keep a steady rhythm", "Swing arms naturally"),
            listOf("Great for warming up", "Increase pace for intensity")
        ),

        // ── Yoga ───────────────────────────────────────────────────
        Exercise("surya_namaskar", "Surya Namaskar (Sun Salutation)", "yoga", "beginner",
            listOf("full-body", "flexibility"), 20, 35, "mat",
            listOf("Pranamasana (prayer pose)", "Hasta Uttanasana (raised arms)", "Hasta Padasana (forward bend)", "Ashwa Sanchalanasana (equestrian)", "Dandasana (stick/plank)", "Ashtanga Namaskara (8 points)", "Bhujangasana (cobra)", "Parvatasana (mountain)", "Reverse sequence back to prayer"),
            listOf("Do 5-12 rounds", "Synchronise with breath", "Best done in morning on empty stomach")
        ),
        Exercise("warrior_poses", "Warrior I, II & III Flow", "yoga", "intermediate",
            listOf("legs", "core", "balance"), 15, 25, "mat",
            listOf("Warrior I: lunge with arms up", "Warrior II: open hips, arms parallel to floor", "Warrior III: balance on one leg, torso forward"),
            listOf("Hold each pose 5-8 breaths", "Focus on steady breathing")
        ),

        // ── Stretching ─────────────────────────────────────────────
        Exercise("hamstring_stretch", "Seated Hamstring Stretch", "stretching", "beginner",
            listOf("hamstrings", "lower-back"), 10, 15, "mat",
            listOf("Sit with legs extended", "Reach forward toward toes", "Hold 20-30 seconds", "Repeat 3 times"),
            listOf("Don't bounce", "Breathe into the stretch")
        ),
        Exercise("hip_opener", "Pigeon Pose Stretch", "stretching", "intermediate",
            listOf("hips", "glutes"), 10, 15, "mat",
            listOf("From downward dog, bring right knee forward behind right wrist", "Extend left leg straight back", "Lower hips toward floor", "Hold 30-60 seconds each side"),
            listOf("Use a cushion under hip if tight", "Breathe deeply")
        )
    )

    fun forCategory(cat: String): List<Exercise> =
        ALL.filter { it.category.equals(cat, ignoreCase = true) }

    fun forDifficulty(level: String): List<Exercise> =
        ALL.filter { it.difficulty.equals(level, ignoreCase = true) }

    fun forMuscle(muscle: String): List<Exercise> {
        val m = muscle.lowercase()
        return ALL.filter { ex -> ex.muscleGroups.any { it.contains(m) } }
    }

    fun search(query: String): List<Exercise> {
        val q = query.lowercase()
        return ALL.filter { ex ->
            ex.name.lowercase().contains(q) ||
                ex.category.lowercase().contains(q) ||
                ex.muscleGroups.any { it.contains(q) }
        }
    }

    /** Suggest a simple daily routine based on day of week pattern */
    fun dailyRoutine(dayOfWeek: Int): List<Exercise> = when (dayOfWeek) {
        1 -> ALL.filter { it.category == "strength" && it.muscleGroups.any { g -> g in listOf("chest", "triceps", "shoulders") } }
        2 -> ALL.filter { it.category == "cardio" }.take(3)
        3 -> ALL.filter { it.category == "strength" && it.muscleGroups.any { g -> g in listOf("quads", "glutes", "hamstrings") } }
        4 -> ALL.filter { it.category == "yoga" }
        5 -> ALL.filter { it.category == "strength" }
        6 -> ALL.filter { it.category == "cardio" || it.category == "yoga" }.take(3)
        else -> ALL.filter { it.category == "stretching" }
    }
}
