package com.opensrcerer.requests;

import com.opensrcerer.BTJ;
import com.opensrcerer.requestEntities.RedditPost;
import com.opensrcerer.util.CompletionType;
import com.opensrcerer.util.Endpoint;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class RedditPostsRequest implements BTJRequest<List<RedditPost>> {

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
    private Consumer<RedditPost> success;

    /**
     * Consumer to handle failed callbacks.
     */
    private Consumer<Throwable> failure;

    /**
     * CompletableFuture in case of usage of .submit();
     */
    private CompletableFuture<List<RedditPost>> future;

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
    private final int number;

    public RedditPostsRequest(BTJ btj, String subreddit, int number) {
        this.btj = btj;
        this.request = btj.getRequest(this);
        this.subreddit = subreddit;
        this.number = number;
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
    public void queue(Consumer<List<RedditPost>> success) {

    }

    @Override
    public void queue(Consumer<List<RedditPost>> success, Consumer<Throwable> failure) {

    }

    @NotNull
    @Override
    public CompletableFuture<List<RedditPost>> submit() {
        return null;
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
    public Endpoint getEndpoint() {
        return Endpoint.REDDIT;
    }

    @NotNull
    @Override
    public CompletionType getCompletion() {
        return type;
    }

    @Nullable
    @Override
    public CompletableFuture<List<RedditPost>> getFuture() {
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
    public int getNumber() {
        return number;
    }
}
