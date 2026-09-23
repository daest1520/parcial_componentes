package com.periodista.casos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.periodista.casos.domain.model.EstadoCaso

@Entity(tableName = "casos")
data class CasoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val descripcion: String,
    val fecha: Long,
    val estado: EstadoCaso,
    val conclusion: String?,
    val fechaCierre: Long? = null
)
