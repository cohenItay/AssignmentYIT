package com.interview.itaycohen.assignmentyit

import android.content.Context
import android.net.ConnectivityManager

fun validateInternetConnection(ctx : Context) : Boolean? {
    val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = cm.activeNetworkInfo
    return activeNetwork?.isConnectedOrConnecting
}