package com.ibrahimethemsen.devicefeaturetracking

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ibrahimethemsen.devicefeaturetracking.battery.BatteryStatusTracker
import com.ibrahimethemsen.devicefeaturetracking.databinding.ActivityMainBinding
import com.ibrahimethemsen.devicefeaturetracking.model.CardState
import com.ibrahimethemsen.devicefeaturetracking.model.MyState
import com.ibrahimethemsen.devicefeaturetracking.network.NetworkStatusTracker
import com.ibrahimethemsen.devicefeaturetracking.sim.SimCardStatusTracker
import com.ibrahimethemsen.devicefeaturetracking.utility.userInfo
import kotlinx.coroutines.launch

//TODO WIFI-3G-SIM CARD-SARJ SOKETI-KULAKLIK-BLUETOOTH-NFC-TITRESIM-FLASH-HOPORLOR-3RENK-PROMIXIMTY-ON KAMERA-ARKA KAMERA
class MainActivity : AppCompatActivity() {
    private val viewModel: NetworkStatusViewModel by lazy {
        ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val networkStatusTracker = NetworkStatusTracker(this@MainActivity)
                    val simStatusTracker = SimCardStatusTracker(this@MainActivity)

                    return NetworkStatusViewModel(networkStatusTracker, simStatusTracker) as T
                }
            },
        )[NetworkStatusViewModel::class.java]
    }

    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observe()
        permission()
        setBatteryState()
    }

    private fun setBatteryState(){
        val batteryStatusTracker = BatteryStatusTracker()
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(batteryStatusTracker, filter)
        viewModel.batteryStatusFlow(batteryStatusTracker)
        setBatteryTrackerUiState()
    }

    private fun setBatteryTrackerUiState() {
        lifecycleScope.launch {
            viewModel.batteryStatus.observe(this@MainActivity) { isCharging ->
                if (isCharging) {
                    binding.statusBattery.propertiesStatusChangeView(
                        R.drawable.ic_battery,
                        R.string.key_charging
                    )

                } else {
                    binding.statusBattery.propertiesStatusChangeView(
                        R.drawable.ic_not_battery,
                        R.string.key_not_charging
                    )
                }
            }
        }
    }

    private fun permission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                1
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    userInfo("İzin Verildi")
                } else {
                    userInfo("İzin Yok")
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun observe() {
        viewModel.state.observe(this) { state ->
            when (state) {
                MyState.Error -> {
                    binding.apply {
                        statusCellularSpeed.propertiesStatusChangeView(
                            R.drawable.ic_speed_unknown,
                            R.string.key_not_connected
                        )
                        statusCellular.propertiesStatusChangeView(
                            R.drawable.cellular_no_connected,
                            R.string.key_not_connected
                        )
                        statusWifi.propertiesStatusChangeView(
                            R.drawable.wifi_off,
                            R.string.key_not_connected
                        )
                    }
                }
                MyState.Cellular -> {
                    binding.apply {
                        statusCellular.propertiesStatusChangeView(
                            R.drawable.cellular_connected,
                            R.string.key_connected
                        )
                        statusWifi.propertiesStatusChangeView(
                            R.drawable.wifi_off,
                            R.string.key_not_connected
                        )
                    }
                }
                MyState.Wifi -> {
                    binding.apply {
                        statusCellularSpeed.propertiesStatusChangeView(
                            R.drawable.ic_speed_unknown,
                            R.string.key_unknown
                        )
                        statusCellular.propertiesStatusChangeView(
                            R.drawable.cellular_no_connected,
                            R.string.key_not_connected
                        )
                        statusWifi.propertiesStatusChangeView(
                            R.drawable.wifi_connected,
                            R.string.key_connected
                        )
                    }
                }
                is MyState.NetworkSpeed -> {
                    when (state.data) {
                        "2G", "?" -> {
                            binding.statusCellularSpeed.propertiesStatusChangeView(
                                R.drawable.ic_speed_unknown,
                                R.string.key_unknown
                            )

                        }
                        "3G" -> {
                            binding.statusCellularSpeed.propertiesStatusChangeView(
                                R.drawable.ic_three_g,
                                R.string.key_unknown
                            )
                        }
                        "4G" -> {
                            binding.statusCellularSpeed.propertiesStatusChangeView(
                                R.drawable.ic_four_g,
                                R.string.key_four_g
                            )
                        }
                        "5G" -> {
                            binding.statusCellularSpeed.propertiesStatusChangeView(
                                R.drawable.ic_five_g,
                                R.string.key_five_g
                            )
                        }
                        else -> {
                            userInfo("İzin verilmemiş")
                        }
                    }
                }
            }
        }
        viewModel.simState.observe(this) {
            when (it) {
                CardState.Inserted -> {
                    binding.statusSimCard.propertiesStatusChangeView(
                        R.drawable.ic_sim,
                        R.string.key_sim_inserted
                    )
                }
                CardState.NotInserted -> {
                    binding.statusSimCard.propertiesStatusChangeView(
                        R.drawable.ic_no_sim,
                        R.string.key_sim_no_inserted
                    )
                }
            }
        }
    }
}