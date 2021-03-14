package com.opensrcerer.requests;

import com.opensrcerer.BTJ;
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

public class TextRequest implements StringRequest {

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
    private Consumer<String> success;

    /**
     * Consumer to handle failed callbacks.
     */
    private Consumer<Throwable> failure;

    /**
     * CompletableFuture in case of usage of .submit();
     */
    private CompletableFuture<String> future;

    /**
     * The way this Request should be asynchronously executed (if at all).
     */
    private CompletionType type;

    public TextRequest(BTJ btj) {
        this.btj = btj;
        this.request = btj.getRequest(this);
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
    public void queue(Consumer<String> success) {

    }

    @Override
    public void queue(Consumer<String> success, Consumer<Throwable> failure) {

    }

    @NotNull
    @Override
    public CompletableFuture<String> submit() {
        return null;
    }

    @Override
    public String complete() {
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
    public Endpoint getEndpoint() {
        return Endpoint.TEXT;
    }

    @NotNull
    @Override
    public CompletionType getCompletion() {
        return type;
    }

    @Nullable
    @Override
    public CompletableFuture<String> getFuture() {
        return future;
    }
}
