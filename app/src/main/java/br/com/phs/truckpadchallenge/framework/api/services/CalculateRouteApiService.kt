package br.com.phs.truckpadchallenge.framework.api.services

import br.com.phs.data.CalculateRouteSource
import br.com.phs.truckpadchallenge.framework.api.HttpHelper
import br.com.phs.truckpadchallenge.framework.api.utils.URLs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class CalculateRouteApiService: CalculateRouteSource {

    override fun getCalculateRoute(requestJsonStr: String): String = runBlocking {

        return@runBlocking withContext(Dispatchers.IO) {
            HttpHelper.post(URLs.truckPadRoute, requestJsonStr)
        }
    }

    override fun getAnttPrices(requestJsonStr: String): String = runBlocking {

        return@runBlocking withContext(Dispatchers.IO) {
            HttpHelper.post(URLs.truckPadPrices, requestJsonStr)
        }
    }
}