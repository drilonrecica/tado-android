package dev.recica.drilon.tadoandroid.internal

import dev.recica.drilon.tadoandroid.TadoException
import dev.recica.drilon.tadoandroid.internal.RequestUtils.doDeleteRequest
import dev.recica.drilon.tadoandroid.internal.RequestUtils.doPutRequest
import dev.recica.drilon.tadoandroid.model.*
import okhttp3.OkHttpClient
import okio.IOException
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

internal class TadoHomeService(
    private val okHttpClient: OkHttpClient,
    private var clientSecret: String? = null,
    username: String,
    password: String,
    private val printDebugResponses: Boolean
) {

    internal var initialized = false
    private lateinit var bearer: String
    private lateinit var refreshToken: String

    init {
        if (clientSecret == null)
            clientSecret = getClientSecretFromTado()
        getTokens(username, password)
    }

    // GET requests

    private fun getClientSecretFromTado(): String? {
        return try {
            var jsonResponse: String? =
                RequestUtils.doGetRequest(
                    okHttpClient,
                    Constants.URL_TADO_CLIENT_SECRET_ENV,
                    null
                )
            RequestUtils.debugMessage(
                printDebugResponses,
                "getClientSecret response: $jsonResponse"
            )
            jsonResponse = jsonResponse?.substring(9)?.trim { it <= ' ' }
            jsonResponse = jsonResponse?.substring(0, jsonResponse.length - 1)?.trim { it <= ' ' }
            jsonResponse?.let {
                val json = JSONObject(it)
                json.getJSONObject(Constants.JSON_KEY_CONFIG)
                    .getJSONObject(Constants.JSON_KEY_OAUTH)
                    .optString(Constants.JSON_KEY_CLIENT_SECRET_JAVASCRIPT_OBJECT)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    @Throws(TadoException::class)
    private fun getTokens(username: String, password: String) {
        val body: MutableMap<String, String> = HashMap()
        body[Constants.JSON_KEY_CLIENT_ID] = Constants.JSON_KEY_CLIENT_ID_VALUE
        body[Constants.JSON_KEY_GRANT_TYPE] = Constants.JSON_KEY_PASSWORD
        body[Constants.JSON_KEY_SCOPE] = Constants.JSON_KEY_SCOPE_VALUE
        body[Constants.JSON_KEY_USERNAME] = username
        body[Constants.JSON_KEY_PASSWORD] = password
        body[Constants.JSON_KEY_CLIENT_SECRET] =
            if (clientSecret != null) clientSecret!! else Constants.FALLBACK_TADO_CLIENT_SECRET
        try {
            val response: String? =
                RequestUtils.doPostRequest(
                    okHttpClient,
                    Constants.URL_TADO_TOKEN,
                    body,
                    null
                )
            RequestUtils.debugMessage(
                printDebugResponses,
                "getBearerTokens response: $response"
            )
            response?.let {
                val json = JSONObject(it)
                RequestUtils.checkException(json)
                bearer = json.optString(Constants.JSON_KEY_ACCESS_TOKEN)
                refreshToken = json.optString(Constants.JSON_KEY_REFRESH_TOKEN)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(TadoException::class)
    fun refresh() {
        if (initialized) {
            refreshTokens()
        }
    }

    @Throws(TadoException::class)
    private fun refreshTokens() {
        val body: MutableMap<String, String> = HashMap()
        body[Constants.JSON_KEY_CLIENT_ID] = Constants.JSON_KEY_CLIENT_ID_VALUE
        body[Constants.JSON_KEY_GRANT_TYPE] = Constants.JSON_KEY_REFRESH_TOKEN
        body[Constants.JSON_KEY_SCOPE] = Constants.JSON_KEY_SCOPE_VALUE
        body[Constants.JSON_KEY_REFRESH_TOKEN] = refreshToken
        body[Constants.JSON_KEY_CLIENT_SECRET] =
            if (clientSecret != null) clientSecret!! else Constants.FALLBACK_TADO_CLIENT_SECRET
        try {
            val response =
                RequestUtils.doPostRequest(
                    okHttpClient,
                    Constants.URL_TADO_TOKEN,
                    body,
                    null
                )
            RequestUtils.debugMessage(
                printDebugResponses,
                "getBearerTokens response: $response"
            )
            response?.let {
                val json = JSONObject(it)
                RequestUtils.checkException(json)
                bearer = json.optString(Constants.JSON_KEY_ACCESS_TOKEN)
                refreshToken = json.optString(Constants.JSON_KEY_REFRESH_TOKEN)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(TadoException::class)
    fun getHomeIDs(
        attempt: Int
    ): List<Int?> {
        var toReturn: MutableList<Int?> = ArrayList()
        try {
            val headers: MutableMap<String, String> = HashMap()
            headers[Constants.JSON_KEY_AUTHORIZATION] = "${Constants.JSON_KEY_BEARER} $bearer"
            val jsonResponse =
                RequestUtils.doGetRequest(
                    okHttpClient,
                    Constants.URL_TADO_HOME_INFO,
                    headers
                )
            RequestUtils.debugMessage(
                printDebugResponses,
                "getHomesIDs response: $jsonResponse"
            )
            jsonResponse?.let {
                val json = JSONObject(it)
                RequestUtils.checkException(json)
                val jsonArray = json.getJSONArray(Constants.JSON_KEY_HOMES)
                for (i in 0 until jsonArray.length()) {
                    val currentItem = jsonArray.get(i)
                    if (currentItem is JSONObject) {
                        toReturn.add(currentItem.getInt(Constants.JSON_KEY_ID))
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: TadoException) {
            if (attempt > 1) {
                throw e
            } else {
                refresh()
                toReturn = getHomeIDs(attempt + 1).toMutableList()
            }
        }
        return toReturn
    }

    @Throws(TadoException::class)
    fun getHomes(
        attempt: Int
    ): MutableList<TadoHome?> {
        var toReturn: MutableList<TadoHome?> = ArrayList()
        try {
            val headers: MutableMap<String, String> = HashMap()
            headers[Constants.JSON_KEY_AUTHORIZATION] = "${Constants.JSON_KEY_BEARER} $bearer"
            val jsonResponse =
                RequestUtils.doGetRequest(
                    okHttpClient,
                    Constants.URL_TADO_HOME_INFO,
                    headers
                )
            RequestUtils.debugMessage(
                printDebugResponses,
                "getHomes response: $jsonResponse"
            )
            jsonResponse?.let {
                val json = JSONObject(it)
                RequestUtils.checkException(json)
                val jsonArray = json.getJSONArray(Constants.JSON_KEY_HOMES)
                for (i in 0 until jsonArray.length()) {
                    val currentItem = jsonArray.get(i)
                    if (currentItem is JSONObject) {
                        toReturn.add(
                            getHome(
                                currentItem.getInt(Constants.JSON_KEY_ID),
                                0
                            )
                        )
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: TadoException) {
            toReturn = if (attempt > 1) {
                throw e
            } else {
                refresh()
                getHomes(attempt + 1)
            }
        }
        return toReturn
    }

    @Throws(TadoException::class)
    fun getHome(
        id: Int,
        attempt: Int
    ): TadoHome? {
        var toReturn: TadoHome? = null
        try {
            val headers: MutableMap<String, String> = HashMap()
            headers[Constants.JSON_KEY_AUTHORIZATION] = "${Constants.JSON_KEY_BEARER} $bearer"
            val jsonResponse =
                RequestUtils.doGetRequest(
                    okHttpClient,
                    "${Constants.URL_TADO_HOMES}$id",
                    headers
                )
            RequestUtils.debugMessage(
                true,
                "getHome response: $jsonResponse"
            )
            jsonResponse?.let {
                val json = JSONObject(it)
                RequestUtils.checkException(json)
                val dateCreated =
                    json.optString(Constants.JSON_KEY_DATE_CREATED).toFormattedUTCDate()
                val jsonContactDetails = json.getJSONObject(Constants.JSON_KEY_CONTACT_DETAILS)
                val contactDetails = ContactDetails(
                    jsonContactDetails.optString(Constants.JSON_KEY_NAME),
                    jsonContactDetails.optString(Constants.JSON_KEY_EMAIL),
                    jsonContactDetails.optString(Constants.JSON_KEY_PHONE)
                )
                val jsonAddress = json.getJSONObject(Constants.JSON_KEY_ADDRESS)
                val address = Address(
                    jsonAddress.optString(Constants.JSON_KEY_ADDRESS_LINE_1),
                    jsonAddress.optString(Constants.JSON_KEY_ADDRESS_LINE_2),
                    jsonAddress.optString(Constants.JSON_KEY_ZIP_CODE),
                    jsonAddress.optString(Constants.JSON_KEY_CITY),
                    jsonAddress.optString(Constants.JSON_KEY_STATE),
                    jsonAddress.optString(Constants.JSON_KEY_COUNTRY)
                )
                val jsonGeolocation = json.getJSONObject(Constants.JSON_KEY_GEOLOCATION)
                val geolocation = Geolocation(
                    jsonGeolocation.optDouble(Constants.JSON_KEY_LATITUDE),
                    jsonGeolocation.optDouble(Constants.JSON_KEY_LONGITUDE)
                )
                toReturn = TadoHome(
                    json.getInt(Constants.JSON_KEY_ID),
                    json.optString(Constants.JSON_KEY_NAME),
                    json.optString(Constants.JSON_KEY_DATE_TIME_ZONE),
                    dateCreated,
                    json.optString(Constants.JSON_KEY_TEMPERATURE_UNIT),
                    json.optBoolean(Constants.JSON_KEY_INSTALLATION_COMPLETED),
                    json.optBoolean(Constants.JSON_KEY_SIMPLE_SMART_SCHEDULE_ENABLED),
                    json.optDouble(Constants.JSON_KEY_AWAY_RADIUS_IN_METERS),
                    json.optBoolean(Constants.JSON_KEY_USE_PRE_SKILL_APPS),
                    json.optBoolean(Constants.JSON_KEY_CHRISTMAS_MODE_ENABLED),
                    contactDetails,
                    address,
                    geolocation,
                    json.optBoolean(Constants.JSON_KEY_CONSENT_GRANT_SKIPPABLE)
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: TadoException) {
            toReturn = if (attempt > 1) {
                throw e
            } else {
                refresh()
                getHome(id, attempt + 1)
            }
        }
        return toReturn
    }

    @Throws(TadoException::class)
    fun getZones(
        homeId: Int,
        attempt: Int
    ): MutableList<TadoZone?> {
        var toReturn: MutableList<TadoZone?> = ArrayList()
        try {
            val headers: MutableMap<String, String> = HashMap()
            headers[Constants.JSON_KEY_AUTHORIZATION] = "${Constants.JSON_KEY_BEARER} $bearer"
            val jsonResponse =
                RequestUtils.doGetRequest(
                    okHttpClient,
                    "${Constants.URL_TADO_HOMES}$homeId/zones",
                    headers
                )
            RequestUtils.debugMessage(
                printDebugResponses,
                "getZones response: $jsonResponse"
            )
            try {
                // IF IT CAN PARSE THE JSONOBJECT IT WILL PROBABLY BE AN EXCEPTION
                jsonResponse?.let {
                    val json = JSONObject(it)
                    RequestUtils.checkException(json)
                }
            } catch (e: JSONException) {
                // IF IT CANNOT PARSE THE JSONOBJECT IT WILL BE AN ARRAY OF ZONES, WHICH IS THE
                // EXPECTED RESULT
                val jsonArray = JSONArray(jsonResponse)
                for (i in 0 until jsonArray.length()) {
                    val currentObject = jsonArray.get(i)
                    if (currentObject is JSONObject) {
                        val dateCreated =
                            currentObject.optString(Constants.JSON_KEY_DATE_CREATED)
                                .toFormattedUTCDate()
                        val jsonDeviceTypes =
                            currentObject.getJSONArray(Constants.JSON_KEY_DEVICE_TYPES)
                        val deviceTypes: MutableList<String> = ArrayList()
                        for (j in 0 until jsonDeviceTypes.length()) {
                            val deviceType = jsonDeviceTypes[j]
                            if (deviceType is String) deviceTypes.add(deviceType)
                        }
                        val jsonDevices = currentObject.getJSONArray(Constants.JSON_KEY_DEVICES)
                        val devices: MutableList<TadoDevice> = ArrayList()
                        for (k in 0 until jsonDevices.length()) {
                            val device = jsonDevices[k] as JSONObject
                            val jsonConnectionState =
                                device.getJSONObject(Constants.JSON_KEY_CONNECTION_STATE)
                            val timestamp =
                                jsonConnectionState.optString(Constants.JSON_KEY_TIMESTAMP)
                                    .toFormattedUTCDate()
                            val connectionState = TadoConnectionState(
                                jsonConnectionState.getBoolean(Constants.JSON_KEY_VALUE), timestamp
                            )
                            val jsonCapabilities =
                                device.getJSONObject(Constants.JSON_KEY_CHARACTERISTICS)
                                    .getJSONArray(Constants.JSON_KEY_CAPABILITIES)
                            val capabilities: MutableList<String> = ArrayList()
                            for (l in 0 until jsonCapabilities.length()) {
                                val capability = jsonCapabilities[l]
                                if (capability is String) capabilities.add(capability)
                            }
                            val jsonDuties = device.getJSONArray(Constants.JSON_KEY_DUTIES)
                            val duties: MutableList<String> = ArrayList()
                            for (m in 0 until jsonDuties.length()) {
                                val duty = jsonDuties[m]
                                if (duty is String) duties.add(duty)
                            }
                            val toAdd = TadoDevice(
                                device.optString(Constants.JSON_KEY_DEVICE_TYPE),
                                device.optString(Constants.JSON_KEY_SERIAL_NO),
                                device.optString(Constants.JSON_KEY_SHORT_SERIAL_NO),
                                device.optString(Constants.JSON_KEY_CURRENT_FW_VERSION),
                                connectionState,
                                capabilities,
                                device.optBoolean(Constants.JSON_KEY_IN_PAIRING_MODE),
                                device.optString(Constants.JSON_KEY_BATTERY_STATE),
                                duties
                            )
                            devices.add(toAdd)
                        }
                        val jsonDazzleMode =
                            currentObject.getJSONObject(Constants.JSON_KEY_DAZZLE_MODE)
                        val dazzleMode = TadoDazzleMode(
                            jsonDazzleMode.getBoolean(Constants.JSON_KEY_SUPPORTED),
                            jsonDazzleMode.getBoolean(Constants.JSON_KEY_ENABLED)
                        )
                        val jsonOpenWindowDetection =
                            currentObject.getJSONObject(Constants.JSON_KEY_OPEN_WINDOW_DETECTION)
                        val openWindowDetection = OpenWindowDetection(
                            jsonOpenWindowDetection.getBoolean(Constants.JSON_KEY_SUPPORTED),
                            jsonOpenWindowDetection.getBoolean(Constants.JSON_KEY_ENABLED),
                            jsonOpenWindowDetection.getInt(Constants.JSON_KEY_TIME_OUT_IN_SECONDS)
                        )
                        val zone = TadoZone(
                            homeId,
                            currentObject.getInt(Constants.JSON_KEY_ID),
                            currentObject.optString(Constants.JSON_KEY_NAME),
                            currentObject.optString(Constants.JSON_KEY_TYPE),
                            dateCreated,
                            deviceTypes,
                            devices,
                            currentObject.getBoolean(Constants.JSON_KEY_REPORT_AVAILABLE),
                            currentObject.getBoolean(Constants.JSON_KEY_SUPPORTS_DAZZLE),
                            currentObject.getBoolean(Constants.JSON_KEY_DAZZLE_ENABLED),
                            dazzleMode,
                            openWindowDetection
                        )
                        toReturn.add(zone)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: TadoException) {
            toReturn = if (attempt > 1) {
                throw e
            } else {
                refresh()
                getZones(homeId, attempt + 1)
            }
        }
        return toReturn
    }

    @Throws(TadoException::class)
    fun getHomeState(
        homeId: Int,
        attempt: Int
    ): TadoState? {
        var toReturn: TadoState? = null
        try {
            val headers: MutableMap<String, String> = HashMap()
            headers[Constants.JSON_KEY_AUTHORIZATION] = "${Constants.JSON_KEY_BEARER} $bearer"
            val jsonResponse =
                RequestUtils.doGetRequest(
                    okHttpClient,
                    "${Constants.URL_TADO_HOMES}$homeId/state",
                    headers
                )
            RequestUtils.debugMessage(
                printDebugResponses,
                "getHomeState response: $jsonResponse"
            )
            jsonResponse?.let {
                val json = JSONObject(jsonResponse)
                RequestUtils.checkException(json)
                toReturn = TadoState(json.optString(Constants.JSON_KEY_PRESENCE))
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: TadoException) {
            toReturn = if (attempt > 1) {
                throw e
            } else {
                refresh()
                getHomeState(homeId, attempt + 1)
            }
        }
        return toReturn
    }

    @Throws(TadoException::class)
    fun getZoneState(
        homeId: Int,
        idZone: Int,
        attempt: Int
    ): TadoZoneState? {
        var toReturn: TadoZoneState? = null
        try {
            val headers: MutableMap<String, String> = HashMap()
            headers[Constants.JSON_KEY_AUTHORIZATION] = "${Constants.JSON_KEY_BEARER} $bearer"
            val jsonResponse = RequestUtils.doGetRequest(
                okHttpClient,
                "${Constants.URL_TADO_HOMES}$homeId/zones/$idZone/state",
                headers
            )
            RequestUtils.debugMessage(
                printDebugResponses,
                "getZoneState response: $jsonResponse"
            )
            jsonResponse?.let {
                val json = JSONObject(it)
                RequestUtils.checkException(json)
                val geolocationOverrideDisableTime: Date?
                geolocationOverrideDisableTime =
                    if (json.optString(Constants.JSON_KEY_GEOLOCATION_OVERRIDE_DISABLE_TIME)
                            .isNullOrEmpty()
                    ) null else json.optString(Constants.JSON_KEY_GEOLOCATION_OVERRIDE_DISABLE_TIME)
                        .toFormattedUTCDate()
                val jsonSetting = json.getJSONObject(Constants.JSON_KEY_SETTING)
                val jsonTemperature = jsonSetting.getJSONObject(Constants.JSON_KEY_TEMPERATURE)
                val temperature = Temperature(
                    jsonTemperature.optDouble(Constants.JSON_KEY_CELSIUS),
                    jsonTemperature.optDouble(Constants.JSON_KEY_FAHRENHEIT)
                )
                val setting = TadoSetting(
                    jsonSetting.optString(Constants.JSON_KEY_TYPE),
                    jsonSetting.optString(Constants.JSON_KEY_POWER) == Constants.JSON_KEY_ON,
                    temperature
                )
                val jsonScheduleChange = json.optJSONObject(Constants.JSON_KEY_NEXT_SCHEDULE_CHANGE)
                var nextScheduleChange: TadoScheduleChange? = null
                if (jsonScheduleChange != null) {
                    val start: Date =
                        jsonScheduleChange.optString(Constants.JSON_KEY_START).toFormattedUTCDate()
                    val jsonSetting2 = jsonScheduleChange.getJSONObject(Constants.JSON_KEY_SETTING)
                    val jsonTemperature2 =
                        jsonSetting2.getJSONObject(Constants.JSON_KEY_TEMPERATURE)
                    val temperature2 = Temperature(
                        jsonTemperature2.optDouble(Constants.JSON_KEY_CELSIUS),
                        jsonTemperature2.optDouble(Constants.JSON_KEY_FAHRENHEIT)
                    )
                    val setting2 = TadoSetting(
                        jsonSetting2.optString(Constants.JSON_KEY_TYPE),
                        jsonSetting2.optString(Constants.JSON_KEY_POWER) == Constants.JSON_KEY_ON,
                        temperature2
                    )
                    nextScheduleChange = TadoScheduleChange(start, setting2)
                }
                val jsonActivityDataPoints =
                    json.optJSONObject(Constants.JSON_KEY_ACTIVITY_DATA_POINTS)
                val activityDataPoints: MutableList<TadoDataPoint> = ArrayList()
                if (jsonActivityDataPoints != null) {
                    val keys = jsonActivityDataPoints.keys()
                    while (keys.hasNext()) {
                        val name = keys.next()
                        var datapoint: JSONObject? = null
                        if (jsonActivityDataPoints[name] is JSONObject) {
                            datapoint = jsonActivityDataPoints.getJSONObject(name)
                        }
                        activityDataPoints.add(TadoDataPoint(name, datapoint))
                    }
                }
                val jsonSensorDataPoints = json.optJSONObject(Constants.JSON_KEY_SENSOR_DATA_POINTS)
                val sensorDataPoints: MutableList<TadoDataPoint> = ArrayList()
                if (jsonActivityDataPoints != null) {
                    val keys = jsonSensorDataPoints?.keys()
                    keys?.let {
                        while (keys.hasNext()) {
                            val name = keys.next()
                            var datapoint: JSONObject? = null
                            if (jsonSensorDataPoints[name] is JSONObject) {
                                datapoint = jsonSensorDataPoints.getJSONObject(name)
                            }
                            sensorDataPoints.add(TadoDataPoint(name, datapoint))
                        }
                    }
                }
                toReturn = TadoZoneState(
                    json.optString(Constants.JSON_KEY_TADO_MODE_FOR_ZONE),
                    json.getBoolean(Constants.JSON_KEY_GEOLOCATION_OVERRIDE),
                    geolocationOverrideDisableTime,
                    setting,
                    nextScheduleChange,
                    json.getJSONObject(Constants.JSON_KEY_LINK).getString(Constants.JSON_KEY_STATE),
                    activityDataPoints,
                    sensorDataPoints
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: TadoException) {
            toReturn = if (attempt > 1) {
                throw e
            } else {
                refresh()
                getZoneState(homeId, idZone, attempt + 1)
            }
        }
        return toReturn
    }

    @Throws(TadoException::class)
    fun getWeather(
        homeId: Int,
        attempt: Int
    ): TadoWeather? {
        var toReturn: TadoWeather? = null
        try {
            val headers: MutableMap<String, String> = HashMap()
            headers[Constants.JSON_KEY_AUTHORIZATION] = "${Constants.JSON_KEY_BEARER} $bearer"
            val jsonResponse =
                RequestUtils.doGetRequest(
                    okHttpClient,
                    "${Constants.URL_TADO_HOMES}$homeId/weather",
                    headers
                )
            RequestUtils.debugMessage(
                printDebugResponses,
                "getWeather response: $jsonResponse"
            )
            jsonResponse?.let {
                val json = JSONObject(it)
                RequestUtils.checkException(json)
                val jsonSolarIntensity = json.getJSONObject(Constants.JSON_KEY_SOLAR_INTENSITY)
                val solarIntensity = SolarIntensity(
                    jsonSolarIntensity.optString(Constants.JSON_KEY_TYPE),
                    jsonSolarIntensity.optDouble(Constants.JSON_KEY_PERCENTAGE),
                    jsonSolarIntensity.optString(Constants.JSON_KEY_TIMESTAMP).toFormattedUTCDate()
                )
                val jsonOutsideTemperature =
                    json.getJSONObject(Constants.JSON_KEY_OUTSIDE_TEMPERATURE)
                val outsideTemperature = OutsideTemperature(
                    jsonOutsideTemperature.optDouble(Constants.JSON_KEY_CELSIUS),
                    jsonOutsideTemperature.optDouble(Constants.JSON_KEY_FAHRENHEIT),
                    jsonOutsideTemperature.optString(Constants.JSON_KEY_TIMESTAMP)
                        .toFormattedUTCDate(),
                    jsonOutsideTemperature.optString(Constants.JSON_KEY_TYPE),
                    jsonOutsideTemperature.getJSONObject(Constants.JSON_KEY_PRECISION)
                        .optDouble(Constants.JSON_KEY_CELSIUS),
                    jsonOutsideTemperature.getJSONObject(Constants.JSON_KEY_PRECISION)
                        .optDouble(Constants.JSON_KEY_FAHRENHEIT)
                )
                val jsonWeatherState = json.getJSONObject(Constants.JSON_KEY_WEATHER_STATE)
                val weatherState = WeatherState(
                    jsonWeatherState.optString(Constants.JSON_KEY_TYPE),
                    jsonWeatherState.optString(Constants.JSON_KEY_VALUE),
                    jsonWeatherState.optString(Constants.JSON_KEY_TIMESTAMP).toFormattedUTCDate()
                )
                toReturn = TadoWeather(solarIntensity, outsideTemperature, weatherState)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: TadoException) {
            toReturn = if (attempt > 1) {
                throw e
            } else {
                refresh()
                getWeather(homeId, attempt + 1)
            }
        }
        return toReturn
    }

    @Throws(TadoException::class)
    fun getDevices(
        homeId: Int,
        attempt: Int
    ): MutableList<TadoDevice?> {
        var toReturn: MutableList<TadoDevice?> = java.util.ArrayList()
        try {
            val headers: MutableMap<String, String> = HashMap()
            headers[Constants.JSON_KEY_AUTHORIZATION] = "${Constants.JSON_KEY_BEARER} $bearer"
            val jsonResponse =
                RequestUtils.doGetRequest(
                    okHttpClient,
                    "${Constants.URL_TADO_HOMES}$homeId/devices",
                    headers
                )
            RequestUtils.debugMessage(
                printDebugResponses,
                "getDevices response: $jsonResponse"
            )
            try {
                // IF IT CAN PARSE THE JSONOBJECT IT WILL PROBABLY BE AN EXCEPTION
                jsonResponse?.let {
                    val json = JSONObject(it)
                    RequestUtils.checkException(json)
                }
            } catch (e: JSONException) {
                // IF IT CANNOT PARSE THE JSONOBJECT IT WILL BE AN ARRAY OF DEVICES, WHICH IS
                // THE EXPECTED RESULT
                jsonResponse?.let {
                    val jsonDevices = JSONArray(it)
                    for (i in 0 until jsonDevices.length()) {
                        val currentObject = jsonDevices[i]
                        val device = currentObject as JSONObject
                        val jsonConnectionState =
                            device.getJSONObject(Constants.JSON_KEY_CONNECTION_STATE)
                        val timestamp =
                            jsonConnectionState.optString(Constants.JSON_KEY_TIMESTAMP)
                                .toFormattedUTCDate()
                        val connectionState = TadoConnectionState(
                            jsonConnectionState.getBoolean(Constants.JSON_KEY_VALUE), timestamp
                        )
                        val jsonCapabilities =
                            device.getJSONObject(Constants.JSON_KEY_CHARACTERISTICS)
                                .getJSONArray(Constants.JSON_KEY_CAPABILITIES)
                        val capabilities: MutableList<String> = java.util.ArrayList()
                        for (j in 0 until jsonCapabilities.length()) {
                            val capability = jsonCapabilities[j]
                            if (capability is String) capabilities.add(capability)
                        }
                        val toAdd = TadoDevice(
                            device.optString(Constants.JSON_KEY_DEVICE_TYPE),
                            device.optString(Constants.JSON_KEY_SERIAL_NO),
                            device.optString(Constants.JSON_KEY_SHORT_SERIAL_NO),
                            device.optString(Constants.JSON_KEY_CURRENT_FW_VERSION),
                            connectionState,
                            capabilities,
                            device.optBoolean(Constants.JSON_KEY_IN_PAIRING_MODE),
                            device.optString(Constants.JSON_KEY_BATTERY_STATE),
                            java.util.ArrayList()
                        )
                        toReturn.add(toAdd)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: TadoException) {
            toReturn = if (attempt > 1) {
                throw e
            } else {
                refresh()
                getDevices(homeId, attempt + 1)
            }
        }
        return toReturn
    }

    @Throws(TadoException::class)
    fun getInstallations(
        homeId: Int,
        attempt: Int
    ): MutableList<TadoInstallation?> {
        var toReturn: MutableList<TadoInstallation?> = java.util.ArrayList()
        try {
            val headers: MutableMap<String, String> = HashMap()
            headers[Constants.JSON_KEY_AUTHORIZATION] = "${Constants.JSON_KEY_BEARER} $bearer"
            val jsonResponse = RequestUtils.doGetRequest(
                okHttpClient,
                "${Constants.URL_TADO_HOMES}$homeId/installations",
                headers
            )
            RequestUtils.debugMessage(
                printDebugResponses,
                "getInstallations response: $jsonResponse"
            )
            try {
                // IF IT CAN PARSE THE JSONOBJECT IT WILL PROBABLY BE AN EXCEPTION
                jsonResponse?.let {
                    val json = JSONObject(it)
                    RequestUtils.checkException(json)
                }
            } catch (e: JSONException) {
                // IF IT CANNOT PARSE THE JSONOBJECT IT WILL BE AN ARRAY OF INSTALLATIONS, WHICH
                // IS THE EXPECTED RESULT
                val jsonInstallations = JSONArray(jsonResponse)
                for (i in 0 until jsonInstallations.length()) {
                    val currentObject = jsonInstallations[i]
                    val installation = currentObject as JSONObject
                    val jsonDevices = installation.getJSONArray(Constants.JSON_KEY_DEVICES)
                    val devices: MutableList<TadoDevice> = java.util.ArrayList()
                    for (j in 0 until jsonDevices.length()) {
                        val jsonDevice = jsonDevices[j]
                        if (jsonDevice is JSONObject) {
                            val jsonConnectionState =
                                jsonDevice.getJSONObject(Constants.JSON_KEY_CONNECTION_STATE)
                            val timestamp =
                                jsonConnectionState.optString(Constants.JSON_KEY_TIMESTAMP)
                                    .toFormattedUTCDate()
                            val connectionState = TadoConnectionState(
                                jsonConnectionState.getBoolean(Constants.JSON_KEY_VALUE), timestamp
                            )
                            val jsonCapabilities =
                                jsonDevice.getJSONObject(Constants.JSON_KEY_CHARACTERISTICS)
                                    .getJSONArray(Constants.JSON_KEY_CAPABILITIES)
                            val capabilities: MutableList<String> = java.util.ArrayList()
                            for (k in 0 until jsonCapabilities.length()) {
                                val capability = jsonCapabilities[k]
                                if (capability is String) capabilities.add(capability)
                            }
                            val toAdd = TadoDevice(
                                jsonDevice.optString(Constants.JSON_KEY_DEVICE_TYPE),
                                jsonDevice.optString(Constants.JSON_KEY_SERIAL_NO),
                                jsonDevice.optString(Constants.JSON_KEY_SHORT_SERIAL_NO),
                                jsonDevice.optString(Constants.JSON_KEY_CURRENT_FW_VERSION),
                                connectionState,
                                capabilities,
                                jsonDevice.optBoolean(Constants.JSON_KEY_IN_PAIRING_MODE),
                                jsonDevice.optString(Constants.JSON_KEY_BATTERY_STATE),
                                java.util.ArrayList()
                            )
                            devices.add(toAdd)
                        }
                        val toAdd = TadoInstallation(
                            installation.getInt(Constants.JSON_KEY_ID),
                            installation.optString(Constants.JSON_KEY_TYPE),
                            installation.getInt(Constants.JSON_KEY_REVISION),
                            installation.getString(Constants.JSON_KEY_STATE),
                            devices
                        )
                        toReturn.add(toAdd)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: TadoException) {
            toReturn = if (attempt > 1) {
                throw e
            } else {
                refresh()
                getInstallations(homeId, attempt + 1)
            }
        }
        return toReturn
    }

    @Throws(TadoException::class)
    fun getUsers(
        homeId: Int,
        attempt: Int
    ): MutableList<User?> {
        var toReturn: MutableList<User?> =
            java.util.ArrayList()
        try {
            val headers: MutableMap<String, String> = HashMap()
            headers[Constants.JSON_KEY_AUTHORIZATION] = "${Constants.JSON_KEY_BEARER} $bearer"
            val jsonResponse =
                RequestUtils.doGetRequest(
                    okHttpClient,
                    "${Constants.URL_TADO_HOMES}$homeId/users",
                    headers
                )
            RequestUtils.debugMessage(
                printDebugResponses,
                "getUsers response: $jsonResponse"
            )
            try {
                // IF IT CAN PARSE THE JSONOBJECT IT WILL PROBABLY BE AN EXCEPTION
                jsonResponse?.let {
                    val json = JSONObject(it)
                    RequestUtils.checkException(json)
                }
            } catch (e: JSONException) {
                // IF IT CANNOT PARSE THE JSONOBJECT IT WILL BE AN ARRAY OF INSTALLATIONS, WHICH
                // IS THE EXPECTED RESULT
                val jsonUsers = JSONArray(jsonResponse)
                for (i in 0 until jsonUsers.length()) {
                    val currentObject = jsonUsers[i]
                    val user = currentObject as JSONObject
                    val jsonHomes = user.getJSONArray(Constants.JSON_KEY_HOMES)
                    val homes: MutableMap<Int, String> = HashMap()
                    for (j in 0 until jsonHomes.length()) {
                        val home = jsonHomes[j] as JSONObject
                        homes[home.getInt(Constants.JSON_KEY_ID)] =
                            home.optString(Constants.JSON_KEY_NAME)
                    }
                    val jsonDevices = user.getJSONArray(Constants.JSON_KEY_MOBILE_DEVICES)
                    val mobileDevices: MutableList<MobileDevice> = java.util.ArrayList()
                    for (k in 0 until jsonDevices.length()) {
                        val device = jsonDevices[k] as JSONObject
                        mobileDevices.add(parseMobileDevice(homeId, device))
                    }
                    val toAdd =
                        User(
                            user.optString(Constants.JSON_KEY_NAME),
                            user.optString(Constants.JSON_KEY_EMAIL),
                            user.optString(Constants.JSON_KEY_USERNAME),
                            homes,
                            user.optString(Constants.JSON_KEY_LOCALE),
                            mobileDevices
                        )
                    toReturn.add(toAdd)

                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: TadoException) {
            toReturn = if (attempt > 1) {
                throw e
            } else {
                refresh()
                getUsers(homeId, attempt + 1)
            }
        }
        return toReturn
    }

    @Throws(TadoException::class)
    fun getMobileDevices(
        homeId: Int,
        attempt: Int
    ): MutableList<MobileDevice?> {
        var toReturn: MutableList<MobileDevice?> = java.util.ArrayList()
        try {
            val headers: MutableMap<String, String> = HashMap()
            headers[Constants.JSON_KEY_AUTHORIZATION] = "${Constants.JSON_KEY_BEARER} $bearer"
            val jsonResponse = RequestUtils.doGetRequest(
                okHttpClient,
                "${Constants.URL_TADO_HOMES}$homeId/mobileDevices",
                headers
            )
            RequestUtils.debugMessage(
                printDebugResponses,
                "getMobileDevices response: $jsonResponse"
            )
            try {
                // IF IT CAN PARSE THE JSONOBJECT IT WILL PROBABLY BE AN EXCEPTION
                jsonResponse?.let {
                    val json = JSONObject(it)
                    RequestUtils.checkException(json)
                }
            } catch (e: JSONException) {
                // IF IT CANNOT PARSE THE JSONOBJECT IT WILL BE AN ARRAY OF INSTALLATIONS, WHICH
                // IS THE EXPECTED RESULT
                val jsonDevices = JSONArray(jsonResponse)
                for (i in 0 until jsonDevices.length()) {
                    val device = jsonDevices[i] as JSONObject
                    toReturn.add(parseMobileDevice(homeId, device))
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: TadoException) {
            toReturn = if (attempt > 1) {
                throw e
            } else {
                refresh()
                getMobileDevices(homeId, attempt + 1)
            }
        }
        return toReturn
    }

    @Throws(TadoException::class)
    fun getMobileDevice(
        deviceId: Int,
        homeId: Int,
        attempt: Int
    ): MobileDevice? {
        var toReturn: MobileDevice? = null
        try {
            val headers: MutableMap<String, String> = HashMap()
            headers[Constants.JSON_KEY_AUTHORIZATION] = "${Constants.JSON_KEY_BEARER} $bearer"
            val jsonResponse = RequestUtils.doGetRequest(
                okHttpClient,
                "${Constants.URL_TADO_HOMES}$homeId/mobileDevices/$deviceId",
                headers
            )
            RequestUtils.debugMessage(
                printDebugResponses,
                "getMobileDevice response: $jsonResponse"
            )
            jsonResponse?.let {
                val json = JSONObject(it)
                RequestUtils.checkException(json)
                toReturn = parseMobileDevice(homeId, json)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: TadoException) {
            toReturn = if (attempt > 1) {
                throw e
            } else {
                refresh()
                getMobileDevice(deviceId, homeId, attempt + 1)
            }
        }
        return toReturn
    }

    @Throws(TadoException::class)
    fun getMobileDeviceSettings(
        homeId: Int,
        deviceId: Int,
        attempt: Int
    ): MutableMap<String?, Any?>? {
        var toReturn: MutableMap<String?, Any?>? = HashMap()
        try {
            val headers: MutableMap<String, String> = HashMap()
            headers[Constants.JSON_KEY_AUTHORIZATION] = "${Constants.JSON_KEY_BEARER} $bearer"
            val jsonResponse = RequestUtils.doGetRequest(
                okHttpClient,
                "${Constants.URL_TADO_HOMES}$homeId/mobileDevices/$deviceId/settings",
                headers
            )
            RequestUtils.debugMessage(
                printDebugResponses,
                "getMobileDeviceSettings response: $jsonResponse"
            )
            jsonResponse?.let {
                val json = JSONObject(it)
                RequestUtils.checkException(json)
                toReturn?.clear()
                json.keys().forEach { key ->
                    toReturn?.put(key, json[key])
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: TadoException) {
            toReturn = if (attempt > 1) {
                throw e
            } else {
                refresh()
                getMobileDeviceSettings(
                    homeId,
                    deviceId,
                    attempt + 1
                )
            }
        }
        return toReturn
    }

    @Throws(TadoException::class)
    fun getZoneCapabilities(
        homeId: Int,
        zoneId: Int,
        attempt: Int
    ): Capability? {
        var toReturn: Capability? = null
        try {
            val headers: MutableMap<String, String> = HashMap()
            headers[Constants.JSON_KEY_AUTHORIZATION] = "${Constants.JSON_KEY_BEARER} $bearer"
            val jsonResponse = RequestUtils.doGetRequest(
                okHttpClient,
                "${Constants.URL_TADO_HOMES}$homeId/zones/$zoneId/capabilities",
                headers
            )
            RequestUtils.debugMessage(
                printDebugResponses,
                "getZoneCapabilities response: $jsonResponse"
            )
            jsonResponse?.let {
                val json = JSONObject(it)
                RequestUtils.checkException(json)
                var type: String? = null
                var key: String? = null
                var value: Any? = null


                json.keys().forEach { key1 ->
                    if (key1 == Constants.JSON_KEY_TYPE) type = json.getString(key1) else {
                        key = key1
                        value = json[key1]
                    }
                }
                toReturn =
                    if (type == null && key == null && value == null) null else Capability(
                        type!!,
                        key!!,
                        value!!
                    )
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: TadoException) {
            toReturn = if (attempt > 1) {
                throw e
            } else {
                refresh()
                getZoneCapabilities(homeId, zoneId, attempt + 1)
            }
        }
        return toReturn
    }

    @Throws(TadoException::class)
    fun getZoneEarlyStart(
        homeId: Int,
        zoneId: Int,
        attempt: Int
    ): Boolean {
        var toReturn = false
        try {
            val headers: MutableMap<String, String> = HashMap()
            headers[Constants.JSON_KEY_AUTHORIZATION] = "${Constants.JSON_KEY_BEARER} $bearer"
            val jsonResponse = RequestUtils.doGetRequest(
                okHttpClient,
                "${Constants.URL_TADO_HOMES}$homeId/zones/$zoneId/earlyStart",
                headers
            )
            RequestUtils.debugMessage(
                printDebugResponses,
                "getZoneEarlyStart response: $jsonResponse"
            )
            jsonResponse?.let {
                val json = JSONObject(it)
                RequestUtils.checkException(json)
                toReturn = json.getBoolean(Constants.JSON_KEY_ENABLED)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: TadoException) {
            toReturn = if (attempt > 1) {
                throw e
            } else {
                refresh()
                getZoneEarlyStart(homeId, zoneId, attempt + 1)
            }
        }
        return toReturn
    }

    @Throws(TadoException::class)
    fun getZoneOverlay(
        homeId: Int,
        zoneId: Int,
        attempt: Int
    ): TadoOverlay? {
        var toReturn: TadoOverlay? = null
        try {
            val headers: MutableMap<String, String> = HashMap()
            headers[Constants.JSON_KEY_AUTHORIZATION] = "${Constants.JSON_KEY_BEARER} $bearer"
            val jsonResponse = RequestUtils.doGetRequest(
                okHttpClient,
                "${Constants.URL_TADO_HOMES}$homeId/zones/$zoneId/overlay",
                headers
            )
            RequestUtils.debugMessage(
                printDebugResponses,
                "getZoneOverlay response: $jsonResponse"
            )
            jsonResponse?.let {
                val json = JSONObject()
                RequestUtils.checkException(json)
                toReturn = parseTadoOverlay(json)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: TadoException) {
            toReturn = if (attempt > 1) {
                throw e
            } else {
                refresh()
                getZoneOverlay(homeId, zoneId, attempt + 1)
            }
        }
        return toReturn
    }

    // PUT requests

    @Throws(TadoException::class)
    fun setGeoTracking(
        homeId: Int,
        deviceId: Int,
        enabled: Boolean,
        attempt: Int
    ): Boolean {
        var toReturn = false
        try {
            val headers: MutableMap<String, String> = HashMap()
            headers[Constants.JSON_KEY_AUTHORIZATION] = "${Constants.JSON_KEY_BEARER} $bearer"
            headers[Constants.JSON_KEY_CONTENT_TYPE] = Constants.JSON_KEY_CONTENT_TYPE_VALUE
            val toPut = JSONObject()
            toPut.put(Constants.JSON_KEY_GEP_TRACKING_ENABLED, enabled)
            val jsonResponse: String? = doPutRequest(
                okHttpClient,
                "${Constants.URL_TADO_HOMES}$homeId/mobileDevices/$deviceId/settings",
                toPut.toString(),
                headers
            )
            RequestUtils.debugMessage(
                printDebugResponses,
                "setGeoTracking response: $jsonResponse"
            )
            jsonResponse?.let {
                val json = JSONObject(it)
                RequestUtils.checkException(json)
                toReturn = true
            }
        } catch (e: IOException) {
            e.printStackTrace()
            toReturn = false
        } catch (e: TadoException) {
            toReturn = if (attempt > 1) {
                throw e
            } else {
                refresh()
                setGeoTracking(
                    homeId,
                    deviceId,
                    enabled,
                    attempt + 1
                )
            }
        }
        return toReturn
    }

    @Throws(TadoException::class)
    fun setZoneEarlyStart(
        homeId: Int,
        zoneId: Int,
        enabled: Boolean,
        attempt: Int
    ): Boolean {
        var toReturn = false
        try {
            val headers: MutableMap<String, String> = HashMap()
            headers[Constants.JSON_KEY_AUTHORIZATION] = "${Constants.JSON_KEY_BEARER} $bearer"
            headers[Constants.JSON_KEY_CONTENT_TYPE] = Constants.JSON_KEY_CONTENT_TYPE_VALUE
            val toPut = JSONObject()
            toPut.put(Constants.JSON_KEY_ENABLED, enabled)
            val jsonResponse: String? = doPutRequest(
                okHttpClient,
                "${Constants.URL_TADO_HOMES}$homeId/zones/$zoneId/earlyStart",
                toPut.toString(),
                headers
            )
            RequestUtils.debugMessage(
                printDebugResponses,
                "setZoneEarlyStart response: $jsonResponse"
            )
            jsonResponse?.let {
                val json = JSONObject(it)
                RequestUtils.checkException(json)
                toReturn = true
            }
        } catch (e: IOException) {
            e.printStackTrace()
            toReturn = false
        } catch (e: TadoException) {
            toReturn = if (attempt > 1) {
                throw e
            } else {
                refresh()
                setZoneEarlyStart(
                    homeId,
                    zoneId,
                    enabled,
                    attempt + 1
                )
            }
        }
        return toReturn
    }

    @Throws(TadoException::class)
    fun setZoneOverlay(
        homeId: Int,
        zoneId: Int,
        overlay: TadoOverlay,
        attempt: Int
    ): TadoOverlay? {
        var toReturn: TadoOverlay? = null
        try {
            val headers: MutableMap<String, String> = HashMap()
            headers[Constants.JSON_KEY_AUTHORIZATION] = "${Constants.JSON_KEY_BEARER} $bearer"
            headers[Constants.JSON_KEY_CONTENT_TYPE] = Constants.JSON_KEY_CONTENT_TYPE_VALUE
            val jsonResponse: String? = doPutRequest(
                okHttpClient,
                "${Constants.URL_TADO_HOMES}$homeId/zones/$zoneId/overlay",
                overlay.toJSONObject().toString(),
                headers
            )
            RequestUtils.debugMessage(
                printDebugResponses,
                "setZoneOverlay response: $jsonResponse"
            )
            jsonResponse?.let {
                val json = JSONObject()
                RequestUtils.checkException(json)
                toReturn = parseTadoOverlay(json)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            toReturn = null
        } catch (e: TadoException) {
            toReturn = if (attempt > 1) {
                throw e
            } else {
                refresh()
                setZoneOverlay(homeId, zoneId, overlay, attempt + 1)
            }
        }
        return toReturn
    }

    @Throws(TadoException::class)
    fun setHomeState(
        homeId: Int,
        presence: String,
        attempt: Int
    ): Boolean {
        var toReturn: Boolean
        try {
            val headers: MutableMap<String, String> = HashMap()
            headers[Constants.JSON_KEY_AUTHORIZATION] = "${Constants.JSON_KEY_BEARER} $bearer"
            headers[Constants.JSON_KEY_CONTENT_TYPE] = Constants.JSON_KEY_CONTENT_TYPE_VALUE
            val toPut = JSONObject()
            toPut.put(Constants.JSON_KEY_HOME_PRESENCE, presence)
            val jsonResponse: String? = doPutRequest(
                okHttpClient,
                "${Constants.URL_TADO_HOMES}$homeId/presence",
                toPut.toString(), headers
            )
            if (jsonResponse != null && jsonResponse.trim { it <= ' ' }.isNotEmpty()) {
                val json = JSONObject(jsonResponse)
                RequestUtils.checkException(json)
            }
            RequestUtils.debugMessage(
                printDebugResponses,
                "setHomeState response: $jsonResponse"
            )
            toReturn = true
        } catch (e: IOException) {
            e.printStackTrace()
            toReturn = false
        } catch (e: TadoException) {
            toReturn = if (attempt > 1) {
                throw e
            } else {
                refresh()
                setHomeState(homeId, presence, attempt + 1)
            }
        }
        return toReturn
    }

    // DELETE requests

    @Throws(TadoException::class)
    fun deleteZoneOverlay(
        homeId: Int,
        zoneId: Int,
        attempt: Int
    ) {
        try {
            val headers: MutableMap<String, String> = HashMap()
            headers[Constants.JSON_KEY_AUTHORIZATION] = "${Constants.JSON_KEY_BEARER} $bearer"
            headers[Constants.JSON_KEY_CONTENT_TYPE] = Constants.JSON_KEY_CONTENT_TYPE_VALUE
            val jsonResponse: String? = doDeleteRequest(
                okHttpClient,
                "${Constants.URL_TADO_HOMES}$homeId/zones/$zoneId/overlay",
                headers
            )
            RequestUtils.debugMessage(
                printDebugResponses,
                "deleteZoneOverlay response: $jsonResponse"
            )
            try {
                // IF IT CAN PARSE THE JSONOBJECT PROBABLY IT WILL BE AN EXCEPTION BECAUSE THE
                // DELETE METHOD DOESN'T RETURN ANYTHING
                jsonResponse?.let {
                    val json = JSONObject(it)
                    RequestUtils.checkException(json)
                }
            } catch (ignored: JSONException) {
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: TadoException) {
            if (attempt > 1) {
                throw e
            } else {
                refresh()
                deleteZoneOverlay(
                    homeId,
                    zoneId,
                    attempt + 1
                )
            }
        }
    }

    private fun parseMobileDevice(homeId: Int, device: JSONObject): MobileDevice {
        val jsonSettings = device.getJSONObject(Constants.JSON_KEY_SETTINGS)
        val settings: MutableMap<String, Any> = HashMap()
        jsonSettings.keys().forEach { key ->
            settings[key] = jsonSettings[key]
        }
        var location: MobileLocation? = null
        if (!device.isNull(Constants.JSON_KEY_LOCATION)) {
            val jsonLocation = device.getJSONObject(Constants.JSON_KEY_LOCATION)
            location = MobileLocation(
                jsonLocation.getBoolean(Constants.JSON_KEY_STALE),
                jsonLocation.getBoolean(Constants.JSON_KEY_AT_HOME),
                jsonLocation.getJSONObject(Constants.JSON_KEY_BEARING_FROM_HOME)
                    .getDouble(Constants.JSON_KEY_DEGREES),
                jsonLocation.getJSONObject(Constants.JSON_KEY_BEARING_FROM_HOME)
                    .getDouble(Constants.JSON_KEY_RADIANS),
                jsonLocation.getDouble(Constants.JSON_KEY_RELATIVE_DISTANCE_FROM_HOME_FENCE)
            )
        }
        var deviceMetadata: DeviceMetadata? = null
        if (!device.isNull(Constants.JSON_KEY_DEVICE_METADATA)) {
            val jsonMetadata = device.getJSONObject(Constants.JSON_KEY_DEVICE_METADATA)
            deviceMetadata = DeviceMetadata(
                jsonMetadata.getString(Constants.JSON_KEY_PLATFORM),
                jsonMetadata.getString(Constants.JSON_KEY_OS_VERSION),
                jsonMetadata.getString(Constants.JSON_KEY_MODEL),
                jsonMetadata.getString(Constants.JSON_KEY_LOCALE)
            )
        }
        return MobileDevice(
            homeId,
            device.optString(Constants.JSON_KEY_NAME),
            device.getInt(Constants.JSON_KEY_ID),
            settings,
            location!!,
            deviceMetadata!!
        )
    }

    @Throws(TadoException::class)
    private fun parseTadoOverlay(json: JSONObject): TadoOverlay {
        val jsonSetting = json.getJSONObject(Constants.JSON_KEY_SETTING)
        val temperature = Temperature(
            jsonSetting.getJSONObject(Constants.JSON_KEY_TEMPERATURE)
                .getDouble(Constants.JSON_KEY_CELSIUS),
            jsonSetting.getJSONObject(Constants.JSON_KEY_TEMPERATURE)
                .getDouble(Constants.JSON_KEY_FAHRENHEIT)
        )
        val setting = TadoSetting(
            jsonSetting.getString(Constants.JSON_KEY_TYPE),
            jsonSetting.getString(Constants.JSON_KEY_POWER) == Constants.JSON_KEY_ON,
            temperature
        )
        val jsonTermination = json.getJSONObject(Constants.JSON_KEY_TERMINATION)
        val termination: Termination
        when (jsonTermination.getString(Constants.JSON_KEY_TYPE)) {
            Constants.JSON_KEY_TIMER -> {
                val expiry: Date =
                    jsonTermination.optString(Constants.JSON_KEY_EXPIRY).toFormattedUTCDate()
                val projectedExpiry: Date =
                    jsonTermination.optString(Constants.JSON_KEY_PROJECTED_EXPIRY)
                        .toFormattedUTCDate()
                termination = TimerTermination(
                    jsonTermination.getString(Constants.JSON_KEY_TYPE_SKILL_BASED_APP),
                    jsonTermination.getInt(Constants.JSON_KEY_DURATION_IN_SECONDS),
                    expiry,
                    jsonTermination.getInt(Constants.JSON_KEY_REMAINING_TIME_IN_SECONDS),
                    projectedExpiry
                )
            }
            Constants.JSON_KEY_MANUAL -> {
                val projectedExpiry: Date =
                    jsonTermination.optString(Constants.JSON_KEY_PROJECTED_EXPIRY)
                        .toFormattedUTCDate()
                termination = ManualTermination(
                    jsonTermination.getString(Constants.JSON_KEY_TYPE_SKILL_BASED_APP),
                    projectedExpiry
                )
            }
            Constants.JSON_KEY_TADO_MODE_FOR_OVERLAY -> {
                val projectedExpiry: Date =
                    jsonTermination.optString(Constants.JSON_KEY_PROJECTED_EXPIRY)
                        .toFormattedUTCDate()
                termination = TadoModeTermination(
                    jsonTermination.getString(Constants.JSON_KEY_TYPE_SKILL_BASED_APP),
                    projectedExpiry
                )
            }
            else -> {
                throw TadoException(
                    "error",
                    "The termination type \"" + jsonTermination.getString(Constants.JSON_KEY_TYPE) + "\" is not valid."
                )
            }
        }
        return TadoOverlay(json.getString(Constants.JSON_KEY_TYPE), setting, termination)
    }
}