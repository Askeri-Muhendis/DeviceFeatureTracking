package com.ibrahimethemsen.devicefeaturetracking.model

sealed class RingerModeState{
    object Vibrate : RingerModeState()
    object Silent : RingerModeState()
    object Normal : RingerModeState()
}