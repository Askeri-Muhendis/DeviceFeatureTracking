package com.ibrahimethemsen.devicefeaturetracking.network

sealed class NetworkStatus{
    object Unavailable : NetworkStatus()
    object WifiConnected : NetworkStatus()
    object CellularConnected : NetworkStatus()
}
