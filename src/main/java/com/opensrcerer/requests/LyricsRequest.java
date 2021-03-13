package com.opensrcerer.requests;

import com.opensrcerer.BTJ;
import com.opensrcerer.entities.SongLyrics;

import java.util.concurrent.CompletableFuture;

public class LyricsRequest implements BTJRequest<SongLyrics> {

    private final BTJ btj;

    public LyricsRequest(BTJ btj) {
        this.btj = btj;
    }

    @Override
    public CompletableFuture<SongLyrics> queue() {
        return null;
    }

    @Override
    public SongLyrics complete() {
        return null;
    }
}
