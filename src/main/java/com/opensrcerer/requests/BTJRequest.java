package com.opensrcerer.requests;

import java.util.concurrent.CompletableFuture;

/**
 * @param <X> Return type of request.
 * The main interface that is implemented by all BTJ Requests.
 */
public interface BTJRequest<X> {

    // ***************************************************************
    // **                      ASYNCHRONOUS                         **
    // ***************************************************************

    /**
     * Complete a request asynchronously.
     * @return A CompletableFuture that encapsulates the requested data.
     * @throws RuntimeException with a descriptive message of what went wrong.
     */
    CompletableFuture<X> queue() throws RuntimeException;

    // ***************************************************************
    // **                       SYNCHRONOUS                         **
    // ***************************************************************

    /**
     * Block this thread until the request is completed.
     * @return The completed request.
     * @throws RuntimeException with a descriptive message of what went wrong.
     */
    X complete() throws RuntimeException;
}
