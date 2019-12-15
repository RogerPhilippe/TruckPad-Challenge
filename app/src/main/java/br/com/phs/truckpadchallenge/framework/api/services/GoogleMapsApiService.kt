package br.com.phs.truckpadchallenge.framework.api.services

import android.content.Context
import br.com.phs.domain.LocationModel
import br.com.phs.truckpadchallenge.R
import br.com.phs.truckpadchallenge.framework.api.HttpHelper
import br.com.phs.truckpadchallenge.framework.api.utils.URLs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

object GoogleMapsApiService {

    fun getLocationName(locationModel: LocationModel, context: Context): String = runBlocking {

        return@runBlocking withContext(Dispatchers.IO) {
            val url = "${URLs.getLocationName}${locationModel.lat},${locationModel.lng}" +
                    "${URLs.addressComponentsFilter}&key=" +
                    context.resources.getString(R.string.google_maps_key)
            HttpHelper.get(url)
        }
    }

    fun getCoordinatesFromLocationName(locality: String, context: Context): String = runBlocking {

        return@runBlocking  withContext(Dispatchers.IO) {
            val url = "${URLs.getCoordinatesFromLocationName}$locality|country:BR&key=" +
                    context.resources.getString(R.string.google_maps_key)
            HttpHelper.get(url)
        }

    }

}
