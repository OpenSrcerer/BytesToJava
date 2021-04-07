package opensrcerer.requests;

import okhttp3.Request;
import opensrcerer.BTJImpl;
import opensrcerer.consumers.BTJAsync;
import opensrcerer.requestEntities.SongLyrics;
import opensrcerer.util.CompletionType;
import opensrcerer.util.Endpoint;
import opensrcerer.util.JSONParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class LyricsRequest implements BTJRequest<SongLyrics> {

    /**
     * The BTJ instance for this Request.
     */
    private final BTJImpl btj;

    /**
     * The OkHttp Request of this BTJRequest.
     */
    private final Request request;

    /**
     * Consumer to handle futures & callbacks.
     */
    private BTJAsync<SongLyrics> async = null;

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

    public LyricsRequest(BTJImpl btj, String songName, @Nullable String artist) {
        this.songName = songName;
        this.artist = artist;
        this.btj = btj;
        this.request = btj.getRequest(this);
    }

    // ***************************************************************
    // **                      COMPLETION                           **
    // ***************************************************************

    @Override
    public void queue(Consumer<SongLyrics> success) {
        type = CompletionType.CALLBACK;
        async = new BTJAsync<>(this, success, null);
        btj.invoke(this);
    }

    @Override
    public void queue(Consumer<SongLyrics> success, Consumer<Throwable> failure) {
        type = CompletionType.CALLBACK;
        async = new BTJAsync<>(this, success, failure);
        btj.invoke(this);
    }

    @NotNull
    @Override
    public CompletableFuture<SongLyrics> submit() {
        type = CompletionType.FUTURE;
        async = new BTJAsync<>();
        btj.invoke(this);
        return this.async.getFuture();
    }

    @NotNull
    @Override
    public SongLyrics complete() {
        type = CompletionType.SYNCHRONOUS;
        try {
            return JSONParser.matchSynchronous(this, btj.getClient().newCall(request).execute());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
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
    public BTJAsync<SongLyrics> getAsync() {
        return async;
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
