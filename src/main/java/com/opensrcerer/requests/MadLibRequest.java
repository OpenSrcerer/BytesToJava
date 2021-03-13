package com.opensrcerer.requests;

import com.opensrcerer.BTJ;
import com.opensrcerer.entities.MadLib;

import java.util.concurrent.CompletableFuture;

public class MadLibRequest implements BTJRequest<MadLib> {

    private final BTJ btj;

    public MadLibRequest(BTJ btj) {
        this.btj = btj;
    }

    @Override
    public CompletableFuture<MadLib> queue() {

        return null;
    }

    @Override
    public MadLib complete() {
        return null;
    }
}
