package github.opensrcerer.util;

import github.opensrcerer.BTJ;
import github.opensrcerer.requestEntities.BTJReturnable;
import github.opensrcerer.requests.BTJRequest;
import okhttp3.Response;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
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
     * Queue for BTJRequests.
     */
    private final LinkedBlockingQueue<BTJRequest<? extends BTJReturnable>> requests = new LinkedBlockingQueue<>();

    /**
     * Boolean condition that signifies whether the queue is currently falling back until next reset to prevent ratelimiting.
     */
    private boolean fallback = false;

    public BTJQueue(BTJ btj, ExecutorService executor) {
        this.btj = btj;
        this.executor = executor;

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

    /**
     * @return Whether the request queue is currently awaiting until next reset to request.
     */
    public boolean isFallback() {
        return fallback;
    }
}
