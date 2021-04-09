package opensrcerer.util;

import opensrcerer.BTJImpl;
import opensrcerer.requestEntities.BTJReturnable;
import opensrcerer.requestEntities.TokenInfo;
import opensrcerer.requests.BTJRequest;

import javax.security.auth.login.LoginException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class BTJQueue {

    /**
     * BTJ instance for this Queue.
     */
    private final BTJImpl btj;

    /**
     * Pool of threads to drain requests queue.
     */
    private final ExecutorService executor;

    /**
     * Single-threaded executor used to assign a timed job in changing the fallback value.
     */
    private final ScheduledExecutorService releaseScheduler;

    /**
     * Queue for BTJRequests.
     */
    private final LinkedBlockingQueue<BTJRequest<? extends BTJReturnable>> requests;

    /**
     * Whether the queue is currently falling back. This value gets modified by a scheduled runnable.
     */
    private final AtomicBoolean fallback;

    public BTJQueue(BTJImpl btj, ExecutorService executor, ScheduledExecutorService scheduledExecutor) throws LoginException {
        // Firstly, make an /info/ request to the API to check whether it can be reached
        TokenInfo info;

        try {
            info = btj.getInfo().complete();
        } catch (Exception ex) {
            throw new LoginException("Could not access the API: " + ex.getMessage()); // Could not reach API
        }

        this.btj = btj;
        this.executor = executor;
        this.releaseScheduler = scheduledExecutor;
        this.requests = new LinkedBlockingQueue<>();

        if (info.getUses() >= info.getLimit()) {
            this.fallback = new AtomicBoolean(true); // Start queue falling back if BTJ instance is created whilst token is overused
        } else {
            this.fallback = new AtomicBoolean(false); // Start queue normally
        }

        // Create workers
        executor.submit(new BTJRunnable(btj, this));
        executor.submit(new BTJRunnable(btj, this));
        // Set the queue instance of BTJParser
        BTJParser.setQueue(this);
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

    /**
     * Sets the queue in fallback mode and schedules release.
     */
    public void fallback() {
        if (!fallback.get()) { // Only one thread notifies
            fallback.set(true);
            TokenInfo info = btj.getInfo().complete(); // Get token's current info

            releaseScheduler.schedule(() -> { // Schedule time to get out of fallback
                synchronized (fallback) { // Grab the lock of fallback
                    fallback.set(false); // Set fallback to false
                    fallback.notifyAll(); // Release all threads waiting on the fallback object.
                }
            }, info.getNextReset() + 1, TimeUnit.SECONDS);

            // Show 429 message to user
            btj.getLogger().debug("Encountered 429! Falling back for {} seconds.", info.getNextReset());
        }
    }

    /**
     * Check execution before request.
     */
    public void checkExecution() throws InterruptedException {
        synchronized (fallback) {
            while (fallback.get()) {
                fallback.wait(); // Make the thread wait on the AtomicBoolean object until it gets released
            }
        }
        // TODO FIND A WAY TO NOT SEND REQUEST TO DISPATCH IF SOME ASYNC REQUEST RETURNS 429
    }

    /**
     * @return A BTJRequest in the queue.
     * @throws InterruptedException If thread was interrupted whilst waiting for request.
     */
    public BTJRequest<? extends BTJReturnable> takeRequest() throws InterruptedException {
        return requests.take();
    }
}
