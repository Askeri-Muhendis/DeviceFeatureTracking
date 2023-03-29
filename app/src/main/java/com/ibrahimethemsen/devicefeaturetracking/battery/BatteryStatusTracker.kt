package com.ibrahimethemsen.devicefeaturetracking.battery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class BatteryStatusTracker(private val context : Context){
    fun observeBattery(): Flow<Boolean> = callbackFlow {
        val batteryReceiver = object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)) {
                    BatteryManager.BATTERY_STATUS_CHARGING -> {
                       trySend(true)
                    }
                    BatteryManager.BATTERY_STATUS_FULL -> {
                        trySend(false)
                    }
                    else -> {
                        trySend(false)
                    }
                }
            }
        }
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(batteryReceiver, filter)

        awaitClose{context.unregisterReceiver(batteryReceiver)}
    }
}