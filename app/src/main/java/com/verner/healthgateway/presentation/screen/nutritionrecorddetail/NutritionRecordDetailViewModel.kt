package com.verner.healthgateway.presentation.screen.nutritionrecorddetail

import android.os.RemoteException
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.verner.healthgateway.data.HealthConnectManager
import com.verner.healthgateway.data.NutritionRecordData
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID


class NutritionRecordDetailViewModel(
  private val uid: String,
  private val healthConnectManager: HealthConnectManager,
) : ViewModel() {
  val permissions = setOf(
    HealthPermission.getReadPermission(StepsRecord::class),
    HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
    HealthPermission.getReadPermission(HeartRateRecord::class)
  )

  var permissionsGranted = mutableStateOf(false)
    private set

  var uiState: UiState by mutableStateOf(UiState.Uninitialized)
    private set

  var recordData: MutableState<NutritionRecordData> = mutableStateOf(NutritionRecordData(uid))
    private set

  val permissionsLauncher = healthConnectManager.requestPermissionsActivityContract()

  fun initialLoad() {
    readAssociatedRecordData()
  }

  private fun readAssociatedRecordData() {
    viewModelScope.launch {
      tryWithPermissionsCheck {
        recordData.value = healthConnectManager.readAssociatedRecordData(uid)
      }
    }
  }

  /**
   * Provides permission check and error handling for Health Connect suspend function calls.
   *
   * Permissions are checked prior to execution of [block], and if all permissions aren't granted
   * the [block] won't be executed, and [permissionsGranted] will be set to false, which will
   * result in the UI showing the permissions button.
   *
   * Where an error is caught, of the type Health Connect is known to throw, [uiState] is set to
   * [UiState.Error], which results in the snackbar being used to show the error message.
   */
  private suspend fun tryWithPermissionsCheck(block: suspend () -> Unit) {
    permissionsGranted.value = healthConnectManager.hasAllPermissions(permissions)
    uiState = try {
      if (permissionsGranted.value) {
        block()
      }
      UiState.Done
    } catch (remoteException: RemoteException) {
      UiState.Error(remoteException)
    } catch (securityException: SecurityException) {
      UiState.Error(securityException)
    } catch (ioException: IOException) {
      UiState.Error(ioException)
    } catch (illegalStateException: IllegalStateException) {
      UiState.Error(illegalStateException)
    }
  }

  sealed class UiState {
    object Uninitialized : UiState()
    object Done : UiState()

    // A random UUID is used in each Error object to allow errors to be uniquely identified,
    // and recomposition won't result in multiple snackbars.
    data class Error(val exception: Throwable, val uuid: UUID = UUID.randomUUID()) : UiState()
  }
}

class NutritionRecordDetailViewModelFactory(
  private val uid: String,
  private val healthConnectManager: HealthConnectManager,
) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(NutritionRecordDetailViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return NutritionRecordDetailViewModel(
        uid = uid,
        healthConnectManager = healthConnectManager
      ) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
