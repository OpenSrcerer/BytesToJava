package github.opensrcerer.consumers;

import github.opensrcerer.requestEntities.BTJReturnable;
import github.opensrcerer.requests.BTJRequest;
import github.opensrcerer.util.JSONParser;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Represents an operation that takes in a BTJReturnable returned from an asynchronous source, and returns no value.
 */
public final class BTJConsumer<X extends BTJReturnable> implements Callback {

    /**
     * BTJRequest that created this Consumer.
     */
    private final BTJRequest<X> request;

    /**
     * Consumer to handle successful callbacks.
     */
    private final Consumer<X> success;

    /**
     * Consumer to handle failed callbacks.
     */
    private final Consumer<Throwable> failure;

    /**
     * Create a new BTJConsumer with a default failure Consumer.
     * @param success Action to take with returned object upon success.
     */
    public BTJConsumer(BTJRequest<X> request, Consumer<X> success) {
        this.request = request;
        this.success = success;
        this.failure = throwable -> {};
    }

    /**
     * Create a new BTJConsumer with a default failure Consumer.
     * @param success Action to take with returned object upon failure.
     */
    public BTJConsumer(BTJRequest<X> request, Consumer<X> success, Consumer<Throwable> failure) {
        this.request = request;
        this.success = success;
        this.failure = failure;
    }

    /**
     * @param returnable Accept a returnable on successful callback & parsing of BTJReturnable.
     */
    public void succeed(X returnable) {
        success.accept(returnable);
    }

    /**
     * @param throwable Accept a Throwable on failed execution.
     */
    public void fail(Throwable throwable) {
        failure.accept(throwable);
    }

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) {
        JSONParser.matchAsynchronous(request, response);
    }

    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException ex) {
        failure.accept(ex);
    }
}
