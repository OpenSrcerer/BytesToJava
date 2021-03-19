package github.opensrcerer.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import github.opensrcerer.requestEntities.*;
import github.opensrcerer.requests.BTJRequest;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public static <X extends BTJReturnable> X matchSynchronous(BTJRequest<X> request, Response response) throws Exception {
        if (request.getCompletion() != CompletionType.SYNCHRONOUS) {
            throw new IllegalArgumentException("Invalid Request type for call.");
        }

        if (response.isSuccessful()) {
            lgr.debug("Response returned code " + response.code());
            final String JSONBody = response.body().string();
            for (Method m : JSONParser.class.getDeclaredMethods()) {
                if (m.getName().equals(request.getEndpoint().getDeclaredMethod())) {
                    return (X) m.invoke(null, JSONBody);
                }
            }
            throw new IllegalArgumentException("Unexpected completion type");
        } else {
            throw new RuntimeException("Request failed: Code " + response.code() + ": " + response.message());
        }
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public static <X extends BTJReturnable> void matchAsynchronous(BTJRequest<X> request, Response response) {
        if (request.getCompletion() == CompletionType.SYNCHRONOUS) {
            throw new IllegalArgumentException("Invalid Request type for call.");
        }

        try {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Request failed: Code " + response.code() + ": " + response.message());
            }

            final String JSONBody = response.body().string();
            for (Method m : JSONParser.class.getDeclaredMethods()) {
                if (m.getName().equals(request.getEndpoint().getDeclaredMethod())) {
                    X returnable = (X) m.invoke(null, JSONBody);
                    switch (request.getCompletion()) {
                        case FUTURE -> request.getAsync().getFuture().complete(returnable);
                        case CALLBACK -> request.getAsync().getConsumer().succeed(returnable);
                    }
                    return;
                }
            }
            throw new IllegalArgumentException(request.getCompletion().toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            lgr.debug(String.valueOf(ex));
            switch (request.getCompletion()) {
                case FUTURE -> request.getAsync().getFuture().completeExceptionally(ex);
                case CALLBACK -> request.getAsync().getConsumer().fail(ex);
                default -> throw new RuntimeException("Unexpected completion type");
            }
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
