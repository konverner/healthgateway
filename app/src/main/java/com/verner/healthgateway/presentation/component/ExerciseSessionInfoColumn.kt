package com.verner.healthgateway.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.verner.healthgateway.presentation.theme.HealthConnectTheme
import java.time.ZonedDateTime
import java.util.UUID

/**
 * Displays summary information about the [ExerciseSessionRecord]
 */

@Composable
fun ExerciseSessionInfoColumn(
    start: ZonedDateTime,
    end: ZonedDateTime,
    uid: String,
    name: String,
    onClick: (String) -> Unit = {},
) {
  Column(
    modifier = Modifier.clickable {
      onClick(uid)
    }
  ) {
    Text(
      color = MaterialTheme.colors.primary,
      text = "${start.toLocalDate()}",
      style = MaterialTheme.typography.caption
    )
    Text(
      color = MaterialTheme.colors.primary,
      text = "${start.toLocalTime()} - ${end.toLocalTime()}",
      style = MaterialTheme.typography.caption
    )
    Text(name)
    Text(uid)
  }
}

@Preview
@Composable
fun ExerciseSessionInfoColumnPreview() {
  HealthConnectTheme {
    ExerciseSessionInfoColumn(
      ZonedDateTime.now().minusMinutes(30),
      ZonedDateTime.now(),
      UUID.randomUUID().toString(),
      "Running"
    )
  }
}
