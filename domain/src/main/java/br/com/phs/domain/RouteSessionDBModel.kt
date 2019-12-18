package br.com.phs.domain

data class RouteSessionDBModel(
    val id: Int = -1,
    val dateTime: Long = 0,
    val originLocationJson: String = "",
    val destinyLocationJson: String = "",
    val calculateRouteAnttCostJson: String = "",
    val currentRoute: Byte = 0,
    val status: Byte = 0
)