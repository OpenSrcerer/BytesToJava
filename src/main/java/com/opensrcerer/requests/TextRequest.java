package com.opensrcerer.requests;

import com.opensrcerer.BTJ;

import java.util.concurrent.CompletableFuture;

public class TextRequest implements BTJRequest<String> {

    private final BTJ btj;

    public TextRequest(BTJ btj) {
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
