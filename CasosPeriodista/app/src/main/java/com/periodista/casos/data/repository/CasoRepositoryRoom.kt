package com.periodista.casos.data.repository

import com.periodista.casos.data.local.CasoDao
import com.periodista.casos.data.local.EntrevistaDao
import com.periodista.casos.data.local.aDominio
import com.periodista.casos.data.local.aEntity
import com.periodista.casos.domain.model.Caso
import com.periodista.casos.domain.model.Entrevista
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CasoRepositoryRoom(
    private val casoDao: CasoDao,
    private val entrevistaDao: EntrevistaDao
) : CasoRepository {

    override fun observarCasos(): Flow<List<Caso>> =
        casoDao.observarTodos().map { lista -> lista.map { it.aDominio() } }

    override fun observarCaso(id: Long): Flow<Caso?> =
        casoDao.observarPorId(id).map { it?.aDominio() }

    override fun observarEntrevistas(casoId: Long): Flow<List<Entrevista>> =
        entrevistaDao.observarPorCaso(casoId).map { lista -> lista.map { it.aDominio() } }

    override suspend fun obtenerCaso(id: Long): Caso? = casoDao.obtenerPorId(id)?.aDominio()

    override suspend fun guardarCaso(caso: Caso): Long =
        if (caso.id == 0L) {
            casoDao.insertar(caso.aEntity())
        } else {
            casoDao.actualizar(caso.aEntity())
            caso.id
        }

    override suspend fun eliminarCaso(id: Long) = casoDao.eliminar(id)

    override suspend fun guardarEntrevista(entrevista: Entrevista): Long =
        entrevistaDao.insertar(entrevista.aEntity())

    override suspend fun eliminarEntrevista(id: Long) = entrevistaDao.eliminar(id)
}
