package com.example.blechatcompose

import android.app.Application
import android.bluetooth.*
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.content.Intent
import android.os.ParcelUuid
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object ChatServer {

    private var app: Application? = null
    private lateinit var bluetoothManager: BluetoothManager

    private var adapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()


    fun startServer(app: Application, activity: ComponentActivity) {
        bluetoothManager = app.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        adapter = bluetoothManager.adapter
        if (!adapter.isEnabled) {
            _requestEnableBluetooth.value = true

            val takeResultListener =
                activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

                    if (result.resultCode == -1) {
                        Toast.makeText(activity, "Bluetooth ON", Toast.LENGTH_LONG).show()
                        setupGattServer(app)
                        startAdvertisement()
                    } else {
                        Toast.makeText(activity, "Bluetooth OFF", Toast.LENGTH_LONG).show()
                    }
                }

            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            takeResultListener.launch(intent)

        } else {
            _requestEnableBluetooth.value = false
            setupGattServer(app)
            startAdvertisement()
        }
    }

}

private const val TAG = "ChatServerTAG"