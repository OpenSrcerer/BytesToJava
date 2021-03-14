package com.opensrcerer.util;

import com.opensrcerer.requestEntities.TokenInfo;
import com.opensrcerer.requests.BTJRequest;
import com.opensrcerer.requests.LyricsRequest;
import com.opensrcerer.requests.RedditPostsRequest;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.jetbrains.annotations.Nullable;

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
     * @param request Endpoint to bind new HTTP Request to.
     * @return A new authorized Request bound to a specific endpoint.
     */
    public Request createHttpRequest(BTJRequest<?> request) {
        Objects.requireNonNull(request);
        return new Request.Builder()
                .header("Authorization", tokenInfo.getToken())
                .url(buildUrl(request))
                .build();
    }

    /**
     * @param request Request that contains the endpoint to attach to the URL.
     * @return The parsed HttpUrl.
     */
    private static HttpUrl buildUrl(BTJRequest<?> request) {
        final HttpUrl url = HttpUrl.parse(Constants.API_URL.concat(request.getEndpoint().getEndpointName()));

        if (url == null) {
            throw new IllegalArgumentException();
        }

        HttpUrl httpUrl = url.newBuilder().build();

        if (request instanceof LyricsRequest) {
            String song = ((LyricsRequest) request).getSongName();
            @Nullable String artist = ((LyricsRequest) request).getArtist();

            if (artist == null) {

            }

        } else if (request instanceof RedditPostsRequest) {
            String subreddit = ((RedditPostsRequest) request).getSubreddit();
            int posts = ((RedditPostsRequest) request).getNumber();
        }

        return url.newBuilder().build();
    }
}
