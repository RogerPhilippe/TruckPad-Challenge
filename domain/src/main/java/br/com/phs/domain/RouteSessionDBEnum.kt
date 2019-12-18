package br.com.phs.domain

enum class RouteSessionDBEnum(val value: String) {
    TABLE_NAME("tb_route_session"),
    ID("id"),
    DATE_TIME("date_time"),
    ORIGIN_LOCATION_JSON("origin_location_json"),
    DESTINY_LOCATION_JSON("destiny_location_json"),
    CALCULATE_ROUTE_JSON("calculate_route_json"),
    CURRENT_ROUTE("current_route"),
    STATUS("status")
}