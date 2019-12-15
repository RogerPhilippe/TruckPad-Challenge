package br.com.phs.truckpadchallenge.framework.api.model.google

import com.google.gson.annotations.SerializedName

data class GeoCordingLocalityModel(
    @SerializedName("results") var results: MutableList<ResultsLocality>)

data class ResultsLocality(var geometry: Geometry)

data class Geometry(val location: Location)

data class Location(val lat: Double, val lng: Double)