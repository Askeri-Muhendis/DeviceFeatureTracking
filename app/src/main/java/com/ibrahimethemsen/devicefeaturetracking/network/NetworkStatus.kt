package com.ibrahimethemsen.devicefeaturetracking.network

sealed class NetworkStatus{
    data class NetworkSpeed(val data : String) : NetworkStatus()
    object Unavailable : NetworkStatus()
    object WifiConnected : NetworkStatus()
    object CellularConnected : NetworkStatus()
}
