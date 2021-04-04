package personal.opensrcerer.requests;

import personal.opensrcerer.BTJ;
import personal.opensrcerer.consumers.BTJAsync;
import personal.opensrcerer.requestEntities.RedditPosts;
import personal.opensrcerer.util.CompletionType;
import personal.opensrcerer.util.Endpoint;
import personal.opensrcerer.util.JSONParser;
import okhttp3.Request;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class RedditPostsRequest implements BTJRequest<RedditPosts> {

    /**
     * The BTJ instance for this Request.
     */
    private final BTJ btj;

    /**
     * The OkHttp Request of this BTJRequest.
     */
    private final Request request;

    /**
     * Consumer to handle futures & callbacks.
     */
    private BTJAsync<RedditPosts> async = null;

    /**
     * The way this Request should be asynchronously executed (if at all).
     */
    private CompletionType type;

    /**
     * The subreddit to fetch posts from.
     */
    private final String subreddit;

    /**
     * Number of posts to fetch.
     */
    private final int limit;

    public RedditPostsRequest(BTJ btj, String subreddit, int limit) {
        this.subreddit = subreddit;
        this.limit = limit;
        this.btj = btj;
        this.request = btj.getRequest(this);
    }

    // ***************************************************************
    // **                      COMPLETION                           **
    // ***************************************************************

    @Override
    public void queue(Consumer<RedditPosts> success) {
        type = CompletionType.CALLBACK;
        async = new BTJAsync<>(this, success, null);
        btj.invoke(this);
    }

    @Override
    public void queue(Consumer<RedditPosts> success, Consumer<Throwable> failure) {
        type = CompletionType.CALLBACK;
        async = new BTJAsync<>(this, success, failure);
        btj.invoke(this);
    }

    @NotNull
    @Override
    public CompletableFuture<RedditPosts> submit() {
        type = CompletionType.FUTURE;
        async = new BTJAsync<>();
        btj.invoke(this);
        return this.async.getFuture();
    }

    @NotNull
    @Override
    public RedditPosts complete() {
        type = CompletionType.SYNCHRONOUS;
        try {
            return JSONParser.matchSynchronous(this, btj.getClient().newCall(request).execute());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    // ***************************************************************
    // **                        GETTERS                            **
    // ***************************************************************

    @NotNull
    @Override
    public Request getRequest() {
        return request;
    }

    @NotNull
    @Override
    public BTJAsync<RedditPosts> getAsync() {
        return async;
    }

    @NotNull
    @Override
    public Endpoint getEndpoint() {
        return Endpoint.REDDIT;
    }

    @NotNull
    @Override
    public CompletionType getCompletion() {
        return type;
    }

    /**
     * @return The subreddit this request will fetch from.
     */
    public String getSubreddit() {
        return subreddit;
    }

    /**
     * @return The number of RedditPost-s that will be fetched by this request.
     */
    public int getLimit() {
        return limit;
    }
}
