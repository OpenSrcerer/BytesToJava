package com.opensrcerer;

import com.opensrcerer.entities.MadLib;
import com.opensrcerer.entities.RedditMeme;
import com.opensrcerer.entities.RedditPost;
import com.opensrcerer.entities.SongLyrics;
import com.opensrcerer.requests.BTJRequest;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * Interface for the BytesToJava API.
 * Contains factory methods to build an instance of the
 * BTJImpl class.
 */
public interface BTJ {
    /**
     * @param token API token for this BTJ instance.
     * @return Get a BTJ instance with default settings.
     */
    static BTJImpl getBTJ(String token) {
        Objects.requireNonNull(token);
        return new BTJImpl(token);
    }

    /**
     * @param token API token for this BTJ instance.
     * @param service ExecutorService to supply.
     * @return Get a BTJ instance that executes requests on the provided ExecutorService.
     *         Only use this if you know what you're doing.
     */
    static BTJImpl getBTJ(String token, ExecutorService service) {
        Objects.requireNonNull(token);
        Objects.requireNonNull(service);
        return new BTJImpl(token, service);
    }

    // ***************************************************************
    // **                       INTERNAL                            **
    // ***************************************************************

    @NotNull
    OkHttpClient getClient();

    // ***************************************************************
    // **                      RETRIEVALS                           **
    // ***************************************************************

    /**
     * @return A BTJRequest that will be executed to retrieve a random word from the
     *         BytesToBits API.
     *         Can be executed using .queue() or .complete().
     */
    @NotNull
    BTJRequest<String> getWord();

    /**
     * @return A BTJRequest that will be executed to retrieve a random piece of text from the
     *         BytesToBits API.
     *         Can be executed using .queue() or .complete().
     */
    @NotNull
    BTJRequest<String> getText();

    /**
     * @return A BTJRequest that will be executed to retrieve a random MadLib from the
     *         BytesToBits API.
     *         Can be executed using .queue() or .complete().
     */
    @NotNull
    BTJRequest<MadLib> getMadLib();

    /**
     * @return A BTJRequest that will be executed to retrieve a random Meme from the
     *         BytesToBits API.
     *         Can be executed using .queue() or .complete().
     */
    @NotNull
    BTJRequest<RedditMeme> getMeme();

    /**
     * @param song The name of the song to look for.
     * @param artist The artist that made this song.
     * @return A BTJRequest that will be executed to retrieve lyrics for a Song from the
     *         BytesToBits API.
     *         Can be executed using .queue() or .complete().
     */
    @NotNull
    @Contract("null, null -> fail; null, _ -> fail; _, null -> fail")
    BTJRequest<SongLyrics> getLyrics(String song, String artist);

    /**
     * @param subreddit Subreddit to retrieve from.
     * @param limit Number of posts to retrieve.
     * @return A BTJRequest that will be executed to retrieve a random word from the
     *         BytesToBits API.
     *         Can be executed using .queue() or .complete().
     */
    @NotNull
    @Contract("null, _ -> fail")
    BTJRequest<List<RedditPost>> getRedditPost(String subreddit, int limit);
}
