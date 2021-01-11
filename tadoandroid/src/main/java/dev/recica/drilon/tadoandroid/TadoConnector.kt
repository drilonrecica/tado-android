package dev.recica.drilon.tadoandroid

import dev.recica.drilon.tadoandroid.internal.TadoHomeService
import dev.recica.drilon.tadoandroid.model.*
import okhttp3.OkHttpClient

@Suppress("unused")
class TadoConnector(
    username: String,
    password: String,
    clientSecret: String? = null,
    printDebugResponses: Boolean = true
) {
    private var okHttpClient: OkHttpClient = OkHttpClient()
    private var tadoHomeService: TadoHomeService

    init {
        tadoHomeService =
            TadoHomeService(okHttpClient, clientSecret, username, password, printDebugResponses)
        tadoHomeService.initialized = true
    }

    // GET requests

    @Throws(TadoException::class)
    fun getHomeIDs(): List<Int?> {
        throwNotInitializedErrorIfNecessary()
        return tadoHomeService.getHomeIDs(0)
    }

    @Throws(TadoException::class)
    fun getHomes(): List<TadoHome?> {
        throwNotInitializedErrorIfNecessary()
        return tadoHomeService.getHomes(0)
    }

    @Throws(TadoException::class)
    fun getHome(id: Int): TadoHome? {
        throwNotInitializedErrorIfNecessary()
        return tadoHomeService.getHome(id, 0)
    }

    @Throws(TadoException::class)
    fun getZones(tadoHome: TadoHome): List<TadoZone?> {
        return getZones(tadoHome.id)
    }

    @Throws(TadoException::class)
    fun getZones(homeId: Int): List<TadoZone?> {
        throwNotInitializedErrorIfNecessary()
        return tadoHomeService.getZones(homeId, 0)
    }

    @Throws(TadoException::class)
    fun getHomeState(tadoHome: TadoHome): TadoState? {
        return getHomeState(tadoHome.id)
    }

    @Throws(TadoException::class)
    fun getHomeState(homeId: Int): TadoState? {
        throwNotInitializedErrorIfNecessary()
        return tadoHomeService.getHomeState(homeId, 0)
    }

    @Throws(TadoException::class)
    fun getZoneState(tadoZone: TadoZone): TadoZoneState? {
        return getZoneState(tadoZone.homeId, tadoZone.id)
    }

    @Throws(TadoException::class)
    fun getZoneState(homeId: Int, idZone: Int): TadoZoneState? {
        throwNotInitializedErrorIfNecessary()
        return tadoHomeService.getZoneState(
            homeId,
            idZone,
            0
        )
    }

    @Throws(TadoException::class)
    fun getWeather(tadoHome: TadoHome): TadoWeather? {
        return getWeather(tadoHome.id)
    }

    @Throws(TadoException::class)
    fun getWeather(homeId: Int): TadoWeather? {
        throwNotInitializedErrorIfNecessary()
        return tadoHomeService.getWeather(homeId, 0)
    }

    @Throws(TadoException::class)
    fun getDevices(tadoHome: TadoHome): List<TadoDevice?> {
        return getDevices(tadoHome.id)
    }

    @Throws(TadoException::class)
    fun getDevices(homeId: Int): List<TadoDevice?> {
        throwNotInitializedErrorIfNecessary()
        return tadoHomeService.getDevices(homeId, 0)
    }

    @Throws(TadoException::class)
    fun getInstallations(tadoHome: TadoHome): List<TadoInstallation?> {
        return getInstallations(tadoHome.id)
    }

    @Throws(TadoException::class)
    fun getInstallations(homeId: Int): List<TadoInstallation?> {
        throwNotInitializedErrorIfNecessary()
        return tadoHomeService.getInstallations(
            homeId,
            0
        )
    }

    @Throws(TadoException::class)
    fun getUsers(tadoHome: TadoHome): List<User?> {
        return getUsers(tadoHome.id)
    }

    @Throws(TadoException::class)
    fun getUsers(homeId: Int): List<User?> {
        throwNotInitializedErrorIfNecessary()
        return tadoHomeService.getUsers(homeId, 0)
    }

    @Throws(TadoException::class)
    fun getMobileDevices(tadoHome: TadoHome): List<MobileDevice?> {
        return getMobileDevices(tadoHome.id)
    }

    @Throws(TadoException::class)
    fun getMobileDevices(homeId: Int): List<MobileDevice?> {
        throwNotInitializedErrorIfNecessary()
        return tadoHomeService.getMobileDevices(
            homeId,
            0
        )
    }

    @Throws(TadoException::class)
    fun getMobileDevice(deviceId: Int, tadoHome: TadoHome): MobileDevice? {
        return getMobileDevice(deviceId, tadoHome.id)
    }

    @Throws(TadoException::class)
    fun getMobileDevice(deviceId: Int, homeId: Int): MobileDevice? {
        throwNotInitializedErrorIfNecessary()
        return tadoHomeService.getMobileDevice(
            deviceId,
            homeId,
            0
        )
    }

    @Throws(TadoException::class)
    fun getMobileDeviceSettings(device: MobileDevice): Map<String?, Any?>? {
        return getMobileDeviceSettings(device.homeId, device.id)
    }

    @Throws(TadoException::class)
    fun getMobileDeviceSettings(homeId: Int, deviceId: Int): Map<String?, Any?>? {
        throwNotInitializedErrorIfNecessary()
        return tadoHomeService.getMobileDeviceSettings(
            homeId,
            deviceId,
            0
        )
    }

    @Throws(TadoException::class)
    fun getZoneCapabilities(tadoZone: TadoZone): Capability? {
        return getZoneCapabilities(tadoZone.homeId, tadoZone.id)
    }

    @Throws(TadoException::class)
    fun getZoneCapabilities(homeId: Int, zoneId: Int): Capability? {
        throwNotInitializedErrorIfNecessary()
        return tadoHomeService.getZoneCapabilities(
            homeId,
            zoneId,
            0
        )
    }

    @Throws(TadoException::class)
    fun getZoneEarlyStart(tadoZone: TadoZone): Boolean {
        return getZoneEarlyStart(tadoZone.homeId, tadoZone.id)
    }

    @Throws(TadoException::class)
    fun getZoneEarlyStart(homeId: Int, zoneId: Int): Boolean {
        throwNotInitializedErrorIfNecessary()
        return tadoHomeService.getZoneEarlyStart(
            homeId,
            zoneId,
            0
        )
    }

    @Throws(TadoException::class)
    fun getZoneOverlay(tadoZone: TadoZone): TadoOverlay? {
        return getZoneOverlay(tadoZone.homeId, tadoZone.id)
    }

    @Throws(TadoException::class)
    fun getZoneOverlay(homeId: Int, zoneId: Int): TadoOverlay? {
        throwNotInitializedErrorIfNecessary()
        return tadoHomeService.getZoneOverlay(
            homeId,
            zoneId,
            0
        )
    }

    // PUT requests
    @Throws(TadoException::class)
    fun setGeoTracking(device: MobileDevice, enabled: Boolean): Boolean {
        return setGeoTracking(device.homeId, device.id, enabled)
    }

    @Throws(TadoException::class)
    fun setGeoTracking(homeId: Int, deviceId: Int, enabled: Boolean): Boolean {
        throwNotInitializedErrorIfNecessary()
        return tadoHomeService.setGeoTracking(
            homeId,
            deviceId,
            enabled,
            0
        )
    }

    @Throws(TadoException::class)
    fun setZoneEarlyStart(zone: TadoZone, enabled: Boolean): Boolean {
        return setZoneEarlyStart(zone.homeId, zone.id, enabled)
    }

    @Throws(TadoException::class)
    fun setZoneEarlyStart(homeId: Int, zoneId: Int, enabled: Boolean): Boolean {
        throwNotInitializedErrorIfNecessary()
        return tadoHomeService.setZoneEarlyStart(
            homeId,
            zoneId,
            enabled,
            0
        )
    }

    @Throws(TadoException::class)
    fun setZoneOverlay(zone: TadoZone, overlay: TadoOverlay): TadoOverlay? {
        return setZoneOverlay(zone.homeId, zone.id, overlay)
    }

    @Throws(TadoException::class)
    fun setZoneOverlay(homeId: Int, zoneId: Int, overlay: TadoOverlay): TadoOverlay? {
        throwNotInitializedErrorIfNecessary()
        return tadoHomeService.setZoneOverlay(
            homeId,
            zoneId,
            overlay,
            0
        )
    }

    @Throws(TadoException::class)
    fun setHomeState(home: TadoHome, state: TadoState): Boolean {
        return setHomeState(home.id, state.presence)
    }

    @Throws(TadoException::class)
    fun setHomeState(homeId: Int, presence: String): Boolean {
        throwNotInitializedErrorIfNecessary()
        return tadoHomeService.setHomeState(
            homeId,
            presence,
            0
        )
    }

    // DELETE requests

    @Throws(TadoException::class)
    fun deleteZoneOverlay(zone: TadoZone) {
        deleteZoneOverlay(zone.homeId, zone.id)
    }

    @Throws(TadoException::class)
    fun deleteZoneOverlay(homeId: Int, zoneId: Int) {
        throwNotInitializedErrorIfNecessary()
        tadoHomeService.deleteZoneOverlay(
            homeId,
            zoneId,
            0
        )
    }

    private fun throwNotInitializedErrorIfNecessary() {
        if (!tadoHomeService.initialized) throw TadoException(
            "error",
            "You must initialize the TadoConnector"
        )
    }
}