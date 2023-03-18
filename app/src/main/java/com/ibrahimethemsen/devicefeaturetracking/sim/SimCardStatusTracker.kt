package com.ibrahimethemsen.devicefeaturetracking.sim

import android.content.Context
import android.telephony.TelephonyManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map


class SimCardStatusTracker(context : Context) {
    private val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val simCardStatus = callbackFlow{
        if (telephonyManager.simState == TelephonyManager.SIM_STATE_ABSENT){
            trySend(SimStatus.NoCardInserted)
        }else{
            trySend(SimStatus.CardInserted)
        }

        awaitClose()
    }
}

inline fun <Result> Flow<SimStatus>.simMap(
    crossinline onInserted: suspend () -> Result,
    crossinline onNotInserted: suspend () -> Result
): Flow<Result> = map { status ->
    when (status) {
        SimStatus.CardInserted -> onInserted()
        SimStatus.NoCardInserted -> onNotInserted()
    }
}