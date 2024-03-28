package com.verner.healthgateway.presentation.component

import androidx.health.connect.client.units.Mass
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
fun WeightRecordRow(
  time: ZonedDateTime,
  uid: String,
  weight: Mass,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 4.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    WeightRecordInfoColumn(
      time = time.truncatedTo(ChronoUnit.SECONDS),
      uid = uid,
      weight = weight
    )
  }
}

@Preview
@Composable
fun WeightRecordRowPreview() {
  HealthConnectTheme {
    WeightRecordRow(
      ZonedDateTime.now(),
      UUID.randomUUID().toString(),
      Mass.kilograms(51.0)
    )
  }
}
