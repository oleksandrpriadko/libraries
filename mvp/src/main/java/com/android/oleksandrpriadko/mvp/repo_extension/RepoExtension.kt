package com.android.oleksandrpriadko.mvp.repo_extension

import com.android.oleksandrpriadko.loggalitic.LogPublishService

interface RepoExtension {

    fun cleanUp()

    fun logState(message: String) {
        if (enableLog()) {
            LogPublishService.logger().d(this::class.java.simpleName, message)
        }
    }

    fun enableLog() : Boolean = true

}
