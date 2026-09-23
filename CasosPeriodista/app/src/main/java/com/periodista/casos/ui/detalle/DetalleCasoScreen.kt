package com.periodista.casos.ui.detalle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.periodista.casos.domain.model.Caso
import com.periodista.casos.domain.model.Entrevista
import com.periodista.casos.domain.model.EstadoCaso
import com.periodista.casos.domain.rules.ErroresEntrevista
import com.periodista.casos.R
import com.periodista.casos.ui.AppViewModelProvider
import com.periodista.casos.ui.components.FondoPantalla
import com.periodista.casos.ui.formulario.SelectorFecha
import com.periodista.casos.util.hoyEnMillis
import com.periodista.casos.ui.components.EstadoChip
import com.periodista.casos.ui.components.EstadoVacio
import com.periodista.casos.util.aTextoFecha
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleCasoScreen(
    onVolver: () -> Unit,
    onEditar: (Long) -> Unit,
    viewModel: DetalleCasoViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val caso = estado.caso

    var confirmarEliminar by remember { mutableStateOf(false) }
    var mostrarCerrar by remember { mutableStateOf(false) }
    var mostrarEntrevista by remember { mutableStateOf(false) }

    FondoPantalla(imagen = R.drawable.fondo_juez) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Detalle del caso") },
                    navigationIcon = {
                        IconButton(onClick = onVolver) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    },
                    actions = {
                        if (caso != null) {
                            IconButton(onClick = { onEditar(caso.id) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar caso")
                            }
                            IconButton(onClick = { confirmarEliminar = true }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar caso")
                            }
                        }
                    }
                )
            }
        ) { padding ->
            val modificador = Modifier.padding(padding).fillMaxSize()
            when {
                estado.cargando -> Box(modificador, contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                caso == null -> EstadoVacio("El caso no existe o fue eliminado.", modificador)

                else -> LazyColumn(
                    modifier = modificador,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { InformacionCaso(caso) }
                    item {
                        SeccionCierre(
                            caso = caso,
                            onCerrar = { mostrarCerrar = true },
                            onReabrir = viewModel::reabrirCaso
                        )
                    }
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Entrevistas (${estado.entrevistas.size})",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = { mostrarEntrevista = true }) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(Modifier.width(4.dp))
                                Text("Agregar")
                            }
                        }
                    }
                    if (estado.entrevistas.isEmpty()) {
                        item { EstadoVacio("Aún no hay entrevistas registradas en este caso.") }
                    } else {
                        items(estado.entrevistas, key = { it.id }) { entrevista ->
                            EntrevistaCard(
                                entrevista = entrevista,
                                onEliminar = { viewModel.eliminarEntrevista(entrevista.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (confirmarEliminar) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            title = { Text("Eliminar caso") },
            text = { Text("Se eliminará el caso y todas sus entrevistas. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarEliminar = false
                    viewModel.eliminarCaso(alTerminar = onVolver)
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarEliminar = false }) { Text("Cancelar") }
            }
        )
    }

    if (mostrarCerrar) {
        DialogoCerrarCaso(
            onConfirmar = { conclusion, fechaCierre ->
                viewModel.cerrarCaso(conclusion, fechaCierre).also { error -> if (error == null) mostrarCerrar = false }
            },
            onCancelar = { mostrarCerrar = false }
        )
    }

    if (mostrarEntrevista) {
        DialogoEntrevista(
            onGuardar = { entrevistado, hallazgos, evidencias ->
                viewModel.agregarEntrevista(entrevistado, hallazgos, evidencias)
                    .also { errores -> if (errores.esValido) mostrarEntrevista = false }
            },
            onCancelar = { mostrarEntrevista = false }
        )
    }
}

@Composable
private fun InformacionCaso(caso: Caso) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(caso.titulo, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                EstadoChip(caso.estado)
                Spacer(Modifier.width(12.dp))
                Text(caso.fecha.aTextoFecha(), style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(12.dp))
            Text("Descripción", style = MaterialTheme.typography.labelLarge)
            Text(caso.descripcion, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun SeccionCierre(caso: Caso, onCerrar: () -> Unit, onReabrir: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Conclusión", style = MaterialTheme.typography.titleMedium)
            caso.fechaCierre?.let {
                Text(
                    "Fecha de cierre: ${it.aTextoFecha()}",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                caso.conclusion ?: "Sin conclusión registrada.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(12.dp))
            if (caso.estado == EstadoCaso.CERRADO) {
                OutlinedButton(onClick = onReabrir) { Text("Reabrir caso") }
            } else {
                Button(onClick = onCerrar) { Text("Cerrar caso") }
            }
        }
    }
}

@Composable
private fun EntrevistaCard(entrevista: Entrevista, onEliminar: () -> Unit) {
    val formato = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(entrevista.entrevistado, style = MaterialTheme.typography.titleSmall)
                    Text(
                        formato.format(Date(entrevista.fecha)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onEliminar) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar entrevista")
                }
            }
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            Text("Hallazgos", style = MaterialTheme.typography.labelLarge)
            Text(entrevista.hallazgos, style = MaterialTheme.typography.bodyMedium)
            if (entrevista.evidencias.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text("Evidencias", style = MaterialTheme.typography.labelLarge)
                Text(entrevista.evidencias, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun DialogoCerrarCaso(onConfirmar: (String, Long?) -> String?, onCancelar: () -> Unit) {
    var conclusion by remember { mutableStateOf("") }
    var fechaCierre by remember { mutableStateOf<Long?>(hoyEnMillis()) }
    var mostrarCalendario by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Cerrar caso") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = fechaCierre?.aTextoFecha().orEmpty(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha de cierre *") },
                    trailingIcon = {
                        IconButton(onClick = { mostrarCalendario = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Elegir fecha de cierre")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = conclusion,
                    onValueChange = { conclusion = it; error = null },
                    label = { Text("Conclusión *") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { error = onConfirmar(conclusion, fechaCierre) }) { Text("Cerrar caso") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )

    if (mostrarCalendario) {
        SelectorFecha(
            fechaInicial = fechaCierre,
            onSeleccionar = { fechaCierre = it; error = null; mostrarCalendario = false },
            onCancelar = { mostrarCalendario = false }
        )
    }
}

@Composable
private fun DialogoEntrevista(
    onGuardar: (String, String, String) -> ErroresEntrevista,
    onCancelar: () -> Unit
) {
    var entrevistado by remember { mutableStateOf("") }
    var hallazgos by remember { mutableStateOf("") }
    var evidencias by remember { mutableStateOf("") }
    var errores by remember { mutableStateOf(ErroresEntrevista()) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Nueva entrevista") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = entrevistado,
                    onValueChange = { entrevistado = it; errores = errores.copy(entrevistado = null) },
                    label = { Text("Entrevistado *") },
                    isError = errores.entrevistado != null,
                    supportingText = errores.entrevistado?.let { { Text(it) } },
                    singleLine = true
                )
                OutlinedTextField(
                    value = hallazgos,
                    onValueChange = { hallazgos = it; errores = errores.copy(hallazgos = null) },
                    label = { Text("Principales hallazgos *") },
                    isError = errores.hallazgos != null,
                    supportingText = errores.hallazgos?.let { { Text(it) } },
                    minLines = 3
                )
                OutlinedTextField(
                    value = evidencias,
                    onValueChange = { evidencias = it },
                    label = { Text("Evidencias (opcional)") },
                    minLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { errores = onGuardar(entrevistado, hallazgos, evidencias) }) {
                Text("Guardar")
            }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}
