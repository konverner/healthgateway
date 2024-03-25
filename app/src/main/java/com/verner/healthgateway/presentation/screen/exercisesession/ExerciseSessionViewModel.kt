package com.verner.healthgateway.presentation.screen.exercisesession

import android.content.Context
import android.os.RemoteException
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.verner.healthgateway.data.ExerciseSessionData
import com.verner.healthgateway.data.HealthConnectManager
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.models.Collection
import io.appwrite.models.Database
import io.appwrite.services.Databases
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID


class ExerciseSessionViewModel(
  private val context: Context,
  private val healthConnectManager: HealthConnectManager
  ) :
  ViewModel() {
  val permissions = setOf(
    HealthPermission.getWritePermission(ExerciseSessionRecord::class),
    HealthPermission.getReadPermission(ExerciseSessionRecord::class),
    HealthPermission.getWritePermission(StepsRecord::class),
    HealthPermission.getWritePermission(SpeedRecord::class),
    HealthPermission.getReadPermission(SpeedRecord::class),
    HealthPermission.getWritePermission(TotalCaloriesBurnedRecord::class),
    HealthPermission.getWritePermission(HeartRateRecord::class),
    HealthPermission.getWritePermission(DistanceRecord::class),
    HealthPermission.getReadPermission(DistanceRecord::class)
  )

  var permissionsGranted = mutableStateOf(false)
    private set

  var sessionsList: MutableState<List<ExerciseSessionRecord>> = mutableStateOf(listOf())
    private set

  var sessionsMetrics: MutableState<Map<String, ExerciseSessionData>> = mutableStateOf(emptyMap())
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

  fun insertExerciseSession() {
    viewModelScope.launch {
      tryWithPermissionsCheck {
        readExerciseSessions()
      }
    }
  }

  fun readExerciseSessions() {
    viewModelScope.launch {
      tryWithPermissionsCheck {
        val startOfDay = ZonedDateTime.now().minusYears(5)
        val now = ZonedDateTime.now()
        sessionsList.value = healthConnectManager.readExerciseSessions(
          startOfDay.toInstant(), now.toInstant()
        )

        // Read associated metrics for each record
        for (session in sessionsList.value) {
          val sessionMetrics = healthConnectManager.readAssociatedSessionData(session.metadata.id)

          // Add sessionMetrics as value to sessionsMap with key session.metadata.id
          val updatedSessionsMap = sessionsMetrics.value.toMutableMap()
          updatedSessionsMap[session.metadata.id] = sessionMetrics
          sessionsMetrics.value = updatedSessionsMap

        }

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
          "$countRecordsImported new records imported starting from $earliestSessionDateFormatted",
          Toast.LENGTH_LONG
        ).show()
      }
    }
  }

  fun exportCsvExerciseSessions() {
    viewModelScope.launch {
      val sessions = sessionsList.value
      var countRecordsExported = 0
      try {
        val directory = File(
          "/storage/emulated/0/Download/Health Connect Data",
          "exercise sessions"
        )

        if (!directory.exists()) {
          directory.mkdirs()
        }

        val file = File(
                directory,
                SimpleDateFormat(
                  "yyyy-MM-dd-HH-mm-ss",
                  Locale.getDefault()
                ).format(Date()) + ".csv"
        )
        val writer = FileWriter(file)

        // Writing CSV header
        writer.append("uid,startTime,endTime,exerciseType,totalDistance,totalEnergy\n")

        // Writing session data
        for (session in sessions) {

          val sessionMetrics = sessionsMetrics.value[session.metadata.id]

          val distMatch = Regex("""(\d+(\.\d+)?)""").find(
            sessionMetrics?.totalDistance.toString()
          )
          val floatDistValue = distMatch?.value?.toFloatOrNull() ?: 0.0f

          val energyMatch = Regex("""(\d+(\.\d+)?)""").find(
            sessionMetrics?.totalEnergyBurned.toString()
          )
          val floatEnergyValue = energyMatch?.value?.toFloatOrNull() ?: 0.0f

          if (floatEnergyValue > 1) {
            writer.append(
              "${session.metadata.id}," +
                      "${session.startTime}," +
                      "${session.endTime}," +
                      "${session.exerciseType}," +
                      "${floatDistValue}," +
                      "${floatEnergyValue}\n"
              //                    "${sessionMetrics.minSpeed}," +
              //                    "${sessionMetrics.maxSpeed}," +
              //                    "${sessionMetrics.avgSpeed}"
            )
            countRecordsExported++
          }
        }
        writer.close()
        Toast.makeText(
          context,
          "$countRecordsExported records have been exported to ${file.path}",
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

  // TODO
  fun exportDbExerciseSessions() {
    viewModelScope.launch {
      val sessions = sessionsList.value

      val client = Client()
        .setEndpoint("https://cloud.appwrite.io/v1")
        .setProject("<PROJECT_ID>")
        .setKey("<API_KEY>");

      val databases = Databases(client)

      var database: Database? = null
      var exercises_colelction: Collection? = null

      database = databases.create(ID.unique(), "HealthConnectData")
      //database = databases.get("65fc32cb35132728c7d3")

      exercises_colelction = databases.createCollection(database?.id!!, ID.unique(), "Exercises")
      //exercises_colelction = databases.getCollection(database?.id!!, "65fc32cbdcb1e3952d6b")

      databases.createStringAttribute(
        databaseId = database?.id!!,
        collectionId = exercises_colelction?.id!!,
        key = "uid",
        size = 255,
        required = true
      )

      databases.createDatetimeAttribute(
        databaseId = database?.id!!,
        collectionId = exercises_colelction?.id!!,
        key = "startTime",
        required = true
      )

      databases.createDatetimeAttribute(
        databaseId = database?.id!!,
        collectionId = exercises_colelction?.id!!,
        key = "endTime",
        required = true
      )

      databases.createIntegerAttribute(
        databaseId = database?.id!!,
        collectionId = exercises_colelction?.id!!,
        key = "exerciseType",
        required = true
      )

      databases.createFloatAttribute(
        databaseId = database?.id!!,
        collectionId = exercises_colelction?.id!!,
        key = "totalDistance",
        required = true
      )

      databases.createFloatAttribute(
        databaseId = database?.id!!,
        collectionId = exercises_colelction?.id!!,
        key = "totalEnergyBurned",
        required = true
      )

      // Writing session data
      for (session in sessions) {
        val sessionMetrics = healthConnectManager.readAssociatedSessionData(session.metadata.id)

        val distMatch = Regex("""(\d+(\.\d+)?)""").find(sessionMetrics.totalDistance.toString())
        val floatDistValue = distMatch?.value?.toFloatOrNull() ?: 0.0f

        val energyMatch = Regex("""(\d+(\.\d+)?)""").find(sessionMetrics.totalEnergyBurned.toString())
        val floatEnergyValue = energyMatch?.value?.toFloatOrNull() ?: 0.0f

        if (floatEnergyValue > 1) {
          val item = mapOf(
            "uid" to session.metadata.id,
            "startTime" to session.startTime.toString(),
            "endTime" to session.endTime.toString(),
            "exerciseType" to session.exerciseType,
            "totalDistance" to floatDistValue,
            "totalEnergyBurned" to floatEnergyValue
          )
          println(item)
          databases.createDocument(
            databaseId = database?.id!!,
            collectionId = exercises_colelction?.id!!,
            documentId = ID.unique(),
            data = item
          )
        }

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

  sealed class UiState {
    object Uninitialized : UiState()
    object Done : UiState()

    // A random UUID is used in each Error object to allow errors to be uniquely identified,
    // and recomposition won't result in multiple snackbars.
    data class Error(val exception: Throwable, val uuid: UUID = UUID.randomUUID()) : UiState()
  }
}

class ExerciseSessionViewModelFactory(
    private val context: Context,
    private val healthConnectManager: HealthConnectManager,
) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(ExerciseSessionViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return ExerciseSessionViewModel(
        context = context,
        healthConnectManager = healthConnectManager
      ) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
