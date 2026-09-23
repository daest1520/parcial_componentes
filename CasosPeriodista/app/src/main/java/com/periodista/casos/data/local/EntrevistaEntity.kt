package com.periodista.casos.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Al eliminar un caso se eliminan en cascada sus entrevistas. */
@Entity(
    tableName = "entrevistas",
    foreignKeys = [
        ForeignKey(
            entity = CasoEntity::class,
            parentColumns = ["id"],
            childColumns = ["casoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("casoId")]
)
data class EntrevistaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val casoId: Long,
    val entrevistado: String,
    val fecha: Long,
    val hallazgos: String,
    val evidencias: String
)
