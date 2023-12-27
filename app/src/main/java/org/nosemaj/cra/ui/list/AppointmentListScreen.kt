package org.nosemaj.cra.ui.list

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign.Companion.Right
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.nosemaj.cra.ui.list.UiEvent.BottomReached
import org.nosemaj.cra.ui.list.UiEvent.InitialLoad
import org.nosemaj.cra.ui.list.UiEvent.RetryClicked
import org.nosemaj.cra.ui.shared.ErrorUi
import org.nosemaj.cra.ui.shared.LoadingUI
import org.nosemaj.cra.ui.shared.RemoteImage

@Composable
fun AppointmentListScreen(
    navigateToAppointment: (appointmentId: Int) -> Unit,
) {
    val viewModel: AppointmentListViewModel = hiltViewModel()

    LaunchedEffect(key1 = true) {
        viewModel.onEvent(InitialLoad)
    }
    val viewState by viewModel.uiState.collectAsStateWithLifecycle()
    when (viewState.displayState) {
        DisplayState.LOADING -> {
            LoadingUI()
        }

        DisplayState.CONTENT -> {
            AppointmentList(
                appointmentSummaries = viewState.appointmentSummaries,
                onBottomReached = {
                    viewModel.onEvent(BottomReached)
                },
                onAppointmentClicked = {
                    navigateToAppointment(it.id)
                }
            )
        }

        DisplayState.ERROR -> {
            ErrorUi(viewState.errorMessage) {
                viewModel.onEvent(RetryClicked)
            }
        }
    }
}

@Composable
fun AppointmentList(
    appointmentSummaries: List<AppointmentSummary>,
    onBottomReached: () -> Unit,
    onAppointmentClicked: (AppointmentSummary) -> Unit,
) {
    val columnCount = when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> 4
        else -> 2
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(columnCount)
    ) {
        items(appointmentSummaries) { summary ->
            AppointmentItem(appointmentSummary = summary) {
                onAppointmentClicked(summary)
            }
            if (summary == appointmentSummaries.last()) {
                onBottomReached()
            }
        }
    }
}

@Composable
fun AppointmentItem(
    appointmentSummary: AppointmentSummary,
    modifier: Modifier = Modifier,
    onClicked: () -> Unit,
) {
    Box(
        modifier = modifier.clickable { onClicked() }
    ) {
        RemoteImage(
            imageUrl = appointmentSummary.imageUrl,
            contentDescription = appointmentSummary.name
        )
        Text(
            text = appointmentSummary.name,
            textAlign = Right,
            fontSize = 20.sp,
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.66f))
                .fillMaxWidth()
                .align(BottomEnd)
                .padding(8.dp)
        )
    }
}