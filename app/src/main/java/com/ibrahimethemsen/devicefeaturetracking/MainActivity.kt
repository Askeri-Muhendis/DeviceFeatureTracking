package com.ibrahimethemsen.devicefeaturetracking

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ibrahimethemsen.devicefeaturetracking.databinding.ActivityMainBinding
import com.ibrahimethemsen.devicefeaturetracking.network.NetworkStatusTracker

class MainActivity : AppCompatActivity() {
    private val viewModel : NetworkStatusViewModel by lazy {
        ViewModelProvider(
            this,
            object : ViewModelProvider.Factory{
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val networkStatusTracker = NetworkStatusTracker(this@MainActivity)
                    return NetworkStatusViewModel(networkStatusTracker) as T
                }
            },
        )[NetworkStatusViewModel::class.java]
    }

    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observe()
    }

    private fun observe(){
        viewModel.state.observe(this) { state ->
            when(state){
                MyState.Error -> {
                    binding.apply {
                        wifiIv.setImageDrawable(ContextCompat.getDrawable(this@MainActivity,R.drawable.wifi_off))
                        wifiStatusTv.text = getString(R.string.wifi_status_not_connected)
                        wifiIv.contentDescription = getString(R.string.wifi_status_not_connected)
                    }
                    binding.apply {
                        cellularIv.setImageDrawable(ContextCompat.getDrawable(this@MainActivity,R.drawable.cellular_no_connected))
                        cellularStatusTv.text = getString(R.string.wifi_status_not_connected)
                        cellularIv.contentDescription = getString(R.string.wifi_status_not_connected)
                    }
                }
                MyState.Fetched -> {

                }
                MyState.Cellular -> {
                    binding.apply {
                        cellularIv.setImageDrawable(ContextCompat.getDrawable(this@MainActivity,R.drawable.cellular_connected))
                        cellularStatusTv.text = getString(R.string.wifi_status_connected)
                        cellularIv.contentDescription = getString(R.string.wifi_status_connected)
                    }
                    binding.apply {
                        wifiIv.setImageDrawable(ContextCompat.getDrawable(this@MainActivity,R.drawable.wifi_off))
                        wifiStatusTv.text = getString(R.string.wifi_status_not_connected)
                        wifiIv.contentDescription = getString(R.string.wifi_status_not_connected)
                    }
                }
                MyState.Wifi -> {
                    binding.apply {
                        wifiIv.setImageDrawable(ContextCompat.getDrawable(this@MainActivity,R.drawable.wifi_connected))
                        wifiStatusTv.text = getString(R.string.wifi_status_connected)
                        wifiIv.contentDescription = getString(R.string.wifi_status_connected)
                    }
                    binding.apply {
                        cellularIv.setImageDrawable(ContextCompat.getDrawable(this@MainActivity,R.drawable.cellular_no_connected))
                        cellularStatusTv.text = getString(R.string.wifi_status_not_connected)
                        cellularIv.contentDescription = getString(R.string.wifi_status_not_connected)
                    }
                }
            }
        }
    }
}