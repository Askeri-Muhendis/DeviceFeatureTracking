package com.ibrahimethemsen.devicefeaturetracking.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.CancellationSignal
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

sealed class BluetoothState{
    object Enabled : BluetoothState()
    object Disable : BluetoothState()
    object Connected : BluetoothState()
}

class BluetoothTracker(private val context : Context) {
    fun observeBluetoothStateChanges() : Flow<BluetoothState> = callbackFlow {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter

        // Bluetooth hizmet durumu değişikliklerini dinleyen bir BluetoothProfile.ServiceListener oluşturun
        val serviceListener = object : BluetoothProfile.ServiceListener {
            override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
                // BluetoothProfile hizmeti bağlandığında çağrılır
                if (profile == BluetoothProfile.A2DP) {
                    // A2DP profili bağlandığında Bluetooth özelliğinin açık olduğunu varsayıyoruz
                    trySend(BluetoothState.Enabled)
                }
            }

            override fun onServiceDisconnected(profile: Int) {
                // BluetoothProfile hizmeti kesildiğinde çağrılır
                if (profile == BluetoothProfile.A2DP) {
                    // A2DP profili kesildiğinde Bluetooth özelliğinin kapalı olduğunu varsayıyoruz
                    trySend(BluetoothState.Disable)
                }
            }
        }

        // BluetoothProfile hizmetinin bağlantısını açın
        bluetoothAdapter.getProfileProxy(context, serviceListener, BluetoothProfile.A2DP)

        // Akışı sonlandırmak için kullanılacak bir iptal işareti oluşturun
        val cancelationSignal = CancellationSignal()

        // Bluetooth özelliği etkinleştirildiğinde ve Bluetooth cihazlarından birine bağlandığında akışa değer gönderin
        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    BluetoothAdapter.ACTION_STATE_CHANGED -> {
                        when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
                            BluetoothAdapter.STATE_ON ->trySend(BluetoothState.Enabled)
                            BluetoothAdapter.STATE_OFF ->trySend(BluetoothState.Disable)
                        }
                    }
                    BluetoothDevice.ACTION_ACL_CONNECTED ->trySend(BluetoothState.Connected)
                }
            }
        }

        // BroadcastReceiver'ı kaydedin
        val filter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        }
        context.registerReceiver(broadcastReceiver, filter)

        // Akışın iptal edilmesini dinleyin ve BluetoothProfile hizmetinin bağlantısını kapatın
        cancelationSignal.setOnCancelListener {
            bluetoothAdapter.closeProfileProxy(BluetoothProfile.A2DP, null)
            context.unregisterReceiver(broadcastReceiver)
        }
        awaitClose { cancelationSignal.cancel() }
    }
}