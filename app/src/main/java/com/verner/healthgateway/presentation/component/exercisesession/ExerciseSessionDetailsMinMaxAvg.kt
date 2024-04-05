package com.verner.healthgateway.presentation.component.exercisesession

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.verner.healthgateway.R
import com.verner.healthgateway.presentation.theme.HealthConnectTheme

/**
 * Shows the statistical min, max and average values, as can be returned from Health Platform.
 */
@Composable
fun ExerciseSessionDetailsMinMaxAvg(
  minimum: String?,
  maximum: String?,
  average: String?,
) {
  Row(
    modifier = Modifier.fillMaxWidth()
  ) {
    Text(
      modifier = Modifier
        .weight(1f),
      text = stringResource(
        R.string.label_and_value,
        stringResource(R.string.min_label),
        minimum ?: "N/A"
      ),
      textAlign = TextAlign.Center
    )
    Text(
      modifier = Modifier
        .weight(1f),
      text = stringResource(
        R.string.label_and_value,
        stringResource(R.string.max_label),
        maximum ?: "N/A"
      ),
      textAlign = TextAlign.Center
    )
    Text(
      modifier = Modifier
        .weight(1f),
      text = stringResource(
        R.string.label_and_value,
        stringResource(R.string.avg_label),
        average ?: "N/A"
      ),
      textAlign = TextAlign.Center
    )
  }
}

@Preview
@Composable
fun ExerciseSessionDetailsMinMaxAvgPreview() {
  HealthConnectTheme {
    ExerciseSessionDetailsMinMaxAvg(minimum = "10", maximum = "100", average = "55")
  }
}
