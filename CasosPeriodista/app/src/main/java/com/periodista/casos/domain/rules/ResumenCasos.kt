package com.periodista.casos.domain.rules

import com.periodista.casos.domain.model.Caso
import com.periodista.casos.domain.model.EstadoCaso

/** Indicadores que se muestran en la pantalla de resumen. */
data class ResumenCasos(
    val total: Int = 0,
    val abiertos: Int = 0,
    val enInvestigacion: Int = 0,
    val cerrados: Int = 0
) {
    companion object {
        fun de(casos: List<Caso>) = ResumenCasos(
            total = casos.size,
            abiertos = casos.count { it.estado == EstadoCaso.ABIERTO },
            enInvestigacion = casos.count { it.estado == EstadoCaso.EN_INVESTIGACION },
            cerrados = casos.count { it.estado == EstadoCaso.CERRADO }
        )
    }
}
