package opensrcerer.util;

import opensrcerer.BTJ;
import opensrcerer.requestEntities.TokenInfo;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Prevents the user from blocking their token by executing too many requests.
 */
public class BTJRatelimiter {
    /**
     * Used to block the queue and control requests as so they do not exceed the rate-limit quota.
     */
    private final Semaphore semaphore;

    /**
     * The number of requests it takes for the API to rate-limit the given token.
     */
    private final int rateLimitValue;

    private final ScheduledFuture<?> future;

    public BTJRatelimiter(BTJ btj, ScheduledExecutorService scheduledExecutor) {
        TokenInfo info = btj.getInfo().complete();

        // Save the ratelimit value
        rateLimitValue = info.getLimit();

        // Initialize fair semaphore with available permits equal to the remaining uses on the API
        semaphore = new Semaphore(rateLimitValue - info.getUses(), true);

        // Schedule action to reset semaphore permits
        future = scheduledExecutor.scheduleAtFixedRate(this::resetPermits,
                info.getNextReset() + 1, // Time until current next reset, offset by 1 to make sure API reset
                61, // For every other reset: Every minute
                TimeUnit.SECONDS);
    }

    /**
     * Acquires a permit synchronously from the semaphore if the given endpoint is ratelimited.
     * @throws InterruptedException If the thread was interrupted while waiting.
     */
    public synchronized int acquirePermit(Endpoint endpoint) throws InterruptedException {
        if (!endpoint.equals(Endpoint.INFO)) {
            semaphore.acquire();
        }
        return semaphore.availablePermits();
    }

    /**
     * Releases enough permits in the semaphore to equal the token limit.
     */
    private void resetPermits() {
        semaphore.release(rateLimitValue - semaphore.availablePermits()); // Release the previously-consumed permits
    }

    public long getDelay() {
        return future.getDelay(TimeUnit.MILLISECONDS);
    }
}
