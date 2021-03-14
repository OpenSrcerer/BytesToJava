package com.opensrcerer.requests;

import com.opensrcerer.BTJ;
import com.opensrcerer.requestEntities.RandomWord;
import com.opensrcerer.util.CompletionType;
import com.opensrcerer.util.Endpoint;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class WordRequest implements BTJRequest<RandomWord> {

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
    private Consumer<RandomWord> success;

    /**
     * Consumer to handle failed callbacks.
     */
    private Consumer<Throwable> failure;

    /**
     * CompletableFuture in case of usage of .submit();
     */
    private CompletableFuture<RandomWord> future;

    /**
     * The way this Request should be asynchronously executed (if at all).
     */
    private CompletionType type;

    public WordRequest(BTJ btj) {
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
    public void queue(Consumer<RandomWord> success) {

    }

    @Override
    public void queue(Consumer<RandomWord> success, Consumer<Throwable> failure) {

    }

    @NotNull
    @Override
    public CompletableFuture<RandomWord> submit() {
        type = CompletionType.SUBMIT;
        this.future = new CompletableFuture<>();
        return this.future;
    }

    @Override
    public RandomWord complete() {
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
    public Consumer<RandomWord> getSuccessConsumer() {
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
        return Endpoint.WORD;
    }

    @NotNull
    @Override
    public CompletionType getCompletion() {
        return type;
    }

    @NotNull
    @Override
    public CompletableFuture<RandomWord> getFuture() {
        return future;
    }
}
