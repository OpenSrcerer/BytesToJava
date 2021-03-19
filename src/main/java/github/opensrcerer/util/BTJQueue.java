package github.opensrcerer.util;

import github.opensrcerer.BTJ;
import github.opensrcerer.requestEntities.BTJReturnable;
import github.opensrcerer.requests.BTJRequest;
import okhttp3.Response;

import javax.security.auth.login.LoginException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class BTJQueue {

    /**
     * BTJ instance for this Queue.
     */
    private final BTJ btj;

    /**
     * Pool of threads to drain requests queue.
     */
    private final ExecutorService executor;

    /**
     * The ratelimiter for this queue.
     */
    private final BTJRatelimiter ratelimiter;

    /**
     * Queue for BTJRequests.
     */
    private final LinkedBlockingQueue<BTJRequest<? extends BTJReturnable>> requests = new LinkedBlockingQueue<>();

    public BTJQueue(BTJ btj, ExecutorService executor, ScheduledExecutorService scheduledExecutor) throws LoginException {
        this.btj = btj;
        this.executor = executor;
        this.ratelimiter = new BTJRatelimiter(btj, scheduledExecutor);

        // Runnable to execute while spinning
        Runnable drainQueue = () -> {
            while (true) {
                if (executor.isShutdown()) {
                    btj.getLogger().debug(Thread.currentThread().getName() + " - Shutting down.");
                    return;
                }

                final BTJRequest<? extends BTJReturnable> request;
                try {
                    request = requests.poll(5000, TimeUnit.MILLISECONDS); // Take a request from the request queue
                    if (request == null) {
                        continue;
                    }

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
                    }

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
        };

        // Submit twice for two threads
        this.executor.submit(drainQueue);
        this.executor.submit(drainQueue);
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
}
