package dev.recica.drilon.tadoandroid.model

data class TadoDevice(
    val deviceType: String,
    val serialNo: String,
    val shortSerialNo: String,
    val currentFwVersion: String,
    val connectionState: TadoConnectionState,
    val capabilities: List<String>,
    val inPairingMode: Boolean,
    val batteryState: String,
    val duties: List<String>
) {
    override fun toString(): String {
        return "TadoDevice [deviceType=$deviceType, serialNo=$serialNo, shortSerialNo=$shortSerialNo, currentFwVersion=$currentFwVersion, connectionState=$connectionState, capabilities=$capabilities, inPairingMode=$inPairingMode, batteryState=$batteryState, duties=$duties]"
    }
}