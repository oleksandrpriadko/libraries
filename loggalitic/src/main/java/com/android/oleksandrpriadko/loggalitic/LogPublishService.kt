package com.android.oleksandrpriadko.loggalitic

import android.util.Log

import com.android.oleksandrpriadko.loggalitic.analytics.Publisher
import com.android.oleksandrpriadko.core.logger.Logger

/**
 * This class will use provided [Publisher] for analytics and [Logger] for logging.
 */
object LogPublishService {

    private var publisher = Publisher.NOT_SET

    private var logger = Logger.NOT_SET

    fun init(logger: Logger) {
        LogPublishService.logger = logger
    }

    fun init(publisher: Publisher) {
        LogPublishService.publisher = publisher
    }

    fun init(logger: Logger, publisher: Publisher) {
        init(logger)
        init(publisher)
    }

    fun logger(): Logger {
        if (logger === Logger.NOT_SET) {
            Log.i(LogPublishService::class.java.simpleName,
                    "Default ${Logger::class.java.simpleName} in use")
        }
        return logger
    }

    fun publisher(): Publisher {
        if (publisher === Publisher.NOT_SET) {
            Log.i(LogPublishService::class.java.simpleName,
                    "Default ${Publisher::class.java.simpleName} in use")
        }
        return publisher
    }
}
