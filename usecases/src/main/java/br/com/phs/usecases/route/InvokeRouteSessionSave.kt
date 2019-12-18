package br.com.phs.usecases.route

import br.com.phs.data.RouteSessionRepository
import br.com.phs.domain.RouteSessionDBModel

class InvokeRouteSessionSave(private val routeSessionRepository: RouteSessionRepository) {

    operator fun invoke(routeSession: RouteSessionDBModel) = routeSessionRepository.saveRouteSession(routeSession)

}