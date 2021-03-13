package com.opensrcerer.requests;

import com.opensrcerer.BTJ;
import com.opensrcerer.entities.RedditPost;

import java.util.concurrent.CompletableFuture;

public class RedditPostRequest implements BTJRequest<RedditPost> {

    private final BTJ btj;

    public RedditPostRequest(BTJ btj) {
        this.btj = btj;
    }

    @Override
    public CompletableFuture<RedditPost> queue() {
        return null;
    }

    @Override
    public RedditPost complete() {
        return null;
    }
}
