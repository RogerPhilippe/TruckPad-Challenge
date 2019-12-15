package br.com.phs.truckpadchallenge.framework.api.model.truckpad

import com.google.gson.annotations.SerializedName

data class CalculateRouteRequestModel(
    var places: ArrayList<Places>,
    @SerializedName("fuel_consumption") var fuelConsumption: Double,
    @SerializedName("fuel_price") var fuelPrice: Double
)

data class Places(var point: ArrayList<Double>)
