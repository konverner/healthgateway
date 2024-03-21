package com.verner.healthgateway.presentation

import android.app.Application
import com.verner.healthgateway.data.HealthConnectManager

class BaseApplication : Application() {
  val healthConnectManager by lazy {
    HealthConnectManager(this)
  }
}
