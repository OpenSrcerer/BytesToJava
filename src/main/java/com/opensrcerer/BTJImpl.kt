package com.opensrcerer

import com.opensrcerer.requestEntities.*
import com.opensrcerer.requests.*
import com.opensrcerer.util.BTJQueue
import com.opensrcerer.util.RequestBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.annotations.Contract
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
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
        requests = BTJQueue(this, defaultExecutor)
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
        requests = BTJQueue(this, executor)
        builder = RequestBuilder(token) // Create a new RequestBuilder with given token
        client = OkHttpClient().newBuilder().build()
    }

    override fun shutdown() {
        client.dispatcher.executorService.shutdown()
    }

    override fun shutdownNow(): List<Runnable> {
        return client.dispatcher.executorService.shutdownNow()
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

    override fun putIntoQueue(request: BTJRequest<*>) {
        requests.addRequest(request)
    }

    override fun getRequest(request: BTJRequest<*>): Request {
        return builder.createHttpRequest(request)
    }

    override fun getClient(): OkHttpClient {
        return client
    }

    // -----------------------------------------------

    override fun getWord(): BTJRequest<String> {
        return WordRequest(this)
    }

    override fun getText(): BTJRequest<String> {
        return TextRequest(this)
    }

    override fun getMadLib(): BTJRequest<MadLib> {
        return MadLibRequest(this)
    }

    override fun getMeme(): BTJRequest<RedditMeme> {
        return MemeRequest(this)
    }

    override fun getLyrics(song: String): BTJRequest<SongLyrics> {
        return LyricsRequest(this, song, null)
    }

    override fun getLyrics(song: String, artist: String?): BTJRequest<SongLyrics> {
        return LyricsRequest(this, song, artist)
    }

    override fun getRedditPost(subreddit: String): BTJRequest<List<RedditPost>> {
        return RedditPostsRequest(this, subreddit, 1)
    }

    @Throws(IllegalArgumentException::class)
    override fun getRedditPost(subreddit: String, limit: Int): BTJRequest<List<RedditPost>> {
        if (limit < 1 || limit > 50) {
            throw IllegalArgumentException("Number of reddit posts to fetch must be between 1 and 50.")
        }
        return RedditPostsRequest(this, subreddit, limit)
    }
}