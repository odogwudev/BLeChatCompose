package com.example.blechatcompose

import androidx.compose.runtime.Composable

@Composable
fun DeviceScan(deviceScanViewState: DeviceScanViewState, onDeviceSelected: () -> Unit) {
    when (deviceScanViewState) {
        is DeviceScanViewState.ActiveScan -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        text = "Scanning for devices",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Light
                    )
                }
            }

        }
        is DeviceScanViewState.ScanResults -> {
            ShowDevices(scanResults = deviceScanViewState.scanResults, onClick = {
                Log.i(TAG, "Device Selected ${it!!.name ?: ""}")
                ChatServer.setCurrentChatConnection(device = it!!)
                ChatServer.currentDevice = it
                onDeviceSelected()
            })
        }
        is DeviceScanViewState.Error -> {
            Text(text = deviceScanViewState.message)
        }
        else -> {
            Text(text = "Nothing")
        }
    }
}
view raw