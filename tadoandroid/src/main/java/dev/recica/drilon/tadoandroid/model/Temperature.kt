package dev.recica.drilon.tadoandroid.model

import dev.recica.drilon.tadoandroid.TadoException
import dev.recica.drilon.tadoandroid.internal.Constants
import org.json.JSONObject

class Temperature {
    var celsius: Double = 0.0
        private set
    var fahrenheit: Double = 0.0
        private set

    constructor(celsius: Double?, fahrenheit: Double?) : super() {
        if (celsius == null && fahrenheit == null) throw TadoException(
            "error",
            "Please specify at least celsius or fahrenheit temperature."
        )
        if (celsius != null) this.celsius = celsius
        if (fahrenheit != null) this.fahrenheit = fahrenheit
    }

    constructor() : super()

    fun toJSONObject(): JSONObject {
        val root = JSONObject()
        root.put(Constants.JSON_KEY_CELSIUS, celsius)
        root.put(Constants.JSON_KEY_FAHRENHEIT, fahrenheit)
        return root
    }

    override fun toString(): String {
        return "TadoTemperature [celsius=$celsius, fahrenheit=$fahrenheit]"
    }
}