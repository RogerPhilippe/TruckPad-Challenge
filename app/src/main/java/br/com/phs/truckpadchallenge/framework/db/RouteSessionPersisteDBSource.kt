package br.com.phs.truckpadchallenge.framework.db

import br.com.phs.data.RouteSessionPersistenceSource
import br.com.phs.domain.RouteSessionDBModel
import br.com.phs.truckpadchallenge.framework.db.dao.RouteSessionDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class RouteSessionPersisteDBSource(private val dbHandler: DatabaseHandler): RouteSessionPersistenceSource {

    override fun getPersistedRouteSessions(): MutableList<RouteSessionDBModel> = runBlocking {

        return@runBlocking withContext(Dispatchers.IO) {
            RouteSessionDAO(dbHandler).findAll()
        }
    }

    override fun getPersistedRouteSessionById(id: Int): RouteSessionDBModel = runBlocking {
        return@runBlocking withContext(Dispatchers.IO) {
            RouteSessionDAO(dbHandler).findById(id)
        }
    }

    override fun getPersistedCurrentRoute(): RouteSessionDBModel = runBlocking {

        return@runBlocking withContext(Dispatchers.IO) {
            RouteSessionDAO(dbHandler).findCurrentRoute()
        }
    }

    override fun finishCurrentRoute(id: Int) = runBlocking {

        withContext(Dispatchers.IO) {
            RouteSessionDAO(dbHandler).finishCurrentRoute(id)
        }
    }

    override fun saveRouteSession(routeSession: RouteSessionDBModel) = runBlocking {
        withContext(Dispatchers.IO) {
            RouteSessionDAO(dbHandler).save(routeSession)
        }

    }

    override fun removePersistedRouteSession(id: Int) = runBlocking {
        withContext(Dispatchers.IO) {
            RouteSessionDAO(dbHandler).delete(id)
        }
    }

}