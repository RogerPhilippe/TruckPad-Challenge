package br.com.phs.data

class CitiesRequestRepository(private val citiesRequestSource: CitiesRequestSource) {
    fun getCities(): String = citiesRequestSource.getCitiesFromAPI()
}

interface CitiesRequestSource{
    fun getCitiesFromAPI(): String
}