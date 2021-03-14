package com.opensrcerer.requests;

import com.opensrcerer.BTJ;
import com.opensrcerer.requestEntities.TokenInfo;
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

public class InfoRequest implements BTJRequest<TokenInfo> {

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
    private Consumer<TokenInfo> success;

    /**
     * Consumer to handle failed callbacks.
     */
    private Consumer<Throwable> failure;

    /**
     * CompletableFuture in case of usage of .submit();
     */
    private CompletableFuture<TokenInfo> future = null;

    /**
     * The way this Request should be asynchronously executed (if at all).
     */
    private CompletionType type;

    public InfoRequest(BTJ btj) {
        this.btj = btj;
        this.request = btj.getRequest(this);
    }

    // ***************************************************************
    // **                       CALLBACK                            **
    // ***************************************************************

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) {
        // parse whatever
    }

    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException ex) {
        // parse whatever

    }

    // ***************************************************************
    // **                      COMPLETION                           **
    // ***************************************************************

    @Override
    public void queue(Consumer<TokenInfo> success) {
        type = CompletionType.QUEUE;
        if (success == null) {
            // use default consumer
        }
        this.success = success;
        this.btj.getClient().newCall(request).enqueue(this);
    }

    @Override
    public void queue(Consumer<TokenInfo> success, Consumer<Throwable> failure) {
        type = CompletionType.QUEUE;
        if (success == null) {
            // use default consumer
        }
        if (failure == null) {
            // use default consumer
        }
        this.success = success;
        this.failure = failure;
        this.btj.getClient().newCall(request).enqueue(this);
    }

    @NotNull
    @Override
    public CompletableFuture<TokenInfo> submit() {
        type = CompletionType.SUBMIT;
        this.future = new CompletableFuture<>();
        return this.future;
    }

    @Override
    public TokenInfo complete() throws RuntimeException {
        type = CompletionType.COMPLETE;
        if (btj.)
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

    @Nullable
    @Override
    public CompletableFuture<TokenInfo> getFuture() {
        return future;
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
