package br.com.phs.truckpadchallenge.framework.api.utils

import br.com.phs.domain.CitiesModel
import br.com.phs.truckpadchallenge.framework.api.model.google.GeoCordingApiModel
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.SimpleFormatter
import kotlin.collections.ArrayList

/**
 * Method to convert Google Maps API JSon to city name and state acronym.
 */
fun getLocationName(jsonStr: String): String {

    var locationName = ""

    jsonStr.apply {

        val geoCordingApiModel = Gson().fromJson(this, GeoCordingApiModel::class.java)

        geoCordingApiModel.results[0].addressComponents.forEach {
            (it["types"] as ArrayList<*>).forEach { types ->
                if (types == "administrative_area_level_2") {
                    locationName = it["long_name"].toString()
                }
                else if (types == "administrative_area_level_1" && locationName.isNotEmpty()) {
                    locationName = "$locationName - ${it["short_name"].toString()}"
                }
            }
        }

    }

    return locationName
}

fun getCities(jsonStr: String): MutableList<CitiesModel> {

    val cities = mutableListOf<CitiesModel>()

    jsonStr.apply {

        val citiesArray = Gson().fromJson(this, ArrayList::class.java)

        citiesArray.forEach {

            val city = CitiesModel()
            it as LinkedTreeMap<*, *>
            city.cityName = it["nome"].toString()
            val microregion = it["microrregiao"] as LinkedTreeMap<*, *>
            val mesoregion = microregion["mesorregiao"] as LinkedTreeMap<*, *>
            val uf = mesoregion["UF"] as LinkedTreeMap<*, *>
            city.stateName = uf["nome"].toString()
            city.stateAcronym = uf["sigla"].toString()
            cities.add(city)
        }

    }

    return cities
}

fun formatLocation(valor: Double) = String.format(Locale.US, "%.6f", valor).toDouble()

fun dateTimeHistoricFormatter(dateToFormat: Date): String {

    val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale("pt", "BR"))
    return simpleDateFormat.format(dateToFormat)

}
