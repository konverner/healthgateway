package com.verner.healthgateway.presentation.navigation

import com.verner.healthgateway.R

const val UID_NAV_ARGUMENT = "uid"

/**
 * Represent all Screens in the app.
 *
 * @param route The route string used for Compose navigation
 * @param titleId The ID of the string resource to display as a title
 * @param hasMenuItem Whether this Screen should be shown as a menu item in the left-hand menu (not
 *     all screens in the navigation graph are intended to be directly reached from the menu).
 */
enum class Screen(val route: String, val titleId: Int, val hasMenuItem: Boolean = true) {
  WelcomeScreen("welcome_screen", R.string.welcome_screen, false),
  ExerciseSessions("exercise_sessions", R.string.exercise_sessions),
  ExerciseSessionDetail("exercise_session_detail", R.string.exercise_session_detail, false),
  DifferentialChanges("differential_changes", R.string.differential_changes),
  PrivacyPolicy("privacy_policy", R.string.privacy_policy, false)
}
