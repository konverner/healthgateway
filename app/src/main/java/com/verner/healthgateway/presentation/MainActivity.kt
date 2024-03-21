package com.verner.healthgateway.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

/**
 * The entry point into the sample.
 */
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val healthConnectManager = (application as BaseApplication).healthConnectManager

    setContent {
      HealthConnectApp(context=this, healthConnectManager = healthConnectManager)
    }
  }
}
