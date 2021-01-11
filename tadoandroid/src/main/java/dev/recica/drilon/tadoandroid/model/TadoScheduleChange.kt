package dev.recica.drilon.tadoandroid.model

import java.util.*

data class TadoScheduleChange(
    val start: Date,
    val setting: TadoSetting
) {
    override fun toString(): String {
        return "TadoScheduleChange [start=$start, setting=$setting]"
    }
}
