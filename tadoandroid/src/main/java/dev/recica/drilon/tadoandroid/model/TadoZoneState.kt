package dev.recica.drilon.tadoandroid.model

import java.util.*

data class TadoZoneState(
    val tadoMode: String,
    val geolocationOverride: Boolean,
    val geolocationOverrideDisableTime: Date?,
    val setting: TadoSetting,
    val nextScheduleChange: TadoScheduleChange?,
    val linkState: String,
    val activityDataPoints: List<TadoDataPoint>,
    val sensorDataPoints: List<TadoDataPoint>
) {
    override fun toString(): String {
        return "TadoZoneState [tadoMode=$tadoMode, geolocationOverride=$geolocationOverride, geolocationOverrideDisableTime=$geolocationOverrideDisableTime, setting=$setting, nextScheduleChange=$nextScheduleChange, linkState=$linkState, activityDataPoints=$activityDataPoints, sensorDataPoints=$sensorDataPoints]"
    }
}
