package com.android.oleksandrpriadko.core

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.android.oleksandrpriadko.core.logger.Logger
import java.io.IOException

@SuppressLint("MissingPermission")
class CoreServiceManager {
    companion object {
        var logService = Logger.NOT_SET

        fun isOnline(): Boolean {
            val runtime = Runtime.getRuntime()
            try {
                val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
                val exitValue = ipProcess.waitFor()
                return exitValue == 0
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            return false
        }

        @Deprecated("has to be updated")
        fun isOnline(context: Context): Boolean {
            val info = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
            if (info == null || !info.isConnected) {
                return false
            }
            if (info.isRoaming) {
                // here is the roaming option you can change it if you want to
                // disable internet while roaming, just return false
                return false
            }
            if (isUsingWiFi(context)) {
                return true
            } else if (isUsingMobileData(context)) {
                return true
            }
            return false
        }

        private fun isUsingWiFi(context: Context): Boolean {
            val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val wifiInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI)

            return wifiInfo != null && (wifiInfo.state == NetworkInfo.State.CONNECTED || wifiInfo.state == NetworkInfo.State.CONNECTING)
        }

        private fun isUsingMobileData(context: Context): Boolean {
            val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val mobileInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            connectivity.allNetworks

            return mobileInfo != null && (mobileInfo.state == NetworkInfo.State.CONNECTED || mobileInfo.state == NetworkInfo.State.CONNECTING)
        }
    }
}