package com.opensrcerer.requests;

import com.opensrcerer.BTJ;
import com.opensrcerer.requestEntities.SongLyrics;
import com.opensrcerer.util.CompletionType;
import com.opensrcerer.util.Endpoint;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class LyricsRequest implements BTJRequest<SongLyrics> {

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
    private Consumer<SongLyrics> success;

    /**
     * Consumer to handle failed callbacks.
     */
    private Consumer<Throwable> failure;

    /**
     * CompletableFuture in case of usage of .submit();
     */
    private CompletableFuture<SongLyrics> future;

    /**
     * The way this Request should be asynchronously executed (if at all).
     */
    private CompletionType type;

    // ***************************************************************
    // **                       ARGUMENTS                           **
    // ***************************************************************

    /**
     * Name of the Song to fetch lyrics for.
     */
    private final String songName;

    /**
     * Name of the artist that made the song.
     */
    private final String artist;

    public LyricsRequest(BTJ btj, String songName, @Nullable String artist) {
        this.btj = btj;
        this.request = btj.getRequest(this);
        this.songName = songName;
        this.artist = artist;
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
    public void queue(Consumer<SongLyrics> success) {

    }

    @Override
    public void queue(Consumer<SongLyrics> success, Consumer<Throwable> failure) {

    }

    @NotNull
    @Override
    public CompletableFuture<SongLyrics> submit() {
        type = CompletionType.SUBMIT;
        this.future = new CompletableFuture<>();
        return this.future;
    }

    @Override
    public SongLyrics complete() {
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
    public Consumer<SongLyrics> getSuccessConsumer() {
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
        return Endpoint.LYRICS;
    }

    @NotNull
    @Override
    public CompletionType getCompletion() {
        return type;
    }

    @NotNull
    @Override
    public CompletableFuture<SongLyrics> getFuture() {
        return future;
    }

    /**
     * @return The name of the song that will be looked up in this BTJRequest.
     */
    public String getSongName() {
        return songName;
    }

    /**
     * @return The name of the artist who created the song that will be looked up in this BTJRequest.
     */
    public String getArtist() {
        return artist;
    }
}
