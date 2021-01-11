package dev.recica.drilon.tadoandroid.model

data class TadoWeather(
    val solarIntensity: SolarIntensity,
    val outsideTemperature: OutsideTemperature,
    val weatherState: WeatherState
) {
    override fun toString(): String {
        return "TadoWeather [solarIntensity=$solarIntensity, outsideTemperature=$outsideTemperature, weatherState=$weatherState]"
    }
}
