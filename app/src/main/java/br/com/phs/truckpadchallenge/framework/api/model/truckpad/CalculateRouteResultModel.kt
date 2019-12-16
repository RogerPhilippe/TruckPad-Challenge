package br.com.phs.truckpadchallenge.framework.api.model.truckpad

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CalculateRouteResultModel(
    val calculateRouteModel: CalculateRouteModel,
    val calculatePrices: CalculatePrices
): Parcelable

@Parcelize
data class CalculateRouteModel(
    val origin: String = "",
    val destiny: String = "",
    val axis: Int = 0,
    val distance: Double = 0.0,
    val duration: Double = 0.0,
    val hasToll: Boolean = false,
    val tollPrice: Double = 0.0,
    val tollTotal: Double = 0.0,
    val necessaryFuel: Double = 0.0,
    val fuelTotal: Double = 0.0,
    val totalCost: Double = 0.0
): Parcelable

@Parcelize
data class CalculatePrices(
    val geral: Double = 0.0,
    val granel: Double = 0.0,
    val neoGranel: Double = 0.0,
    val frigorificada: Double = 0.0,
    val perigosa: Double = 0.0
): Parcelable
