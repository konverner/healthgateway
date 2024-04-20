package com.verner.healthgateway.presentation.component.weightrecord

import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.units.Mass
import com.verner.healthgateway.presentation.theme.HealthConnectTheme
import java.time.ZonedDateTime

/**
 * Displays summary information about the [WeightRecord]
 */

@Composable
fun WeightRecordInfoColumn(
  time: ZonedDateTime,
  weight: Mass,
) {
  Column {
    // Assuming the weight is in kilograms. You can change the unit if necessary.
    val weightInKilograms = weight.inKilograms
    val formattedWeight = "%.1f kg".format(weightInKilograms)

    Text(text = formattedWeight)
    Text(
      color = MaterialTheme.colors.primary,
      text = "${time.toLocalDate()}",
      style = MaterialTheme.typography.caption
    )
  }
}

@Preview
@Composable
fun WeightRecordInfoColumnPreview() {
  HealthConnectTheme {
    WeightRecordInfoColumn(
      ZonedDateTime.now(),
      Mass.kilograms(51.055)
    )
  }
}
