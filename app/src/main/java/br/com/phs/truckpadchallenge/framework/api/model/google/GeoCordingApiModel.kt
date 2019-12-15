package br.com.phs.truckpadchallenge.framework.api.model.google

import com.google.gson.annotations.SerializedName

data class GeoCordingApiModel(var results: MutableList<Results>)

data class Results(
    @SerializedName("address_components")var addressComponents: MutableList<HashMap<String, Any>>
)