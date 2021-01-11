package dev.recica.drilon.tadoandroid.model

data class OpenWindowDetection(
    val supported: Boolean,
    val enabled: Boolean,
    val timeoutInSeconds: Int
) {
    override fun toString(): String {
        return "TadoOpenWindowDetection [supported=$supported, enabled=$enabled, timeoutInSeconds=$timeoutInSeconds]"
    }
}