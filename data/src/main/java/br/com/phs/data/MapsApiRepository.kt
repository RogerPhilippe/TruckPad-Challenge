package br.com.phs.data

import br.com.phs.domain.LocationModel

class MapsApiRepository(private val mapsApiSource: MapsApiSource) {

    fun getLocationByName(locationModel: LocationModel) =
        mapsApiSource.getLocationByName(locationModel)

    fun getCoordinatesFromLocationName(locality: String) =
        mapsApiSource.getCoordinatesFromLocationName(locality)
}

interface MapsApiSource {
    fun getLocationByName(locationModel: LocationModel): String
    fun getCoordinatesFromLocationName(locality: String): String
}