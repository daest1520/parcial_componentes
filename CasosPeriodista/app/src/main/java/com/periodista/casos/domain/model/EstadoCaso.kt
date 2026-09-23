package com.periodista.casos.domain.model

/** Estados por los que pasa un caso durante su ciclo de vida. */
enum class EstadoCaso(val etiqueta: String) {
    ABIERTO("Abierto"),
    EN_INVESTIGACION("En investigación"),
    CERRADO("Cerrado")
}
