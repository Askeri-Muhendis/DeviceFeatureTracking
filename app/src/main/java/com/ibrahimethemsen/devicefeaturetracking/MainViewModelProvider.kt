package com.ibrahimethemsen.devicefeaturetracking

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ibrahimethemsen.devicefeaturetracking.bluetooth.BluetoothTracker
import com.ibrahimethemsen.devicefeaturetracking.earphones.HeadsetTracker
import com.ibrahimethemsen.devicefeaturetracking.network.NetworkStatusTracker
import com.ibrahimethemsen.devicefeaturetracking.ringermode.RingerModeTracker
import com.ibrahimethemsen.devicefeaturetracking.sim.SimCardStatusTracker
import com.ibrahimethemsen.devicefeaturetracking.torch.TorchTracker

class MainViewModelProvider(private val context : Context) : ViewModelProvider.Factory {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val networkStatusTracker = NetworkStatusTracker(context)
            val simStatusTracker = SimCardStatusTracker(context)
            val headsetTracker = HeadsetTracker(context)
            val bluetoothTracker = BluetoothTracker(context)
            val torchTracker = TorchTracker(context)
            val ringerMode = RingerModeTracker(context)
            return NetworkStatusViewModel(networkStatusTracker, simStatusTracker,headsetTracker,bluetoothTracker,torchTracker,ringerMode) as T
        }
}