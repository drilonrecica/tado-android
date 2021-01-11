package dev.recica.drilon.tadoandroid.model

import org.json.JSONObject

data class TadoOverlay(
    val type: String? = null,
    val setting: TadoSetting,
    val termination: Termination
) {

    fun toJSONObject(): JSONObject {
        val root = JSONObject()
        root.put("type", type)
        root.put("setting", setting.toJSONObject())
        root.put("termination", termination.toJSONObject())
        return root
    }

    override fun toString(): String {
        return "TadoOverlay [type=$type, setting=$setting, termination=$termination]"
    }
}
