package com.example.blechatcompose

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.blechatcompose.ui.theme.BLeChatComposeTheme
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BLeChatComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    color = MaterialTheme.colors.background
                ) {
                    val deviceScanningState by viewModel.viewState.observeAsState()

                    val deviceConnectionState by ChatServer.deviceConnection.observeAsState()

                    var isChatOpen by remember {
                        mutableStateOf(false)
                    }

                    Box(
                        contentAlignment = Alignment.TopCenter,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (deviceScanningState != null && !isChatOpen || deviceConnectionState == DeviceConnectionState.Disconnected) {
                            Column {
                                Text(
                                    text = "Choose a device to chat with:",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                DeviceScanCompose.DeviceScan(deviceScanViewState = deviceScanningState!!) {
                                    isChatOpen = true
                                }
                            }

                        } else {
                            ChatCompose.Chats()
                        }
                    }
                }
            }
        }
    }


    override fun onStart() {
        super.onStart()
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    ChatServer.startServer(application, activity = this@MainActivity)
                    viewModel.startScan()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?
                ) {

                }
            })
            .check()
    }
}