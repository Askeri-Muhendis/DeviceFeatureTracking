package com.ibrahimethemsen.devicefeaturetracking.earphones

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.ibrahimethemsen.devicefeaturetracking.model.HeadsetState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class HeadsetTracker(private val context: Context) {

    fun observeHeadsetConnection(): Flow<HeadsetState> = callbackFlow {
        val headsetReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == Intent.ACTION_HEADSET_PLUG) {
                    val state = intent.getIntExtra("state", -1) //HEADSET_STATE_PLUGGED-HEADSET_STATE_UNPLUGGED
                    val hasMicrophone = intent.getIntExtra("microphone", -1) //DEVICE_OUT_WIRED_HEADSET_MIC-DEVICE_OUT_WIRED_HEADSET
                    if (state == 1 && hasMicrophone == 1) {
                        trySend(HeadsetState.MicrophoneEarphone) // Kulaklık takılı ve mikrofon desteği var
                    } else if (state == 1) {
                        trySend(HeadsetState.Earphone) // Kulaklık takılı ama mikrofon desteği yok
                    }else{
                        //kulaklık takılı değil
                        trySend(HeadsetState.NotEarphone)
                    }
                }
            }
        }
        val filter = IntentFilter(Intent.ACTION_HEADSET_PLUG)
        context.registerReceiver(headsetReceiver, filter)

        awaitClose { context.unregisterReceiver(headsetReceiver) }
    }
}