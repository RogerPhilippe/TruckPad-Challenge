package br.com.phs.domain

data class LocationModel(
    var id: Long = 0,
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var status: Byte = 0
)