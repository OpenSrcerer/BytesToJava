package com.opensrcerer.util;

import com.opensrcerer.requests.BTJRequest;
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
     * Pool of threads to drain requests queue.
     */
    private final ExecutorService executor;

    /**
     * Queue for BTJRequests.
     */
    private final LinkedBlockingQueue<BTJRequest<?>> requests = new LinkedBlockingQueue<>();

    public BTJQueue(ExecutorService executor) {
        this.executor = executor;

        Runnable drainCommands = () -> {
            while (true) {
                if (Thread.interrupted()) {
                    return;
                }

                BTJRequest<?> requestToProcess;
                try {
                    requestToProcess = requests.take(); // Take a request from the request queue

                    // Identify Request type.


                    requestToProcess.run(); // Run the command
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
        executor.submit(drainCommands);
        executor.submit(drainCommands);
    }

    public void addRequest(BTJRequest<?> request) throws InterruptedException {
        requests.put(request);
    }
}
