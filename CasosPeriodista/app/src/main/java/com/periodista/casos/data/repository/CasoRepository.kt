package com.periodista.casos.data.repository

import com.periodista.casos.domain.model.Caso
import com.periodista.casos.domain.model.Entrevista
import kotlinx.coroutines.flow.Flow

/** Contrato de acceso a datos. La UI solo conoce esta interfaz, no Room. */
interface CasoRepository {
    fun observarCasos(): Flow<List<Caso>>
    fun observarCaso(id: Long): Flow<Caso?>
    fun observarEntrevistas(casoId: Long): Flow<List<Entrevista>>

    suspend fun obtenerCaso(id: Long): Caso?
    /** Crea el caso si id == 0, o lo actualiza si ya existe. Devuelve el id. */
    suspend fun guardarCaso(caso: Caso): Long
    suspend fun eliminarCaso(id: Long)

    suspend fun guardarEntrevista(entrevista: Entrevista): Long
    suspend fun eliminarEntrevista(id: Long)
}
