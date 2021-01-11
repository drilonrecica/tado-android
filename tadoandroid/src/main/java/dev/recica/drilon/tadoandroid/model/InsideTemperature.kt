package dev.recica.drilon.tadoandroid.model

data class InsideTemperature(
    val celsius: Double?,
    val fahrenheit: Double?,
    val timestamp: String?,
    val type: String?,
    val precision: Precision?
)