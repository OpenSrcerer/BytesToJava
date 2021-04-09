package opensrcerer.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Response;
import opensrcerer.requestEntities.*;
import opensrcerer.requests.BTJRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Provides static methods to Parse incoming JSON payloads from the BytesToBits API.
 */
public class BTJParser {

    /**
     * Logger for JSONParser.
     */
    private static final Logger lgr = LoggerFactory.getLogger(BTJParser.class);

    /**
     * Mapper that correspondingly matches JSON values to a Java class.
     */
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * The queue associated with this parser.
     */
    private static BTJQueue queue = null;

    /**
     * Parses an OkHttp Response into a bean, then returns the constructed bean immediately.
     * @param request The originating request that issued a response.
     * @param response The response of the given request.
     * @param <X> The type of the return object.
     * @return The return object of the matching type.
     * @throws Exception If parsing went wrong, or the response was invalid.
     */
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public static <X extends BTJReturnable> X matchSynchronous(@NotNull BTJRequest<X> request, Response response) throws Exception {
        if (request.getCompletion() != CompletionType.SYNCHRONOUS) {
            throw new IllegalArgumentException("Invalid Request type for call.");
        }

        checkResponse(response);
        lgr.debug("Successful response for request " + request.getEndpoint().getEndpointName() + " returned code " + response.code());

        final String JSONBody = response.body().string();
        for (Method m : BTJParser.class.getDeclaredMethods()) {
            if (m.getName().equals(request.getEndpoint().getDeclaredMethod())) {
                return (X) m.invoke(null, JSONBody);
            }
        }
        throw new IllegalArgumentException("Unexpected completion type");
    }

    /**
     * Parses an OkHttp Response into a bean, then returns the constructed bean asynchronously using callbacks or futures,
     * depending on the type of given request.
     * @param request The originating request that issued a response.
     * @param response The response of the given request.
     * @param <X> The type of the return object.
     */
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public static <X extends BTJReturnable> void matchAsynchronous(@NotNull BTJRequest<X> request, Response response) {
        Objects.requireNonNull(request.getAsync());

        try {
            checkResponse(response);
            lgr.debug("Successful response for request " + request.getEndpoint().getEndpointName() + " returned code " + response.code());

            final String JSONBody = response.body().string();
            for (Method m : BTJParser.class.getDeclaredMethods()) {
                if (m.getName().equals(request.getEndpoint().getDeclaredMethod())) {
                    X returnable = (X) m.invoke(null, JSONBody);
                    switch (request.getCompletion()) {
                        case FUTURE -> request.getAsync().getFuture().complete(returnable);
                        case CALLBACK -> request.getAsync().getConsumer().succeed(returnable);
                        case SYNCHRONOUS -> throw new RuntimeException("Unexpected completion type in asynchronous match:" + request.getCompletion());
                    }
                    return;
                }
            }
            throw new IllegalArgumentException(request.getCompletion().toString());
        } catch (Exception ex) {
            lgr.debug(String.valueOf(ex));
            switch (request.getCompletion()) {
                case FUTURE -> request.getAsync().getFuture().completeExceptionally(ex);
                case CALLBACK -> request.getAsync().getConsumer().fail(ex);
                case SYNCHRONOUS -> throw new RuntimeException("Unexpected completion type in asynchronous match:" + request.getCompletion());
            }
        }
    }

    /**
     * Takes the appropriate action if the given OkHttp response is not successful.
     * @param response Given OkHttp response to check.
     * @throws LoginException If the token provided to access the API was invalid.
     */
    private static void checkResponse(@NotNull Response response) throws Exception {
        if (!response.isSuccessful()) {
            if (response.code() == 401) {
                throw new LoginException("Failed to login because the token used to access the API was invalid.");
            } else if (response.code() == 429) { // Rate limited, fall back
                queue.fallback(); // Sets the queue in fallback mode.
            }
            throw new RuntimeException("Request failed: Code " + response.code() + " " + response.message());
        }
    }

    /**
     * Setter for the queue reference of this class.
     * @param queue Queue to set.
     */
    public static void setQueue(BTJQueue queue) {
        BTJParser.queue = queue;
    }

    // ***************************************************************
    // **                METHODS INVOKED REFLECTIVELY               **
    // ***************************************************************

    /**
     * Parses a given json into a RandomWord bean.
     * @param json JSON extracted from a Response.
     * @return A parsed RandomWord.
     */
    @NotNull
    public static RandomWord mapToRandomWord(final String json) {
        return new RandomWord(json.substring(1, json.length() - 2));
    }

    /**
     * Parses a given json into a RandomText bean.
     * @param json JSON extracted from a Response.
     * @return A parsed RandomText.
     */
    @NotNull
    public static RandomText mapToRandomText(final String json) {
        return new RandomText(json.substring(1, json.length() - 2));
    }

    /**
     * Parses a given json into a MadLib.
     * @param json JSON extracted from a Response.
     * @return A parsed MadLib.
     * @throws IOException If MadLib could not be mapped.
     */
    @Nullable
    public static MadLib mapToMadLib(final String json) throws IOException {
        return mapper.readValue(json, MadLib.class);
    }

    /**
     * Parses a given json into a RedditMeme.
     * @param json JSON extracted from a Response.
     * @return A parsed RedditMeme.
     * @throws IOException If RedditMeme could not be mapped.
     */
    @Nullable
    public static RedditMeme mapToRedditMeme(final String json) throws IOException {
        return mapper.readValue(json, RedditMeme.class);
    }

    /**
     * Parses a given json into a RedditPosts list.
     * @param json JSON extracted from a Response.
     * @return A parsed RedditPosts list.
     * @throws IOException If RedditPosts could not be mapped.
     */
    @Nullable
    public static RedditPosts mapToRedditPosts(final String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, new TypeReference<>() {});
    }

    /**
     * Parses a given json into a SongLyrics bean.
     * @param json JSON extracted from a Response.
     * @return A parsed SongLyrics.
     * @throws IOException If SongLyrics could not be mapped.
     */
    @Nullable
    public static SongLyrics mapToSongLyrics(final String json) throws IOException {
        return mapper.readValue(json, SongLyrics.class);
    }

    /**
     * Parses a given json into a TokenInfo bean.
     * @param json JSON extracted from a Response.
     * @return A parsed TokenInfo.
     * @throws IOException If TokenInfo could not be mapped.
     */
    @Nullable
    public static TokenInfo mapToTokenInfo(final String json) throws IOException {
        return mapper.readValue(json, TokenInfo.class);
    }
}
