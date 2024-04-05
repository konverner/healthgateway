package com.verner.healthgateway.presentation.screen.sleepsession

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.records.SleepSessionRecord
import com.verner.healthgateway.R
import com.verner.healthgateway.data.SleepSessionData
import com.verner.healthgateway.presentation.component.sleepsession.SleepSessionRow
import com.verner.healthgateway.presentation.theme.HealthConnectTheme
import java.time.Duration
import java.time.ZonedDateTime
import java.util.UUID

/**
 * Shows a week's worth of sleep data.
 */
@Composable
fun SleepSessionScreen(
    permissions: Set<String>,
    permissionsGranted: Boolean,
    sessionsList: List<SleepSessionData>,
    uiState: SleepSessionViewModel.UiState,
    onImportClick: () -> Unit = {},
    onExportCsvClick: () -> Unit = {},
    onError: (Throwable?) -> Unit = {},
    onPermissionsResult: () -> Unit = {},
    onPermissionsLaunch: (Set<String>) -> Unit = {}
) {

    // Remember the last error ID, such that it is possible to avoid re-launching the error
    // notification for the same error when the screen is recomposed, or configuration changes etc.
    val errorId = rememberSaveable { mutableStateOf(UUID.randomUUID()) }

    LaunchedEffect(uiState) {
        // If the initial data load has not taken place, attempt to load the data.
        if (uiState is SleepSessionViewModel.UiState.Uninitialized) {
            onPermissionsResult()
        }

        // The [SleepSessionViewModel.UiState] provides details of whether the last action was a
        // success or resulted in an error. Where an error occurred, for example in reading and
        // writing to Health Connect, the user is notified, and where the error is one that can be
        // recovered from, an attempt to do so is made.
        if (uiState is SleepSessionViewModel.UiState.Error && errorId.value != uiState.uuid) {
            onError(uiState.exception)
            errorId.value = uiState.uuid
        }
    }

    if (uiState != SleepSessionViewModel.UiState.Uninitialized) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!permissionsGranted) {
                item {
                    Button(
                        onClick = { onPermissionsLaunch(permissions) }
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
                        Text(stringResource(id = R.string.import_sleep_sessions))
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
                        Text(stringResource(id = R.string.export_csv_sleep_sessions))
                    }
                }
                items(sessionsList) { session ->
                    SleepSessionRow(session)
                }
            }
        }
    }
}

@Preview
@Composable
fun SleepSessionScreenPreview() {
    HealthConnectTheme {
        val end2 = ZonedDateTime.now()
        val start2 = end2.minusHours(5)
        val end1 = end2.minusDays(1)
        val start1 = end1.minusHours(5)
        SleepSessionScreen(
            permissions = setOf(),
            permissionsGranted = true,
            sessionsList = listOf(
                SleepSessionData(
                    uid = "123",
                    title = "My sleep",
                    notes = "Slept well",
                    startTime = start1.toInstant(),
                    startZoneOffset = start1.offset,
                    endTime = end1.toInstant(),
                    endZoneOffset = end1.offset,
                    duration = Duration.between(start1, end1),
                    stages = listOf(
                        SleepSessionRecord.Stage(
                            stage = SleepSessionRecord.STAGE_TYPE_DEEP,
                            startTime = start1.toInstant(),
                            endTime = end1.toInstant()
                        )
                    )
                ),
                SleepSessionData(
                    uid = "123",
                    title = "My sleep",
                    notes = "Slept well",
                    startTime = start2.toInstant(),
                    startZoneOffset = start2.offset,
                    endTime = end2.toInstant(),
                    endZoneOffset = end2.offset,
                    duration = Duration.between(start2, end2),
                    stages = listOf(
                        SleepSessionRecord.Stage(
                            stage = SleepSessionRecord.STAGE_TYPE_DEEP,
                            startTime = start2.toInstant(),
                            endTime = end2.toInstant()
                        )
                    )
                )
            ),
            uiState = SleepSessionViewModel.UiState.Done
        )
    }
}
