package se.max.androidlab1.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import se.max.androidlab1.data.model.Alert
import se.max.androidlab1.ui.theme.AndroidLab1Theme
import se.max.androidlab1.viewmodel.MonitorViewModel

@Composable
fun MonitorScreen(vm: MonitorViewModel = viewModel()) {
    val alertInfo by vm.alertInfo.collectAsState()
    val errorMsg by vm.errorMsg.collectAsState()
    MonitorContent(
        alert = alertInfo,
        error = errorMsg,
        onForceReject = { vm.forceReject() },
        onForceMerge = { vm.forceMerge() },
        onDismissError = { vm.clearError() }
    )
}

@Composable
fun MonitorContent(
    alert: Alert?,
    error: String?,
    onForceReject: () -> Unit,
    onForceMerge: () -> Unit,
    onDismissError: () -> Unit
) {
    val bGColor = if (alert != null) Color.Red else Color.Green
    val textColor = if (alert != null) Color.White else Color.Black
    val headerText = if (alert != null) "DECEPTION DETECTED" else "Safe"
    Box(
        modifier = Modifier
            .background(color = bGColor)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                headerText,
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                textAlign = TextAlign.Center
            )
            if (alert != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Deception Score: ${alert.result.confidenceScore}",
                    color = textColor
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "\"${alert.result.maliciousComment}\"",
                    color = textColor,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))

                val buttonColors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Red,
                    disabledContainerColor = Color.White.copy(0.4f),
                    disabledContentColor = Color.Red.copy(0.6f)
                )

                Button(
                    onClick = onForceReject,
                    colors = buttonColors,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Force Reject")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onForceMerge,
                    colors = buttonColors,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Force Merge")
                }
            }
            if (error != null) {
                Spacer(modifier = Modifier.height(32.dp))
                Text(error, color = textColor, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onDismissError,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) { Text("Dismiss") }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MonitorPreview() {
    AndroidLab1Theme {
        MonitorContent(alert = null, error = null, {}, {}, {})
    }
}