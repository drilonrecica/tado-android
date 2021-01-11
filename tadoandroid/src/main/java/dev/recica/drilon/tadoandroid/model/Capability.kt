package dev.recica.drilon.tadoandroid.model

data class Capability(
    val type: String,
    val key: String,
    val value: Any
) {
    override fun toString(): String {
        return "Capability [type=$type, key=$key, value=$value]"
    }
}
