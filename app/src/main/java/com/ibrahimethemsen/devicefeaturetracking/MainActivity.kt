package com.ibrahimethemsen.devicefeaturetracking

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.ibrahimethemsen.devicefeaturetracking.databinding.ActivityMainBinding
import com.ibrahimethemsen.devicefeaturetracking.network.NetworkStatusTracker
import com.ibrahimethemsen.devicefeaturetracking.utility.userInfo

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


    private fun permission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                1)
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
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
                        wifiIv.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@MainActivity,
                                R.drawable.wifi_off
                            )
                        )
                        wifiStatusTv.text = getString(R.string.wifi_status_not_connected)
                        wifiIv.contentDescription = getString(R.string.wifi_status_not_connected)
                    }
                    binding.apply {
                        cellularIv.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@MainActivity,
                                R.drawable.cellular_no_connected
                            )
                        )
                        cellularStatusTv.text = getString(R.string.wifi_status_not_connected)
                        cellularIv.contentDescription =
                            getString(R.string.wifi_status_not_connected)
                    }
                }
                MyState.Cellular -> {
                    binding.apply {
                        this@MainActivity.changeViewState(
                            this,
                            cellularIv,
                            cellularStatusTv,
                            R.drawable.cellular_connected,
                            wifiIv,
                            wifiStatusTv,
                            R.drawable.wifi_off
                        )

                    }
                }
                MyState.Wifi -> {
                    binding.apply {
                        this@MainActivity.changeViewState(
                            this,
                            wifiIv,
                            wifiStatusTv,
                            R.drawable.wifi_connected,
                            cellularIv,
                            cellularStatusTv,
                            R.drawable.cellular_no_connected
                        )
                        networkSpeedStatusTv.text = "Kapalı"
                        networkSpeedIv.setImageDrawable(ContextCompat.getDrawable(this@MainActivity,R.drawable.cellular_no_connected))
                    }
                }
                is MyState.NetworkSpeed -> {
                    when(state.data){
                        "2G","?"->{
                            binding.apply {
                                networkSpeedStatusTv.text = "bilinmiyor"
                            }
                        }
                        "3G"->{
                            binding.apply {
                                networkSpeedStatusTv.text = "3G"
                                networkSpeedIv.setImageDrawable(ContextCompat.getDrawable(this@MainActivity,R.drawable.ic_three_g))
                            }
                        }
                        "4G"->{
                            binding.apply {
                                networkSpeedStatusTv.text = "4G"
                                networkSpeedIv.setImageDrawable(ContextCompat.getDrawable(this@MainActivity,R.drawable.ic_four_g))
                            }
                        }
                        "5G"->{
                            binding.apply {
                                networkSpeedStatusTv.text = "5G"
                                networkSpeedIv.setImageDrawable(ContextCompat.getDrawable(this@MainActivity,R.drawable.ic_five_g))
                            }
                        }
                    }
                }
            }
        }
    }
}


fun Context.changeViewState(
    binding: ViewBinding,
    connectedIv: ImageView,
    connectedTv: TextView,
    @DrawableRes connectedDrawable : Int,
    notConnectedIv: ImageView,
    notConnectedTv: TextView,
    @DrawableRes notConnectedDrawable : Int
) {
    binding.apply {
        connectedIv.setImageDrawable(
            ContextCompat.getDrawable(
                this@changeViewState,
                connectedDrawable
            )
        )
        connectedTv.text = getString(R.string.wifi_status_connected)
        connectedIv.contentDescription = getString(R.string.wifi_status_connected)
        //notConnected
        notConnectedIv.setImageDrawable(
            ContextCompat.getDrawable(
                this@changeViewState,
                notConnectedDrawable
            )
        )
        notConnectedTv.text = getString(R.string.wifi_status_not_connected)
        notConnectedIv.contentDescription = getString(R.string.wifi_status_not_connected)
    }
}