package dev.recica.drilon.tadoandroid.model

data class DeviceMetadata(
    val platform: String,
    val osVersion: String,
    val model: String,
    val locale: String,
) {
    override fun toString(): String {
        return "DeviceMetadata [platform=$platform, osVersion=$osVersion, model=$model, locale=$locale]"
    }
}
