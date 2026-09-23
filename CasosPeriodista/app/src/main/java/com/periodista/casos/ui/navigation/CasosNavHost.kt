package com.periodista.casos.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.periodista.casos.ui.detalle.DetalleCasoScreen
import com.periodista.casos.ui.formulario.CasoFormScreen
import com.periodista.casos.ui.lista.ListaCasosScreen
import com.periodista.casos.ui.resumen.ResumenScreen

object Rutas {
    const val RESUMEN = "resumen"
    const val LISTA = "casos"
    const val FORMULARIO = "formulario?casoId={casoId}"
    const val DETALLE = "detalle/{casoId}"

    fun formulario(casoId: Long = 0L) = "formulario?casoId=$casoId"
    fun detalle(casoId: Long) = "detalle/$casoId"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CasosNavHost(navController: NavHostController = rememberNavController()) {
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route
    val esPantallaPrincipal = rutaActual == Rutas.RESUMEN || rutaActual == Rutas.LISTA

    Scaffold(
        topBar = {
            if (esPantallaPrincipal) TopAppBar(title = { Text("Casos del periodista") })
        },
        bottomBar = {
            if (esPantallaPrincipal) {
                NavigationBar {
                    NavigationBarItem(
                        selected = rutaActual == Rutas.RESUMEN,
                        onClick = { navController.irAPrincipal(Rutas.RESUMEN) },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Resumen") }
                    )
                    NavigationBarItem(
                        selected = rutaActual == Rutas.LISTA,
                        onClick = { navController.irAPrincipal(Rutas.LISTA) },
                        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                        label = { Text("Casos") }
                    )
                }
            }
        },
        floatingActionButton = {
            if (esPantallaPrincipal) {
                FloatingActionButton(onClick = { navController.navigate(Rutas.formulario()) }) {
                    Icon(Icons.Default.Add, contentDescription = "Nuevo caso")
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Rutas.RESUMEN,
            modifier = Modifier.padding(padding)
        ) {
            composable(Rutas.RESUMEN) {
                ResumenScreen(
                    onCasoClick = { navController.navigate(Rutas.detalle(it)) },
                    onVerTodos = { navController.irAPrincipal(Rutas.LISTA) }
                )
            }
            composable(Rutas.LISTA) {
                ListaCasosScreen(onCasoClick = { navController.navigate(Rutas.detalle(it)) })
            }
            composable(
                route = Rutas.FORMULARIO,
                arguments = listOf(navArgument("casoId") {
                    type = NavType.LongType
                    defaultValue = 0L
                })
            ) {
                CasoFormScreen(onVolver = { navController.popBackStack() })
            }
            composable(
                route = Rutas.DETALLE,
                arguments = listOf(navArgument("casoId") { type = NavType.LongType })
            ) {
                DetalleCasoScreen(
                    onVolver = { navController.popBackStack() },
                    onEditar = { navController.navigate(Rutas.formulario(it)) }
                )
            }
        }
    }
}

/** Navega entre pestañas sin apilar pantallas repetidas. */
private fun NavHostController.irAPrincipal(ruta: String) {
    navigate(ruta) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
