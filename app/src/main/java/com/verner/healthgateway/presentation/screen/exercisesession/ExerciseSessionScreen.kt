package com.verner.healthgateway.presentation.screen.exercisesession

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.verner.healthgateway.R
import com.verner.healthgateway.data.ExerciseSessionData
import com.verner.healthgateway.presentation.component.exercisesession.ExerciseSessionRow
import java.time.ZonedDateTime
import java.util.UUID

/**
 * Shows a list of [ExerciseSessionRecord]s from today.
 */
@Composable
fun mapExerciseType(exerciseType: Int): String {
  return when (exerciseType) {
    56 -> stringResource(R.string.exercise_type_running)
    79 -> stringResource(R.string.exercise_type_walking)
    8 -> stringResource(R.string.exercise_type_biking)
    else -> "Unknown exercise"
  }
}

@Composable
fun ExerciseSessionScreen(
  permissions: Set<String>,
  permissionsGranted: Boolean,
  sessionsList: List<ExerciseSessionRecord>,
  sessionsMetrics: Map<String, ExerciseSessionData>,
  uiState: ExerciseSessionViewModel.UiState,
  onImportClick: () -> Unit = {},
  onExportCsvClick: () -> Unit = {},
  onExportDbClick: () -> Unit = {},
  onDetailsClick: (String) -> Unit = {},
  onError: (Throwable?) -> Unit = {},
  onPermissionsResult: () -> Unit = {},
  onPermissionsLaunch: (Set<String>) -> Unit = {},
) {

  // Remember the last error ID, such that it is possible to avoid re-launching the error
  // notification for the same error when the screen is recomposed, or configuration changes etc.
  val errorId = rememberSaveable { mutableStateOf(UUID.randomUUID()) }

  LaunchedEffect(uiState) {
    // If the initial data load has not taken place, attempt to load the data.
    if (uiState is ExerciseSessionViewModel.UiState.Uninitialized) {
      onPermissionsResult()
    }

    // The [ExerciseSessionViewModel.UiState] provides details of whether the last action was a
    // success or resulted in an error. Where an error occurred, for example in reading and
    // writing to Health Connect, the user is notified, and where the error is one that can be
    // recovered from, an attempt to do so is made.
    if (uiState is ExerciseSessionViewModel.UiState.Error && errorId.value != uiState.uuid) {
      onError(uiState.exception)
      errorId.value = uiState.uuid
    }
  }

  if (uiState != ExerciseSessionViewModel.UiState.Uninitialized) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      if (!permissionsGranted) {
        item {
          Button(
            onClick = {
              onPermissionsLaunch(permissions)
            }
          ) {
            Text(text = stringResource(R.string.permissions_button_label))
          }
        }
      } else {
        item {
          Button(
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .padding(4.dp),
            onClick = {
              onImportClick()
            }
          ) {
            Text(stringResource(id = R.string.import_sessions))
          }
        }
        item {
          Button(
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .padding(4.dp),
            onClick = {
              onExportCsvClick()
            }
          ) {
            Text(stringResource(id = R.string.export_csv_sessions))
          }
        }
//        item {
//          Button(
//            modifier = Modifier
//              .fillMaxWidth()
//              .height(48.dp)
//              .padding(4.dp),
//            onClick = {
//              onExportDbClick()
//            }
//          ) {
//            Text(stringResource(id = R.string.export_db_exercise_session))
//          }
//        }
      }
      items(sessionsList) { session ->
        ExerciseSessionRow(
          ZonedDateTime.ofInstant(session.startTime, session.startZoneOffset),
          ZonedDateTime.ofInstant(session.endTime, session.endZoneOffset),
          sessionsMetrics[session.metadata.id]?.totalEnergyBurned,
          mapExerciseType(session.exerciseType),
          onDetailsClick = { onDetailsClick(session.metadata.id)
          }
        )
      }
    }
  }
}
