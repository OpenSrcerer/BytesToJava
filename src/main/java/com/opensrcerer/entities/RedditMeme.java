package com.opensrcerer.entities;

/**
 * Class that encapsulates the data received from JSON requests on the
 * Meme endpoint.
 */
public class RedditMeme {

    private final String title;
    private final String url;
    private final String link;
    private final String subreddit;
    private final int upvotes;
    private final int downvotes;
    private final int comments;

    public RedditMeme(String title, String url, String link,
                      String subreddit, int upvotes, int downvotes, int comments)
    {
        this.title = title;
        this.url = url;
        this.link = link;
        this.subreddit = subreddit;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.comments = comments;
    }
}
