package dev.recica.drilon.tadoandroid.model

data class ContactDetails(
    val name: String,
    val email: String,
    val phone: String
) {
    override fun toString(): String {
        return "TadoContact [name=$name, email=$email, phone=$phone]"
    }
}