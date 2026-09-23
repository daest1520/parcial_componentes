package com.periodista.casos.domain

import com.periodista.casos.domain.model.Caso
import com.periodista.casos.domain.model.EstadoCaso
import com.periodista.casos.domain.rules.CasoRules
import com.periodista.casos.domain.rules.ResumenCasos
import org.junit.Assert.assertEquals
import org.junit.Test

class BusquedaYResumenTest {

    private val casos = listOf(
        Caso(1, "Robo en Chapinero", "Asalto a un banco", 0L, EstadoCaso.ABIERTO),
        Caso(2, "Homicidio en Suba", "Crimen sin resolver", 0L, EstadoCaso.EN_INVESTIGACION),
        Caso(3, "Fraude electoral", "Compra de votos en Suba", 0L, EstadoCaso.CERRADO, "Condenados"),
        Caso(4, "Robo de vehículo", "Carro hurtado", 0L, EstadoCaso.CERRADO, "Recuperado")
    )

    @Test
    fun busquedaVacia_devuelveTodos() {
        assertEquals(4, CasoRules.filtrar(casos, "", null).size)
    }

    @Test
    fun busquedaPorTitulo_ignoraMayusculas() {
        val resultado = CasoRules.filtrar(casos, "ROBO", null)
        assertEquals(listOf(1L, 4L), resultado.map { it.id })
    }

    @Test
    fun busquedaTambienEnDescripcion() {
        val resultado = CasoRules.filtrar(casos, "suba", null)
        assertEquals(listOf(2L, 3L), resultado.map { it.id })
    }

    @Test
    fun filtroPorEstado() {
        val resultado = CasoRules.filtrar(casos, "", EstadoCaso.CERRADO)
        assertEquals(listOf(3L, 4L), resultado.map { it.id })
    }

    @Test
    fun textoYEstadoCombinados() {
        val resultado = CasoRules.filtrar(casos, "robo", EstadoCaso.CERRADO)
        assertEquals(listOf(4L), resultado.map { it.id })
    }

    @Test
    fun resumen_cuentaCasosPorEstado() {
        val resumen = ResumenCasos.de(casos)
        assertEquals(ResumenCasos(total = 4, abiertos = 1, enInvestigacion = 1, cerrados = 2), resumen)
    }

    @Test
    fun resumen_sinCasos_todoEnCero() {
        assertEquals(ResumenCasos(), ResumenCasos.de(emptyList()))
    }
}
