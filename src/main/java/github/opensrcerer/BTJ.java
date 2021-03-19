package github.opensrcerer;

import github.opensrcerer.requestEntities.*;
import github.opensrcerer.requests.BTJRequest;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.security.auth.login.LoginException;
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
    static BTJImpl getBTJ(String token) throws LoginException {
        Objects.requireNonNull(token);
        return new BTJImpl(token);
    }

    /**
     * @param token API token for this BTJ instance.
     * @param executor ExecutorService to supply.
     * @return Get a BTJ instance that executes requests on the provided ExecutorService.
     *         Only use this if you know what you're doing.
     */
    static BTJImpl getBTJ(String token, ExecutorService executor) throws LoginException {
        Objects.requireNonNull(token);
        Objects.requireNonNull(executor);
        return new BTJImpl(token, executor);
    }

    // ***************************************************************
    // **                       INTERNAL                            **
    // ***************************************************************

    /**
     * Add a Request to the request queue.
     * @param request BTJRequest to insert into the queue.
     */
    void invoke(BTJRequest<? extends BTJReturnable> request);

    /**
     * @return Logger of the BTJ instance.
     */
    Logger getLogger();

    /**
     * Create a new OkHttp request from a BTJRequest.
     * @param request BTJRequest to use for creation.
     */
    @NotNull
    Request getRequest(final BTJRequest<? extends BTJReturnable> request);

    /**
     * @return Return the OkHttpClient of this BTJ instance.
     */
    @NotNull
    OkHttpClient getClient();

    /**
     * Shuts down this instance of BTJ.
     */
    void shutdown();

    /**
     * Shuts down this instance of BTJ immediately and returns a list of Runnable-s that were not completed.
     */
    List<Runnable> shutdownNow();

    // ***************************************************************
    // **                      RETRIEVALS                           **
    // ***************************************************************

    /**
     * @return A BTJRequest that will be executed to retrieve information
     *         about your token from the BTB API.
     *         Can be executed using .queue(), .submit() or .complete().
     */
    @NotNull
    BTJRequest<TokenInfo> getInfo();

    /**
     * @return A BTJRequest that will be executed to retrieve a random word from the
     *         BytesToBits API.
     *         Can be executed using .queue(), .submit() or .complete().
     */
    @NotNull
    BTJRequest<RandomWord> getWord();

    /**
     * @return A BTJRequest that will be executed to retrieve a random piece of text from the
     *         BytesToBits API.
     *         Can be executed using .queue(), .submit() or .complete().
     */
    @NotNull
    BTJRequest<RandomText> getText();

    /**
     * @return A BTJRequest that will be executed to retrieve a random MadLib from the
     *         BytesToBits API.
     *         Can be executed using .queue(), .submit() or .complete().
     */
    @NotNull
    BTJRequest<MadLib> getMadLib();

    /**
     * @return A BTJRequest that will be executed to retrieve a random Meme from the
     *         BytesToBits API.
     *         Can be executed using .queue(), .submit() or .complete().
     */
    @NotNull
    BTJRequest<RedditMeme> getMeme();

    /**
     * @param song The name of the song to look for.
     * @return A BTJRequest that will be executed to retrieve lyrics for a Song from the
     *         BytesToBits API.
     *         Can be executed using .queue(), .submit() or .complete().
     */
    @NotNull
    @Contract("null -> fail")
    BTJRequest<SongLyrics> getLyrics(String song);

    /**
     * @param song The name of the song to look for.
     * @param artist The artist that made this song.
     * @return A BTJRequest that will be executed to retrieve lyrics for a Song from the
     *         BytesToBits API.
     *         Can be executed using .queue(), .submit() or .complete().
     */
    @NotNull
    @Contract("null, _ -> fail")
    BTJRequest<SongLyrics> getLyrics(String song, String artist);

    /**
     * @param subreddit Subreddit to retrieve from.
     * @return A BTJRequest that will be executed to retrieve a random word from the
     *         BytesToBits API.
     *         Can be executed using .queue(), .submit() or .complete().
     */
    @NotNull
    @Contract("null -> fail")
    BTJRequest<RedditPosts> getRedditPosts(String subreddit);

    /**
     * @param subreddit Subreddit to retrieve from.
     * @param limit Number of posts to retrieve.
     * @return A BTJRequest that will be executed to retrieve a random word from the
     *         BytesToBits API.
     *         Can be executed using .queue(), .submit() or .complete().
     */
    @NotNull
    @Contract("null, _ -> fail")
    BTJRequest<RedditPosts> getRedditPosts(String subreddit, int limit);
}
