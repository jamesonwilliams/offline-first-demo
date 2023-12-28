package org.nosemaj.cra.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.nosemaj.cra.ui.list.UiEvent.InitialLoad
import org.nosemaj.cra.ui.list.UiEvent.RetryClicked
import org.nosemaj.cra.ui.shared.ErrorUi
import org.nosemaj.cra.ui.shared.LoadingUI

@Composable
fun AppointmentListScreen(navigateToAppointment: (appointmentId: String) -> Unit) {
    val viewModel: AppointmentListViewModel = hiltViewModel()

    LaunchedEffect(key1 = true) {
        viewModel.onEvent(InitialLoad)
    }
    val viewState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val currentState = viewState) {
        is UiState.Loading -> LoadingUI()
        is UiState.Content -> AppointmentList(currentState.appointmentSummaries) {
            navigateToAppointment(it.id)
        }
        is UiState.Error -> ErrorUi(currentState.message) {
            viewModel.onEvent(RetryClicked)
        }
    }
}

@Composable
fun AppointmentList(
    appointmentSummaries: List<AppointmentSummary>,
    onAppointmentClicked: (AppointmentSummary) -> Unit
) {
    LazyColumn {
        items(appointmentSummaries) { summary ->
            AppointmentItem(appointmentSummary = summary) {
                onAppointmentClicked(summary)
            }
        }
    }
}

@Composable
fun AppointmentItem(appointmentSummary: AppointmentSummary, onClicked: () -> Unit) {
    Column(
        modifier = Modifier.clickable { onClicked() }
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                color = Color.LightGray,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        with(appointmentSummary) {
            Text(
                style = MaterialTheme.typography.headlineLarge,
                text = patientName
            )
            Text(
                style = MaterialTheme.typography.headlineSmall,
                text = "$startTime • $endTime • $status"
            )
        }
    }
}
