package com.ibrahimethemsen.devicefeaturetracking.sim

import android.content.Context
import android.telephony.TelephonyManager
import com.ibrahimethemsen.devicefeaturetracking.model.CardState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow


class SimCardStatusTracker(context : Context) {
    private val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    fun simCardStatus() = callbackFlow{
        if (telephonyManager.simState == TelephonyManager.SIM_STATE_ABSENT){
            trySend(CardState.NotInserted)
        }else{
            trySend(CardState.Inserted)
        }

        awaitClose()
    }
}

