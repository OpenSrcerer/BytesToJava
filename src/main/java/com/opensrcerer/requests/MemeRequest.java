package com.opensrcerer.requests;

import com.opensrcerer.BTJ;
import com.opensrcerer.entities.RedditMeme;

import java.util.concurrent.CompletableFuture;

public class MemeRequest implements BTJRequest<RedditMeme> {

    private final BTJ btj;

    public MemeRequest(BTJ btj) {
        this.btj = btj;
    }

    @Override
    public CompletableFuture<RedditMeme> queue() {
        return null;
    }

    @Override
    public RedditMeme complete() {
        return null;
    }
}
