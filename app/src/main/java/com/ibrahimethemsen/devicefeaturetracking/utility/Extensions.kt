package com.ibrahimethemsen.devicefeaturetracking.utility

import android.content.Context
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.ibrahimethemsen.devicefeaturetracking.model.PropertiesUiState

fun Context.userInfo(msg : String){
    Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
}

fun Context.devicePropertiesStatusChanged(
    propertiesUiState: PropertiesUiState
) {
    propertiesUiState.apply {
        statusView.setImageDrawable(
            ContextCompat.getDrawable(
                this@devicePropertiesStatusChanged,
                statusViewDrawable
            )
        )
        getString(statusStringRes).let {
            statusView.contentDescription = it
            statusTv.text = it
        }
    }
}