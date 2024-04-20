package com.verner.healthgateway.presentation.screen.sleepsession

import android.content.Context
import android.os.RemoteException
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.verner.healthgateway.R
import com.verner.healthgateway.data.HealthConnectManager
import com.verner.healthgateway.data.SleepSessionData
import com.verner.healthgateway.presentation.DOWNLOAD_DIR
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class SleepSessionViewModel(
    private val context: Context,
    private val healthConnectManager: HealthConnectManager
    ) :
    ViewModel() {

    val permissions = setOf(
        HealthPermission.getReadPermission(SleepSessionRecord::class),
        HealthPermission.getWritePermission(SleepSessionRecord::class)
    )

    var permissionsGranted = mutableStateOf(false)
        private set

    var sessionsList: MutableState<List<SleepSessionData>> = mutableStateOf(listOf())
        private set

    var uiState: UiState by mutableStateOf(UiState.Uninitialized)
        private set

    val permissionsLauncher = healthConnectManager.requestPermissionsActivityContract()

    fun initialLoad() {
        viewModelScope.launch {
            tryWithPermissionsCheck {
            }
        }
    }

    /**
     * Provides permission check and error handling for Health Connect suspend function calls.
     *
     * Permissions are checked prior to execution of [block], and if all permissions aren't granted
     * the [block] won't be executed, and [permissionsGranted] will be set to false, which will
     * result in the UI showing the permissions button.
     *
     * Where an error is caught, of the type Health Connect is known to throw, [uiState] is set to
     * [UiState.Error], which results in the snackbar being used to show the error message.
     */
    private suspend fun tryWithPermissionsCheck(block: suspend () -> Unit) {
        permissionsGranted.value = healthConnectManager.hasAllPermissions(permissions)
        uiState = try {
            if (permissionsGranted.value) {
                block()
            }
            UiState.Done
        } catch (remoteException: RemoteException) {
            UiState.Error(remoteException)
        } catch (securityException: SecurityException) {
            UiState.Error(securityException)
        } catch (ioException: IOException) {
            UiState.Error(ioException)
        } catch (illegalStateException: IllegalStateException) {
            UiState.Error(illegalStateException)
        }
    }

    fun importSleepSessions() {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                val startOfDay = ZonedDateTime.now().minusYears(5)
                val now = ZonedDateTime.now()
                sessionsList.value = healthConnectManager.readSleepSessions(
                    startOfDay.toInstant(),
                    now.toInstant()
                )

                // Get earliest session which is date of initial app install - 30 days
                // see: https://developer.android.com/health-and-fitness/guides/health-connect/develop/read-data#read-restriction
                val firstInstallTime: Long = context
                    .packageManager
                    .getPackageInfo(context.getPackageName(), 0).firstInstallTime

                val firstInstallDate = Date(firstInstallTime)
                val calendar = Calendar.getInstance()

                calendar.time = firstInstallDate
                calendar.add(Calendar.DAY_OF_MONTH, -30)

                val earliestSessionDate = calendar.time
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val earliestSessionDateFormatted = dateFormat.format(earliestSessionDate)

                val countRecordsImported = sessionsList.value.count()
                Toast.makeText(
                    context,
                    "$countRecordsImported new sessions imported starting from $earliestSessionDateFormatted",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun exportCsvSleepSessions() {
        viewModelScope.launch {
            val sessions = sessionsList.value
            val countRecordsExported = sessions.count()

            try {
                val fullDownloadDirectory = File(
                    "/storage/emulated/0",
                    DOWNLOAD_DIR
                )
                val sleepDirectory = File(
                    fullDownloadDirectory,
                    "sleep sessions"
                )

                if (!sleepDirectory.exists()) {
                    sleepDirectory.mkdirs()
                }

                val file = File(
                    sleepDirectory,
                    SimpleDateFormat(
                        "yyyy-MM-dd-HH-mm-ss",
                        Locale.getDefault()
                    ).format(Date()) + ".csv"
                )
                val writer = FileWriter(file)

                // Writing CSV header
                writer.append(R.string.sleep_csv_header.toString() + "\n")

                // Writing session data
                for (session in sessions) {
                    writer.append(
                        "${session.uid}," +
                        "${session.startTime}," +
                        "${session.endTime}," +
                        "${session.duration?.toMinutes()}," +
                        "${session.title}," +
                        "${session.notes}\n"
                    )
                }
                writer.close()
                Toast.makeText(
                    context,
                    "$countRecordsExported records have been exported to $DOWNLOAD_DIR",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    context,
                    "Error while exporting",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    sealed class UiState {
        object Uninitialized : UiState()
        object Done : UiState()

        // A random UUID is used in each Error object to allow errors to be uniquely identified,
        // and recomposition won't result in multiple snackbars.
        data class Error(val exception: Throwable, val uuid: UUID = UUID.randomUUID()) : UiState()
    }
}

class SleepSessionViewModelFactory(
    private val context: Context,
    private val healthConnectManager: HealthConnectManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SleepSessionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SleepSessionViewModel(
                context = context,
                healthConnectManager = healthConnectManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
