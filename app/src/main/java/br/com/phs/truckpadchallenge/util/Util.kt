package br.com.phs.truckpadchallenge.util

import java.util.*

fun convertMinuteDoubleToHourString(minutes: Double): String {

    var result = ""
    if (minutes > 0) {
        val minutesToHours = minutes / 3600
        val hour = minutesToHours.toInt()
        val decimalMinute = ((minutesToHours - hour) * 60).toInt()
        val hourLabel = when {
            hour > 2 -> "horas"
            else -> "hora"
        }
        val minuteLabel = when {
            decimalMinute > 0 -> "minutos"
            else -> "minuto"
        }
        result = "$hour $hourLabel e $decimalMinute $minuteLabel"
    }
    return result
}

fun formatCurrency(valor: Double) = String.format(Locale.US, "%.2f", valor).toDouble()