package com.opensrcerer.requests;

import com.opensrcerer.util.CompletionType;
import com.opensrcerer.util.Endpoint;
import okhttp3.Callback;

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
    CompletableFuture<X> submit();

    // ***************************************************************
    // **                       SYNCHRONOUS                         **
    // ***************************************************************

    /**
     * Block this thread until the request is completed.
     * @return The completed request.
     * @throws RuntimeException with a descriptive message of what went wrong.
     */
    X complete();

    // ***************************************************************
    // **                          OTHER                            **
    // ***************************************************************

    /**
     * @return The type of Endpoint this Request refers to.
     */
    Endpoint getEndpoint();

    /**
     * @return The way this Request should be asynchronously completed (if at all).
     */
    CompletionType getCompletion();
}
