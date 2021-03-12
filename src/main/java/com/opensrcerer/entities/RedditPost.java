package com.opensrcerer.entities;

/**
 * Class that encapsulates the data received from JSON requests on the
 * Reddit endpoint.
 */
public class RedditPost {

    private final String title;
    private final String url;
    private final String link;
    private final String subreddit;
    private final String text;
    private final int upvotes;
    private final int downvotes;
    private final int comments;

    public RedditPost(String title, String url, String link,
                      String subreddit, String text,
                      int upvotes, int downvotes, int comments)
    {
        this.title = title;
        this.url = url;
        this.link = link;
        this.subreddit = subreddit;
        this.text = text;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.comments = comments;
    }
}
