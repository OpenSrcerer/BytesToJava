package personal.opensrcerer.requests;

import personal.opensrcerer.consumers.BTJAsync;
import personal.opensrcerer.requestEntities.BTJReturnable;
import personal.opensrcerer.util.CompletionType;
import personal.opensrcerer.util.Endpoint;
import okhttp3.Request;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @param <X> Type of return value that this request has.
 * The main interface that is implemented by all BTJ Requests.
 */
public interface BTJRequest<X extends BTJReturnable> {

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
     * @param failure Callback consumer that handles Throwable-s.
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
     */
    @NotNull
    X complete();

    // ***************************************************************
    // **                        GETTERS                            **
    // ***************************************************************

    /**
     * @return The OkHttp Request of this BTJRequest.
     */
    @NotNull Request getRequest();

    /**
     * Return the BTJAsync object of this BTJRequest.
     */
    @Nullable
    BTJAsync<X> getAsync();

    /**
     * @return The type of Endpoint this Request refers to.
     */
    @NotNull Endpoint getEndpoint();

    /**
     * @return The way this Request should be asynchronously completed (if at all).
     */
    @NotNull
    CompletionType getCompletion();
}
