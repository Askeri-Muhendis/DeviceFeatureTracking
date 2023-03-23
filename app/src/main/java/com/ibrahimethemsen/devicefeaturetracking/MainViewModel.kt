package com.ibrahimethemsen.devicefeaturetracking

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.ibrahimethemsen.devicefeaturetracking.battery.BatteryStatusTracker
import com.ibrahimethemsen.devicefeaturetracking.bluetooth.BluetoothState
import com.ibrahimethemsen.devicefeaturetracking.bluetooth.BluetoothTracker
import com.ibrahimethemsen.devicefeaturetracking.earphones.HeadsetTracker
import com.ibrahimethemsen.devicefeaturetracking.model.CardState
import com.ibrahimethemsen.devicefeaturetracking.model.HeadsetState
import com.ibrahimethemsen.devicefeaturetracking.model.NetworkState
import com.ibrahimethemsen.devicefeaturetracking.model.RingerModeState
import com.ibrahimethemsen.devicefeaturetracking.network.NetworkStatusTracker
import com.ibrahimethemsen.devicefeaturetracking.ringermode.RingerModeTracker
import com.ibrahimethemsen.devicefeaturetracking.sim.SimCardStatusTracker
import com.ibrahimethemsen.devicefeaturetracking.torch.TorchTracker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.N)
class MainViewModel(
    private val networkStatusTracker: NetworkStatusTracker,
    private val simStatusTracker: SimCardStatusTracker,
    private val headsetTracker: HeadsetTracker,
    private val bluetoothTracker: BluetoothTracker,
    private val torchTracker: TorchTracker,
    private val ringerModeTracker: RingerModeTracker,
    private val batteryStatusTracker: BatteryStatusTracker
) : ViewModel() {
    private val _state = MutableLiveData<NetworkState>()
    val state : LiveData<NetworkState> = _state

    private val _simState = MutableLiveData<CardState>()
    val simState : LiveData<CardState> = _simState

    private val _batteryStatus = MutableLiveData<Boolean>()
    val batteryStatus: LiveData<Boolean> = _batteryStatus

    private val _headsetStatus = MutableLiveData<HeadsetState>()
    val headsetStatus: LiveData<HeadsetState> = _headsetStatus

    private val _bluetoothStatus = MutableLiveData<BluetoothState>()
    val bluetoothStatus: LiveData<BluetoothState> = _bluetoothStatus

    private val _torchStatus = MutableLiveData<Boolean>()
    val torchStatus: LiveData<Boolean> = _torchStatus

    private val _ringerModeStatus = MutableLiveData<RingerModeState>()
    val ringerModeStatus: LiveData<RingerModeState> = _ringerModeStatus

    init {
        headsetStatusFlow()
        bluetoothStatusFlow()
        torchStatusFlow()
        ringerModeStatusFlow()
        batteryStatusFlow()
        networkStatusFlow()
        simStatusFlow()
    }
    private fun simStatusFlow(){
        simStatusTracker.simCardStatus().flowToLiveData(_simState)
    }

    private fun networkStatusFlow(){
        networkStatusTracker.networkStatus().flowToLiveData(_state)
    }

    private fun batteryStatusFlow() {
        batteryStatusTracker.isCharging.flowToLiveData(_batteryStatus)
    }
    private fun headsetStatusFlow(){
        headsetTracker.observeHeadsetConnection().flowToLiveData(_headsetStatus)
    }

    private fun bluetoothStatusFlow() {
        bluetoothTracker.observeBluetoothStateChanges().flowToLiveData(_bluetoothStatus)
    }

    private fun torchStatusFlow() {
        torchTracker.torchFlow().flowToLiveData(_torchStatus)
    }

    private fun ringerModeStatusFlow() {
        ringerModeTracker.observeRingerMode().flowToLiveData(_ringerModeStatus)
    }

    private fun <T> Flow<T>.flowToLiveData(liveData: MutableLiveData<T>) {
        viewModelScope.launch {
            collect {
                liveData.postValue(it)
            }
        }
    }
}


