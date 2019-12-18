package br.com.phs.data

class CalculateRouteRepository(private val calculateRouteSorce: CalculateRouteSource) {

    fun getCalculateRoute(requestJsonStr: String): String = calculateRouteSorce.getCalculateRoute(requestJsonStr)
    fun getAnttPrices(requestJsonStr: String): String = calculateRouteSorce.getAnttPrices(requestJsonStr)
}

interface CalculateRouteSource {
    fun getCalculateRoute(requestJsonStr: String): String
    fun getAnttPrices(requestJsonStr: String): String
}