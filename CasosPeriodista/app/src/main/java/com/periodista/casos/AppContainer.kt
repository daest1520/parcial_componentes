package com.periodista.casos

import android.content.Context
import com.periodista.casos.data.local.AppDatabase
import com.periodista.casos.data.repository.CasoRepository
import com.periodista.casos.data.repository.CasoRepositoryRoom

/** Inyección de dependencias manual: crea una sola vez la BD y el repositorio. */
class AppContainer(context: Context) {
    private val baseDatos by lazy { AppDatabase.obtener(context) }

    val casoRepository: CasoRepository by lazy {
        CasoRepositoryRoom(baseDatos.casoDao(), baseDatos.entrevistaDao())
    }
}
