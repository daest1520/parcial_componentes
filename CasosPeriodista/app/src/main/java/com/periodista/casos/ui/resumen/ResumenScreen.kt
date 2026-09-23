package com.periodista.casos.ui.resumen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.periodista.casos.R
import com.periodista.casos.ui.AppViewModelProvider
import com.periodista.casos.ui.components.CasoCard
import com.periodista.casos.ui.components.EstadoVacio
import com.periodista.casos.ui.components.FondoPantalla

@Composable
fun ResumenScreen(
    onCasoClick: (Long) -> Unit,
    onVerTodos: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ResumenViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val resumen = estado.resumen

    FondoPantalla(imagen = R.drawable.fondo_periodista, modifier = modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Resumen general", style = MaterialTheme.typography.headlineSmall)
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Indicador("Total de casos", resumen.total, Modifier.weight(1f))
                    Indicador("Abiertos", resumen.abiertos, Modifier.weight(1f))
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Indicador("En investigación", resumen.enInvestigacion, Modifier.weight(1f))
                    Indicador("Cerrados", resumen.cerrados, Modifier.weight(1f))
                }
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Casos recientes",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = onVerTodos) { Text("Ver todos") }
                }
            }
            if (estado.recientes.isEmpty()) {
                item { EstadoVacio("Aún no hay casos registrados.\nUsa el botón + para crear el primero.") }
            } else {
                items(estado.recientes, key = { it.id }) { caso ->
                    CasoCard(caso = caso, onClick = { onCasoClick(caso.id) })
                }
            }
        }
    }
}

@Composable
private fun Indicador(titulo: String, valor: Int, modifier: Modifier = Modifier) {
    ElevatedCard(modifier = modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = valor.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(text = titulo, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
