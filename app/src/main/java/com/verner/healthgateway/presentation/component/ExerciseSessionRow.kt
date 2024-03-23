package com.verner.healthgateway.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.verner.healthgateway.presentation.theme.HealthConnectTheme
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

/**
 * Creates a row to represent an [ExerciseSessionRecord]
 */
@Composable
fun ExerciseSessionRow(
  start: ZonedDateTime,
  end: ZonedDateTime,
  uid: String,
  type: String,
  onDetailsClick: (String) -> Unit = {},
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 4.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    ExerciseSessionInfoColumn(
      start = start.truncatedTo(ChronoUnit.SECONDS),
      end = end.truncatedTo(ChronoUnit.SECONDS),
      uid = uid,
      type = type,
      onClick = onDetailsClick
    )
  }
}

@Preview
@Composable
fun ExerciseSessionRowPreview() {
  HealthConnectTheme {
    ExerciseSessionRow(
      ZonedDateTime.now().minusMinutes(30),
      ZonedDateTime.now(),
      UUID.randomUUID().toString(),
      "Running"
    )
  }
}
