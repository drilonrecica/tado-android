package dev.recica.drilon.tadoandroid.model

data class Address(
    val addressLine1: String,
    val addressLine2: String,
    val zipCode: String,
    val city: String,
    val state: String,
    val country: String
) {
    override fun toString(): String {
        return "TadoAddress [addressLine1=$addressLine1, addressLine2=$addressLine2, zipCode=$zipCode, city=$city, state=$state, country=$country]"
    }
}