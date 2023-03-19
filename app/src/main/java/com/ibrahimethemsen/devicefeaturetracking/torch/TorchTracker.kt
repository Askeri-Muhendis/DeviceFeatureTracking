package com.ibrahimethemsen.devicefeaturetracking.torch

import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

@RequiresApi(Build.VERSION_CODES.M)
class TorchTracker(private val context: Context) {
    fun torchFlow() = callbackFlow {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val torchCallback = object : CameraManager.TorchCallback() {
            override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                //enabled -> flash durumunu veriyor
                trySend(enabled)
            }
        }
        cameraManager.registerTorchCallback(torchCallback, null)
        awaitClose()
    }
}