package com.periodista.casos.data.local

import androidx.room.TypeConverter
import com.periodista.casos.domain.model.EstadoCaso

/** Room no sabe guardar enums: se almacenan como texto (su nombre). */
class Converters {
    @TypeConverter
    fun desdeEstado(estado: EstadoCaso): String = estado.name

    @TypeConverter
    fun aEstado(valor: String): EstadoCaso = EstadoCaso.valueOf(valor)
}
