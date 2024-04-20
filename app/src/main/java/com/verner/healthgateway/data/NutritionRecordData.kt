package com.verner.healthgateway.data

import androidx.health.connect.client.records.NutritionRecord
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Mass
import java.time.Instant

/**
 * Represents nutrition data for a given [NutritionRecord].
 */
data class NutritionRecordData(
    val uid: String,
    val title: String? = null,
    val notes: String? = null,
    val protein: Mass? = null,
    val dietaryFiber: Mass? = null,
    val sugar: Mass? = null,
    val totalCarbohydrate: Mass? = null,
    val totalFat: Mass? = null,
    val energy: Energy? = null,
    val time: Instant? = null,
    val mealType: Int? = null,
    val unsaturatedFat: Mass? = null,
    val saturatedFat: Mass? = null,
    val cholesterol: Mass? = null
)