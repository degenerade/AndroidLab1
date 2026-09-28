package se.max.androidlab1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import se.max.androidlab1.ui.MonitorScreen
import se.max.androidlab1.ui.theme.AndroidLab1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidLab1Theme {
                MonitorScreen()
            }
        }
    }
}
