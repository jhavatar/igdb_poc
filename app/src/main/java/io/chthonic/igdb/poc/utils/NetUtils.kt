package io.chthonic.igdb.poc.utils

import android.content.Context
import android.net.ConnectivityManager

object NetUtils {

    fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return (activeNetworkInfo != null) && activeNetworkInfo.isConnected
    }

}