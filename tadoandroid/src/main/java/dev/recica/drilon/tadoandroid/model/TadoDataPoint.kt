package dev.recica.drilon.tadoandroid.model

import org.json.JSONObject

data class TadoDataPoint(
    val name: String,
    val datapoint: JSONObject?
) {
    var humidity: Humidity? = null
        private set
    var insideTemperature: InsideTemperature? = null
        private set

    init {
        if (name == KEY_SENSOR_DATA_POINT_NAME_INSIDE_TEMPERATURE) {
            val jsonPrecision = datapoint?.getJSONObject("precision")
            val precision = Precision(
                jsonPrecision?.getDouble("celsius"),
                jsonPrecision?.getDouble("fahrenheit")
            )
            insideTemperature = InsideTemperature(
                datapoint?.getDouble("celsius"),
                datapoint?.getDouble("fahrenheit"),
                datapoint?.getString("timestamp"),
                datapoint?.getString("type"),
                precision
            )
        } else if (name == KEY_SENSOR_DATA_POINT_NAME_HUMIDITY) {
            humidity = Humidity(
                datapoint?.getString("type"),
                datapoint?.getDouble("percentage"),
                datapoint?.getString("timestamp")
            )
        }
    }

    override fun toString(): String {
        return "TadoActivityDataPoint [name=$name, datapoint=$datapoint]"
    }

    companion object {
        const val KEY_SENSOR_DATA_POINT_NAME_INSIDE_TEMPERATURE = "insideTemperature"
        const val KEY_SENSOR_DATA_POINT_NAME_HUMIDITY = "humidity"
    }
}
