package dev.recica.drilon.tadoandroid.model

data class Geolocation(val latitude: Double, val longitude: Double) {
    override fun toString(): String {
        return "TadoGeolocation [latitude=$latitude, longitude=$longitude]"
    }
}