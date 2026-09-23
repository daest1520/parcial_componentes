package com.periodista.casos.ui.formulario

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.periodista.casos.domain.model.EstadoCaso
import com.periodista.casos.R
import com.periodista.casos.ui.AppViewModelProvider
import com.periodista.casos.ui.components.FondoPantalla
import com.periodista.casos.util.aTextoFecha

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CasoFormScreen(
    onVolver: () -> Unit,
    viewModel: CasoFormViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val estado = viewModel.uiState
    // Qué calendario está abierto: fecha del caso o fecha de cierre (null = ninguno)
    var calendarioAbierto by remember { mutableStateOf<CampoFecha?>(null) }

    LaunchedEffect(estado.guardado) {
        if (estado.guardado) onVolver()
    }

    FondoPantalla(imagen = R.drawable.fondo_formulario, opacidadCapa = 0.82f) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(if (estado.esEdicion) "Editar caso" else "Nuevo caso") },
                    navigationIcon = {
                        IconButton(onClick = onVolver) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = estado.titulo,
                    onValueChange = viewModel::onTituloChange,
                    label = { Text("Título *") },
                    isError = estado.errores.titulo != null,
                    supportingText = estado.errores.titulo?.let { { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = estado.descripcion,
                    onValueChange = viewModel::onDescripcionChange,
                    label = { Text("Descripción *") },
                    isError = estado.errores.descripcion != null,
                    supportingText = estado.errores.descripcion?.let { { Text(it) } },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = estado.fecha?.aTextoFecha().orEmpty(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha *") },
                    trailingIcon = {
                        IconButton(onClick = { calendarioAbierto = CampoFecha.CASO }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Elegir fecha")
                        }
                    },
                    isError = estado.errores.fecha != null,
                    supportingText = estado.errores.fecha?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Estado", style = MaterialTheme.typography.titleSmall)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EstadoCaso.entries.forEach { opcion ->
                        FilterChip(
                            selected = estado.estado == opcion,
                            onClick = { viewModel.onEstadoChange(opcion) },
                            label = { Text(opcion.etiqueta) }
                        )
                    }
                }

                if (estado.estado == EstadoCaso.CERRADO) {
                    OutlinedTextField(
                        value = estado.fechaCierre?.aTextoFecha().orEmpty(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Fecha de cierre *") },
                        trailingIcon = {
                            IconButton(onClick = { calendarioAbierto = CampoFecha.CIERRE }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Elegir fecha de cierre")
                            }
                        },
                        isError = estado.errores.fechaCierre != null,
                        supportingText = estado.errores.fechaCierre?.let { { Text(it) } },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = estado.conclusion,
                    onValueChange = viewModel::onConclusionChange,
                    label = {
                        Text(if (estado.estado == EstadoCaso.CERRADO) "Conclusión *" else "Conclusión (opcional)")
                    },
                    isError = estado.errores.conclusion != null,
                    supportingText = estado.errores.conclusion?.let { { Text(it) } },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = viewModel::guardar,
                    enabled = !estado.guardando,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar caso")
                }
            }
        }
    }

    calendarioAbierto?.let { campo ->
        SelectorFecha(
            fechaInicial = if (campo == CampoFecha.CASO) estado.fecha else estado.fechaCierre,
            onSeleccionar = { millis ->
                if (campo == CampoFecha.CASO) viewModel.onFechaChange(millis) else viewModel.onFechaCierreChange(millis)
                calendarioAbierto = null
            },
            onCancelar = { calendarioAbierto = null }
        )
    }
}

private enum class CampoFecha { CASO, CIERRE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorFecha(fechaInicial: Long?, onSeleccionar: (Long) -> Unit, onCancelar: () -> Unit) {
    val estadoCalendario = rememberDatePickerState(initialSelectedDateMillis = fechaInicial)
    DatePickerDialog(
        onDismissRequest = onCancelar,
        confirmButton = {
            TextButton(onClick = {
                estadoCalendario.selectedDateMillis?.let(onSeleccionar) ?: onCancelar()
            }) { Text("Aceptar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    ) {
        DatePicker(state = estadoCalendario)
    }
}
