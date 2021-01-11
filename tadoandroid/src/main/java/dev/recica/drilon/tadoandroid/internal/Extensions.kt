package dev.recica.drilon.tadoandroid.internal

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun String?.toFormattedUTCDate(): Date {
    val format = SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US
    )
    format.timeZone = TimeZone.getTimeZone("UTC")
    return if (this == null) {
        format.calendar.time
    } else {
        try {
            format.parse(this)!!
        } catch (e: ParseException) {
            format.calendar.time
        }
    }
}

fun Date?.toFormattedUTCDateString(): String {
    val format = SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US
    )
    format.timeZone = TimeZone.getTimeZone("UTC")
    return if (this == null) {
        format.format(format.calendar.time)
    } else {
        try {
            format.format(this)
        } catch (e: Exception) {
            format.format(format.calendar.time)
        }
    }
}