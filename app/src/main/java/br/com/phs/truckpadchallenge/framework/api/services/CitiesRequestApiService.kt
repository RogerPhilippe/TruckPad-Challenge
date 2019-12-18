package br.com.phs.truckpadchallenge.framework.api.services

import br.com.phs.data.CitiesRequestSource
import br.com.phs.truckpadchallenge.framework.api.HttpHelper
import br.com.phs.truckpadchallenge.framework.api.utils.URLs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class CitiesRequestApiService: CitiesRequestSource {

    override fun getCitiesFromAPI(): String = runBlocking {

        return@runBlocking withContext(Dispatchers.IO) {

            HttpHelper.get(URLs.cities)
        }
    }
}