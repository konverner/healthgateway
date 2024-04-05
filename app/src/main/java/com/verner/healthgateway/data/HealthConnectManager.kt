package com.verner.healthgateway.data

import android.content.Context
import android.os.Build
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.mutableStateOf
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.HealthConnectClient.Companion.SDK_AVAILABLE
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.changes.Change
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.NutritionRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.records.metadata.DataOrigin
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ChangesTokenRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import java.time.Instant

// The minimum android level that can use Health Connect
const val MIN_SUPPORTED_SDK = Build.VERSION_CODES.O_MR1

/**
 * Demonstrates reading and writing from Health Connect.
 */
class HealthConnectManager(private val context: Context) {
  private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

  var availability = mutableStateOf(HealthConnectAvailability.NOT_SUPPORTED)
    private set

  init {
    checkAvailability()
  }

  fun checkAvailability() {
    availability.value = when {
      HealthConnectClient.getSdkStatus(context) == SDK_AVAILABLE -> HealthConnectAvailability.INSTALLED
      isSupported() -> HealthConnectAvailability.NOT_INSTALLED
      else -> HealthConnectAvailability.NOT_SUPPORTED
    }
  }

  /**
   * Determines whether all the specified permissions are already granted. It is recommended to
   * call [PermissionController.getGrantedPermissions] first in the permissions flow, as if the
   * permissions are already granted then there is no need to request permissions via
   * [PermissionController.createRequestPermissionResultContract].
   */
  suspend fun hasAllPermissions(permissions: Set<String>): Boolean {
    return healthConnectClient.permissionController.getGrantedPermissions().containsAll(permissions)
  }

  fun requestPermissionsActivityContract(): ActivityResultContract<Set<String>, Set<String>> {
    return PermissionController.createRequestPermissionResultContract()
  }

  /**
   * Reads in existing [WeightRecord]s.
   */
  suspend fun readWeightRecords(start: Instant, end: Instant): List<WeightRecord> {
    val request = ReadRecordsRequest(
      recordType = WeightRecord::class,
      timeRangeFilter = TimeRangeFilter.between(start, end),
      ascendingOrder = false
    )
    val response = healthConnectClient.readRecords(request)
    return response.records
  }

  /**
    * Reads in existing [NutritionRecord]s.
   */
  suspend fun readNutritionRecords(start: Instant, end: Instant): List<NutritionRecord> {
    val request = ReadRecordsRequest(
      recordType = NutritionRecord::class,
      timeRangeFilter = TimeRangeFilter.between(start, end),
      ascendingOrder = false
    )
    val response = healthConnectClient.readRecords(request)
    return response.records
  }


  /**
   * Reads sleep sessions for the previous seven days (from yesterday) to show a week's worth of
   * sleep data.
   *
   * In addition to reading [SleepSessionRecord]s, for each session, the duration is calculated to
   * demonstrate aggregation, and the underlying [SleepSessionRecord.Stage] data is also read.
   */
  suspend fun readSleepSessions(
    startTime: Instant,
    endTime: Instant
  ): List<SleepSessionData> {

    val sessions = mutableListOf<SleepSessionData>()
    val sleepSessionRequest = ReadRecordsRequest(
      recordType = SleepSessionRecord::class,
      timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
      ascendingOrder = false
    )
    val sleepSessions = healthConnectClient.readRecords(sleepSessionRequest)
    sleepSessions.records.forEach { session ->
      val sessionTimeFilter = TimeRangeFilter.between(session.startTime, session.endTime)
      val durationAggregateRequest = AggregateRequest(
        metrics = setOf(SleepSessionRecord.SLEEP_DURATION_TOTAL),
        timeRangeFilter = sessionTimeFilter
      )
      val aggregateResponse = healthConnectClient.aggregate(durationAggregateRequest)
      sessions.add(
        SleepSessionData(
          uid = session.metadata.id,
          title = session.title,
          notes = session.notes,
          startTime = session.startTime,
          startZoneOffset = session.startZoneOffset,
          endTime = session.endTime,
          endZoneOffset = session.endZoneOffset,
          duration = aggregateResponse[SleepSessionRecord.SLEEP_DURATION_TOTAL],
          stages = session.stages
        )
      )
    }
    return sessions
  }

  /**
   * Obtains a list of [ExerciseSessionRecord]s in a specified time frame. An Exercise Session Record is a
   * period of time given to an activity, that would make sense to a user, e.g. "Afternoon run"
   * etc. It does not necessarily mean, however, that the user was *running* for that entire time,
   * more that conceptually, this was the activity being undertaken.
   */
  suspend fun readExerciseSessions(start: Instant, end: Instant): List<ExerciseSessionRecord> {
    println("$start, $end")
    val request = ReadRecordsRequest(
      recordType = ExerciseSessionRecord::class,
      timeRangeFilter = TimeRangeFilter.between(start, end),
      ascendingOrder = false
    )
    val response = healthConnectClient.readRecords(request)
    return response.records
  }

  suspend fun readAssociatedRecordData(
    uid: String,
  ): NutritionRecordData {
    val nutritionRecord = healthConnectClient.readRecord(NutritionRecord::class, uid)

    return NutritionRecordData(
      uid = uid,
        mealType = nutritionRecord.record.mealType,
        energy = nutritionRecord.record.energy,
        totalCarbohydrate = nutritionRecord.record.totalCarbohydrate,
        protein = nutritionRecord.record.protein,
        totalFat = nutritionRecord.record.totalFat,
        title = nutritionRecord.record.name,
        time = nutritionRecord.record.startTime,
        notes = nutritionRecord.record.name
    )
  }

  /**
   * Reads aggregated data and raw data for selected data types, for a given [ExerciseSessionRecord].
   */
  suspend fun readAssociatedSessionData(
      uid: String,
  ): ExerciseSessionData {
    val exerciseSession = healthConnectClient.readRecord(ExerciseSessionRecord::class, uid)
    // Use the start time and end time from the session, for reading raw and aggregate data.
    val timeRangeFilter = TimeRangeFilter.between(
      startTime = exerciseSession.record.startTime,
      endTime = exerciseSession.record.endTime
    )
    val aggregateDataTypes = setOf(
      ExerciseSessionRecord.EXERCISE_DURATION_TOTAL,
      StepsRecord.COUNT_TOTAL,
      TotalCaloriesBurnedRecord.ENERGY_TOTAL,
      DistanceRecord.DISTANCE_TOTAL,
      HeartRateRecord.BPM_AVG,
      HeartRateRecord.BPM_MAX,
      HeartRateRecord.BPM_MIN,
//      SpeedRecord.SPEED_AVG,
//      SpeedRecord.SPEED_MAX,
//      SpeedRecord.SPEED_MIN
    )
    // Limit the data read to just the application that wrote the session. This may or may not
    // be desirable depending on the use case: In some cases, it may be useful to combine with
    // data written by other apps.
    val dataOriginFilter = setOf(exerciseSession.record.metadata.dataOrigin)
    val aggregateRequest = AggregateRequest(
      metrics = aggregateDataTypes,
      timeRangeFilter = timeRangeFilter,
      dataOriginFilter = dataOriginFilter
    )
    val aggregateData = healthConnectClient.aggregate(aggregateRequest)
    val speedData = readData<SpeedRecord>(timeRangeFilter, dataOriginFilter)
    val heartRateData = readData<HeartRateRecord>(timeRangeFilter, dataOriginFilter)

    return ExerciseSessionData(
      uid = uid,
      totalActiveTime = aggregateData[ExerciseSessionRecord.EXERCISE_DURATION_TOTAL],
      totalSteps = aggregateData[StepsRecord.COUNT_TOTAL],
      totalDistance = aggregateData[DistanceRecord.DISTANCE_TOTAL],
      totalEnergyBurned = aggregateData[TotalCaloriesBurnedRecord.ENERGY_TOTAL],
//      minHeartRate = aggregateData[HeartRateRecord.BPM_MIN],
//      maxHeartRate = aggregateData[HeartRateRecord.BPM_MAX],
//      avgHeartRate = aggregateData[HeartRateRecord.BPM_AVG],
      heartRateSeries = heartRateData,
      speedRecord = speedData,
      minSpeed = aggregateData[SpeedRecord.SPEED_MIN],
      maxSpeed = aggregateData[SpeedRecord.SPEED_MAX],
      avgSpeed = aggregateData[SpeedRecord.SPEED_AVG],
    )
  }

  /**
   * Obtains a changes token for the specified record types.
   */
  suspend fun getChangesToken(): String {
    return healthConnectClient.getChangesToken(
      ChangesTokenRequest(
        setOf(
          ExerciseSessionRecord::class,
          StepsRecord::class,
          TotalCaloriesBurnedRecord::class,
          HeartRateRecord::class,
          WeightRecord::class
        )
      )
    )
  }

  /**
   * Retrieve changes from a changes token.
   */
  suspend fun getChanges(token: String): Flow<ChangesMessage> = flow {
    var nextChangesToken = token
    do {
      val response = healthConnectClient.getChanges(nextChangesToken)
      if (response.changesTokenExpired) {
        // As described here: https://developer.android.com/guide/health-and-fitness/health-connect/data-and-data-types/differential-changes-api
        // tokens are only valid for 30 days. It is important to check whether the token has
        // expired. As well as ensuring there is a fallback to using the token (for example
        // importing data since a certain date), more importantly, the app should ensure
        // that the changes API is used sufficiently regularly that tokens do not expire.
        throw IOException("Changes token has expired")
      }
      emit(ChangesMessage.ChangeList(response.changes))
      nextChangesToken = response.nextChangesToken
    } while (response.hasMore)
    emit(ChangesMessage.NoMoreChanges(nextChangesToken))
  }

  /**
   * Convenience function to reuse code for reading data.
   */
  private suspend inline fun <reified T : Record> readData(
      timeRangeFilter: TimeRangeFilter,
      dataOriginFilter: Set<DataOrigin> = setOf(),
  ): List<T> {
    val request = ReadRecordsRequest(
      recordType = T::class,
      dataOriginFilter = dataOriginFilter,
      timeRangeFilter = timeRangeFilter
    )
    return healthConnectClient.readRecords(request).records
  }

  private fun isSupported() = Build.VERSION.SDK_INT >= MIN_SUPPORTED_SDK

  // Represents the two types of messages that can be sent in a Changes flow.
  sealed class ChangesMessage {
    data class NoMoreChanges(val nextChangesToken: String) : ChangesMessage()
    data class ChangeList(val changes: List<Change>) : ChangesMessage()
  }
}

/**
 * Health Connect requires that the underlying Health Connect APK is installed on the device.
 * [HealthConnectAvailability] represents whether this APK is indeed installed, whether it is not
 * installed but supported on the device, or whether the device is not supported (based on Android
 * version).
 */
enum class HealthConnectAvailability {
  INSTALLED,
  NOT_INSTALLED,
  NOT_SUPPORTED
}
