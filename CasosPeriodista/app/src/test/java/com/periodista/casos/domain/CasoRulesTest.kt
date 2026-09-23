package com.periodista.casos.domain

import com.periodista.casos.domain.model.Caso
import com.periodista.casos.domain.model.EstadoCaso
import com.periodista.casos.domain.rules.CasoRules
import com.periodista.casos.util.aMillis
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class CasoRulesTest {

    private val hoy = LocalDate.of(2026, 9, 22)

    private fun validar(
        titulo: String = "Robo en el centro",
        descripcion: String = "Robo a mano armada en la calle 10",
        fecha: LocalDate? = hoy,
        estado: EstadoCaso = EstadoCaso.ABIERTO,
        conclusion: String = "",
        fechaCierre: LocalDate? = null
    ) = CasoRules.validarCaso(titulo, descripcion, fecha, estado, conclusion, fechaCierre, hoy)

    private val casoAbierto = Caso(id = 1, titulo = "Caso", descripcion = "Desc", fecha = 0L)

    // ---- Crear / editar caso ----

    @Test
    fun casoCompleto_esValido() {
        assertTrue(validar().esValido)
    }

    @Test
    fun tituloVacio_generaError() {
        val errores = validar(titulo = "   ")
        assertFalse(errores.esValido)
        assertNotNull(errores.titulo)
    }

    @Test
    fun tituloMuyCorto_generaError() {
        assertNotNull(validar(titulo = "ab").titulo)
    }

    @Test
    fun descripcionVacia_generaError() {
        assertNotNull(validar(descripcion = "").descripcion)
    }

    @Test
    fun fechaFutura_generaError() {
        assertNotNull(validar(fecha = hoy.plusDays(1)).fecha)
    }

    @Test
    fun fechaNula_generaError() {
        assertNotNull(validar(fecha = null).fecha)
    }

    @Test
    fun fechaDeHoy_esValida() {
        assertNull(validar(fecha = hoy).fecha)
    }

    // ---- Estado y cierre ----

    @Test
    fun estadoCerradoSinConclusion_esInvalido() {
        val errores = validar(estado = EstadoCaso.CERRADO, conclusion = "")
        assertNotNull(errores.conclusion)
    }

    @Test
    fun estadoCerradoConConclusion_esValido() {
        assertTrue(validar(
            estado = EstadoCaso.CERRADO,
            conclusion = "Se capturó al responsable",
            fechaCierre = hoy
        ).esValido)
    }

    @Test
    fun estadoCerradoSinFechaCierre_esInvalido() {
        val errores = validar(estado = EstadoCaso.CERRADO, conclusion = "Fin", fechaCierre = null)
        assertNotNull(errores.fechaCierre)
    }

    @Test
    fun fechaCierreFutura_esInvalida() {
        val errores = validar(estado = EstadoCaso.CERRADO, conclusion = "Fin", fechaCierre = hoy.plusDays(1))
        assertNotNull(errores.fechaCierre)
    }

    @Test
    fun fechaCierreAnteriorAlCaso_esInvalida() {
        val errores = validar(
            fecha = hoy,
            estado = EstadoCaso.CERRADO,
            conclusion = "Fin",
            fechaCierre = hoy.minusDays(3)
        )
        assertNotNull(errores.fechaCierre)
    }

    @Test
    fun casoAbierto_noExigeFechaCierre() {
        assertNull(validar(estado = EstadoCaso.ABIERTO, fechaCierre = null).fechaCierre)
    }

    @Test
    fun cerrarCaso_asignaEstadoConclusionYFechaCierre() {
        val resultado = CasoRules.cerrar(casoAbierto, "  Culpable identificado  ", hoy, hoy).getOrThrow()
        assertEquals(EstadoCaso.CERRADO, resultado.estado)
        assertEquals("Culpable identificado", resultado.conclusion)
        assertEquals(hoy.aMillis(), resultado.fechaCierre)
    }

    @Test
    fun cerrarCasoSinConclusion_falla() {
        assertTrue(CasoRules.cerrar(casoAbierto, " ", hoy, hoy).isFailure)
    }

    @Test
    fun cerrarCasoSinFechaCierre_falla() {
        assertTrue(CasoRules.cerrar(casoAbierto, "Fin", null, hoy).isFailure)
    }

    @Test
    fun cerrarCasoConFechaAnteriorAlCaso_falla() {
        val caso = casoAbierto.copy(fecha = hoy.aMillis())
        assertTrue(CasoRules.cerrar(caso, "Fin", hoy.minusDays(1), hoy).isFailure)
    }

    @Test
    fun cerrarCasoYaCerrado_falla() {
        val cerrado = casoAbierto.copy(estado = EstadoCaso.CERRADO, conclusion = "Fin")
        assertTrue(CasoRules.cerrar(cerrado, "Otra", hoy, hoy).isFailure)
    }

    @Test
    fun reabrirCaso_conservaConclusionYBorraFechaCierre() {
        val cerrado = casoAbierto.copy(
            estado = EstadoCaso.CERRADO,
            conclusion = "Fin",
            fechaCierre = hoy.aMillis()
        )
        val reabierto = CasoRules.reabrir(cerrado)
        assertEquals(EstadoCaso.EN_INVESTIGACION, reabierto.estado)
        assertEquals("Fin", reabierto.conclusion)
        assertNull(reabierto.fechaCierre)
    }

    // ---- Entrevistas ----

    @Test
    fun entrevistaSinDatos_esInvalida() {
        val errores = CasoRules.validarEntrevista("", "")
        assertNotNull(errores.entrevistado)
        assertNotNull(errores.hallazgos)
    }

    @Test
    fun entrevistaCompleta_esValida() {
        assertTrue(CasoRules.validarEntrevista("Testigo 1", "Vio un carro rojo").esValido)
    }
}
