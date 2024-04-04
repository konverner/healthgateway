package com.verner.healthgateway.presentation.component.utils

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.verner.healthgateway.R
import com.verner.healthgateway.data.MIN_SUPPORTED_SDK
import com.verner.healthgateway.presentation.theme.HealthConnectTheme

/**
 * Welcome text shown when the app first starts, where the device is not running a sufficient
 * Android version for Health Connect to be used.
 */
@Composable
fun NotSupportedMessage() {
  val tag = stringResource(R.string.not_supported_tag)
  val url = stringResource(R.string.not_supported_url)
  val handler = LocalUriHandler.current

  val notSupportedText = stringResource(
    id = R.string.not_supported_description,
    MIN_SUPPORTED_SDK
  )
  val notSupportedLinkText = stringResource(R.string.not_supported_link_text)

  val unavailableText = buildAnnotatedString {
    withStyle(style = SpanStyle(color = MaterialTheme.colors.onBackground)) {
      append(notSupportedText)
      append("\n\n")
    }
    pushStringAnnotation(tag = tag, annotation = url)
    withStyle(style = SpanStyle(color = MaterialTheme.colors.primary)) {
      append(notSupportedLinkText)
    }
  }
  ClickableText(
    text = unavailableText,
    style = TextStyle(textAlign = TextAlign.Justify)
  ) { offset ->
    unavailableText.getStringAnnotations(tag = tag, start = offset, end = offset)
      .firstOrNull()?.let {
        handler.openUri(it.item)
      }
  }
}

@Preview
@Composable
fun NotSupportedMessagePreview() {
  HealthConnectTheme {
    NotSupportedMessage()
  }
}
