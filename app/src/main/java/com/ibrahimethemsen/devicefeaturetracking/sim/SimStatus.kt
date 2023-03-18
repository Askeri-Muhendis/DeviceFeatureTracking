package com.ibrahimethemsen.devicefeaturetracking.sim

sealed class SimStatus(){
    object noCardInserted : SimStatus()
    object cardInserted : SimStatus()
}
