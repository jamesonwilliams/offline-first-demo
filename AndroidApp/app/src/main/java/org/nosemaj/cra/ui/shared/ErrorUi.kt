package org.nosemaj.cra.ui.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ErrorUi(message: String?, onRetryClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val errorMessage = if (message != null) "Error: $message" else "Error"
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.padding(10.dp))
        Button(
            onClick = { onRetryClicked() },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Retry?",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
