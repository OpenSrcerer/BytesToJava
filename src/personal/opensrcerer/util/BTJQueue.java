package opensrcerer.util;

import opensrcerer.BTJImpl;
import opensrcerer.requestEntities.BTJReturnable;
import opensrcerer.requests.BTJRequest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

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
     * Queue for BTJRequests.
     */
    private final LinkedBlockingQueue<BTJRequest<? extends BTJReturnable>> requests;

    public BTJQueue(BTJImpl btj, ExecutorService executor) {
        this.btj = btj;
        this.executor = executor;
        this.requests = new LinkedBlockingQueue<>();

        // Create workers
        executor.submit(new BTJRunnable(btj, this));
        executor.submit(new BTJRunnable(btj, this));
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
            btj.getLogger().warn("Request queue interrupted while awaiting insertion!");
        }
    }

    /**
     * @return A BTJRequest in the queue.
     * @throws InterruptedException If thread was interrupted whilst waiting for request.
     */
    public BTJRequest<? extends BTJReturnable> takeRequest() throws InterruptedException {
        return requests.take();
    }
}
