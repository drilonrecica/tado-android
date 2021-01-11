package dev.recica.drilon.tadoandroid.model

import org.json.JSONObject
import java.util.*

abstract class Termination {
    var type: String
        private set
    lateinit var typeSkillBasedApp: String
        private set
    lateinit var projectedExpiry: Date
        private set

    constructor(type: String, typeSkillBasedApp: String, projectedExpiry: Date) {
        this.type = type
        this.typeSkillBasedApp = typeSkillBasedApp
        this.projectedExpiry = projectedExpiry
    }

    constructor(type: String) {
        this.type = type
    }

    override fun toString(): String {
        return "Termination [type=$type, typeSkillBasedApp=$typeSkillBasedApp, projectedExpiry=$projectedExpiry]"
    }

    abstract fun toJSONObject(): JSONObject?
}
