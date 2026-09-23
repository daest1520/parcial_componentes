package com.periodista.casos.domain.rules

import com.periodista.casos.domain.model.Caso
import com.periodista.casos.domain.model.EstadoCaso
import com.periodista.casos.util.aLocalDate
import com.periodista.casos.util.aMillis
import java.time.LocalDate

/** Errores de validación del formulario de caso (null = campo correcto). */
data class ErroresCaso(
    val titulo: String? = null,
    val descripcion: String? = null,
    val fecha: String? = null,
    val conclusion: String? = null,
    val fechaCierre: String? = null
) {
    val esValido: Boolean
        get() = listOf(titulo, descripcion, fecha, conclusion, fechaCierre).all { it == null }
}

/** Errores de validación de una entrevista. */
data class ErroresEntrevista(
    val entrevistado: String? = null,
    val hallazgos: String? = null
) {
    val esValido: Boolean
        get() = entrevistado == null && hallazgos == null
}

/**
 * Reglas de negocio de los casos. No depende de Android ni de la base de datos,
 * por eso se puede probar con pruebas unitarias simples.
 */
object CasoRules {

    const val MIN_LONGITUD_TITULO = 3

    fun validarCaso(
        titulo: String,
        descripcion: String,
        fecha: LocalDate?,
        estado: EstadoCaso,
        conclusion: String,
        fechaCierre: LocalDate? = null,
        hoy: LocalDate = LocalDate.now()
    ): ErroresCaso = ErroresCaso(
        titulo = when {
            titulo.isBlank() -> "El título es obligatorio"
            titulo.trim().length < MIN_LONGITUD_TITULO ->
                "El título debe tener al menos $MIN_LONGITUD_TITULO caracteres"
            else -> null
        },
        descripcion = if (descripcion.isBlank()) "La descripción es obligatoria" else null,
        fecha = when {
            fecha == null -> "Seleccione una fecha"
            fecha.isAfter(hoy) -> "La fecha no puede ser futura"
            else -> null
        },
        conclusion = if (estado == EstadoCaso.CERRADO && conclusion.isBlank()) {
            "Para cerrar el caso debe registrar una conclusión"
        } else null,
        fechaCierre = if (estado == EstadoCaso.CERRADO) validarFechaCierre(fechaCierre, fecha, hoy) else null
    )

    /** La fecha de cierre es obligatoria, no puede ser futura ni anterior a la fecha del caso. */
    fun validarFechaCierre(fechaCierre: LocalDate?, fechaCaso: LocalDate?, hoy: LocalDate): String? = when {
        fechaCierre == null -> "Indique la fecha de cierre"
        fechaCierre.isAfter(hoy) -> "La fecha de cierre no puede ser futura"
        fechaCaso != null && fechaCierre.isBefore(fechaCaso) ->
            "La fecha de cierre no puede ser anterior a la fecha del caso"
        else -> null
    }

    fun validarEntrevista(entrevistado: String, hallazgos: String): ErroresEntrevista =
        ErroresEntrevista(
            entrevistado = if (entrevistado.isBlank()) "Indique a quién entrevistó" else null,
            hallazgos = if (hallazgos.isBlank()) "Registre al menos un hallazgo" else null
        )

    /** Cierra el caso con su conclusión y fecha de cierre. */
    fun cerrar(
        caso: Caso,
        conclusion: String,
        fechaCierre: LocalDate?,
        hoy: LocalDate = LocalDate.now()
    ): Result<Caso> {
        if (caso.estado == EstadoCaso.CERRADO) {
            return Result.failure(IllegalStateException("El caso ya está cerrado"))
        }
        if (conclusion.isBlank()) {
            return Result.failure(IllegalArgumentException("La conclusión es obligatoria para cerrar el caso"))
        }
        validarFechaCierre(fechaCierre, caso.fecha.aLocalDate(), hoy)?.let { error ->
            return Result.failure(IllegalArgumentException(error))
        }
        return Result.success(
            caso.copy(
                estado = EstadoCaso.CERRADO,
                conclusion = conclusion.trim(),
                fechaCierre = fechaCierre!!.aMillis()
            )
        )
    }

    /** Reabre un caso cerrado: conserva la conclusión previa pero borra la fecha de cierre. */
    fun reabrir(caso: Caso): Caso =
        if (caso.estado == EstadoCaso.CERRADO) {
            caso.copy(estado = EstadoCaso.EN_INVESTIGACION, fechaCierre = null)
        } else caso

    /** Búsqueda por texto (título o descripción, sin distinguir mayúsculas) y filtro por estado. */
    fun filtrar(casos: List<Caso>, texto: String, estado: EstadoCaso?): List<Caso> {
        val consulta = texto.trim()
        return casos.filter { caso ->
            val coincideEstado = estado == null || caso.estado == estado
            val coincideTexto = consulta.isEmpty() ||
                caso.titulo.contains(consulta, ignoreCase = true) ||
                caso.descripcion.contains(consulta, ignoreCase = true)
            coincideEstado && coincideTexto
        }
    }
}
