package com.android.oleksandrpriadko.retrofit

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.android.oleksandrpriadko.core.CoreServiceManager
import com.android.oleksandrpriadko.extension.connectivityManager
import java.io.IOException

class ConnectionStatusReceiver : BroadcastReceiver() {
    private val subscribers = mutableListOf<ConnectionStatusSubscriber>()

    fun subscribe(subscriber: ConnectionStatusSubscriber) {
        if (subscriber.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            subscriber.lifecycle.addObserver(object : DefaultLifecycleObserver {

                override fun onDestroy(owner: LifecycleOwner) {
                    subscribers.remove(subscriber)
                    logState("$subscriber destroyed and removed, size after ${subscribers.size}")
                }
            })
            subscribers.add(subscriber)
            logState("$subscriber added, size after ${subscribers.size}")
            subscriber.onConnectionStatusChanged(isConnectedToInternet())
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.net.conn.CONNECTIVITY_CHANGE") {
            val connectivityManager = context?.connectivityManager()
            val activeNetworkInfo = connectivityManager?.activeNetworkInfo
            val isConnectedToNetwork = activeNetworkInfo?.isConnected
            val isConnectedToInternet = isConnectedToNetwork ?: false && isOnline
            notifySubscribers(isConnectedToInternet)
        }
    }

    private fun notifySubscribers(isConnectedToInternet: Boolean) {
        for (subscriber in subscribers) {
            subscriber.onConnectionStatusChanged(isConnectedToInternet)
        }
    }

    companion object {
        val isOnline: Boolean
            get() {
                return isConnectedToInternet()
            }

        private fun isConnectedToInternet(): Boolean {
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
    }

    private fun logState(message: String) {
        CoreServiceManager.logService.d(this::class.java.simpleName, message)
    }
}

interface ConnectionStatusSubscriber : LifecycleOwner {

    fun onConnectionStatusChanged(isConnectedToInternet: Boolean)

}