package br.com.phs.truckpadchallenge.framework.session

import br.com.phs.domain.LocationModel
import br.com.phs.domain.RouteSessionDBModel
import br.com.phs.truckpadchallenge.framework.api.model.truckpad.CalculateRouteResultModel
import com.google.gson.Gson

object RouteSession {

    var routeSessionModel: RouteSessionModel? = null

}

data class RouteSessionModel(
    var hasCurrentRoute: Boolean = false,
    val idCurrentRoute: Int = -1,
    val originLocation: LocationModel,
    val destinyLocation: LocationModel,
    val calculateRouteAnttCost: CalculateRouteResultModel
)

fun routeSessionAux(routeSessionDBModel: RouteSessionDBModel) {

    if (routeSessionDBModel.id > -1) {
        val originLocation = Gson().fromJson(
            routeSessionDBModel.originLocationJson, LocationModel::class.java
        )
        val destinyLocation = Gson().fromJson(
            routeSessionDBModel.destinyLocationJson, LocationModel::class.java
        )
        val calculateRouteResultModel = Gson().fromJson(
            routeSessionDBModel.calculateRouteAnttCostJson,
            CalculateRouteResultModel::class.java
        )
        val hasCurrentRoute = routeSessionDBModel.currentRoute == 1.toByte()

        val routeSession = RouteSession
        val routeSessionModel = RouteSessionModel(
            hasCurrentRoute = hasCurrentRoute, idCurrentRoute = routeSessionDBModel.id,
            originLocation = originLocation, destinyLocation = destinyLocation,
            calculateRouteAnttCost = calculateRouteResultModel
        )
        routeSession.routeSessionModel = routeSessionModel
    }
}

fun haveRouteSession() = RouteSession.routeSessionModel?.hasCurrentRoute != null &&
        RouteSession.routeSessionModel!!.hasCurrentRoute
