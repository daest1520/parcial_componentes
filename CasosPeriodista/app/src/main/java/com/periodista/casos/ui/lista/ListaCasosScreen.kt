package com.periodista.casos.ui.lista

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.periodista.casos.domain.model.EstadoCaso
import com.periodista.casos.R
import com.periodista.casos.ui.AppViewModelProvider
import com.periodista.casos.ui.components.CasoCard
import com.periodista.casos.ui.components.EstadoVacio
import com.periodista.casos.ui.components.FondoPantalla

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaCasosScreen(
    onCasoClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ListaCasosViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val busqueda by viewModel.busqueda.collectAsStateWithLifecycle()

    FondoPantalla(imagen = R.drawable.fondo_juez, modifier = modifier) {
        Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            OutlinedTextField(
                value = busqueda,
                onValueChange = viewModel::onBusquedaChange,
                placeholder = { Text("Buscar por título o descripción") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (busqueda.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onBusquedaChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpiar búsqueda")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            )

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()).padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = estado.filtroEstado == null,
                    onClick = { viewModel.onFiltroEstadoChange(null) },
                    label = { Text("Todos") }
                )
                EstadoCaso.entries.forEach { opcion ->
                    FilterChip(
                        selected = estado.filtroEstado == opcion,
                        onClick = { viewModel.onFiltroEstadoChange(opcion) },
                        label = { Text(opcion.etiqueta) }
                    )
                }
            }

            Text(
                text = "${estado.casos.size} caso(s)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (estado.casos.isEmpty()) {
                EstadoVacio("No se encontraron casos.")
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(estado.casos, key = { it.id }) { caso ->
                        CasoCard(caso = caso, onClick = { onCasoClick(caso.id) })
                    }
                }
            }
        }
    }
}
