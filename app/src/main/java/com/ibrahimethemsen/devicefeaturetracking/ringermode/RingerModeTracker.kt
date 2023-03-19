package com.ibrahimethemsen.devicefeaturetracking.ringermode

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import com.ibrahimethemsen.devicefeaturetracking.model.RingerModeState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class RingerModeTracker(private val context: Context) {
    fun observeRingerMode() : Flow<RingerModeState> = callbackFlow {
        val ringerModeReceiver = object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == AudioManager.RINGER_MODE_CHANGED_ACTION){
                    val audioManager = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                    when(audioManager.ringerMode){
                        AudioManager.RINGER_MODE_SILENT -> {
                            trySend(RingerModeState.Silent)
                        }
                        AudioManager.RINGER_MODE_VIBRATE ->{
                            trySend(RingerModeState.Vibrate)
                        }
                        AudioManager.RINGER_MODE_NORMAL -> {
                            trySend(RingerModeState.Normal)
                        }
                    }
                }
            }

        }
        //Cihaz mod değiştirdiğinde AudioManager.RINGER_MODE_CHANGED_ACTION yayıyor bunu dinliyoruz
        val filter = IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION)
        context.registerReceiver(ringerModeReceiver,filter)
        awaitClose { context.unregisterReceiver(ringerModeReceiver) }
    }
}