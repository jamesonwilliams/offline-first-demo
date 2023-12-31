package org.nosemaj.cra.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import org.nosemaj.cra.R

@Composable
fun RecordButton(isRecording: Boolean, onRecordingRequested: (Boolean) -> Unit) {
    RecordingPermissions {
        Box(
            modifier = Modifier
                .padding(32.dp)
                .size(120.dp)
                .background(
                    color = if (isRecording) Color.Red else Color.DarkGray,
                    shape = RoundedCornerShape(32.dp)
                ).clickable {
                    onRecordingRequested(!isRecording)
                }
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_mic_24),
                contentDescription = "Record audio",
                tint = Color.White,
                modifier = Modifier
                    .size(40.dp)
                    .padding(8.dp)
            )
            Text(
                text = if (isRecording) "Recording..." else "Record",
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier.padding(top = 45.dp)
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RecordingPermissions(onGranted: @Composable () -> Unit) {
    val recordPermissionState = rememberPermissionState(android.Manifest.permission.RECORD_AUDIO)

    when {
        recordPermissionState.status.isGranted -> onGranted()
        recordPermissionState.status.shouldShowRationale -> {
            Button(
                onClick = { recordPermissionState.launchPermissionRequest() }
            ) {
                Text("Request Recording Permission")
            }
        }
        else -> {
            Button(
                onClick = { recordPermissionState.launchPermissionRequest() }
            ) {
                Text("Request Recording Permission")
            }
        }
    }
}
