package com.ibrahimethemsen.devicefeaturetracking.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import com.ibrahimethemsen.devicefeaturetracking.R
import com.ibrahimethemsen.devicefeaturetracking.databinding.ComponentStatusChangeViewBinding
import com.ibrahimethemsen.devicefeaturetracking.model.PropertiesUiState
import com.ibrahimethemsen.devicefeaturetracking.utility.devicePropertiesStatusChanged

class StatusChangeView @JvmOverloads constructor(
    context: Context,
    attrs : AttributeSet? = null,
) : ConstraintLayout(context, attrs) {
    private val binding = ComponentStatusChangeViewBinding.inflate(LayoutInflater.from(context),this,false)

    private var defaultSrc = 0
    private var defaultString = "0"
    private var titleString = "0"
    private var onStatusChangeListener: OnStatusChangeListener? = null
    init {
        addView(binding.root)
        context.withStyledAttributes(attrs, R.styleable.StatusChangeView){
            defaultSrc = getResourceId(R.styleable.StatusChangeView_defaultSrc,R.drawable.cellular_no_connected)
            defaultString = getString(R.styleable.StatusChangeView_defaultString).toString()
            titleString = getString(R.styleable.StatusChangeView_titleString).toString()
        }
        setInitUiState()
    }

    private fun setInitUiState(){
        binding.apply {
            statusChangeIv.setImageDrawable(ContextCompat.getDrawable(context,defaultSrc))
            statusChangeStatusTv.text = defaultString
            statusChangeTitleTv.text =  titleString
        }
    }

    fun propertiesStatusChangeView(
        @DrawableRes statusViewDrawable: Int,
        @StringRes statusStringRes: Int
    ){
        binding.apply {
            context.devicePropertiesStatusChanged(
                PropertiesUiState(
                    statusChangeIv,
                    statusChangeStatusTv,
                    statusViewDrawable,
                    statusStringRes
                )
            )
        }
    }

    fun setOnStatusChangeListener(listener : OnStatusChangeListener){
        onStatusChangeListener = listener
        setOnClickListener {
            onStatusChangeListener?.onStatusChange()
        }
    }
}