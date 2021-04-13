package opensrcerer.util;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import opensrcerer.BTJImpl;
import opensrcerer.requestEntities.TokenInfo;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manages the BTJ OkHttp Request Queue.
 */
public class BTJInterceptor implements Interceptor {

    /**
     * Interceptor's Logger.
     */
    private static final Logger lgr = LoggerFactory.getLogger(BTJInterceptor.class);

    /**
     * Whether the chain is currently falling back. This value gets modified by a scheduled runnable.
     */
    private static final AtomicBoolean fallback = new AtomicBoolean(false);

    /**
     * Single-threaded executor used to assign a timed job in changing the fallback value.
     */
    private final ScheduledExecutorService releaseScheduler;

    /**
     * The BTJ instance for this Interceptor.
     */
    private final BTJImpl btj;

    /**
     * Create a new BTJInterceptor to manage the incoming requests.
     */
    public BTJInterceptor(BTJImpl btj, ScheduledExecutorService service) {
        this.releaseScheduler = service;
        this.btj = btj;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();

        // If request is from info endpoint, do not block
        if (request.url().pathSegments().stream().anyMatch(s -> s.equals("info"))) {
            return chain.proceed(request); // execute request as usual
        }

        synchronized (fallback) {
            await();
            Response response = chain.proceed(request); // Get the response from the request after awaiting

            if (!response.isSuccessful() && response.code() == 429) { // Handle ratelimiting
                TokenInfo info = btj.getInfo().complete(); // Make synchronous request to receive token info

                lgr.debug("429 Received! Falling back for {} seconds", info.getNextReset());
                fallback.compareAndSet(false, true); // Interceptor makes requests wait

                releaseScheduler.schedule(() -> {
                    synchronized (fallback) {
                        fallback.compareAndSet(true, false);
                        fallback.notifyAll(); // release interceptor
                    }
                }, info.getNextReset() + 1, TimeUnit.SECONDS); // 70s because API is currently not synced.

                await();
                response.close(); // Close the previous response
                response = chain.proceed(request); // Retry request
            }

            return response; // Return the original response
        }
    }

    public void await() {
        try { // await fallback if necessary
            while (fallback.get()) {
                fallback.wait();
            }
        } catch (InterruptedException ignored) {}
    }
}
