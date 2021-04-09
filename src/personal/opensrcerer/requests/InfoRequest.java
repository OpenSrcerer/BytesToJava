package opensrcerer.requests;

import okhttp3.Request;
import opensrcerer.BTJImpl;
import opensrcerer.consumers.BTJAsync;
import opensrcerer.requestEntities.TokenInfo;
import opensrcerer.util.CompletionType;
import opensrcerer.util.Endpoint;
import opensrcerer.util.BTJParser;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class InfoRequest implements BTJRequest<TokenInfo> {

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
    private BTJAsync<TokenInfo> async = null;

    /**
     * The way this Request should be asynchronously executed (if at all).
     */
    private CompletionType type;

    public InfoRequest(BTJImpl btj) {
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
            return BTJParser.matchSynchronous(this, btj.getClient().newCall(request).execute());
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
