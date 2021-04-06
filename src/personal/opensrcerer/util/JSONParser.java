package opensrcerer.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import opensrcerer.requestEntities.*;
import opensrcerer.requests.BTJRequest;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Provides static methods to Parse incoming JSON payloads from the BytesToBits API.
 */
public class JSONParser {

    /**
     * Logger for JSONParser.
     */
    private static final Logger lgr = LoggerFactory.getLogger(JSONParser.class);

    /**
     * Mapper that correspondingly matches JSON values to a Java class.
     */
    private static final ObjectMapper mapper = new ObjectMapper();


    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public static <X extends BTJReturnable> X matchSynchronous(@NotNull BTJRequest<X> request, Response response) throws Exception {
        if (request.getCompletion() != CompletionType.SYNCHRONOUS) {
            throw new IllegalArgumentException("Invalid Request type for call.");
        }

        checkResponse(response);
        lgr.debug("Successful response for request " + request.getEndpoint().getEndpointName() + " returned code " + response.code());

        final String JSONBody = response.body().string();
        for (Method m : JSONParser.class.getDeclaredMethods()) {
            if (m.getName().equals(request.getEndpoint().getDeclaredMethod())) {
                return (X) m.invoke(null, JSONBody);
            }
        }
        throw new IllegalArgumentException("Unexpected completion type");
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public static <X extends BTJReturnable> void matchAsynchronous(@NotNull BTJRequest<X> request, Response response) {
        if (request.getCompletion() == CompletionType.SYNCHRONOUS) {
            throw new IllegalArgumentException("Invalid Request type for call.");
        }

        try {
            checkResponse(response);
            lgr.debug("Successful response for request " + request.getEndpoint().getEndpointName() + " returned code " + response.code());
            final String JSONBody = response.body().string();
            for (Method m : JSONParser.class.getDeclaredMethods()) {
                if (m.getName().equals(request.getEndpoint().getDeclaredMethod())) {
                    X returnable = (X) m.invoke(null, JSONBody);
                    switch (request.getCompletion()) {
                        case FUTURE:
                            request.getAsync().getFuture().complete(returnable);
                            break;
                        case CALLBACK:
                            request.getAsync().getConsumer().succeed(returnable);
                            break;
                    }
                    return;
                }
            }
            throw new IllegalArgumentException(request.getCompletion().toString());
        } catch (Exception ex) {
            lgr.debug(String.valueOf(ex));
            switch (request.getCompletion()) {
                case FUTURE:
                    request.getAsync().getFuture().completeExceptionally(ex);
                    break;
                case CALLBACK:
                    request.getAsync().getConsumer().fail(ex);
                    break;
                default: throw new RuntimeException("Unexpected completion type");
            }
        }
    }

    private static void checkResponse(@NotNull Response response) throws LoginException {
        if (!response.isSuccessful()) {
            if (response.code() == 401) {
                throw new LoginException("Failed to login because the token used to access the API was invalid.");
            }
            throw new RuntimeException("Request failed: Code " + response.code() + ": " + response.message());
        }
    }

    // ***************************************************************
    // **                METHODS INVOKED REFLECTIVELY               **
    // ***************************************************************

    @NotNull
    public static RandomWord mapToRandomWord(final String json) {
        return new RandomWord(json.substring(1, json.length() - 2));
    }

    @NotNull
    public static RandomText mapToRandomText(final String json) {
        return new RandomText(json.substring(1, json.length() - 2));
    }

    @Nullable
    public static MadLib mapToMadLib(final String json) throws IOException {
        return mapper.readValue(json, MadLib.class);
    }

    @Nullable
    public static RedditMeme mapToRedditMeme(final String json) throws IOException {
        return mapper.readValue(json, RedditMeme.class);
    }

    @Nullable
    public static RedditPosts mapToRedditPosts(final String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, new TypeReference<>() {});
    }

    @Nullable
    public static SongLyrics mapToSongLyrics(final String json) throws IOException {
        return mapper.readValue(json, SongLyrics.class);
    }

    @Nullable
    public static TokenInfo mapToTokenInfo(final String json) throws IOException {
        return mapper.readValue(json, TokenInfo.class);
    }
}
