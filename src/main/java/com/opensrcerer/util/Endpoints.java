package com.opensrcerer.util;

public enum Endpoints {
    WORD("/word/"),
    TEXT("/text/"),
    MADLIBS("/madlibs/"),
    MEME("/meme/"),
    LYRICS("/lyrics/"),
    REDDIT("/reddit/");

    private final String endpointName;

    Endpoints(String endpointName) {
        this.endpointName = endpointName;
    }

    /**
     * @return The String that matches the URL of an endpoint.
     */
    public String getName() {
        return endpointName;
    }
}
