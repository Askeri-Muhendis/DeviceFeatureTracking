package com.ibrahimethemsen.devicefeaturetracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ibrahimethemsen.devicefeaturetracking.network.NetworkStatusTracker
import com.ibrahimethemsen.devicefeaturetracking.network.map
import kotlinx.coroutines.Dispatchers

sealed class MyState{
    object Wifi : MyState()
    object Cellular : MyState()
    object Error : MyState()
}

class NetworkStatusViewModel(
    networkStatusTracker: NetworkStatusTracker
) : ViewModel(){
    val state = networkStatusTracker.networkStatus.map(
        onUnavailable = {MyState.Error},
        onWifi = {MyState.Wifi},
        onCellular = {MyState.Cellular}
    ).asLiveData(Dispatchers.IO)
}