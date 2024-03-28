package com.verner.healthgateway.presentation.component

import androidx.health.connect.client.units.Mass
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
fun WeightRecordInfoColumn(
    time: ZonedDateTime,
    uid: String,
    weight: Mass,
    onClick: (String) -> Unit = {}
) {
  Column(
    modifier = Modifier.clickable {
      onClick(uid)
    }
  ) {
    Text(weight.toString())
    Text(
      color = MaterialTheme.colors.primary,
      text = "${time.toLocalDate()}",
      style = MaterialTheme.typography.caption
    )
    Text(uid)
  }
}

@Preview
@Composable
fun WeightRecordInfoColumnPreview() {
  HealthConnectTheme {
    WeightRecordInfoColumn(
      ZonedDateTime.now(),
      UUID.randomUUID().toString(),
      Mass.kilograms(51.055)
    )
  }
}
