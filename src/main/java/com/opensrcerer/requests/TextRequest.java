package com.opensrcerer.requests;

import com.opensrcerer.BTJ;
import com.opensrcerer.util.Endpoint;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

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

    public TextRequest(BTJ btj, Request request) {
        this.btj = btj;
        this.request = request;
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

    @Override
    public CompletableFuture<String> submit() {
        return null;
    }

    @Override
    public String complete() {
        return null;
    }

    @Override
    public Endpoint getEndpoint() {
        return Endpoint.TEXT;
    }
}
