package com.opensrcerer

import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import org.jetbrains.annotations.Contract
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory

/**
 * The implementation class for the BytesToJava API wrapper.
 */
class BTJImpl : BTJ {
    private val client: OkHttpClient

    constructor() {
        // Dispatcher to use for the OkHttpClient
        val dispatcher = Dispatcher(defaultExecutor)
        client = OkHttpClient().newBuilder()
                .dispatcher(dispatcher)
                .build()
    }

    constructor(executor: ExecutorService) {
        // Dispatcher to use for the OkHttpClient
        val dispatcher = Dispatcher(defaultExecutor)
        client = OkHttpClient().newBuilder()
                .dispatcher(dispatcher)
                .build()
    }

    /**
     * Shuts down this instance of BTJ.
     */
    private fun shutdown() {
        client.dispatcher.executorService.shutdown()
    }

    /**
     * @return The default ScheduledExecutorService using a named ThreadFactory.
     */
    companion object {
        private val defaultExecutor: ExecutorService
            get() {
                val nonScheduledFactory: ThreadFactory = object : ThreadFactory {
                    private var counter = 1

                    @Contract("_ -> new")
                    override fun newThread(r: Runnable): Thread {
                        return Thread(r, "BTJ-Requester-" + counter++)
                    }
                }
                return Executors.newScheduledThreadPool(2, nonScheduledFactory)
            }
    }
}