package br.com.phs.usecases.route

import br.com.phs.data.RouteSessionRepository
import br.com.phs.domain.RouteSessionDBModel

class InvokeRouteSessionsSaved(private val routeSessionRepository: RouteSessionRepository) {

    operator fun invoke(): MutableList<RouteSessionDBModel> = routeSessionRepository.getPersistedRouteSession()

}