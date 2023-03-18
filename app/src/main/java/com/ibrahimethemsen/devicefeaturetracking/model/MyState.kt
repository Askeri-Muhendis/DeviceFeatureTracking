package com.ibrahimethemsen.devicefeaturetracking.model

sealed class MyState{
    object Wifi : MyState()
    object Cellular : MyState()
    object Error : MyState()

    data class NetworkSpeed(val data : String) : MyState()
}