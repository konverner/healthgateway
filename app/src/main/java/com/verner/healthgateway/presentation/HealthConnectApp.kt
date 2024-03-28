package com.verner.healthgateway.presentation

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.verner.healthgateway.R
import com.verner.healthgateway.data.HealthConnectAvailability
import com.verner.healthgateway.data.HealthConnectManager
import com.verner.healthgateway.presentation.navigation.Drawer
import com.verner.healthgateway.presentation.navigation.HealthConnectNavigation
import com.verner.healthgateway.presentation.navigation.Screen
import com.verner.healthgateway.presentation.theme.HealthConnectTheme
import kotlinx.coroutines.launch

const val TAG = "Health Gateway"

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HealthConnectApp(context: Context, healthConnectManager: HealthConnectManager) {
  HealthConnectTheme {
    val scaffoldState = rememberScaffoldState()
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val availability by healthConnectManager.availability

    Scaffold(
      scaffoldState = scaffoldState,
      topBar = {
        TopAppBar(
          title = {
            val titleId = when (currentRoute) {
              Screen.ExerciseSessions.route -> Screen.ExerciseSessions.titleId
              Screen.WeightRecords.route -> Screen.WeightRecords.titleId
              Screen.DifferentialChanges.route -> Screen.DifferentialChanges.titleId
              else -> R.string.app_name
            }
            Text(stringResource(titleId))
          },
          navigationIcon = {
            IconButton(
              onClick = {
                if (availability == HealthConnectAvailability.INSTALLED) {
                  scope.launch {
                    scaffoldState.drawerState.open()
                  }
                }
              }
            ) {
              Icon(
                imageVector = Icons.Rounded.Menu,
                stringResource(id = R.string.menu)
              )
            }
          }
        )
      },
      drawerContent = {
        if (availability == HealthConnectAvailability.INSTALLED) {
          Drawer(
            scope = scope,
            scaffoldState = scaffoldState,
            navController = navController
          )
        }
      },
      snackbarHost = {
        SnackbarHost(it) { data -> Snackbar(snackbarData = data) }
      }
    ) {
      HealthConnectNavigation(
        context = context,
        healthConnectManager = healthConnectManager,
        navController = navController,
        scaffoldState = scaffoldState
      )
    }
  }
}
