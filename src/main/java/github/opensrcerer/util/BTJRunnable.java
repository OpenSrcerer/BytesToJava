package github.opensrcerer.util;

import github.opensrcerer.BTJ;
import github.opensrcerer.requestEntities.BTJReturnable;
import github.opensrcerer.requests.BTJRequest;
import okhttp3.Response;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

public class BTJRunnable implements Runnable {

    /**
     * BTJ instance for this Queue.
     */
    private final BTJ btj;

    /**
     * Pool of threads to drain requests queue.
     */
    private final ScheduledExecutorService executor;

    /**
     * The ratelimiter for this worker.
     */
    private final BTJRatelimiter ratelimiter;

    /**
     * Queue for BTJRequests.
     */
    private final LinkedBlockingQueue<BTJRequest<? extends BTJReturnable>> queue;

    /**
     * Shows whether this Runnable should park the thread.
     */
    private final AtomicBoolean parkThread = new AtomicBoolean(false);

    public BTJRunnable(BTJ btj, ScheduledExecutorService executor,
                       BTJRatelimiter ratelimiter, LinkedBlockingQueue<BTJRequest<? extends BTJReturnable>> queue) {
        this.btj = btj;
        this.executor = executor;
        this.ratelimiter = ratelimiter;
        this.queue = queue;
        executor.submit(this);
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (executor.isShutdown()) {
                    btj.getLogger().debug(Thread.currentThread().getName() + " - Shutting down.");
                    return;
                }

                if (parkThread.get()) {
                    ratelimiter.addAwaiting();
                    LockSupport.park(ratelimiter);
                }

                // Take a request from the request queue
                final BTJRequest<? extends BTJReturnable> request = queue.take();

                // Identify Request completion type and init
                switch (request.getCompletion()) {
                    case FUTURE -> {
                        try {
                            // Await synchronous response on worker thread
                            Response response = btj.getClient().newCall(request.getRequest()).execute();
                            btj.getLogger().debug("Received response for Request of type " + CompletionType.FUTURE.name());
                            JSONParser.matchAsynchronous(request, response);
                        } catch (Exception ex) {
                            // Exception handling, complete future exceptionally
                            btj.getLogger().debug("Received exception for Request of type " + CompletionType.FUTURE.name());
                            request.getAsync().getFuture().completeExceptionally(ex);
                        }
                    }
                    // Dispatch async call to the OkHttpClient dispatcher
                    case CALLBACK -> btj.getClient().newCall(request.getRequest()).enqueue(request.getAsync().getConsumer());
                    default -> throw new RuntimeException("Invalid Request type in queue");
                }

                // Increment uses every spin cycle
                ratelimiter.incrementUses(request.getEndpoint());

            } catch (InterruptedException ex) {
                btj.getLogger().debug(Thread.currentThread().getName() + " - Shutting down.");
                return; // Interrupted
            } catch (Exception ex) {
                // Other exceptions
                btj.getLogger().error(Thread.currentThread().getName() + " encountered an exception:", ex);
            } catch (Error err) {
                // Fatal Error, terminate instance
                btj.getLogger().error("A fatal error was thrown. BTJ instance terminating. Details:", err);
                btj.shutdownNow();
            }
        }
    }

    public void park() {
        parkThread.set(true);
    }

    public void unpark() {
        parkThread.set(false);
    }
}
