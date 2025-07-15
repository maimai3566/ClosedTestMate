package com.rururi.closedtestmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.rururi.closedtestmate.ui.RurustaApp
import com.rururi.closedtestmate.ui.theme.RurustaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RurustaTheme {
                RurustaApp()
            }
        }
    }
}

