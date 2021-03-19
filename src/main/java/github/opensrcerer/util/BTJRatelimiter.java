package github.opensrcerer.util;

import github.opensrcerer.BTJ;
import github.opensrcerer.requestEntities.TokenInfo;

import javax.security.auth.login.LoginException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Prevents the user from blocking their token by executing too many requests.
 */
public class BTJRatelimiter {
    /**
     * The BTJ instance that manages this ratelimiter.
     */
    private final BTJ btj;

    /**
     * A scheduled executor that is used to manage the API timer reset.
     */
    private final ScheduledExecutorService scheduledExecutor;

    /**
     * An AtomicInteger that carries the number of requests made to the BTB API for rate limit check purposes.
     */
    private final AtomicInteger uses = new AtomicInteger(0);

    /**
     * Boolean condition that signifies whether the queue is currently falling back until next reset to prevent rate limiting.
     */
    private final AtomicBoolean fallback = new AtomicBoolean(false);

    /**
     * Future that will contain the scheduled action
     */
    private final ScheduledFuture<?> future;

    /**
     * Object that carries information about the current token.
     */
    private TokenInfo tokenInfo;

    public BTJRatelimiter(BTJ btj, ScheduledExecutorService scheduledExecutor) throws LoginException {
        this.btj = btj;
        this.scheduledExecutor = scheduledExecutor;
        syncTokenInfo();

        Runnable r = () -> {
            uses.set(0);
            fallback.set(false);
        };

        future = this.scheduledExecutor.scheduleAtFixedRate(r, tokenInfo.getNextReset(), 60, TimeUnit.SECONDS);
    }

    /**
     * Increments the number of performed requests.
     */
    public synchronized void incrementContinue() {
        int u = uses.incrementAndGet();
        System.out.println("Total uses: " + uses.get());

        if (u >= 19) {
            fallback.set(true);
            System.out.println("Falling Back - " + uses.get());
        }
    }

    /**
     * @return Whether the request queue should fallback.
     */
    public boolean fallback() {
        return fallback.get();
    }

    /**
     * @return For how long the queue should fall back (in ms).
     */
    public long getFallbackTime() {
        return future.getDelay(TimeUnit.MILLISECONDS);
    }

    /**
     * Updates the TokenInfo instance.
     * @throws LoginException If unable to log in to the API.
     */
    public void syncTokenInfo() throws LoginException {
        try {
            tokenInfo = btj.getInfo().complete();
            uses.set(tokenInfo.getUses());
        } catch (Exception ex) {
            throw new LoginException("Unable to access the API: " + ex);
        }
    }
}
