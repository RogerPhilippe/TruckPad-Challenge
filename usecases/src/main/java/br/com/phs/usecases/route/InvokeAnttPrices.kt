package br.com.phs.usecases.route

import br.com.phs.data.CalculateRouteRepository

class InvokeAnttPrices(private val repository: CalculateRouteRepository) {

    operator fun invoke(requestJsonStr: String): String = repository.getAnttPrices(requestJsonStr)
}