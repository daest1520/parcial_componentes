package com.periodista.casos.domain.model

/**
 * Caso criminal que el periodista está documentando.
 * Las fechas se guardan como milisegundos (medianoche UTC del día elegido).
 * [fechaCierre] solo tiene valor cuando el caso está CERRADO.
 */
data class Caso(
    val id: Long = 0,
    val titulo: String,
    val descripcion: String,
    val fecha: Long,
    val estado: EstadoCaso = EstadoCaso.ABIERTO,
    val conclusion: String? = null,
    val fechaCierre: Long? = null
)
