package br.com.phs.usecases.maps

import br.com.phs.data.MapsApiRepository

class InvokeMapsRequestCoordinatesByName(private val repository: MapsApiRepository) {
    operator fun invoke(locality: String): String = repository.getCoordinatesFromLocationName(locality)
}