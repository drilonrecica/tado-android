package dev.recica.drilon.tadoandroid.model

import java.util.*

data class WeatherState(
    val type: String,
    val value: String,
    val timestamp: Date
) {
    override fun toString(): String {
        return "WeatherState [type=$type, value=$value]"
    }
}