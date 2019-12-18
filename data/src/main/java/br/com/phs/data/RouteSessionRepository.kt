package br.com.phs.data

import br.com.phs.domain.RouteSessionDBModel

class RouteSessionRepository(private val routeSessionPSource: RouteSessionPersistenceSource) {

    fun getPersistedRouteSession() = routeSessionPSource.getPersistedRouteSessions()

    fun getPersistedRouteSessionById(id: Int) = routeSessionPSource.getPersistedRouteSessionById(id)

    fun getPersistedCurrentRoute() = routeSessionPSource.getPersistedCurrentRoute()

    fun finishCurrentRoute(id: Int) = routeSessionPSource.finishCurrentRoute(id)

    fun saveRouteSession(routeSession: RouteSessionDBModel) {
        routeSessionPSource.saveRouteSession(routeSession)
    }

    fun removePersistedSession(id: Int) {
        routeSessionPSource.removePersistedRouteSession(id)
    }

}

interface RouteSessionPersistenceSource {
    fun getPersistedRouteSessions(): MutableList<RouteSessionDBModel>
    fun getPersistedRouteSessionById(id: Int): RouteSessionDBModel
    fun getPersistedCurrentRoute(): RouteSessionDBModel
    fun finishCurrentRoute(id: Int)
    fun saveRouteSession(routeSession: RouteSessionDBModel)
    fun removePersistedRouteSession(id: Int)
}