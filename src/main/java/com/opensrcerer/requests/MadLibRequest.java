package com.opensrcerer.requests;

import com.opensrcerer.BTJ;
import com.opensrcerer.requestEntities.MadLib;
import com.opensrcerer.util.CompletionType;
import com.opensrcerer.util.Endpoint;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class MadLibRequest implements BTJRequest<MadLib> {

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
    private Consumer<MadLib> success;

    /**
     * Consumer to handle failed callbacks.
     */
    private Consumer<Throwable> failure;

    /**
     * CompletableFuture in case of usage of .submit();
     */
    private CompletableFuture<MadLib> future;

    /**
     * The way this Request should be asynchronously executed (if at all).
     */
    private CompletionType type;

    public MadLibRequest(BTJ btj) {
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
    public void queue(Consumer<MadLib> success) {

    }

    @Override
    public void queue(Consumer<MadLib> success, Consumer<Throwable> failure) {

    }

    @NotNull
    @Override
    public CompletableFuture<MadLib> submit() {
        type = CompletionType.SUBMIT;
        this.future = new CompletableFuture<>();
        return this.future;
    }

    @Override
    public MadLib complete() {
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
    public Consumer<MadLib> getSuccessConsumer() {
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
        return Endpoint.MADLIBS;
    }

    @NotNull
    @Override
    public CompletionType getCompletion() {
        return type;
    }

    @NotNull
    @Override
    public CompletableFuture<MadLib> getFuture() {
        return future;
    }
}
