package br.com.phs.truckpadchallenge.framework.api.model.truckpad

import com.google.gson.annotations.SerializedName

data class AnttPricesRequestModel(
    val axis: Int,
    val distance: Double,
    @SerializedName("has_return_shipment") val hasReturnShipment: Boolean
)


// {"axis":2,"distance":2976.087,"has_return_shipment":true}