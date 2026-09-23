package com.periodista.casos.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

// Las fechas se manejan en UTC porque así las entrega el DatePicker de Material 3.
private val FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy")

fun Long.aLocalDate(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()

fun Long.aTextoFecha(): String = aLocalDate().format(FORMATO)

fun LocalDate.aMillis(): Long = atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

fun hoyEnMillis(): Long = LocalDate.now().aMillis()
