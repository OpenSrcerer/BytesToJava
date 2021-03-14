package com.opensrcerer.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensrcerer.requestEntities.*;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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

    private static MadLib mapToMadLib(final Response response) throws IOException {
        try (ResponseBody body = response.body()) {
            if (body == null) {
                lgr.warn("Trivia API body was null.");
                return null;
            }
            return mapper.readValue(body.string(), MadLib.class);
        }
    }

    private static RedditMeme mapToRedditMeme(final Response response) throws IOException {
        try (ResponseBody body = response.body()) {
            if (body == null) {
                lgr.warn("Trivia API body was null.");
                return null;
            }
            return mapper.readValue(body.string(), RedditMeme.class);
        }
    }

    private static RedditPost mapToRedditPost(final Response response) throws IOException {
        try (ResponseBody body = response.body()) {
            if (body == null) {
                lgr.warn("Trivia API body was null.");
                return null;
            }
            return mapper.readValue(body.string(), RedditPost.class);
        }
    }

    private static SongLyrics mapToSongLyrics(final Response response) throws IOException {
        try (ResponseBody body = response.body()) {
            if (body == null) {
                lgr.warn("Trivia API body was null.");
                return null;
            }
            return mapper.readValue(body.string(), SongLyrics.class);
        }
    }

    private static TokenInfo mapToTokenInfo(final Response response) throws IOException {
        try (ResponseBody body = response.body()) {
            if (body == null) {
                lgr.warn("Trivia API body was null.");
                return null;
            }
            return mapper.readValue(body.string(), TokenInfo.class);
        }
    }
}
