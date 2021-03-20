package github.opensrcerer.util;

import github.opensrcerer.BTJ;
import github.opensrcerer.requestEntities.TokenInfo;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

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
     * Thread queue for worker threads that are awaiting for the fallback timer to end.
     */
    private final Queue<Thread> waiters = new ConcurrentLinkedQueue<>();

    /**
     * An AtomicInteger that carries the number of requests made to the BTB API for rate limit check purposes.
     */
    private final AtomicInteger uses = new AtomicInteger(0);

    /**
     * An AtomicInteger that carries the time until the next use reset for the BTB API.
     */
    private final AtomicInteger nextReset = new AtomicInteger(0);

    /**
     * Array that contains worker BTJRunnables.
     */
    private BTJRunnable[] runnables;


    public BTJRatelimiter(BTJ btj, ScheduledExecutorService scheduledExecutor) {
        this.btj = btj;
        this.scheduledExecutor = scheduledExecutor;
    }

    /**
     * Increments the number of performed requests.
     */
    public synchronized void incrementUses(Endpoint endpoint) {
        // Do not increment INFO calls as they do not increase the use counter
        if (endpoint.equals(Endpoint.INFO)) {
            return;
        }

        int u = uses.incrementAndGet();
        System.out.println("Total uses: " + u);

        if (u >= 19) {
            syncTokenInfo();
            System.out.println("Threads parked - Uses: " + u + " Fallback time: " + nextReset.get() * 1000);
            Arrays.stream(runnables).forEach(BTJRunnable::park); // Flag all runnables

            scheduledExecutor.schedule(() -> {
                Arrays.stream(runnables).forEach(BTJRunnable::unpark); // Unflag all runnables

                uses.set(0); // Reset uses
                // Unparks all waiting threads
                while (true) {
                    Thread thread = waiters.poll();
                    if (thread == null) {
                        break;
                    }
                    System.out.println("Thread unparked - " + thread.getName());
                    LockSupport.unpark(thread);
                }
            }, (nextReset.get() + 1) /*One second added to prevent time drifts*/ * 1000L, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Updates the TokenInfo instance.
     */
    public void syncTokenInfo() {
        // Object that carries information about the current token.
        TokenInfo tokenInfo = btj.getInfo().complete();
        nextReset.set(tokenInfo.getNextReset());
    }

    /**
     * Method to be called by worker threads before they are about
     * to be parked on the queue.
     */
    public void addAwaiting() {
        this.waiters.add(Thread.currentThread());
    }

    public void setWorkers(BTJRunnable... workers) {
        this.runnables = workers;
    }
}
