package com.rururi.closedtestmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.rururi.closedtestmate.core.ui.theme.RurustaTheme
import com.rururi.closedtestmate.ui.RurustaApp
import dagger.hilt.android.AndroidEntryPoint

//val Context.isDebuggable: Boolean
//    get() = (applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RurustaTheme {
                RurustaApp(onExit = { finish() })
            }

        }
    }
}
