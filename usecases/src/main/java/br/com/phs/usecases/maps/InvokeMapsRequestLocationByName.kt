package br.com.phs.usecases.maps

import br.com.phs.data.MapsApiRepository
import br.com.phs.domain.LocationModel

class InvokeMapsRequestLocationByName(private val repository: MapsApiRepository) {

    operator fun invoke(locationModel: LocationModel): String = repository.getLocationByName(locationModel)

}