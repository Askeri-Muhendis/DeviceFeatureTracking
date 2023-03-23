package com.ibrahimethemsen.devicefeaturetracking.model

sealed class NetworkState{
    object Wifi : NetworkState()
    object Cellular : NetworkState()
    object Error : NetworkState()
    data class NetworkSpeed(val data : String) : NetworkState()
}