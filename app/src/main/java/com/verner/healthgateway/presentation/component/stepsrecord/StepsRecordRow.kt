package com.verner.healthgateway.presentation.component.stepsrecord

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.records.StepsRecord
import com.verner.healthgateway.presentation.theme.HealthConnectTheme
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

/**
 * Creates a row to represent an [StepsRecord]
 */
@Composable
fun StepsRecordRow(
  time: ZonedDateTime,
  steps: Long,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 4.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    StepsRecordInfoColumn(
      time = time.truncatedTo(ChronoUnit.SECONDS),
      steps = steps
    )
  }
}

@Preview
@Composable
fun StepsRecordRowPreview() {
  HealthConnectTheme {
    StepsRecordRow(
      ZonedDateTime.now(),
      10001
    )
  }
}
