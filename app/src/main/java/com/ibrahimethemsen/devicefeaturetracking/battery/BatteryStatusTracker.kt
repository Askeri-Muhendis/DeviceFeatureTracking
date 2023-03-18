package com.ibrahimethemsen.devicefeaturetracking.battery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class BatteryStatusTracker : BroadcastReceiver() {
    private val _isCharging = MutableStateFlow(false)

    val isCharging: Flow<Boolean>
        get() = _isCharging

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)) {
            BatteryManager.BATTERY_STATUS_CHARGING -> {
                _isCharging.value = true
            }
            BatteryManager.BATTERY_STATUS_FULL -> {
                _isCharging.value = false

            }
            else -> {
                _isCharging.value = false

            }
        }
    }
}