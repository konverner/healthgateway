package com.verner.healthgateway.data

import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime


fun dateTimeWithOffsetOrDefault(time: Instant, offset: ZoneOffset?): ZonedDateTime =
  if (offset != null) {
    ZonedDateTime.ofInstant(time, offset)
  } else {
    ZonedDateTime.ofInstant(time, ZoneId.systemDefault())
  }

fun Duration.formatTime() = String.format(
  "%02d:%02d:%02d",
  this.toHours() % 24,
  this.toMinutes() % 60,
  this.seconds % 60
)

fun Duration.formatHoursMinutes() = String.format(
  "%01dh%02dm",
  this.toHours() % 24,
  this.toMinutes() % 60
)
