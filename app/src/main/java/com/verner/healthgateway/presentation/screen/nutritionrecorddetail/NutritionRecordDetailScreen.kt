package com.verner.healthgateway.presentation.screen.nutritionrecorddetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Mass
import com.verner.healthgateway.R
import com.verner.healthgateway.data.NutritionRecordData
import com.verner.healthgateway.presentation.component.exercisesession.sessionDetailsItem
import com.verner.healthgateway.presentation.screen.nutritionrecord.mapMealType
import com.verner.healthgateway.presentation.theme.HealthConnectTheme
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.ZonedDateTime
import java.util.UUID
import kotlin.random.Random

/**
 * Shows a details of a given [NutritionRecord], including aggregates and underlying raw data.
 */
@Composable
fun NutritionRecordDetailScreen(
  permissions: Set<String>,
  permissionsGranted: Boolean,
  nutritionRecord: NutritionRecordData,
  uiState: NutritionRecordDetailViewModel.UiState,
  onError: (Throwable?) -> Unit = {},
  onPermissionsResult: () -> Unit = {},
  onPermissionsLaunch: (Set<String>) -> Unit = {},
) {

  // Remember the last error ID, such that it is possible to avoid re-launching the error
  // notification for the same error when the screen is recomposed, or configuration changes etc.
  val errorId = rememberSaveable { mutableStateOf(UUID.randomUUID()) }

  LaunchedEffect(uiState) {
    // If the initial data load has not taken place, attempt to load the data.
    if (uiState is NutritionRecordDetailViewModel.UiState.Uninitialized) {
      onPermissionsResult()
    }

    // The [NutritionRecordDetailViewModel.UiState] provides details of whether the last action
    // was a success or resulted in an error. Where an error occurred, for example in reading
    // and writing to Health Connect, the user is notified, and where the error is one that can
    // be recovered from, an attempt to do so is made.
    if (uiState is NutritionRecordDetailViewModel.UiState.Error &&
      errorId.value != uiState.uuid
    ) {
      onError(uiState.exception)
      errorId.value = uiState.uuid
    }
  }

  if (uiState != NutritionRecordDetailViewModel.UiState.Uninitialized) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      if (!permissionsGranted) {
        item {
          Button(
            onClick = { onPermissionsLaunch(permissions) }
          ) {
            Text(text = stringResource(R.string.permissions_button_label))
          }
        }
      } else {
        sessionDetailsItem(labelId = R.string.meal) {
          val mealTypeName = mapMealType(nutritionRecord.mealType)
          Text(mealTypeName)
        }
        sessionDetailsItem(labelId = R.string.total_energy) {
          val energyKcal = nutritionRecord.energy?.inKilocalories
          val roundedEnergy = if (energyKcal != null) {
            BigDecimal(energyKcal).setScale(1, RoundingMode.CEILING).toString()
          } else {
            "N/A" // Handle the case where inKilocalories is null
          }
          Text(roundedEnergy + " kcal")
        }
        sessionDetailsItem(labelId = R.string.carbs) {
          val carbs = nutritionRecord.totalCarbohydrate?.inGrams
          val roundedCarbs = if (carbs != null) {
            BigDecimal(carbs).setScale(1, RoundingMode.CEILING).toString()
          } else {
            "N/A" // Handle the case where totalCarbohydrate is null
          }
          Text(roundedCarbs + " grams")
        }

        sessionDetailsItem(labelId = R.string.protein) {
          val protein = nutritionRecord.protein?.inGrams
          val roundedProtein = if (protein != null) {
            BigDecimal(protein).setScale(1, RoundingMode.CEILING).toString()
          } else {
            "N/A" // Handle the case where protein is null
          }
          Text(roundedProtein + " grams")
        }

        sessionDetailsItem(labelId = R.string.fat) {
          val fat = nutritionRecord.totalFat?.inGrams
          val roundedFat = if (fat != null) {
            BigDecimal(fat).setScale(1, RoundingMode.CEILING).toString()
          } else {
            "N/A" // Handle the case where totalFat is null
          }
          Text(roundedFat + " grams")
        }

      }
    }
  }
}

@Preview
@Composable
fun NutritionRecordScreenPreview() {
  HealthConnectTheme {
    val uid = UUID.randomUUID().toString()
    val record = NutritionRecordData(
      uid = uid,
      mealType = 1,
      energy = Energy.kilocalories(Random.nextDouble(100.0, 1000.0)),
      totalCarbohydrate = Mass.grams(Random.nextDouble(100.0, 1000.0)),
      protein = Mass.grams(Random.nextDouble(100.0, 1000.0)),
      totalFat = Mass.grams(Random.nextDouble(100.0, 1000.0)),
      notes = "Some notes",
      time = ZonedDateTime.now().toInstant(),
      title = "Some title"
    )

    NutritionRecordDetailScreen(
      permissions = setOf(),
      permissionsGranted = true,
      nutritionRecord = record,
      uiState = NutritionRecordDetailViewModel.UiState.Done
    )
  }
}
