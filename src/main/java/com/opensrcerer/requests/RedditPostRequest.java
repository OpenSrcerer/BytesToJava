package com.opensrcerer.requests;

import com.opensrcerer.BTJ;
import com.opensrcerer.requestEntities.RedditPost;
import com.opensrcerer.util.Endpoint;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class RedditPostRequest implements BTJRequest<List<RedditPost>> {

    /**
     * The BTJ instance for this Request.
     */
    private final BTJ btj;

    /**
     * The OkHttp Request of this BTJRequest.
     */
    private final Request request;

    public RedditPostRequest(BTJ btj, Request request) {
        this.btj = btj;
        this.request = request;
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

    @Override
    public CompletableFuture<List<RedditPost>> submit() {
        return null;
    }

    @Override
    public List<RedditPost> complete() {
        return null;
    }

    @Override
    public Endpoint getEndpoint() {
        return Endpoint.REDDIT;
    }
}
