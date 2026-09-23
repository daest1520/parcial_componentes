package com.periodista.casos.ui.formulario

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.periodista.casos.data.repository.CasoRepository
import com.periodista.casos.domain.model.Caso
import com.periodista.casos.domain.model.EstadoCaso
import com.periodista.casos.domain.rules.CasoRules
import com.periodista.casos.domain.rules.ErroresCaso
import com.periodista.casos.util.aLocalDate
import com.periodista.casos.util.hoyEnMillis
import kotlinx.coroutines.launch

data class CasoFormUiState(
    val id: Long = 0,
    val titulo: String = "",
    val descripcion: String = "",
    val fecha: Long? = hoyEnMillis(),
    val estado: EstadoCaso = EstadoCaso.ABIERTO,
    val conclusion: String = "",
    val fechaCierre: Long? = null,
    val errores: ErroresCaso = ErroresCaso(),
    val esEdicion: Boolean = false,
    val guardando: Boolean = false,
    val guardado: Boolean = false
)

/** Sirve tanto para crear (casoId = 0) como para editar un caso existente. */
class CasoFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositorio: CasoRepository
) : ViewModel() {

    private val casoId: Long = savedStateHandle.get<Long>(ARG_CASO_ID) ?: 0L

    var uiState by mutableStateOf(CasoFormUiState())
        private set

    init {
        if (casoId != 0L) cargarCaso()
    }

    private fun cargarCaso() = viewModelScope.launch {
        repositorio.obtenerCaso(casoId)?.let { caso ->
            uiState = uiState.copy(
                id = caso.id,
                titulo = caso.titulo,
                descripcion = caso.descripcion,
                fecha = caso.fecha,
                estado = caso.estado,
                conclusion = caso.conclusion.orEmpty(),
                fechaCierre = caso.fechaCierre,
                esEdicion = true
            )
        }
    }

    fun onTituloChange(valor: String) {
        uiState = uiState.copy(titulo = valor, errores = uiState.errores.copy(titulo = null))
    }

    fun onDescripcionChange(valor: String) {
        uiState = uiState.copy(descripcion = valor, errores = uiState.errores.copy(descripcion = null))
    }

    fun onFechaChange(valor: Long) {
        uiState = uiState.copy(fecha = valor, errores = uiState.errores.copy(fecha = null))
    }

    fun onEstadoChange(valor: EstadoCaso) {
        uiState = uiState.copy(
            estado = valor,
            // Al marcar el caso como cerrado se propone la fecha de hoy como fecha de cierre
            fechaCierre = if (valor == EstadoCaso.CERRADO) uiState.fechaCierre ?: hoyEnMillis() else uiState.fechaCierre,
            errores = uiState.errores.copy(conclusion = null, fechaCierre = null)
        )
    }

    fun onFechaCierreChange(valor: Long) {
        uiState = uiState.copy(fechaCierre = valor, errores = uiState.errores.copy(fechaCierre = null))
    }

    fun onConclusionChange(valor: String) {
        uiState = uiState.copy(conclusion = valor, errores = uiState.errores.copy(conclusion = null))
    }

    fun guardar() {
        val actual = uiState
        val errores = CasoRules.validarCaso(
            titulo = actual.titulo,
            descripcion = actual.descripcion,
            fecha = actual.fecha?.aLocalDate(),
            estado = actual.estado,
            conclusion = actual.conclusion,
            fechaCierre = actual.fechaCierre?.aLocalDate()
        )
        val fecha = actual.fecha
        if (!errores.esValido || fecha == null) {
            uiState = actual.copy(errores = errores)
            return
        }

        uiState = actual.copy(guardando = true)
        viewModelScope.launch {
            repositorio.guardarCaso(
                Caso(
                    id = actual.id,
                    titulo = actual.titulo.trim(),
                    descripcion = actual.descripcion.trim(),
                    fecha = fecha,
                    estado = actual.estado,
                    conclusion = actual.conclusion.trim().ifBlank { null },
                    // Solo los casos cerrados guardan fecha de cierre
                    fechaCierre = if (actual.estado == EstadoCaso.CERRADO) actual.fechaCierre else null
                )
            )
            uiState = uiState.copy(guardando = false, guardado = true)
        }
    }

    companion object {
        const val ARG_CASO_ID = "casoId"
    }
}
