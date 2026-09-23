package com.periodista.casos.data.local

import com.periodista.casos.domain.model.Caso
import com.periodista.casos.domain.model.Entrevista

// Conversión entre el modelo de base de datos y el modelo de dominio.

fun CasoEntity.aDominio() = Caso(id, titulo, descripcion, fecha, estado, conclusion, fechaCierre)

fun Caso.aEntity() = CasoEntity(id, titulo, descripcion, fecha, estado, conclusion, fechaCierre)

fun EntrevistaEntity.aDominio() = Entrevista(id, casoId, entrevistado, fecha, hallazgos, evidencias)

fun Entrevista.aEntity() = EntrevistaEntity(id, casoId, entrevistado, fecha, hallazgos, evidencias)
