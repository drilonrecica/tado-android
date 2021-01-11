package dev.recica.drilon.tadoandroid.model

import java.util.*

data class TadoConnectionState(
    val value: Boolean,
    val timestamp: Date
) {
    override fun toString(): String {
        return "TadoConnectionState [value=$value, timestamp=$timestamp]"
    }
}