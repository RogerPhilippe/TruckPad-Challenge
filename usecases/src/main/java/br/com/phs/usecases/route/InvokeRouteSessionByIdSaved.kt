package br.com.phs.usecases.route

import br.com.phs.data.RouteSessionRepository

class InvokeRouteSessionByIdSaved(private val routeSessionRepository: RouteSessionRepository) {

    operator fun invoke(id: Int) = routeSessionRepository.getPersistedRouteSessionById(id)

}