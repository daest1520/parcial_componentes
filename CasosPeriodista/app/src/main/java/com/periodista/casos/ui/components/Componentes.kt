package com.periodista.casos.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.periodista.casos.domain.model.Caso
import com.periodista.casos.domain.model.EstadoCaso
import com.periodista.casos.util.aTextoFecha

/**
 * Pone una imagen de fondo que cubre toda la pantalla, con una capa semitransparente
 * encima para que los textos se sigan leyendo bien.
 */
@Composable
fun FondoPantalla(
    @DrawableRes imagen: Int,
    modifier: Modifier = Modifier,
    opacidadCapa: Float = 0.72f,
    contenido: @Composable BoxScope.() -> Unit
) {
    Box(modifier.fillMaxSize()) {
        Image(
            painter = painterResource(imagen),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = opacidadCapa))
        )
        contenido()
    }
}

@Composable
fun EstadoChip(estado: EstadoCaso, modifier: Modifier = Modifier) {
    val (fondo, texto) = when (estado) {
        EstadoCaso.ABIERTO -> Color(0xFFE3F2FD) to Color(0xFF0D47A1)
        EstadoCaso.EN_INVESTIGACION -> Color(0xFFFFF3E0) to Color(0xFFE65100)
        EstadoCaso.CERRADO -> Color(0xFFE8F5E9) to Color(0xFF1B5E20)
    }
    Surface(color = fondo, shape = RoundedCornerShape(50), modifier = modifier) {
        Text(
            text = estado.etiqueta,
            color = texto,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun CasoCard(caso: Caso, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = caso.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                EstadoChip(caso.estado)
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = caso.fechaCierre
                    ?.let { "${caso.fecha.aTextoFecha()}  •  Cerrado el ${it.aTextoFecha()}" }
                    ?: caso.fecha.aTextoFecha(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = caso.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun EstadoVacio(mensaje: String, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(
            text = mensaje,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
