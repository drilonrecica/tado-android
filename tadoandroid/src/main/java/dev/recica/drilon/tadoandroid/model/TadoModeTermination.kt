package dev.recica.drilon.tadoandroid.model

import dev.recica.drilon.tadoandroid.internal.Constants
import dev.recica.drilon.tadoandroid.internal.toFormattedUTCDateString
import org.json.JSONObject
import java.util.*

class TadoModeTermination : Termination {

    constructor(typeSkillBasedApp: String, projectedExpiry: Date) : super(
        Constants.JSON_KEY_TADO_MODE_FOR_OVERLAY,
        typeSkillBasedApp,
        projectedExpiry
    )

    constructor() : super(Constants.JSON_KEY_TADO_MODE_FOR_OVERLAY)

    override fun toJSONObject(): JSONObject {
        val root = JSONObject()
        root.put(Constants.JSON_KEY_TYPE, type)
        root.put(Constants.JSON_KEY_TYPE_SKILL_BASED_APP, typeSkillBasedApp)
        root.put(Constants.JSON_KEY_PROJECTED_EXPIRY, projectedExpiry.toFormattedUTCDateString())
        return root
    }
}