package com.verner.healthgateway.presentation.screen.privacypolicy

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.verner.healthgateway.R
import com.verner.healthgateway.presentation.theme.HealthConnectTheme

/**
 * Shows the privacy policy.
 */
@Composable
fun PrivacyPolicyScreen() {
  Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(32.dp),
    verticalArrangement = Arrangement.Top,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Image(
      modifier = Modifier.fillMaxWidth(0.5f),
      painter = painterResource(id = R.drawable.ic_health_connect_logo),
      contentDescription = stringResource(id = R.string.health_connect_logo)
    )
    Spacer(modifier = Modifier.height(32.dp))
    Text(
      text = stringResource(id = R.string.privacy_policy),
      color = MaterialTheme.colors.onBackground
    )
    Spacer(modifier = Modifier.height(32.dp))
    Text(stringResource(R.string.privacy_policy_description))
  }
}

@Preview
@Composable
fun PrivacyPolicyScreenPreview() {
  HealthConnectTheme {
    PrivacyPolicyScreen()
  }
}
