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
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        takeResultListener.launch(intent)
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

    private fun setupGattServer(app: Application) {
        gattServerCallback = GattServerCallback()

        gattServer = bluetoothManager.openGattServer(
            app,
            gattServerCallback
        ).apply {
            addService(setupGattService())
        }
    }


    private fun setupGattService(): BluetoothGattService {
        val service = BluetoothGattService(SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY)

        val messageCharacteristic = BluetoothGattCharacteristic(
            MESSAGE_UUID,
            BluetoothGattCharacteristic.PROPERTY_WRITE,
            BluetoothGattCharacteristic.PERMISSION_WRITE
        )
        service.addCharacteristic(messageCharacteristic)
        return service
    }

    private class GattServerCallback : BluetoothGattServerCallback() {
        override fun onConnectionStateChange(device: BluetoothDevice, status: Int, newState: Int) {
            super.onConnectionStateChange(device, status, newState)
            val isSuccess = status == BluetoothGatt.GATT_SUCCESS
            val isConnected = newState == BluetoothProfile.STATE_CONNECTED
            if (isSuccess && isConnected) {
                setCurrentChatConnection(device)
            } else {
                _deviceConnection.postValue(DeviceConnectionState.Disconnected)
            }
        }

        override fun onCharacteristicWriteRequest(
            device: BluetoothDevice,
            requestId: Int,
            characteristic: BluetoothGattCharacteristic,
            preparedWrite: Boolean,
            responseNeeded: Boolean,
            offset: Int,
            value: ByteArray?
        ) {
            super.onCharacteristicWriteRequest(
                device,
                requestId,
                characteristic,
                preparedWrite,
                responseNeeded,
                offset,
                value
            )
            if (characteristic.uuid == MESSAGE_UUID) {
                gattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null)
                val message = value?.toString(Charsets.UTF_8)

                message?.let {
                    _messages.postValue(Message.RemoteMessage(it))
                }
            }
        }
    }

    private fun startAdvertisement() {
        advertiser = adapter.bluetoothLeAdvertiser
        if (advertiseCallback == null) {
            advertiseCallback = DeviceAdvertiseCallback()
            advertiser?.startAdvertising(advertiseSettings, advertiseData, advertiseCallback)
        }
    }

    private class DeviceAdvertiseCallback : AdvertiseCallback() {
        override fun onStartFailure(errorCode: Int) {
            super.onStartFailure(errorCode)
            val errorMessage = "failed with error: $errorCode"
            Log.i(TAG,errorMessage)
        }

        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            super.onStartSuccess(settingsInEffect)
            Log.d(TAG, "successfully started")
        }
    }

    private fun buildAdvertiseSettings(): AdvertiseSettings {
        return AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
            .setTimeout(0)
            .build()
    }

    private fun buildAdvertiseData(): AdvertiseData {
        val dataBuilder = AdvertiseData.Builder()
            .addServiceUuid(ParcelUuid(SERVICE_UUID))
            .setIncludeDeviceName(true)
        return dataBuilder.build()
    }

}

private const val TAG = "ChatServerTAG"