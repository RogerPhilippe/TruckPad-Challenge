package br.com.phs.truckpadchallenge.framework.api.services

import br.com.phs.truckpadchallenge.framework.api.HttpHelper
import br.com.phs.truckpadchallenge.framework.api.utils.URLs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

object TruckPadApiServicce {

    fun getCalculateRoute(requestJsonStr: String): String = runBlocking {

        return@runBlocking withContext(Dispatchers.IO) {
            HttpHelper.post(URLs.truckPadRoute, requestJsonStr)
        }
    }

    fun getAnttPrices(requestJsonStr: String): String = runBlocking {

        return@runBlocking withContext(Dispatchers.IO) {
            HttpHelper.post(URLs.truckPadPrices, requestJsonStr)
        }
    }
}