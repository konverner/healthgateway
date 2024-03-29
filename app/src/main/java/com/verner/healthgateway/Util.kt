package com.verner.healthgateway

import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import com.verner.healthgateway.data.dateTimeWithOffsetOrDefault
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Shows details of a given throwable in the snackbar
 */
fun showExceptionSnackbar(
  scaffoldState: ScaffoldState,
  scope: CoroutineScope,
  throwable: Throwable?,
) {
  scope.launch {
    scaffoldState.snackbarHostState.showSnackbar(
      message = throwable?.localizedMessage ?: "Unknown exception",
      duration = SnackbarDuration.Short
    )
  }
}


fun formatDisplayTimeStartEnd(
  startTime: Instant,
  startZoneOffset: ZoneOffset?,
  endTime: Instant,
  endZoneOffset: ZoneOffset?
): String {
  val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
  val start = timeFormatter.format(dateTimeWithOffsetOrDefault(startTime, startZoneOffset))
  val end = timeFormatter.format(dateTimeWithOffsetOrDefault(endTime, endZoneOffset))
  return "$start - $end"
}