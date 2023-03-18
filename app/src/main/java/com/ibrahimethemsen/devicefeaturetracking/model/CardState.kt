package com.ibrahimethemsen.devicefeaturetracking.model

sealed class CardState{
    object Inserted : CardState()
    object NotInserted : CardState()
}