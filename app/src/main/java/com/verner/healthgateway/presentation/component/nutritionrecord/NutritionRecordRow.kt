package com.verner.healthgateway.presentation.component.nutritionrecord

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.records.NutritionRecord
import androidx.health.connect.client.units.Energy
import com.verner.healthgateway.presentation.theme.HealthConnectTheme
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

/**
 * Creates a row to represent an [NutritionRecord]
 */
@Composable
fun NutritionRecordRow(
  start: ZonedDateTime,
  uid: String,
  type: String,
  energy: Energy? = null,
  onDetailsClick: (String) -> Unit = {},
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 4.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    NutritionRecordInfoColumn(
      time = start.truncatedTo(ChronoUnit.SECONDS),
      uid = uid,
      type = type,
      energy = energy,
      onClick = onDetailsClick
    )
  }
}

@Preview
@Composable
fun NutritionRecordRowPreview() {
  HealthConnectTheme {
    NutritionRecordRow(
      ZonedDateTime.now(),
      UUID.randomUUID().toString(),
      "Dinner",
      Energy.calories(1000.0),
    )
  }
}
