package com.ibrahimethemsen.devicefeaturetracking.utility

import android.content.Context
import android.widget.Toast

fun Context.userInfo(msg : String){
    Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
}