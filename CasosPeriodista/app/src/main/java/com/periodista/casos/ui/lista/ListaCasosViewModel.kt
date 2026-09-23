package com.periodista.casos.ui.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.periodista.casos.data.repository.CasoRepository
import com.periodista.casos.domain.model.Caso
import com.periodista.casos.domain.model.EstadoCaso
import com.periodista.casos.domain.rules.CasoRules
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ListaCasosUiState(
    val filtroEstado: EstadoCaso? = null,
    val casos: List<Caso> = emptyList()
)

class ListaCasosViewModel(repositorio: CasoRepository) : ViewModel() {

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    private val filtroEstado = MutableStateFlow<EstadoCaso?>(null)

    val uiState: StateFlow<ListaCasosUiState> =
        combine(repositorio.observarCasos(), _busqueda, filtroEstado) { casos, texto, estado ->
            ListaCasosUiState(estado, CasoRules.filtrar(casos, texto, estado))
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ListaCasosUiState())

    fun onBusquedaChange(texto: String) {
        _busqueda.value = texto
    }

    fun onFiltroEstadoChange(estado: EstadoCaso?) {
        filtroEstado.value = estado
    }
}
