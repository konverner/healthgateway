package com.verner.healthgateway.presentation.component

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.verner.healthgateway.R
import com.verner.healthgateway.presentation.theme.HealthConnectTheme

/**
 * Welcome text shown when the app first starts, where the Health Connect APK is already installed.
 */
@Composable
fun InstalledMessage() {
  Text(
    text = stringResource(id = R.string.installed_welcome_message),
    textAlign = TextAlign.Justify
  )
}

@Preview
@Composable
fun InstalledMessagePreview() {
  HealthConnectTheme {
    InstalledMessage()
  }
}
