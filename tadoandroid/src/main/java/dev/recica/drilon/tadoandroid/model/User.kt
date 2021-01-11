package dev.recica.drilon.tadoandroid.model

data class User(
    val name: String,
    val email: String,
    val username: String,
    val homes: Map<Int, String>,
    val locale: String,
    val mobileDevices: List<MobileDevice>
) {
    override fun toString(): String {
        return "User [name=$name, email=$email, username=$username, homes=$homes, locale=$locale, mobileDevices=$mobileDevices]"
    }
}
