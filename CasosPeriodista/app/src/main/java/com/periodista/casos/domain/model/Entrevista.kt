package com.periodista.casos.domain.model

/** Entrevista realizada dentro de un caso, con sus hallazgos y evidencias. */
data class Entrevista(
    val id: Long = 0,
    val casoId: Long,
    val entrevistado: String,
    val fecha: Long,
    val hallazgos: String,
    val evidencias: String = ""
)
