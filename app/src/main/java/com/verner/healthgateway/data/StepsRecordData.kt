package com.verner.healthgateway.data

import androidx.health.connect.client.records.StepsRecord
import java.time.Instant
import java.time.ZoneOffset

/**
 * Data class representing a record of steps data from [StepsRecord]
 */
data class StepsRecordData(
    val uid: String,
    val startTime: Instant,
    val startZoneOffset: ZoneOffset?,
    val endTime: Instant,
    val endZoneOffset: ZoneOffset?,
    val count: Long
)