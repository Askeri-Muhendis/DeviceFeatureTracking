package com.ibrahimethemsen.devicefeaturetracking.sim

sealed class SimStatus(){
    object NoCardInserted : SimStatus()
    object CardInserted : SimStatus()
}
