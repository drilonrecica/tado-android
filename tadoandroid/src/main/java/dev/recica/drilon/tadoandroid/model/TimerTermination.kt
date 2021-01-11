package dev.recica.drilon.tadoandroid.model

import dev.recica.drilon.tadoandroid.internal.Constants
import dev.recica.drilon.tadoandroid.internal.toFormattedUTCDateString
import org.json.JSONObject
import java.util.*

class TimerTermination : Termination {
    var durationInSeconds: Int? = null
        private set
    var expiry: Date? = null
        private set
    var remainingTimeInSeconds: Int? = null
        private set

    constructor(
        typeSkillBasedApp: String,
        durationInSeconds: Int,
        expiry: Date,
        remainingTimeInSeconds: Int,
        projectedExpiry: Date
    ) : super(Constants.JSON_KEY_TIMER, typeSkillBasedApp, projectedExpiry) {
        this.durationInSeconds = durationInSeconds
        this.expiry = expiry
        this.remainingTimeInSeconds = remainingTimeInSeconds
    }

    constructor(durationInSeconds: Int) : super(Constants.JSON_KEY_TIMER) {
        this.durationInSeconds = durationInSeconds
    }

    override fun toJSONObject(): JSONObject {
        val root = JSONObject()
        root.put(Constants.JSON_KEY_TYPE, type)
        root.put(Constants.JSON_KEY_DURATION_IN_SECONDS, durationInSeconds)
        root.put(Constants.JSON_KEY_TYPE_SKILL_BASED_APP, typeSkillBasedApp)
        if (expiry != null) root.put(Constants.JSON_KEY_EXPIRY, expiry?.toFormattedUTCDateString())
        if (remainingTimeInSeconds != null) root.put(
            Constants.JSON_KEY_REMAINING_TIME_IN_SECONDS,
            remainingTimeInSeconds
        )
        root.put(
            Constants.JSON_KEY_PROJECTED_EXPIRY,
            projectedExpiry.toFormattedUTCDateString()
        )
        return root
    }

    override fun toString(): String {
        return "TimerTermination [durationInSeconds=$durationInSeconds, expiry=$expiry, remainingTimeInSeconds=$remainingTimeInSeconds]"
    }
}