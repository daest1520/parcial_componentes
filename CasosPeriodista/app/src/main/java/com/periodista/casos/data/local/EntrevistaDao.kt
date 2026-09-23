package com.periodista.casos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EntrevistaDao {

    @Query("SELECT * FROM entrevistas WHERE casoId = :casoId ORDER BY fecha DESC, id DESC")
    fun observarPorCaso(casoId: Long): Flow<List<EntrevistaEntity>>

    @Insert
    suspend fun insertar(entrevista: EntrevistaEntity): Long

    @Query("DELETE FROM entrevistas WHERE id = :id")
    suspend fun eliminar(id: Long)
}
