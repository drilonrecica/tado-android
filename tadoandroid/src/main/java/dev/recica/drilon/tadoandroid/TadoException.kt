package dev.recica.drilon.tadoandroid

class TadoException(code: String, title: String) : Exception(
    "$code: $title"
)