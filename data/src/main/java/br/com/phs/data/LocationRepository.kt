package br.com.phs.data

import br.com.phs.domain.LocationModel

class LocationRepository(
    private val deviceLocationSource: DeviceLocationSource
) {

    fun getLocation(): LocationModel = deviceLocationSource.getDeviceLocation()

}

interface DeviceLocationSource {
    fun getDeviceLocation(): LocationModel
}