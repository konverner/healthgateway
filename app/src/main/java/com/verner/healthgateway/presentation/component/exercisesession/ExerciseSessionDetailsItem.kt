package com.verner.healthgateway.presentation.component.exercisesession

import androidx.annotation.StringRes
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.verner.healthgateway.R
import com.verner.healthgateway.presentation.theme.HealthConnectTheme

/**
 * Displays a title and content, for use in conveying session details.
 */
fun LazyListScope.sessionDetailsItem(
  @StringRes labelId: Int,
  content: @Composable () -> Unit,
) {
  item {
    Text(
      text = stringResource(id = labelId),
      style = MaterialTheme.typography.h5,
      color = MaterialTheme.colors.primary
    )
    content()
  }
}

@Preview
@Composable
fun SessionDetailsItemPreview() {
  HealthConnectTheme {
    LazyColumn {
      sessionDetailsItem(R.string.total_steps) {
        Text(text = "12345")
      }
    }
  }
}
