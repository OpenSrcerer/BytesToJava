package com.opensrcerer;

import java.util.concurrent.ExecutorService;

/**
 * Interface for the BytesToJava API.
 * Contains factory methods to build an instance of the
 * BTJImpl class.
 */
public interface BTJ {
    /**
     * @return Get a BTJ instance with default settings.
     */
    static BTJImpl getBTJ() {
        return new BTJImpl();
    }

    /**
     * @param service ExecutorService to supply.
     * @return Get a BTJ instance that executes requests on the provided ExecutorService.
     *         Only use this if you know what you're doing.
     */
    static BTJImpl getBTJ(ExecutorService service) {
        return new BTJImpl(service);
    }


}
