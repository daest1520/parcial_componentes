package com.periodista.casos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.periodista.casos.ui.navigation.CasosNavHost
import com.periodista.casos.ui.theme.CasosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CasosTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CasosNavHost()
                }
            }
        }
    }
}
