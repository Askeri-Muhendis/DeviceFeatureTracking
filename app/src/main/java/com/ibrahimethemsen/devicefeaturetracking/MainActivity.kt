package com.ibrahimethemsen.devicefeaturetracking

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ibrahimethemsen.devicefeaturetracking.databinding.ActivityMainBinding
import com.ibrahimethemsen.devicefeaturetracking.model.PropertiesUiState
import com.ibrahimethemsen.devicefeaturetracking.network.NetworkStatusTracker
import com.ibrahimethemsen.devicefeaturetracking.utility.devicePropertiesStatusChanged
import com.ibrahimethemsen.devicefeaturetracking.utility.devicePropertiesUiState
import com.ibrahimethemsen.devicefeaturetracking.utility.userInfo
//TODO WIFI-3G-SIM CARD-SARJ SOKETI-KULAKLIK-BLUETOOTH-NFC-TITRESIM-FLASH-HOPORLOR-3RENK-PROMIXIMTY-ON KAMERA-ARKA KAMERA
class MainActivity : AppCompatActivity() {
    private val viewModel: NetworkStatusViewModel by lazy {
        ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val networkStatusTracker = NetworkStatusTracker(this@MainActivity)
                    return NetworkStatusViewModel(networkStatusTracker) as T
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
                        this@MainActivity.devicePropertiesUiState(
                            PropertiesUiState(
                                cellularIv,
                                cellularStatusTv,
                                R.drawable.cellular_no_connected,
                                R.string.key_not_connected
                            ),
                            PropertiesUiState(
                                wifiIv,
                                wifiStatusTv,
                                R.drawable.wifi_off,
                                R.string.key_not_connected
                            ),
                            PropertiesUiState(
                                networkSpeedIv,
                                networkSpeedStatusTv,
                                R.drawable.ic_speed_unknown,
                                R.string.key_not_connected
                            )
                        )
                    }
                }
                MyState.Cellular -> {
                    binding.apply {
                        this@MainActivity.devicePropertiesUiState(
                            PropertiesUiState(
                                cellularIv,
                                cellularStatusTv,
                                R.drawable.cellular_connected,
                                R.string.key_connected
                            ),
                            PropertiesUiState(
                                wifiIv,
                                wifiStatusTv,
                                R.drawable.wifi_off,
                                R.string.key_not_connected
                            )
                        )
                    }
                }
                MyState.Wifi -> {
                    binding.apply {
                        this@MainActivity.devicePropertiesUiState(
                            PropertiesUiState(
                                wifiIv,
                                wifiStatusTv,
                                R.drawable.wifi_connected,
                                R.string.key_connected
                            ),
                            PropertiesUiState(
                                cellularIv,
                                cellularStatusTv,
                                R.drawable.cellular_no_connected,
                                R.string.key_not_connected
                            ),
                            PropertiesUiState(
                                networkSpeedIv,
                                networkSpeedStatusTv,
                                R.drawable.ic_speed_unknown,
                                R.string.key_unknown
                            )
                        )
                    }
                }
                is MyState.NetworkSpeed -> {
                    when (state.data) {
                        "2G", "?" -> {
                            binding.apply {
                                this@MainActivity.devicePropertiesStatusChanged(
                                    PropertiesUiState(
                                        networkSpeedIv,
                                        networkSpeedStatusTv,
                                        R.drawable.ic_speed_unknown,
                                        R.string.key_unknown
                                    )
                                )
                            }
                        }
                        "3G" -> {
                            binding.apply {
                                this@MainActivity.devicePropertiesStatusChanged(
                                    PropertiesUiState(
                                        networkSpeedIv,
                                        networkSpeedStatusTv,
                                        R.drawable.ic_three_g,
                                        R.string.key_unknown
                                    )
                                )
                            }
                        }
                        "4G" -> {
                            binding.apply {
                                this@MainActivity.devicePropertiesStatusChanged(
                                    PropertiesUiState(
                                        networkSpeedIv,
                                        networkSpeedStatusTv,
                                        R.drawable.ic_four_g,
                                        R.string.key_four_g
                                    )
                                )
                            }
                        }
                        "5G" -> {
                            binding.apply {
                                this@MainActivity.devicePropertiesStatusChanged(
                                    PropertiesUiState(
                                        networkSpeedIv,
                                        networkSpeedStatusTv,
                                        R.drawable.ic_five_g,
                                        R.string.key_five_g
                                    )
                                )
                            }
                        }
                        else -> {
                            userInfo("İzin verilmemiş")
                        }
                    }
                }
            }
        }
    }
}






