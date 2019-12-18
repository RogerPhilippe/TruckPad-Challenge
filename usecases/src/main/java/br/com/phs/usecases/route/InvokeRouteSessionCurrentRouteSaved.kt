package br.com.phs.usecases.route

import br.com.phs.data.RouteSessionRepository
import br.com.phs.domain.RouteSessionDBModel

class InvokeRouteSessionCurrentRouteSaved(private val routeSessionRepository: RouteSessionRepository) {

    operator fun invoke(): RouteSessionDBModel = routeSessionRepository.getPersistedCurrentRoute()

}