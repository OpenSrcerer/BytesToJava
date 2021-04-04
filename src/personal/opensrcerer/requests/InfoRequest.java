package personal.opensrcerer.requests;

import personal.opensrcerer.BTJ;
import personal.opensrcerer.consumers.BTJAsync;
import personal.opensrcerer.requestEntities.TokenInfo;
import personal.opensrcerer.util.CompletionType;
import personal.opensrcerer.util.Endpoint;
import personal.opensrcerer.util.JSONParser;
import okhttp3.Request;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class InfoRequest implements BTJRequest<TokenInfo> {

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
    private BTJAsync<TokenInfo> async = null;

    /**
     * The way this Request should be asynchronously executed (if at all).
     */
    private CompletionType type;

    public InfoRequest(BTJ btj) {
        this.btj = btj;
        this.request = btj.getRequest(this);
    }

    // ***************************************************************
    // **                      COMPLETION                           **
    // ***************************************************************

    @Override
    public void queue(Consumer<TokenInfo> success) {
        type = CompletionType.CALLBACK;
        async = new BTJAsync<>(this, success, null);
        btj.invoke(this);
    }

    @Override
    public void queue(Consumer<TokenInfo> success, Consumer<Throwable> failure) {
        type = CompletionType.CALLBACK;
        async = new BTJAsync<>(this, success, failure);
        btj.invoke(this);
    }

    @NotNull
    @Override
    public CompletableFuture<TokenInfo> submit() {
        type = CompletionType.FUTURE;
        async = new BTJAsync<>();
        btj.invoke(this);
        return this.async.getFuture();
    }

    @NotNull
    @Override
    public TokenInfo complete() throws RuntimeException {
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
    public BTJAsync<TokenInfo> getAsync() {
        return async;
    }

    @NotNull
    @Override
    public Endpoint getEndpoint() {
        return Endpoint.INFO;
    }

    @NotNull
    @Override
    public CompletionType getCompletion() {
        return type;
    }
}
