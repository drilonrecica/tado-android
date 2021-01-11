package dev.recica.drilon.tadoandroid.model

import dev.recica.drilon.tadoandroid.TadoConnector
import dev.recica.drilon.tadoandroid.TadoException
import java.util.*

data class TadoZone(
    val homeId: Int,
    val id: Int,
    val name: String,
    val type: String,
    val dateCreated: Date,
    val deviceTypes: List<String>,
    val devices: List<TadoDevice>,
    val reportAvailable: Boolean,
    val supportsDazzle: Boolean,
    val dazzleEnabled: Boolean,
    val dazzleMode: TadoDazzleMode,
    val openWindowDetection: OpenWindowDetection
) {

    @Throws(TadoException::class)
    fun getState(connector: TadoConnector): TadoZoneState? {
        return connector.getZoneState(homeId, id)
    }

    @Throws(TadoException::class)
    fun getCapabilities(connector: TadoConnector): Capability? {
        return connector.getZoneCapabilities(homeId, id)
    }

    @Throws(TadoException::class)
    fun getEarlyStart(connector: TadoConnector): Boolean {
        return connector.getZoneEarlyStart(homeId, id)
    }

    @Throws(TadoException::class)
    fun setEarlyStart(enabled: Boolean, connector: TadoConnector): Boolean {
        return connector.setZoneEarlyStart(homeId, id, enabled)
    }

    @Throws(TadoException::class)
    fun getOverlay(connector: TadoConnector): TadoOverlay? {
        return connector.getZoneOverlay(homeId, id)
    }

    @Throws(TadoException::class)
    fun setOverlay(overlay: TadoOverlay?, connector: TadoConnector): TadoOverlay? {
        return connector.setZoneOverlay(homeId, id, overlay!!)
    }

    @Throws(TadoException::class)
    fun deleteOverlay(connector: TadoConnector) {
        connector.deleteZoneOverlay(homeId, id)
    }

    override fun toString(): String {
        return "TadoZone [homeId=$homeId, id=$id, name=$name, type=$type, dateCreated=$dateCreated, deviceTypes=$deviceTypes, devices=$devices, reportAvailable=$reportAvailable, supportsDazzle=$supportsDazzle, dazzleEnabled=$dazzleEnabled, dazzleMode=$dazzleMode, openWindowDetection=$openWindowDetection]"
    }
}