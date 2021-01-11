package dev.recica.drilon.tadoandroid.model

import java.util.*

data class OutsideTemperature(
    val celsius: Double,
    val fahrenheit: Double,
    val timestamp: Date,
    val type: String,
    val celsiusPrecision: Double,
    val fahrenheitPrecision: Double
) {
    override fun toString(): String {
        return "OutsideTemperature [celsius=$celsius, fahrenheit=$fahrenheit, timestamp=$timestamp, type=$type, celsiusPrecision=$celsiusPrecision, fahrenheitPrecision=$fahrenheitPrecision]"
    }
}
