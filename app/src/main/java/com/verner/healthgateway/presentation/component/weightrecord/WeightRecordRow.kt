package com.verner.healthgateway.presentation.component.weightrecord

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.units.Mass
import com.verner.healthgateway.presentation.theme.HealthConnectTheme
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

/**
 * Creates a row to represent an [WeightRecord]
 */
@Composable
fun WeightRecordRow(
  time: ZonedDateTime,
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
      Mass.kilograms(51.0)
    )
  }
}
