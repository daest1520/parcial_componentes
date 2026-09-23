package com.periodista.casos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CasoDao {

    @Query("SELECT * FROM casos ORDER BY fecha DESC, id DESC")
    fun observarTodos(): Flow<List<CasoEntity>>

    @Query("SELECT * FROM casos WHERE id = :id")
    fun observarPorId(id: Long): Flow<CasoEntity?>

    @Query("SELECT * FROM casos WHERE id = :id")
    suspend fun obtenerPorId(id: Long): CasoEntity?

    @Insert
    suspend fun insertar(caso: CasoEntity): Long

    @Update
    suspend fun actualizar(caso: CasoEntity)

    @Query("DELETE FROM casos WHERE id = :id")
    suspend fun eliminar(id: Long)
}
