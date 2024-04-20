package com.verner.healthgateway.presentation.component.stepsrecord

import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.health.connect.client.records.StepsRecord
import com.verner.healthgateway.presentation.theme.HealthConnectTheme
import java.time.ZonedDateTime

/**
 * Displays summary information about the [StepsRecord]
 */

@Composable
fun StepsRecordInfoColumn(
    time: ZonedDateTime,
    steps: Long
) {
  Column(
  ) {
    Text(steps.toString() + " steps")
    Text(
      color = MaterialTheme.colors.primary,
      text = "${time.toLocalDate()}",
      style = MaterialTheme.typography.caption
    )
  }
}

@Preview
@Composable
fun StepsRecordInfoColumnPreview() {
  HealthConnectTheme {
    StepsRecordInfoColumn(
      ZonedDateTime.now(),
      10001
    )
  }
}
