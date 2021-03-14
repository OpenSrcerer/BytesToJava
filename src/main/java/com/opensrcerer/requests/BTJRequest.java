package com.opensrcerer.requests;

import com.opensrcerer.util.CompletionType;
import com.opensrcerer.util.Endpoint;
import okhttp3.Callback;
import okhttp3.Request;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @param <X> Return type of request.
 * The main interface that is implemented by all BTJ Requests.
 */
public interface BTJRequest<X> extends Callback {

    // ***************************************************************
    // **                      ASYNCHRONOUS                         **
    // ***************************************************************

    /**
     * Complete a request asynchronously using a callback.
     * @param success Callback consumer that handles the return value.
     */
    void queue(Consumer<X> success);

    /**
     * Complete a request asynchronously using a callback.
     * @param success Callback consumer that handles the return value.
     * @param failure Callback consumer that handles Throwables.
     */
    void queue(Consumer<X> success, Consumer<Throwable> failure);

    /**
     * Complete a request asynchronously.
     * @return A CompletableFuture that encapsulates the requested data.
     */
    @NotNull CompletableFuture<X> submit();

    // ***************************************************************
    // **                       SYNCHRONOUS                         **
    // ***************************************************************

    /**
     * Block this thread until the request is completed.
     * @return The completed request.
     * @throws RuntimeException with a descriptive message of what went wrong.
     */
    @Nullable X complete();

    // ***************************************************************
    // **                        GETTERS                            **
    // ***************************************************************

    /**
     * @return The OkHttp Request of this BTJRequest.
     */
    @NotNull Request getRequest();

    /**
     * @return The Future to this request if it
     */
    @Nullable CompletableFuture<X> getFuture();

    /**
     * @return The type of Endpoint this Request refers to.
     */
    @NotNull Endpoint getEndpoint();

    /**
     * @return The way this Request should be asynchronously completed (if at all).
     */
    @NotNull CompletionType getCompletion();
}
