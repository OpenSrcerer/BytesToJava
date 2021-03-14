package com.opensrcerer

import com.opensrcerer.requestEntities.*
import com.opensrcerer.requests.*
import com.opensrcerer.util.BTJQueue
import com.opensrcerer.util.Endpoint
import com.opensrcerer.util.RequestBuilder
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.annotations.Contract
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import kotlin.jvm.Throws

/**
 * The implementation class for the BytesToJava API wrapper.
 */
class BTJImpl : BTJ {

    /**
     * Queue for all requests.
     */
    private val requests: BTJQueue

    /**
     * Request builder for all OkHttp requests associated with this BTJ instance.
     */
    private val builder: RequestBuilder

    /**
     * Client that handles Requests for this BTJ instance.
     */
    private val client: OkHttpClient

    /**
     * @param token API token for this BTJ instance.
     * @return Get a BTJ instance with default settings.
     */
    protected constructor(token: String) {
        requests = BTJQueue(defaultExecutor)
        builder = RequestBuilder(token) // Create a new RequestBuilder with given token
        client = OkHttpClient().newBuilder().build()
    }

    /**
     * @param token API token for this BTJ instance.
     * @param executor ExecutorService to supply.
     * @return Get a BTJ instance that executes requests on the provided ExecutorService.
     *         Only use this if you know what you're doing: ExecutorService provided will be
     *         BLOCKED as it is used to drain a LinkedBlockingQueue.
     */
    protected constructor(token: String, executor: ExecutorService) {
        requests = BTJQueue(executor)
        builder = RequestBuilder(token) // Create a new RequestBuilder with given token
        client = OkHttpClient().newBuilder().build()
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
    private companion object {
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

    // -----------------------------------------------

    @Throws(InterruptedException::class)
    override fun putIntoQueue(request: BTJRequest<*>) {
        requests.put(request)
    }

    override fun getClient(): OkHttpClient {
        return client
    }

    override fun getRequest(endpoint: Endpoint): Request {
        return builder.createRequest(endpoint)
    }

    // -----------------------------------------------

    override fun getWord(): BTJRequest<String> {
        return WordRequest(this, this.builder.createRequest(Endpoint.WORD))
    }

    override fun getText(): BTJRequest<String> {
        return TextRequest(this, this.builder.createRequest(Endpoint.TEXT))
    }

    override fun getMadLib(): BTJRequest<MadLib> {
        return MadLibRequest(this, this.builder.createRequest(Endpoint.MADLIBS))
    }

    override fun getMeme(): BTJRequest<RedditMeme> {
        return MemeRequest(this, this.builder.createRequest(Endpoint.MEME))
    }

    override fun getLyrics(song: String, artist: String): BTJRequest<SongLyrics> {
        return LyricsRequest(this, this.builder.createRequest(Endpoint.LYRICS))
    }

    override fun getRedditPost(subreddit: String, limit: Int): BTJRequest<List<RedditPost>> {
        return RedditPostRequest(this, this.builder.createRequest(Endpoint.REDDIT))
    }
}