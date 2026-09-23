package com.periodista.casos.ui.resumen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.periodista.casos.data.repository.CasoRepository
import com.periodista.casos.domain.model.Caso
import com.periodista.casos.domain.rules.ResumenCasos
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ResumenUiState(
    val resumen: ResumenCasos = ResumenCasos(),
    val recientes: List<Caso> = emptyList()
)

class ResumenViewModel(repositorio: CasoRepository) : ViewModel() {

    val uiState: StateFlow<ResumenUiState> = repositorio.observarCasos()
        .map { casos -> ResumenUiState(ResumenCasos.de(casos), casos.take(CASOS_RECIENTES)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ResumenUiState())

    private companion object {
        const val CASOS_RECIENTES = 3
    }
}
