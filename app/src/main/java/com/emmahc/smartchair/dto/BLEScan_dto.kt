package com.emmahc.smartchair.dto

import android.bluetooth.BluetoothDevice

data class BLEScan_dto(
        val device_name:String? = null,
        val device_address:String? = null,
        val device: BluetoothDevice? = null
)