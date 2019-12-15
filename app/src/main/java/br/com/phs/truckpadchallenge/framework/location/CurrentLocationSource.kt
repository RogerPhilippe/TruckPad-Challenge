package br.com.phs.truckpadchallenge.framework.location

import android.content.Context
import br.com.phs.data.DeviceLocationSource
import br.com.phs.domain.LocationModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class CurrentLocationSource(private val context: Context): DeviceLocationSource {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun getDeviceLocation(): LocationModel = runBlocking {

        // Local Variables
        val locationModel = LocationModel(-1, 0.0, 0.0, 0)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        withContext(Dispatchers.IO) {

            var retry = 3
            do {
                retry--
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    with(location) {
                        if (this == null) { return@with }
                        retry = 0
                        locationModel.lat = latitude
                        locationModel.lng = longitude
                    }
                }
                Thread.sleep(100)
            } while (retry > 0)

        }

        return@runBlocking locationModel

    }
}