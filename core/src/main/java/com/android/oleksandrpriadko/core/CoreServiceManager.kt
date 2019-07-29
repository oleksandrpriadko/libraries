package com.android.oleksandrpriadko.core

import com.android.oleksandrpriadko.core.logger.Logger
import java.io.IOException

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
    }
}