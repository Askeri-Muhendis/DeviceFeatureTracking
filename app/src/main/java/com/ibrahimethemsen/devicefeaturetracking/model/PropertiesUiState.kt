package com.ibrahimethemsen.devicefeaturetracking.model

import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class PropertiesUiState(
    val statusView: ImageView,
    val statusTv: TextView,
    @DrawableRes val statusViewDrawable: Int,
    @StringRes val statusStringRes: Int
)
