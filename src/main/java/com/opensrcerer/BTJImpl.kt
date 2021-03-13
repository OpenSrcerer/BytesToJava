package com.opensrcerer

import com.opensrcerer.entities.MadLib
import com.opensrcerer.entities.RedditMeme
import com.opensrcerer.entities.RedditPost
import com.opensrcerer.entities.SongLyrics
import com.opensrcerer.requests.BTJRequest
import com.opensrcerer.util.RequestBuilder
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
    private val builder: RequestBuilder
    private val client: OkHttpClient

    constructor(token: String) {
        builder = RequestBuilder(token) // Create a new RequestBuilder with given token
        client = OkHttpClient().newBuilder()
                .dispatcher(Dispatcher(defaultExecutor))
                .build()
    }

    constructor(token: String, executor: ExecutorService) {
        builder = RequestBuilder(token) // Create a new RequestBuilder with given token
        client = OkHttpClient().newBuilder()
                .dispatcher(Dispatcher(executor))
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

    override fun getClient(): OkHttpClient {
        return client
    }

    override fun getWord(): BTJRequest<String> {
        TODO("Not yet implemented")
    }

    override fun getText(): BTJRequest<String> {
        TODO("Not yet implemented")
    }

    override fun getMadLib(): BTJRequest<MadLib> {
        TODO("Not yet implemented")
    }

    override fun getMeme(): BTJRequest<RedditMeme> {
        TODO("Not yet implemented")
    }

    override fun getLyrics(song: String?, artist: String?): BTJRequest<SongLyrics> {
        TODO("Not yet implemented")
    }

    override fun getRedditPost(subreddit: String?, limit: Int): BTJRequest<MutableList<RedditPost>> {
        TODO("Not yet implemented")
    }
}