package com.ibrahimethemsen.devicefeaturetracking

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.UiModeManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.ibrahimethemsen.devicefeaturetracking.bluetooth.BluetoothState
import com.ibrahimethemsen.devicefeaturetracking.component.OnStatusChangeListener
import com.ibrahimethemsen.devicefeaturetracking.databinding.ActivityMainBinding
import com.ibrahimethemsen.devicefeaturetracking.model.CardState
import com.ibrahimethemsen.devicefeaturetracking.model.HeadsetState
import com.ibrahimethemsen.devicefeaturetracking.model.NetworkState
import com.ibrahimethemsen.devicefeaturetracking.model.RingerModeState
import com.ibrahimethemsen.devicefeaturetracking.utility.userInfo
import kotlin.properties.Delegates


//TODO HOPORLOR-3RENK
@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModelProvider(this))[MainViewModel::class.java]
    }

    private lateinit var binding: ActivityMainBinding
    private var setTorch by Delegates.notNull<Boolean>()
    private lateinit var popupMenu : PopupMenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observe()
        permission()
        setProximitySensor()
        setModeObserve()
        setCamera()
        popupMenu = PopupMenu(this@MainActivity,binding.statusRingerMode)
        popupMenu.menuInflater.inflate(R.menu.menu_ringer_mode,popupMenu.menu)
    }

    private fun setCamera() {
        binding.statusBackCamera.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                startActivityForResult(takePictureIntent, 1)
            } catch (e: ActivityNotFoundException) {
                println("camera hata ${e.localizedMessage}")
            }
        }
    }

    private fun setModeObserve() {
        val uiModeManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        binding.statusTheme.setOnStatusChangeListener(object : OnStatusChangeListener{
            @SuppressLint("WrongConstant")
            override fun onStatusChange() {

            }
        })
        when (uiModeManager.nightMode) {
            UiModeManager.MODE_NIGHT_NO -> {
                // Normal tema
                binding.statusTheme.propertiesStatusChangeView(
                    R.drawable.ic_light_mode,
                    R.string.key_light_mode
                )
            }

            UiModeManager.MODE_NIGHT_YES -> {
                // Koyu tema
                binding.statusTheme.propertiesStatusChangeView(
                    R.drawable.ic_dark_mode,
                    R.string.key_dark_mode
                )
            }

            UiModeManager.MODE_NIGHT_AUTO, UiModeManager.MODE_NIGHT_CUSTOM -> {
                //gece modunu otomatik acma kapama
                binding.statusTheme.propertiesStatusChangeView(
                    R.drawable.ic_night_sight_auto,
                    R.string.key_dark_auto_mode
                )
            }
        }
    }

    private fun permission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH
            )
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.CAMERA
                ),
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

    private fun setProximitySensor() {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        binding.statusProximityTv.text = getString(
            R.string.key_proximity,
            sensor.name,
            sensor.maximumRange.toInt(),
            sensor.vendor
        )
    }

    private fun observe() {
        viewModel.apply {
            state.observe(this@MainActivity, ::networkStateUi)
            simState.observe(this@MainActivity, ::simCardStateUi)
            headsetStatus.observe(this@MainActivity, ::earphonesStateUi)
            batteryStatus.observe(this@MainActivity, ::batteryStateUi)
            bluetoothStatus.observe(this@MainActivity, ::bluetoothStateUi)
            torchStatus.observe(this@MainActivity, ::torchStateUi)
            ringerModeStatus.observe(this@MainActivity, ::ringerStateUi)
        }
    }

    private fun ringerStateUi(ringerModeState: RingerModeState) {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        binding.statusRingerMode.setOnStatusChangeListener(object : OnStatusChangeListener{
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            override fun onStatusChange() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted) {
                    val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                    startActivity(intent)
                }else{
                    popupMenu.setOnMenuItemClickListener { menuItem ->
                        when(menuItem.title){
                            "Sessiz" -> {audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT}
                            "Titreşimde" -> {audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE}
                            "Normal" -> {audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL}
                        }
                        true
                    }
                    popupMenu.show()
                }
            }
        })
        when (ringerModeState) {
            RingerModeState.Normal -> binding.statusRingerMode.propertiesStatusChangeView(
                R.drawable.ic_notifications,
                R.string.key_ringer_mode_normal
            )

            RingerModeState.Silent -> binding.statusRingerMode.propertiesStatusChangeView(
                R.drawable.ic_notifications_silent,
                R.string.key_ringer_mode_silent
            )

            RingerModeState.Vibrate -> binding.statusRingerMode.propertiesStatusChangeView(
                R.drawable.ic_notifications_vibrate,
                R.string.key_ringer_mode_vibrate
            )
        }
    }

    private fun torchStateUi(torch: Boolean) {
        setTorch = torch
        val cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        val cameraId = cameraManager.cameraIdList[0]
        binding.statusTorch.setOnStatusChangeListener(object  : OnStatusChangeListener{
            override fun onStatusChange() {
                cameraManager.setTorchMode(cameraId,!setTorch)
            }
        })
        when (torch) {
            true -> binding.statusTorch.propertiesStatusChangeView(
                R.drawable.ic_flashlight_on,
                R.string.key_torch_on
            )

            false -> binding.statusTorch.propertiesStatusChangeView(
                R.drawable.ic_flashlight_off,
                R.string.key_torch_off
            )
        }
    }

    private fun bluetoothStateUi(bluetoothState: BluetoothState) {
        when (bluetoothState) {
            BluetoothState.Connected -> binding.statusBluetooth.propertiesStatusChangeView(
                R.drawable.ic_bluetooth_connected,
                R.string.key_bluetooth_connected
            )

            BluetoothState.Disable -> binding.statusBluetooth.propertiesStatusChangeView(
                R.drawable.ic_bluetooth_disabled,
                R.string.key_bluetooth_disable
            )

            BluetoothState.Enabled -> binding.statusBluetooth.propertiesStatusChangeView(
                R.drawable.ic_bluetooth,
                R.string.key_bluetooth_enable
            )
        }
    }

    private fun batteryStateUi(isCharging: Boolean) {
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

    private fun earphonesStateUi(headsetState: HeadsetState) {
        when (headsetState) {
            HeadsetState.Earphone -> {
                binding.statusHeadset.propertiesStatusChangeView(
                    R.drawable.ic_headphones,
                    R.string.key_headset
                )
            }

            HeadsetState.MicrophoneEarphone -> {
                binding.statusHeadset.propertiesStatusChangeView(
                    R.drawable.ic_headset_mic,
                    R.string.key_headset_mic
                )
            }

            HeadsetState.NotEarphone -> {
                binding.statusHeadset.propertiesStatusChangeView(
                    R.drawable.ic_headset_off,
                    R.string.key_headset_not
                )
            }
        }
    }

    private fun simCardStateUi(cardState: CardState) {
        when (cardState) {
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

    private fun networkStateUi(state: NetworkState) {
        binding.apply {
            statusWifi.setOnStatusChangeListener(object : OnStatusChangeListener {
                override fun onStatusChange() {
                    setNetworkSettings()
                }
            })
            statusCellular.setOnStatusChangeListener(object  : OnStatusChangeListener{
                override fun onStatusChange() {
                    setNetworkSettings()
                }
            })
        }
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

    private fun setNetworkSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val panelIntent = Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY)
            startActivity(panelIntent)
        } else {
            val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
            wifiManager.isWifiEnabled = !wifiManager.isWifiEnabled
        }
    }


}