package com.opensrcerer.consumers;

import com.opensrcerer.requestEntities.BTJReturnable;
import com.opensrcerer.requests.BTJRequest;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class BTJAsync<X extends BTJReturnable> {

    /**
     * BTJConsumer in case of usage of .queue(). Handles callbacks.
     */
    private final BTJConsumer<X> consumer;

    /**
     * CompletableFuture in case of usage of .submit().
     */
    private final CompletableFuture<X> future;

    /**
     * Constructs a new BTJAsync object that uses futures.
     */
    public BTJAsync() {
        this.consumer = null;
        this.future = new CompletableFuture<>();
    }

    /**
     * Constructs a new BTJAsync object that uses callbacks.
     */
    public BTJAsync(BTJRequest<X> request, Consumer<X> success, @Nullable Consumer<Throwable> fail) {
        this.future = null;
        if (fail == null) {
            this.consumer = new BTJConsumer<>(request, success);
        } else {
            this.consumer = new BTJConsumer<>(request, success, fail);
        }
    }

    /**
     * @return The CompletableFuture to this Async request.
     */
    public CompletableFuture<X> getFuture() {
        return future;
    }

    /**
     * @return The Consumer to this Async request.
     */
    public BTJConsumer<X> getConsumer() {
        return consumer;
    }
}
