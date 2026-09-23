package com.periodista.casos.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.periodista.casos.CasosApp
import com.periodista.casos.ui.detalle.DetalleCasoViewModel
import com.periodista.casos.ui.formulario.CasoFormViewModel
import com.periodista.casos.ui.lista.ListaCasosViewModel
import com.periodista.casos.ui.resumen.ResumenViewModel

/** Fábrica única para crear todos los ViewModels con sus dependencias. */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer { ResumenViewModel(repositorio()) }
        initializer { ListaCasosViewModel(repositorio()) }
        initializer { CasoFormViewModel(createSavedStateHandle(), repositorio()) }
        initializer { DetalleCasoViewModel(createSavedStateHandle(), repositorio()) }
    }
}

private fun CreationExtras.repositorio() =
    (this[APPLICATION_KEY] as CasosApp).container.casoRepository
