package com.ibrahimethemsen.devicefeaturetracking.nfc

import android.content.Context
import android.nfc.NfcAdapter

class NfcTracker(context : Context){
    private val nfcAdapter = NfcAdapter.getDefaultAdapter(context)
    fun nfcTracker(){
        if (nfcAdapter == null) {
            // NFC özelliği desteklenmiyor
            println("nfc desteklenmiyor")
        } else {
            if (!nfcAdapter.isEnabled) {
                // NFC özelliği kapalı
                println("nfc kapalı")
            }else{
                println("nfc açık")
            }
        }
    }
}