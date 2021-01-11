package dev.recica.drilon.tadoandroid.model

data class TadoInstallation(
    var id: Int,
    var type: String,
    var revision: Int,
    var state: String,
    var devices: List<TadoDevice>
) {
    override fun toString(): String {
        return "TadoInstallation [id=$id, type=$type, revision=$revision, state=$state, devices=$devices]"
    }
}
