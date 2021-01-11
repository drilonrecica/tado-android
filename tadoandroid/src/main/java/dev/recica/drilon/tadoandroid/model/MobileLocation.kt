package dev.recica.drilon.tadoandroid.model

data class MobileLocation(
    val stale: Boolean,
    val atHome: Boolean,
    val degreesBearingFromHome: Double,
    val radiansBearingFromHome: Double,
    val relativeDistanceFromHomeFence: Double,
) {
    override fun toString(): String {
        return "MobileLocation [stale=$stale, atHome=$atHome, degreesBearingFromHome=$degreesBearingFromHome, radiansBearingFromHome=$radiansBearingFromHome, relativeDistanceFromHomeFence=$relativeDistanceFromHomeFence]"
    }
}
