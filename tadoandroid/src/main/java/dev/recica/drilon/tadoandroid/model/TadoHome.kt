package dev.recica.drilon.tadoandroid.model

import dev.recica.drilon.tadoandroid.TadoConnector
import dev.recica.drilon.tadoandroid.TadoException
import java.util.*


data class TadoHome(
    val id: Int,
    val name: String,
    val dateTimeZone: String,
    val dateCreated: Date,
    val temperatureUnit: String,
    val installationCompleted: Boolean,
    val simpleSmartScheduleEnabled: Boolean,
    val awayRadiusInMeters: Double,
    val usePreSkillsApps: Boolean,
    val christmasModeEnabled: Boolean,
    val contactDetails: ContactDetails,
    val address: Address,
    val geolocation: Geolocation,
    val consentGrantSkippable: Boolean
) {

    @Throws(TadoException::class)
    fun getZones(connector: TadoConnector): List<TadoZone?> {
        return connector.getZones(id)
    }

    @Throws(TadoException::class)
    fun getState(connector: TadoConnector): TadoState? {
        return connector.getHomeState(id)
    }

    @Throws(TadoException::class)
    fun getWeather(connector: TadoConnector): TadoWeather? {
        return connector.getWeather(id)
    }

    @Throws(TadoException::class)
    fun getDevices(connector: TadoConnector): List<TadoDevice?> {
        return connector.getDevices(id)
    }

    @Throws(TadoException::class)
    fun getInstallations(connector: TadoConnector): List<TadoInstallation?> {
        return connector.getInstallations(id)
    }

    @Throws(TadoException::class)
    fun getUsers(connector: TadoConnector): List<User?> {
        return connector.getUsers(id)
    }

    @Throws(TadoException::class)
    fun getMobileDevices(connector: TadoConnector): List<MobileDevice?> {
        return connector.getMobileDevices(id)
    }

    @Throws(TadoException::class)
    fun getMobileDevice(id: Int, connector: TadoConnector): MobileDevice? {
        return connector.getMobileDevice(id, this.id)
    }

    @Throws(TadoException::class)
    fun getMobileDeviceSettings(deviceId: Int, connector: TadoConnector): Map<String?, Any?>? {
        return connector.getMobileDeviceSettings(deviceId, id)
    }

    @Throws(TadoException::class)
    fun setGeoTracking(deviceId: Int, enabled: Boolean, connector: TadoConnector): Boolean {
        return connector.setGeoTracking(id, deviceId, enabled)
    }

    @Throws(TadoException::class)
    fun setState(state: TadoState, connector: TadoConnector): Boolean {
        return connector.setHomeState(id, state.presence)
    }

    override fun toString(): String {
        return "TadoHome [id=$id, name=$name, dateTimeZone=$dateTimeZone, dateCreated=$dateCreated, temperatureUnit=$temperatureUnit, installationCompleted=$installationCompleted, simpleSmartScheduleEnabled=$simpleSmartScheduleEnabled, awayRadiusInMeters=$awayRadiusInMeters, usePreSkillsApps=$usePreSkillsApps, christmasModeEnabled=$christmasModeEnabled, contactDetails=$contactDetails, address=$address, geolocation=$geolocation, consentGrantSkippable=$consentGrantSkippable]"
    }
}