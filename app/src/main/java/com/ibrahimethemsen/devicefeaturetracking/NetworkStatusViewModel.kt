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
import com.ibrahimethemsen.devicefeaturetracking.model.MyState
import com.ibrahimethemsen.devicefeaturetracking.network.NetworkStatusTracker
import com.ibrahimethemsen.devicefeaturetracking.network.map
import com.ibrahimethemsen.devicefeaturetracking.sim.SimCardStatusTracker
import com.ibrahimethemsen.devicefeaturetracking.sim.simMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class NetworkStatusViewModel(
    networkStatusTracker: NetworkStatusTracker,
    simStatusTracker : SimCardStatusTracker,
    private val headsetTracker: HeadsetTracker,
    private val bluetoothTracker: BluetoothTracker
) : ViewModel(){
    @RequiresApi(Build.VERSION_CODES.N)
    val state = networkStatusTracker.networkStatus.map(
        onUnavailable = { MyState.Error},
        onWifi = {MyState.Wifi},
        onCellular = {MyState.Cellular},
        onNetworkSpeed = {MyState.NetworkSpeed(it)}
    ).asLiveData(Dispatchers.IO)

    val simState = simStatusTracker.simCardStatus.simMap(
        onInserted = { CardState.Inserted},
        onNotInserted = {CardState.NotInserted}
    ).asLiveData(Dispatchers.IO)

    private val _batteryStatus = MutableLiveData<Boolean>()
    val batteryStatus : LiveData<Boolean> = _batteryStatus

    private val _headsetStatus = MutableLiveData<HeadsetState>()
    val headsetStatus : LiveData<HeadsetState> = _headsetStatus

    private val _bluetoothStatus = MutableLiveData<BluetoothState>()
    val bluetoothStatus : LiveData<BluetoothState> = _bluetoothStatus

    fun batteryStatusFlow(batteryStatusTracker: BatteryStatusTracker){
        viewModelScope.launch {
            batteryStatusTracker.isCharging.collect{
                _batteryStatus.postValue(it)
            }
        }
    }
    init {
        headsetStatusFlow()
        bluetoothStatusFlow()
    }
    private fun headsetStatusFlow(){
        viewModelScope.launch {
            headsetTracker.observeHeadsetConnection().collect{
                _headsetStatus.postValue(it)
            }
        }
    }
    private fun bluetoothStatusFlow(){
        viewModelScope.launch {
            bluetoothTracker.observeBluetoothStateChanges().collect{
                _bluetoothStatus.postValue(it)
            }
        }
    }

}