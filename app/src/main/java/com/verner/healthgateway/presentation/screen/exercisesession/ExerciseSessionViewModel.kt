package com.verner.healthgateway.presentation.screen.exercisesession

import android.os.RemoteException
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
import com.verner.healthgateway.data.HealthConnectManager
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale
import java.util.UUID
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.services.Databases
import io.appwrite.models.Database
import io.appwrite.models.Collection


class ExerciseSessionViewModel(
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

  var uiState: UiState by mutableStateOf(UiState.Uninitialized)
    private set

  val permissionsLauncher = healthConnectManager.requestPermissionsActivityContract()

  fun initialLoad() {
    viewModelScope.launch {
      tryWithPermissionsCheck {
        readExerciseSessions()
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
        val startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).minusDays(7)
        val now = Instant.now()
        sessionsList.value = healthConnectManager.readExerciseSessions(startOfDay.toInstant(), now)
      }
    }
  }

  fun exportCsvExerciseSessions() {
    viewModelScope.launch {
      val sessions = sessionsList.value
      var countRecordsExported = 0
      try {
        val fileName = SimpleDateFormat(
          "yyyyMMddHHmmss",
          Locale.getDefault()
        ).format(Date()) + "_exercise_sessions.csv"
        val directory = File("/storage/emulated/0/Download", "Health Connect Data")
        if (!directory.exists()) {
          directory.mkdirs()
        }
        val file = File(directory, fileName)
        val writer = FileWriter(file)

        // Writing CSV header
        writer.append("uid,startTime,endTime,exerciseType,totalDistance,totalEnergy\n")

        // Writing session data
        for (session in sessions) {
          val sessionMetrics = healthConnectManager.readAssociatedSessionData(session.metadata.id)

          val distMatch = Regex("""(\d+(\.\d+)?)""").find(sessionMetrics.totalDistance.toString())
          val floatDistValue = distMatch?.value?.toFloatOrNull() ?: 0.0f

          val energyMatch = Regex("""(\d+(\.\d+)?)""").find(sessionMetrics.totalEnergyBurned.toString())
          val floatEnergyValue = energyMatch?.value?.toFloatOrNull() ?: 0.0f

          if (floatEnergyValue > 1) {
            writer.append(
              "${session.metadata.id}," +
                      "${session.startTime}," +
                      "${session.endTime}," +
                      "${session.exerciseType}," +
                      "${floatDistValue}," +
                      "${floatEnergyValue},"
              //                    "${sessionMetrics.minSpeed}," +
              //                    "${sessionMetrics.maxSpeed}," +
              //                    "${sessionMetrics.avgSpeed}"
            )
            countRecordsExported++
          }
        }
        writer.close()

      } catch (e: Exception) {
        e.printStackTrace()
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
    private val healthConnectManager: HealthConnectManager,
) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(ExerciseSessionViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return ExerciseSessionViewModel(
        healthConnectManager = healthConnectManager
      ) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
