package com.opensrcerer.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensrcerer.requestEntities.*;
import com.opensrcerer.requests.BTJRequest;
import okhttp3.Response;
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

    @SuppressWarnings("ConstantConditions")
    public static void matchRequest(BTJRequest<BTJReturnable> request, Response response) {
        try {
            final String JSONBody = response.body().string();

            for (Method m : Endpoint.class.getDeclaredMethods()) {
                if (m.getName().equals(request.getEndpoint().getDeclaredMethod())) {
                    Object returned = m.invoke(null, response);

                    switch (request.getCompletion()) {
                        case SUBMIT -> {
                            switch (request.getEndpoint()) {
                                case INFO -> request.getFuture().complete((TokenInfo) returned);
                                case LYRICS -> request.getFuture().complete((SongLyrics) returned);
                                case MADLIBS -> request.getFuture().complete((MadLib) returned);
                                case MEME -> request.getFuture().complete((RedditMeme) returned);
                                case REDDIT -> request.getFuture().complete((RedditPosts) returned);
                                case TEXT -> request.getFuture().complete((RandomText) returned);
                                case WORD -> request.getFuture().complete((RandomWord) returned);
                                default -> throw new IllegalStateException("Unexpected value: " + request.getEndpoint());
                            }
                        }

                        case QUEUE -> {
                            switch (request.getEndpoint()) {
                                case INFO -> request.getSuccessConsumer().accept((TokenInfo) returned);
                                case LYRICS -> request.getSuccessConsumer().accept((SongLyrics) returned);
                                case MADLIBS -> request.getSuccessConsumer().accept((MadLib) returned);
                                case MEME -> request.getSuccessConsumer().accept((RedditMeme) returned);
                                case REDDIT -> request.getSuccessConsumer().accept((RedditPosts) returned);
                                case TEXT -> request.getSuccessConsumer().accept((RandomText) returned);
                                case WORD -> request.getSuccessConsumer().accept((RandomWord) returned);
                                default -> throw new IllegalStateException("Unexpected value: " + request.getEndpoint());
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            lgr.wan("a", ex);
        }
    }

    @Nullable
    public static RandomWord mapToRandomWord(final String json) throws IOException {
        return mapper.readValue(json, RandomWord.class);
    }

    @Nullable
    public static RandomText mapToRandomText(final String json) throws IOException {
        return mapper.readValue(json, RandomText.class);
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
        return mapper.readValue(json, RedditPosts.class);
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
