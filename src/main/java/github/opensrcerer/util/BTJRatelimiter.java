package github.opensrcerer.util;

import github.opensrcerer.BTJ;
import github.opensrcerer.requestEntities.TokenInfo;

import javax.security.auth.login.LoginException;
import java.util.concurrent.ScheduledExecutorService;
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

        scheduledExecutor.scheduleAtFixedRate(r, tokenInfo.getNextReset(), 60, TimeUnit.SECONDS);
    }

    /**
     * Increase the uses of the API by 1.
     */
    public void incrementUses() {
        int u = uses.getAndIncrement();
        if (u >= tokenInfo.getLimit()) {
            fallback.set(true);
        }
    }

    /**
     * @return Whether the request queue should fallback.
     */
    public boolean getFallback() {
        return fallback.get();
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
