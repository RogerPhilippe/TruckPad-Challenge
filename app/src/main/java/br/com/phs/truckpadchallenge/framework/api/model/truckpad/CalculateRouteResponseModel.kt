package br.com.phs.truckpadchallenge.framework.api.model.truckpad

import com.google.gson.annotations.SerializedName

data class CalculateRouteResponseModel(
    var distance: Double,
    @SerializedName("distance_unit") var distanceUnit: String,
    var duration: Double,
    @SerializedName("duration_unit") var durationUnit: String,
    @SerializedName("has_tolls") var hasTolls: Boolean,
    @SerializedName("toll_count") var tollCount: Int,
    @SerializedName("toll_cost") var tollCost: Double,
    @SerializedName("toll_cost_unit") var tollCostUnit: String,
    var route: ArrayList<ArrayList<ArrayList<String>>>,
    @SerializedName("fuel_usage") var fuelUsage: Double,
    @SerializedName("fuel_usage_unit") var fuelUsageUnit: String,
    @SerializedName("fuel_cost") var fuelCost: Double,
    @SerializedName("fuel_cost_unit") var fuelCosyUnit: String,
    @SerializedName("total_cost") var totalCost: Double
)
