package com.periodista.casos.ui.detalle

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.periodista.casos.data.repository.CasoRepository
import com.periodista.casos.domain.model.Caso
import com.periodista.casos.domain.model.Entrevista
import com.periodista.casos.domain.rules.CasoRules
import com.periodista.casos.domain.rules.ErroresEntrevista
import com.periodista.casos.util.aLocalDate
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DetalleUiState(
    val cargando: Boolean = true,
    val caso: Caso? = null,
    val entrevistas: List<Entrevista> = emptyList()
)

class DetalleCasoViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositorio: CasoRepository
) : ViewModel() {

    private val casoId: Long = checkNotNull(savedStateHandle.get<Long>(ARG_CASO_ID))

    val uiState: StateFlow<DetalleUiState> =
        combine(
            repositorio.observarCaso(casoId),
            repositorio.observarEntrevistas(casoId)
        ) { caso, entrevistas ->
            DetalleUiState(cargando = false, caso = caso, entrevistas = entrevistas)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DetalleUiState())

    fun eliminarCaso(alTerminar: () -> Unit) {
        viewModelScope.launch {
            repositorio.eliminarCaso(casoId)
            alTerminar()
        }
    }

    /** Devuelve los errores; si no hay errores la entrevista se guarda. */
    fun agregarEntrevista(entrevistado: String, hallazgos: String, evidencias: String): ErroresEntrevista {
        val errores = CasoRules.validarEntrevista(entrevistado, hallazgos)
        if (errores.esValido) {
            viewModelScope.launch {
                repositorio.guardarEntrevista(
                    Entrevista(
                        casoId = casoId,
                        entrevistado = entrevistado.trim(),
                        fecha = System.currentTimeMillis(),
                        hallazgos = hallazgos.trim(),
                        evidencias = evidencias.trim()
                    )
                )
            }
        }
        return errores
    }

    fun eliminarEntrevista(id: Long) {
        viewModelScope.launch { repositorio.eliminarEntrevista(id) }
    }

    /** Devuelve un mensaje de error, o null si el caso se cerró correctamente. */
    fun cerrarCaso(conclusion: String, fechaCierre: Long?): String? {
        val caso = uiState.value.caso ?: return "El caso no existe"
        return CasoRules.cerrar(caso, conclusion, fechaCierre?.aLocalDate()).fold(
            onSuccess = { cerrado ->
                viewModelScope.launch { repositorio.guardarCaso(cerrado) }
                null
            },
            onFailure = { it.message }
        )
    }

    fun reabrirCaso() {
        val caso = uiState.value.caso ?: return
        viewModelScope.launch { repositorio.guardarCaso(CasoRules.reabrir(caso)) }
    }

    companion object {
        const val ARG_CASO_ID = "casoId"
    }
}
