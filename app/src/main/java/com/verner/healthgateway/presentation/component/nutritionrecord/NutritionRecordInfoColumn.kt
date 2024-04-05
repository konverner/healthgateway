package com.verner.healthgateway.presentation.component.nutritionrecord


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.health.connect.client.records.NutritionRecord
import androidx.health.connect.client.units.Energy
import com.verner.healthgateway.presentation.theme.HealthConnectTheme
import java.time.ZonedDateTime
import java.util.UUID

/**
 * Displays summary information about the [NutritionRecord]
 */

@Composable
fun NutritionRecordInfoColumn(
  time: ZonedDateTime,
  uid: String,
  type: String,
  energy: Energy? = null,
  onClick: (String) -> Unit = {},
) {
  Column(
    modifier = Modifier.clickable {
      onClick(uid)
    }
  ) {
    Text(type)
    Text(
      color = MaterialTheme.colors.primary,
      text = "${time.toLocalDateTime().truncatedTo(java.time.temporal.ChronoUnit.MINUTES)}",
      style = MaterialTheme.typography.caption
    )
    Text(energy?.inKilocalories.toString() + " kcal")
  }
}

@Preview
@Composable
fun NutritionRecordInfoColumnPreview() {
  HealthConnectTheme {
    NutritionRecordInfoColumn(
      ZonedDateTime.now(),
      UUID.randomUUID().toString(),
      "Dinner",
      Energy.kilocalories(1000.0)
    )
  }
}
