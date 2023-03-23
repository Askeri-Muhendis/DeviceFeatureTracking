package com.ibrahimethemsen.devicefeaturetracking.network

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.ibrahimethemsen.devicefeaturetracking.R
import com.ibrahimethemsen.devicefeaturetracking.model.NetworkState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow


class NetworkStatusTracker(private val context: Context) {

    //System Service
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    @RequiresApi(Build.VERSION_CODES.N)
    fun networkStatus() = callbackFlow {
        //Network Callback
        val networkStatusCallback = object : ConnectivityManager.NetworkCallback() {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    trySend(NetworkState.Cellular)
                    println("mobil veri ")
                    trySend(NetworkState.NetworkSpeed(getNetworkClass(context)))
                }
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    println("wifi")
                    trySend(NetworkState.Wifi)
                }

            }

            override fun onLost(network: Network) {
                println("onLost")
                trySend(NetworkState.Error)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        connectivityManager.registerNetworkCallback(request, networkStatusCallback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkStatusCallback)
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun getNetworkClass(context: Context): String {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
             when (tm.dataNetworkType) {
                TelephonyManager.NETWORK_TYPE_GPRS,
                TelephonyManager.NETWORK_TYPE_EDGE,
                TelephonyManager.NETWORK_TYPE_CDMA,
                TelephonyManager.NETWORK_TYPE_1xRTT,
                TelephonyManager.NETWORK_TYPE_IDEN,
                TelephonyManager.NETWORK_TYPE_GSM -> return context.getString(R.string.key_two_g)
                TelephonyManager.NETWORK_TYPE_UMTS,
                TelephonyManager.NETWORK_TYPE_EVDO_0,
                TelephonyManager.NETWORK_TYPE_EVDO_A,
                TelephonyManager.NETWORK_TYPE_HSDPA,
                TelephonyManager.NETWORK_TYPE_HSUPA,
                TelephonyManager.NETWORK_TYPE_HSPA,
                TelephonyManager.NETWORK_TYPE_EVDO_B,
                TelephonyManager.NETWORK_TYPE_EHRPD,
                TelephonyManager.NETWORK_TYPE_HSPAP,
                TelephonyManager.NETWORK_TYPE_TD_SCDMA -> return context.getString(R.string.key_three_g)
                TelephonyManager.NETWORK_TYPE_LTE,
                TelephonyManager.NETWORK_TYPE_IWLAN-> return context.getString(R.string.key_four_g)
                TelephonyManager.NETWORK_TYPE_NR -> return context.getString(R.string.key_five_g)
                else -> return context.getString(R.string.key_unknown)
            }

        }else{
            println("izin yok status ")
            "izin yok status"
        }
    }
}
