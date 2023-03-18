package com.ibrahimethemsen.devicefeaturetracking.model

sealed class HeadsetState {
    object Earphone : HeadsetState()
    object NotEarphone : HeadsetState()
    object MicrophoneEarphone : HeadsetState()
}