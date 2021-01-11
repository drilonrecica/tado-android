package dev.recica.drilon.tadoandroid.model

import dev.recica.drilon.tadoandroid.internal.Constants
import dev.recica.drilon.tadoandroid.internal.toFormattedUTCDateString
import org.json.JSONObject
import java.util.*

class ManualTermination : Termination {

    constructor(typeSkillBasedApp: String, projectedExpiry: Date) : super(
        Constants.JSON_KEY_MANUAL,
        typeSkillBasedApp,
        projectedExpiry
    )

    constructor() : super(Constants.JSON_KEY_MANUAL)

    override fun toJSONObject(): JSONObject {
        val root = JSONObject()
        root.put("type", type)
        root.put("typeSkillBasedApp", typeSkillBasedApp)
        root.put(
            "projectedExpiry",
            projectedExpiry.toFormattedUTCDateString()
        )
        return root
    }
}