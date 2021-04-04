package personal.opensrcerer.requests;

import personal.opensrcerer.BTJ;
import personal.opensrcerer.consumers.BTJAsync;
import personal.opensrcerer.requestEntities.RedditMeme;
import personal.opensrcerer.util.CompletionType;
import personal.opensrcerer.util.Endpoint;
import personal.opensrcerer.util.JSONParser;
import okhttp3.Request;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class MemeRequest implements BTJRequest<RedditMeme> {

    /**
     * The BTJ instance for this Request.
     */
    private final BTJ btj;

    /**
     * The OkHttp Request of this BTJRequest.
     */
    private final Request request;

    /**
     * Consumer to handle futures & callbacks.
     */
    private BTJAsync<RedditMeme> async = null;

    /**
     * The way this Request should be asynchronously executed (if at all).
     */
    private CompletionType type;

    public MemeRequest(BTJ btj) {
        this.btj = btj;
        this.request = btj.getRequest(this);
    }

    // ***************************************************************
    // **                      COMPLETION                           **
    // ***************************************************************

    @Override
    public void queue(Consumer<RedditMeme> success) {
        type = CompletionType.CALLBACK;
        async = new BTJAsync<>(this, success, null);
        btj.invoke(this);
    }

    @Override
    public void queue(Consumer<RedditMeme> success, Consumer<Throwable> failure) {
        type = CompletionType.CALLBACK;
        async = new BTJAsync<>(this, success, failure);
        btj.invoke(this);
    }

    @NotNull
    @Override
    public CompletableFuture<RedditMeme> submit() {
        type = CompletionType.FUTURE;
        async = new BTJAsync<>();
        btj.invoke(this);
        return async.getFuture();
    }

    @NotNull
    @Override
    public RedditMeme complete() {
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
    public BTJAsync<RedditMeme> getAsync() {
        return async;
    }

    @NotNull
    @Override
    public Endpoint getEndpoint() {
        return Endpoint.MEME;
    }

    @NotNull
    @Override
    public CompletionType getCompletion() {
        return type;
    }
}
