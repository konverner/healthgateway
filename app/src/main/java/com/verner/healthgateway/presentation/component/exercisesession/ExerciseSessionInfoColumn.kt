package com.verner.healthgateway.presentation.component.exercisesession

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.units.Energy
import com.verner.healthgateway.presentation.theme.HealthConnectTheme
import java.time.ZonedDateTime

/**
 * Displays summary information about the [ExerciseSessionRecord]
 */

@Composable
fun ExerciseSessionInfoColumn(
    start: ZonedDateTime,
    end: ZonedDateTime,
    energy: Energy?,
    type: String,
    onClick: (String) -> Unit = {},
) {
  Column(
    modifier = Modifier.clickable {
      onClick(type)
    }
  ) {
    Text(type)
    Text(
      color = MaterialTheme.colors.primary,
      text = "${start.toLocalDate()}",
      style = MaterialTheme.typography.caption
    )
    Text(energy.toString())
    Text(
      color = MaterialTheme.colors.primary,
      text = "${start.toLocalTime()} - ${end.toLocalTime()}",
      style = MaterialTheme.typography.caption
    )
  }
}

@Preview
@Composable
fun ExerciseSessionInfoColumnPreview() {
  HealthConnectTheme {
    ExerciseSessionInfoColumn(
      ZonedDateTime.now().minusMinutes(30),
      ZonedDateTime.now(),
      Energy.kilocalories(200.0),
      "Running"
    )
  }
}
