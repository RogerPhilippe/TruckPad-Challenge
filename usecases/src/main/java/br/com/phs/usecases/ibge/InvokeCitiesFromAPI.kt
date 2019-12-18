package br.com.phs.usecases.ibge

import br.com.phs.data.CitiesRequestRepository

class InvokeCitiesFromAPI(private val repository: CitiesRequestRepository) {

    operator fun invoke(): String = repository.getCities()

}