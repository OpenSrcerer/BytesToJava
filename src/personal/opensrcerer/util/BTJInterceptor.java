package opensrcerer.util;

import okhttp3.Interceptor;
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

    private static final Logger lgr = LoggerFactory.getLogger(BTJInterceptor.class);

    /**
     * Whether the chain is currently falling back. This value gets modified by a scheduled runnable.
     */
    private final AtomicBoolean fallback;

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
        this.fallback = new AtomicBoolean(false);
        this.releaseScheduler = service;
        this.btj = btj;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Response original = chain.proceed(chain.request());

        // If request is from info endpoint, do not block
        if (original.request().url().pathSegments().stream().anyMatch(s -> s.equals("info"))) {
            return original;
        }

        try { // await fallback if necessary
            synchronized (fallback) {
                while (fallback.get()) {
                    fallback.wait();
                }
            }
        } catch (InterruptedException ignored) {}

        if (!original.isSuccessful() && original.code() == 429) { // Handle return as normal
            lgr.debug("429 Received! Currently falling back.");
            TokenInfo info = btj.getInfo().complete(); // Make synchronous request to receive token info
            fallback.set(true); // Interceptor makes requests wait

            releaseScheduler.schedule(() -> {
                fallback.set(false);
                synchronized (fallback) {
                    fallback.notifyAll(); // release interceptor
                }
            }, info.getNextReset() + 1, TimeUnit.SECONDS);

            original = chain.proceed(chain.request());
        }

        return original;
    }
}
