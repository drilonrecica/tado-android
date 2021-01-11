package dev.recica.drilon.tadoandroid.model

import java.util.*

data class SolarIntensity(
    val type: String,
    val percentage: Double,
    val timestamp: Date
) {
    override fun toString(): String {
        return "SolarIntensity [type=$type, percentage=$percentage, timestamp=$timestamp]"
    }
}
