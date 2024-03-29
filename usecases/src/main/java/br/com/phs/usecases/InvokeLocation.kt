package br.com.phs.usecases

import br.com.phs.data.LocationRepository
import br.com.phs.domain.LocationModel

class InvokeLocation(private val locationRepository: LocationRepository) {
    operator fun invoke(): LocationModel = locationRepository.getLocation()
}