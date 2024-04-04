package com.verner.healthgateway.presentation.component.utils

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.verner.healthgateway.R
import com.verner.healthgateway.presentation.theme.HealthConnectTheme


@Composable
fun NotInstalledMessage() {
  val tag = stringResource(R.string.not_installed_tag)
  // Build the URL to allow the user to install the Health Connect package
  val url = Uri.parse(stringResource(id = R.string.market_url))
    .buildUpon()
    .appendQueryParameter("id", stringResource(id = R.string.health_connect_package))
    // Additional parameter to execute the onboarding flow.
    .appendQueryParameter("url", stringResource(id = R.string.onboarding_url))
    .build()
  val context = LocalContext.current

  val notInstalledText = stringResource(id = R.string.not_installed_description)
  val notInstalledLinkText = stringResource(R.string.not_installed_link_text)

  val unavailableText = buildAnnotatedString {
    withStyle(style = SpanStyle(color = MaterialTheme.colors.onBackground)) {
      append(notInstalledText)
      append("\n\n")
    }
    pushStringAnnotation(tag = tag, annotation = url.toString())
    withStyle(style = SpanStyle(color = MaterialTheme.colors.primary)) {
      append(notInstalledLinkText)
    }
  }
  ClickableText(
    text = unavailableText,
    style = TextStyle(textAlign = TextAlign.Justify)
  ) { offset ->
    unavailableText.getStringAnnotations(tag = tag, start = offset, end = offset)
      .firstOrNull()?.let {
        context.startActivity(
          Intent(Intent.ACTION_VIEW, Uri.parse(it.item))
        )
      }
  }
}

@Preview
@Composable
fun NotInstalledMessagePreview() {
  HealthConnectTheme {
    NotInstalledMessage()
  }
}
