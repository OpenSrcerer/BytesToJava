package com.opensrcerer.util;

import com.opensrcerer.BTJ;
import com.opensrcerer.requestEntities.BTJReturnable;
import com.opensrcerer.requests.*;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

public final class BTJQueue {

    /**
     * Logger for this queue.
     */
    private final Logger lgr = LoggerFactory.getLogger(BTJQueue.class);

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
    private final LinkedBlockingQueue<BTJRequest<BTJReturnable>> requests = new LinkedBlockingQueue<>();

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
                if (Thread.interrupted() || executor.isShutdown()) {
                    return;
                }

                final BTJRequest<BTJReturnable> request;
                try {
                    request = requests.take(); // Take a request from the request queue

                    // Identify Request completion type and init
                    switch (request.getCompletion()) {
                        case SUBMIT -> {
                            try {
                                Response response = btj.getClient().newCall(request.getRequest()).execute();
                                JSONParser.matchRequest(request, response);
                            } catch (Exception ex) {
                                request.getFuture().completeExceptionally(ex);
                            }
                        }
                        case QUEUE -> btj.getClient().newCall(request.getRequest()).enqueue(request);
                    }

                } catch (Exception ex) {
                    // Other exceptions
                    lgr.error(Thread.currentThread().getName() + " encountered an exception:", ex);
                } catch (Error err) {
                    // Fatal Error, terminate program
                    lgr.error("A fatal error was thrown. Details:", err);
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
    public void addRequest(BTJRequest<BTJReturnable> request) {
        try {
            requests.put(request);
        } catch (InterruptedException ex) {
            lgr.warn("Request queue interrupted while waiting to insert Request!");
        }
    }

    /**
     * @return Whether the request queue is currently awaiting until next reset to request.
     */
    public boolean isFallback() {
        return fallback;
    }
}
