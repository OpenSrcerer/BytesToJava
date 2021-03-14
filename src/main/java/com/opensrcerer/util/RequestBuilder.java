package com.opensrcerer.util;

import com.opensrcerer.requestEntities.TokenInfo;
import okhttp3.HttpUrl;
import okhttp3.Request;

import java.util.Objects;

public final class RequestBuilder {
    /**
     * Object that carries information about the current token.
     */
    private final TokenInfo tokenInfo;

    public RequestBuilder(String token) {
        this.tokenInfo = new TokenInfo(token);
    }

    /**
     * @param endpoint Endpoint to bind new Request to.
     * @return A new authorized Request bound to a specific endpoint.
     */
    public Request createRequest(Endpoint endpoint) {
        Objects.requireNonNull(endpoint);
        return new Request.Builder()
                .header("Authorization", tokenInfo.getToken())
                .url(buildUrl(endpoint))
                .build();
    }

    /**
     * @param endpoint Endpoint to attach to URL.
     * @return The parsed HttpUrl.
     */
    private static HttpUrl buildUrl(Endpoint endpoint) {
        final HttpUrl url = HttpUrl.parse(Constants.API_URL.concat(endpoint.getEndpointName()));

        if (url == null) {
            throw new IllegalArgumentException();
        }

        return url.newBuilder().build();
    }
}
