package com.opensrcerer.requests;

import com.opensrcerer.BTJ;
import com.opensrcerer.requestEntities.RedditPost;
import com.opensrcerer.requestEntities.RedditPosts;
import com.opensrcerer.util.CompletionType;
import com.opensrcerer.util.Endpoint;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class RedditPostsRequest implements BTJRequest<RedditPosts> {

    /**
     * The BTJ instance for this Request.
     */
    private final BTJ btj;

    /**
     * The OkHttp Request of this BTJRequest.
     */
    private final Request request;

    /**
     * Consumer to handle successful callbacks.
     */
    private Consumer<RedditPosts> success;

    /**
     * Consumer to handle failed callbacks.
     */
    private Consumer<Throwable> failure;

    /**
     * CompletableFuture in case of usage of .submit();
     */
    private CompletableFuture<RedditPosts> future;

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
        this.btj = btj;
        this.request = btj.getRequest(this);
        this.subreddit = subreddit;
        this.limit = limit;
    }

    // ***************************************************************
    // **                       CALLBACK                            **
    // ***************************************************************

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) {

    }

    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException ex) {

    }

    // ***************************************************************
    // **                      COMPLETION                           **
    // ***************************************************************

    @Override
    public void queue(Consumer<RedditPosts> success) {

    }

    @Override
    public void queue(Consumer<RedditPosts> success, Consumer<Throwable> failure) {

    }

    @NotNull
    @Override
    public CompletableFuture<RedditPosts> submit() {
        type = CompletionType.SUBMIT;
        this.future = new CompletableFuture<>();
        return this.future;
    }

    @Override
    public List<RedditPost> complete() {
        return null;
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
    public Consumer<RedditPosts> getSuccessConsumer() {
        return success;
    }

    @NotNull
    @Override
    public Consumer<Throwable> getFailureConsumer() {
        return failure;
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

    @NotNull
    @Override
    public CompletableFuture<RedditPosts> getFuture() {
        return future;
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
