package com.ibrahimethemsen.devicefeaturetracking

import android.Manifest
import android.app.UiModeManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.ibrahimethemsen.devicefeaturetracking.battery.BatteryStatusTracker
import com.ibrahimethemsen.devicefeaturetracking.bluetooth.BluetoothState
import com.ibrahimethemsen.devicefeaturetracking.databinding.ActivityMainBinding
import com.ibrahimethemsen.devicefeaturetracking.model.CardState
import com.ibrahimethemsen.devicefeaturetracking.model.HeadsetState
import com.ibrahimethemsen.devicefeaturetracking.model.NetworkState
import com.ibrahimethemsen.devicefeaturetracking.model.RingerModeState
import com.ibrahimethemsen.devicefeaturetracking.utility.userInfo

//TODO HOPORLOR-3RENK
@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this,MainViewModelProvider(this))[MainViewModel::class.java]
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observe()
        permission()
        setBatteryState()
        setProximitySensor()
        setModeObserve()
        setCamera()

    }

    private fun setCamera(){
        binding.statusBackCamera.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                startActivityForResult(takePictureIntent, 1)
            } catch (e: ActivityNotFoundException) {
                println("camera hata ${e.localizedMessage}")
            }
        }
    }

    private fun setModeObserve(){
        val uiModeManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        when (uiModeManager.nightMode) {
            UiModeManager.MODE_NIGHT_NO -> {
                // Normal tema
                binding.statusTheme.propertiesStatusChangeView(R.drawable.ic_light_mode,R.string.key_light_mode)
            }

            UiModeManager.MODE_NIGHT_YES -> {
                // Koyu tema
                binding.statusTheme.propertiesStatusChangeView(R.drawable.ic_dark_mode,R.string.key_dark_mode)
            }
            UiModeManager.MODE_NIGHT_AUTO,UiModeManager.MODE_NIGHT_CUSTOM -> {
                //gece modunu otomatik acma kapama
                binding.statusTheme.propertiesStatusChangeView(R.drawable.ic_night_sight_auto,R.string.key_dark_auto_mode)
            }
        }
    }

    private fun setBatteryState(){
        val batteryStatusTracker = BatteryStatusTracker()
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(batteryStatusTracker, filter)
    }
    private fun permission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE,Manifest.permission.BLUETOOTH,Manifest.permission.CAMERA),
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

    private fun setProximitySensor(){
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        binding.statusProximityTv.text = getString(R.string.key_proximity,sensor.name,sensor.maximumRange.toInt(),sensor.vendor)
    }

    private fun observe() {
        viewModel.state.observe(this) { state ->
            when (state) {
                NetworkState.Error -> {
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
                NetworkState.Cellular -> {
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
                NetworkState.Wifi -> {
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
                is NetworkState.NetworkSpeed -> {
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
        viewModel.headsetStatus.observe(this){
            when(it){
                HeadsetState.Earphone -> {
                    binding.statusHeadset.propertiesStatusChangeView(R.drawable.ic_headphones,R.string.key_headset)
                }
                HeadsetState.MicrophoneEarphone -> {
                    binding.statusHeadset.propertiesStatusChangeView(R.drawable.ic_headset_mic,R.string.key_headset_mic)
                }
                HeadsetState.NotEarphone -> {
                    binding.statusHeadset.propertiesStatusChangeView(R.drawable.ic_headset_off,R.string.key_headset_not)
                }
            }
        }
        viewModel.batteryStatus.observe(this) { isCharging ->
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
        viewModel.bluetoothStatus.observe(this){
            when(it){
                BluetoothState.Connected -> binding.statusBluetooth.propertiesStatusChangeView(R.drawable.ic_bluetooth_connected,R.string.key_bluetooth_connected)
                BluetoothState.Disable -> binding.statusBluetooth.propertiesStatusChangeView(R.drawable.ic_bluetooth_disabled,R.string.key_bluetooth_disable)
                BluetoothState.Enabled ->binding.statusBluetooth.propertiesStatusChangeView(R.drawable.ic_bluetooth,R.string.key_bluetooth_enable)
            }
        }
        viewModel.torchStatus.observe(this){
            when(it){
                true -> binding.statusTorch.propertiesStatusChangeView(R.drawable.ic_flashlight_on,R.string.key_torch_on)
                false -> binding.statusTorch.propertiesStatusChangeView(R.drawable.ic_flashlight_off,R.string.key_torch_off)
            }
        }
        viewModel.ringerModeStatus.observe(this){
            when(it){
                RingerModeState.Normal -> binding.statusRingerMode.propertiesStatusChangeView(R.drawable.ic_notifications,R.string.key_ringer_mode_normal)
                RingerModeState.Silent -> binding.statusRingerMode.propertiesStatusChangeView(R.drawable.ic_notifications_silent,R.string.key_ringer_mode_silent)
                RingerModeState.Vibrate ->binding.statusRingerMode.propertiesStatusChangeView(R.drawable.ic_notifications_vibrate,R.string.key_ringer_mode_vibrate)
            }
        }
    }
}