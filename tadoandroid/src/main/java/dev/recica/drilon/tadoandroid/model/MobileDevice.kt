package dev.recica.drilon.tadoandroid.model

import dev.recica.drilon.tadoandroid.TadoConnector
import dev.recica.drilon.tadoandroid.TadoException

data class MobileDevice(
    val homeId: Int,
    val name: String,
    val id: Int,
    val settings: Map<String, Any>,
    val location: MobileLocation,
    val deviceMetadata: DeviceMetadata
) {
    override fun toString(): String {
        return "MobileDevice [homeId=$homeId, name=$name, id=$id, settings=$settings, location=$location, deviceMetadata=$deviceMetadata]"
    }

    @Throws(TadoException::class)
    fun getSettings(connector: TadoConnector): Map<String?, Any?>? =
        connector.getMobileDeviceSettings(this.homeId, this.id)

    @Throws(TadoException::class)
    fun setGeoTracking(enabled: Boolean, connector: TadoConnector): Boolean =
        connector.setGeoTracking(this.homeId, this.id, enabled)
}
