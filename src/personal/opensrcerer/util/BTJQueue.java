package personal.opensrcerer.util;

import personal.opensrcerer.BTJ;
import personal.opensrcerer.requestEntities.BTJReturnable;
import personal.opensrcerer.requests.BTJRequest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

public final class BTJQueue {

    /**
     * BTJ instance for this Queue.
     */
    private final BTJ btj;

    /**
     * Pool of threads to drain requests queue.
     */
    private final ScheduledExecutorService executor;

    /**
     * Queue for BTJRequests.
     */
    private final LinkedBlockingQueue<BTJRequest<? extends BTJReturnable>> requests;

    private final BTJRatelimiter limiter;

    public BTJQueue(BTJ btj, ScheduledExecutorService executor, ScheduledExecutorService scheduledExecutor) {
        this.btj = btj;
        this.executor = executor;
        this.requests = new LinkedBlockingQueue<>();

        // BTJRatelimiter to be used for these workers
        BTJRatelimiter ratelimiter = new BTJRatelimiter(btj, scheduledExecutor);
        limiter = ratelimiter;

        // Create workers
        new BTJRunnable(btj, executor, ratelimiter, requests);
        new BTJRunnable(btj, executor, ratelimiter, requests);
    }

    /**
     * @return The ExecutorService of this queue.
     */
    public ExecutorService getExecutor() {
        return executor;
    }

    /**
     * Add a Request to the request queue.
     * @param request BTJRequest to insert into the queue.
     */
    public void addRequest(BTJRequest<? extends BTJReturnable> request) {
        try {
            requests.put(request);
        } catch (InterruptedException ex) {
            btj.getLogger().warn("Request queue interrupted while waiting to insert Request!");
        }
    }

    public long getDelay() {
        return limiter.getDelay();
    }
}
