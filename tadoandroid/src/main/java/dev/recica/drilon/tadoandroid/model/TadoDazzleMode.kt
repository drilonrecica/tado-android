package dev.recica.drilon.tadoandroid.model

data class TadoDazzleMode(
    val supported: Boolean,
    val enabled: Boolean
) {
    override fun toString(): String {
        return "TadoDazzleMode [supported=$supported, enabled=$enabled]"
    }
}