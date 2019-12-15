package br.com.phs.truckpadchallenge.framework.api.services

import br.com.phs.truckpadchallenge.framework.api.HttpHelper
import br.com.phs.truckpadchallenge.framework.api.utils.URLs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

object IBGEApiService {

    fun getCities(): String = runBlocking {

        return@runBlocking withContext(Dispatchers.IO) {

            HttpHelper.get(URLs.cities)
        }
    }
}