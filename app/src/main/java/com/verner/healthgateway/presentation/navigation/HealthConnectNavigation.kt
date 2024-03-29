package com.verner.healthgateway.presentation.navigation

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.verner.healthgateway.data.HealthConnectManager
import com.verner.healthgateway.presentation.screen.WelcomeScreen
import com.verner.healthgateway.presentation.screen.changes.DifferentialChangesScreen
import com.verner.healthgateway.presentation.screen.changes.DifferentialChangesViewModel
import com.verner.healthgateway.presentation.screen.changes.DifferentialChangesViewModelFactory
import com.verner.healthgateway.presentation.screen.exercisesession.ExerciseSessionScreen
import com.verner.healthgateway.presentation.screen.exercisesession.ExerciseSessionViewModel
import com.verner.healthgateway.presentation.screen.exercisesession.ExerciseSessionViewModelFactory
import com.verner.healthgateway.presentation.screen.exercisesessiondetail.ExerciseSessionDetailScreen
import com.verner.healthgateway.presentation.screen.exercisesessiondetail.ExerciseSessionDetailViewModel
import com.verner.healthgateway.presentation.screen.exercisesessiondetail.ExerciseSessionDetailViewModelFactory
import com.verner.healthgateway.presentation.screen.privacypolicy.PrivacyPolicyScreen
import com.verner.healthgateway.presentation.screen.sleepsession.SleepSessionScreen
import com.verner.healthgateway.presentation.screen.sleepsession.SleepSessionViewModel
import com.verner.healthgateway.presentation.screen.sleepsession.SleepSessionViewModelFactory
import com.verner.healthgateway.presentation.screen.weightrecords.WeightRecordScreen
import com.verner.healthgateway.presentation.screen.weightrecords.WeightRecordViewModel
import com.verner.healthgateway.presentation.screen.weightrecords.WeightRecordViewModelFactory
import com.verner.healthgateway.showExceptionSnackbar

/**
 * Provides the navigation in the app.
 */
@Composable
fun HealthConnectNavigation(
  context: Context,
  navController: NavHostController,
  healthConnectManager: HealthConnectManager,
  scaffoldState: ScaffoldState,
) {
  val scope = rememberCoroutineScope()
  NavHost(navController = navController, startDestination = Screen.WelcomeScreen.route) {
    val availability by healthConnectManager.availability
    composable(Screen.WelcomeScreen.route) {
      WelcomeScreen(
        healthConnectAvailability = availability,
        onResumeAvailabilityCheck = {
          healthConnectManager.checkAvailability()
        }
      )
    }
    composable(
      route = Screen.PrivacyPolicy.route,
      deepLinks = listOf(
        navDeepLink {
          action = "androidx.health.ACTION_SHOW_PERMISSIONS_RATIONALE"
        }
      )
    ) {
      PrivacyPolicyScreen()
    }
    composable(Screen.ExerciseSessions.route) {
      val viewModel: ExerciseSessionViewModel = viewModel(
        factory = ExerciseSessionViewModelFactory(
          context = context,
          healthConnectManager = healthConnectManager
        )
      )
      val permissionsGranted by viewModel.permissionsGranted
      val sessionsList by viewModel.sessionsList
      val permissions = viewModel.permissions
      val onPermissionsResult = { viewModel.initialLoad() }
      val permissionsLauncher =
        rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
          onPermissionsResult()
        }
      ExerciseSessionScreen(
        context = context,
        permissionsGranted = permissionsGranted,
        permissions = permissions,
        sessionsList = sessionsList,
        uiState = viewModel.uiState,
        onImportClick = {
          viewModel.insertExerciseSession()
        },
        onExportCsvClick = {
          viewModel.exportCsvExerciseSessions()
        },
        onExportDbClick = {
          viewModel.exportDbExerciseSessions()
        },
        onDetailsClick = { uid ->
          navController.navigate(Screen.ExerciseSessionDetail.route + "/" + uid)
        },
        onError = { exception ->
          showExceptionSnackbar(scaffoldState, scope, exception)
        },
        onPermissionsResult = {
          viewModel.initialLoad()
        },
        onPermissionsLaunch = { values ->
          permissionsLauncher.launch(values)
        }
      )
    }
    composable(Screen.ExerciseSessionDetail.route + "/{$UID_NAV_ARGUMENT}") {
      val uid = it.arguments?.getString(UID_NAV_ARGUMENT)!!
      println("UID: $uid")
      val viewModel: ExerciseSessionDetailViewModel = viewModel(
        factory = ExerciseSessionDetailViewModelFactory(
          uid = uid,
          healthConnectManager = healthConnectManager
        )
      )
      val permissionsGranted by viewModel.permissionsGranted
      val sessionMetrics by viewModel.sessionMetrics
      val permissions = viewModel.permissions
      val onPermissionsResult = { viewModel.initialLoad() }
      val permissionsLauncher =
        rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
          onPermissionsResult()
        }
      ExerciseSessionDetailScreen(
        permissions = permissions,
        permissionsGranted = permissionsGranted,
        sessionMetrics = sessionMetrics,
        uiState = viewModel.uiState,
        onError = { exception ->
          showExceptionSnackbar(scaffoldState, scope, exception)
        },
        onPermissionsResult = {
          viewModel.initialLoad()
        },
        onPermissionsLaunch = { values ->
          permissionsLauncher.launch(values)
        }
      )
    }
    composable(Screen.SleepSessions.route) {
      val viewModel: SleepSessionViewModel = viewModel(
        factory = SleepSessionViewModelFactory(
          context = context,
          healthConnectManager = healthConnectManager
        )
      )
      val permissionsGranted by viewModel.permissionsGranted
      val sessionsList by viewModel.sessionsList
      val permissions = viewModel.permissions
      val onPermissionsResult = {viewModel.initialLoad()}
      val permissionsLauncher =
        rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
          onPermissionsResult()}
      SleepSessionScreen(
        permissionsGranted = permissionsGranted,
        permissions = permissions,
        sessionsList = sessionsList,
        uiState = viewModel.uiState,
        onImportClick = {
          viewModel.importSleepSessions()
        },
        onExportCsvClick = {
          viewModel.exportCsvSleepSessions()
        },
//        onExportDbClick = {
//          viewModel.exportDbWeightRecords()
//        },
        onError = { exception ->
          showExceptionSnackbar(scaffoldState, scope, exception)
        },
        onPermissionsResult = {
          viewModel.initialLoad()
        },
        onPermissionsLaunch = { values ->
          permissionsLauncher.launch(values)}
      )
    }
    composable(Screen.WeightRecords.route) {
      val viewModel: WeightRecordViewModel = viewModel(
        factory = WeightRecordViewModelFactory(
          context = context,
          healthConnectManager = healthConnectManager
        )
      )
      val permissionsGranted by viewModel.permissionsGranted
      val recordsList by viewModel.recordsList
      val permissions = viewModel.permissions
      val onPermissionsResult = { viewModel.initialLoad() }
      val permissionsLauncher =
        rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
          onPermissionsResult()
        }
      WeightRecordScreen(
        context = context,
        permissionsGranted = permissionsGranted,
        permissions = permissions,
        recordsList = recordsList,
        uiState = viewModel.uiState,
        onImportClick = {
          viewModel.insertWeightRecords()
        },
        onExportCsvClick = {
          viewModel.exportCsvWeightRecords()
        },
//        onExportDbClick = {
//          viewModel.exportDbWeightRecords()
//        },
        onError = { exception ->
          showExceptionSnackbar(scaffoldState, scope, exception)
        },
        onPermissionsResult = {
          viewModel.initialLoad()
        },
        onPermissionsLaunch = { values ->
          permissionsLauncher.launch(values)
        }
      )
    }
    composable(Screen.DifferentialChanges.route) {
      val viewModel: DifferentialChangesViewModel = viewModel(
        factory = DifferentialChangesViewModelFactory(
          healthConnectManager = healthConnectManager
        )
      )
      val changesToken by viewModel.changesToken
      val permissionsGranted by viewModel.permissionsGranted
      val permissions = viewModel.permissions
      val onPermissionsResult = {viewModel.initialLoad()}
      val permissionsLauncher =
        rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
          onPermissionsResult()}
      DifferentialChangesScreen(
        permissionsGranted = permissionsGranted,
        permissions = permissions,
        changesEnabled = changesToken != null,
        onChangesEnable = { enabled ->
          viewModel.enableOrDisableChanges(enabled)
        },
        changes = viewModel.changes,
        changesToken = changesToken,
        onGetChanges = {
          viewModel.getChanges()
        },
        uiState = viewModel.uiState,
        onError = { exception ->
          showExceptionSnackbar(scaffoldState, scope, exception)
        },
        onPermissionsResult = {
          viewModel.initialLoad()
        },
        onPermissionsLaunch = { values ->
          permissionsLauncher.launch(values)}
      )
    }
  }
}
