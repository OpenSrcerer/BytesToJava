package opensrcerer.util;

import okhttp3.Response;
import opensrcerer.BTJImpl;
import opensrcerer.requestEntities.BTJReturnable;
import opensrcerer.requests.BTJRequest;

public class BTJRunnable implements Runnable {

    /**
     * BTJ instance for this worker.
     */
    private final BTJImpl btj;

    /**
     * BTJ request queue..
     */
    private final BTJQueue queue;

    /**
     * Create a new BTJRunnable with the given params.
     */
    public BTJRunnable(BTJImpl btj, BTJQueue queue) {
        this.btj = btj;
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                final BTJRequest<? extends BTJReturnable> request = queue.takeRequest(); // Take a request from the request queue

                queue.checkExecution(); // Block before execution

                switch (request.getCompletion()) { // Identify Request completion type and init
                    case FUTURE -> {
                        try {
                            // Await synchronous response
                            Response response = btj.getClient().newCall(request.getRequest()).execute();
                            btj.getLogger().debug("Received response for Request of type " + CompletionType.FUTURE.name());
                            BTJParser.matchAsynchronous(request, response);
                        } catch (Exception ex) {
                            // Exception handling, complete future exceptionally
                            btj.getLogger().debug("Received exception for Request of type " + CompletionType.FUTURE.name());
                            request.getAsync().getFuture().completeExceptionally(ex);
                        }
                    }
                    // Dispatch async call to the OkHttpClient dispatcher
                    case CALLBACK -> btj.getClient().newCall(request.getRequest()).enqueue(request.getAsync().getConsumer());
                    default -> throw new RuntimeException("Invalid Request type in queue.");
                }

                if (queue.getExecutor().isShutdown()) { // Exit if the executor was shutdown
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
