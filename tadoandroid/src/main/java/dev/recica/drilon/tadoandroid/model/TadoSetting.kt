package dev.recica.drilon.tadoandroid.model

import dev.recica.drilon.tadoandroid.TadoException
import dev.recica.drilon.tadoandroid.internal.Constants
import org.json.JSONObject

class TadoSetting {
    var type: String
        private set
    var power: Boolean? = null
        private set
    var temperature: Temperature
        private set

    constructor(power: Boolean, temperature: Temperature) : super() {
        this.type = Constants.JSON_KEY_HEATING
        this.power = power
        this.temperature = temperature
    }

    @Throws(TadoException::class)
    constructor(power: Boolean, celsius: Double, fahrenheit: Double) : super() {
        type = Constants.JSON_KEY_HEATING
        this.power = power
        temperature = Temperature(celsius, fahrenheit)
    }

    constructor(type: String, power: Boolean, temperature: Temperature) : super() {
        this.type = type
        this.power = power
        this.temperature = temperature
    }

    @Throws(TadoException::class)
    constructor(type: String, power: Boolean, celsius: Double, fahrenheit: Double) : super() {
        this.type = type
        this.power = power
        temperature = Temperature(celsius, fahrenheit)
    }

    fun toJSONObject(): JSONObject {
        val root = JSONObject()
        root.put(Constants.JSON_KEY_TYPE, type)
        power?.let {
            root.put(
                Constants.JSON_KEY_POWER,
                if (it) Constants.JSON_KEY_ON else Constants.JSON_KEY_OFF
            )
        }
        root.put(Constants.JSON_KEY_TEMPERATURE, temperature.toJSONObject())
        return root
    }

    override fun toString(): String {
        return "TadoSetting [type=$type, power=${if (this.power != null && this.power!!) "ON" else "OFF"}, temperature=$temperature]"
    }
}
