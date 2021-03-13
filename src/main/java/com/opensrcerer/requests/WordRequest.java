package com.opensrcerer.requests;

import com.opensrcerer.BTJ;

import java.util.concurrent.CompletableFuture;

public class WordRequest implements BTJRequest<String> {

    private final BTJ btj;

    public WordRequest(BTJ btj) {
        this.btj = btj;
    }

    @Override
    public CompletableFuture<String> queue() {
        return null;
    }

    @Override
    public String complete() {
        return null;
    }
}
