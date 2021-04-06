package opensrcerer

import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.annotations.Contract
import org.slf4j.LoggerFactory
import opensrcerer.requestEntities.*
import opensrcerer.requests.*
import opensrcerer.util.BTJQueue
import opensrcerer.util.RequestBuilder
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ThreadFactory
import javax.security.auth.login.LoginException
import kotlin.collections.ArrayList

/**
 * The implementation class for the BytesToJava API wrapper.
 */
class BTJImpl : BTJ {

    /**
     * Logger for this BTJ Instance.
     */
    private val lgr: org.slf4j.Logger = LoggerFactory.getLogger(BTJ::class.java)

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
    @Throws(LoginException::class)
    constructor(token: String) {
        lgr.debug("Constructing queue with default executor...")
        client = OkHttpClient().newBuilder().build()
        builder = RequestBuilder(token) // Create a new RequestBuilder with given token
        requests = BTJQueue(this, twinScheduledExec, singleScheduledExec)
        testConnection()
        lgr.debug("Finished init!")
    }

    /**
     * @param token API token for this BTJ instance.
     * @param executor ExecutorService to supply.
     * @return Get a BTJ instance that executes requests on the provided ExecutorService.
     *         Only use this if you know what you're doing: ExecutorService provided will be
     *         BLOCKED as it is used to drain a LinkedBlockingQueue.
     */
    @Throws(LoginException::class)
    constructor(token: String, executor: ScheduledExecutorService) {
        lgr.debug("Initializing BTJ instance...")
        client = OkHttpClient().newBuilder().build()
        builder = RequestBuilder(token) // Create a new RequestBuilder with given token
        requests = BTJQueue(this, executor, singleScheduledExec)
        testConnection()
        lgr.debug("Finished init!")
    }

    /**
     * Updates the TokenInfo instance.
     * @throws LoginException If unable to log in to the API.
     */
    @Throws(LoginException::class)
    fun testConnection() {
        try {
            info.complete()
        } catch (ex: Exception) {
            throw LoginException("Unable to access the API: $ex")
        }
    }

    /**
     * Shutdown BTJ instance when requests are done.
     */
    override fun shutdown() {
        client.dispatcher.executorService.shutdown()
        requests.executor.shutdown()
    }

    /**
     * Shutdown BTJ instance immediately, returning an immutable List of all unfinished Runnables.
     * @return An Unmodifiable List of Runnables that were interrupted.
     */
    override fun shutdownNow(): List<Runnable> {
        val runnableList: ArrayList<Runnable> = ArrayList()
        runnableList.addAll(requests.executor.shutdownNow())
        runnableList.addAll(client.dispatcher.executorService.shutdownNow())
        return Collections.unmodifiableList(runnableList)
    }

    /**
     * @return The default ScheduledExecutorService using a named ThreadFactory.
     */
    private companion object {
        private val twinScheduledExec: ScheduledExecutorService
            get() {
                val scheduledFactory: ThreadFactory = object : ThreadFactory {
                    private var counter = 1

                    @Contract("_ -> new")
                    override fun newThread(r: Runnable): Thread {
                        return Thread(r, "BTJ-Requester-" + counter++)
                    }
                }
                return Executors.newScheduledThreadPool(2, scheduledFactory)
            }

        private val singleScheduledExec: ScheduledExecutorService
            get() {
                val scheduledFactory: ThreadFactory = object : ThreadFactory {
                    private var counter = 1

                    @Contract("_ -> new")
                    override fun newThread(r: Runnable): Thread {
                        return Thread(r, "BTJ-Scheduler-" + counter++)
                    }
                }
                return Executors.newSingleThreadScheduledExecutor(scheduledFactory)
            }
    }

    // -----------------------------------------------

    override fun invoke(request: BTJRequest<out BTJReturnable>) {
        requests.addRequest(request)
    }

    override fun getLogger(): org.slf4j.Logger {
        return lgr
    }

    override fun getRequest(request: BTJRequest<out BTJReturnable>): Request {
        return builder.createHttpRequest(request)
    }

    override fun getClient(): OkHttpClient {
        return client
    }

    // -----------------------------------------------

    override fun getInfo(): BTJRequest<TokenInfo> {
        return InfoRequest(this)
    }

    override fun getWord(): BTJRequest<RandomWord> {
        return WordRequest(this)
    }

    override fun getText(): BTJRequest<RandomText> {
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

    override fun getRedditPosts(subreddit: String): BTJRequest<RedditPosts> {
        return RedditPostsRequest(this, subreddit, 1)
    }

    @Throws(IllegalArgumentException::class)
    override fun getRedditPosts(subreddit: String, limit: Int): BTJRequest<RedditPosts> {
        if (limit < 1 || limit > 50) {
            throw IllegalArgumentException("Number of reddit posts to fetch must be between 1 and 50.")
        }
        return RedditPostsRequest(this, subreddit, limit)
    }

    override fun getDelay(): Long {
        return requests.delay
    }
}