package br.com.phs.truckpadchallenge.framework.api.utils

class URLs {

    companion object {
        const val getLocationName = "https://maps.googleapis.com/maps/api/geocode/json?latlng="
        const val addressComponentsFilter = "&result_type=street_address|administrative_area_level_2"
        const val cities = "https://servicodados.ibge.gov.br/api/v1/localidades/municipios"
        const val getCoordinatesFromLocationName =
            "https://maps.googleapis.com/maps/api/geocode/json?components=locality:"
        const val truckPadRoute = "https://geo.api.truckpad.io/v1/route"
        const val truckPadPrices = "https://tictac.api.truckpad.io/v1/antt_price/all"
    }
}