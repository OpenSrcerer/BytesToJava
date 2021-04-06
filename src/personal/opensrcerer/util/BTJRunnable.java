package opensrcerer.util;

import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import opensrcerer.BTJ;
import opensrcerer.requestEntities.BTJReturnable;
import opensrcerer.requests.BTJRequest;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

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

    private final Logger lgr = LoggerFactory.getLogger(this.getClass());

    /**
     * Create a new BTJRunnable with the given params.
     */
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
                final BTJRequest<? extends BTJReturnable> request = queue.take(); // Take a request from the request queue

                lgr.debug("Took Permit! Remaining: " + ratelimiter.acquirePermit(request.getEndpoint())); // Acquire a permit from the ratelimiter (blocks thread)

                switch (request.getCompletion()) { // Identify Request completion type and init
                    case FUTURE: {
                        try {
                            // Await synchronous response
                            Response response = btj.getClient().newCall(request.getRequest()).execute();
                            btj.getLogger().debug("Received response for Request of type " + CompletionType.FUTURE.name());
                            JSONParser.matchAsynchronous(request, response);
                        } catch (Exception ex) {
                            // Exception handling, complete future exceptionally
                            btj.getLogger().debug("Received exception for Request of type " + CompletionType.FUTURE.name());
                            request.getAsync().getFuture().completeExceptionally(ex);
                        }
                        break;
                    }
                    // Dispatch async call to the OkHttpClient dispatcher
                    case CALLBACK:
                        btj.getClient().newCall(request.getRequest()).enqueue(request.getAsync().getConsumer());
                        break;
                    default:
                        throw new RuntimeException("Invalid Request type in queue");
                }

                if (executor.isShutdown()) { // Exit if the executor was shutdown
                    btj.getLogger().debug(Thread.currentThread().getName() + " - Shutting down.");
                    return;
                }

            } catch (InterruptedException ex) {
                btj.getLogger().debug(Thread.currentThread().getName() + " - Shutting down by interruption.");
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
}
