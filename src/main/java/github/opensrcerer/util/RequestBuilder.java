package github.opensrcerer.util;

import github.opensrcerer.BTJ;
import github.opensrcerer.requestEntities.BTJReturnable;
import github.opensrcerer.requests.BTJRequest;
import github.opensrcerer.requests.LyricsRequest;
import github.opensrcerer.requests.RedditPostsRequest;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class RequestBuilder {

    /**
     * BTJ that holds this RequestBuilder.
     */
    private final BTJ btj;

    /**
     * Token for the BTJ instance to create requests.
     */
    private final String token;

    public RequestBuilder(BTJ btj, String token) {
        this.token = token;
        this.btj = btj;
    }

    /**
     * @param request Endpoint to bind new HTTP Request to.
     * @return A new authorized Request bound to a specific endpoint.
     */
    public Request createHttpRequest(BTJRequest<? extends BTJReturnable> request) {
        Objects.requireNonNull(request);
        return new Request.Builder()
                .header("Authorization", token)
                .url(buildUrl(request))
                .build();
    }

    /**
     * @param request Request that contains the endpoint to attach to the URL.
     * @return The parsed HttpUrl.
     */
    private static HttpUrl buildUrl(BTJRequest<? extends BTJReturnable> request) {
        final HttpUrl url = HttpUrl.parse(Constants.API_URL.concat(request.getEndpoint().getEndpointName()));

        if (url == null) {
            throw new IllegalArgumentException();
        }

        HttpUrl.Builder urlBuilder = url.newBuilder();

        // Add query parameters for the endpoints that require them
        if (request.getEndpoint().equals(Endpoint.LYRICS)) {
            String song = ((LyricsRequest) request).getSongName();
            @Nullable String artist = ((LyricsRequest) request).getArtist();

            urlBuilder.addQueryParameter("song", song);
            if (artist != null) {
                urlBuilder.addQueryParameter("artist", artist);
            }

        } else if (request.getEndpoint().equals(Endpoint.REDDIT)) {
            String subreddit = ((RedditPostsRequest) request).getSubreddit();
            int posts = ((RedditPostsRequest) request).getLimit();
            urlBuilder.addQueryParameter("subreddit", subreddit);
            urlBuilder.addQueryParameter("limit", String.valueOf(posts));
        }

        return urlBuilder.build();
    }
}
