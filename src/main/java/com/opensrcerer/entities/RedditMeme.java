package com.opensrcerer.entities;

/**
 * Class that encapsulates the data received from JSON requests on the
 * Meme endpoint.
 */
public class RedditMeme {

    /**
     * The title of this Meme.
     */
    private final String title;

    /**
     * The direct URL to the image of this meme.
     */
    private final String url;

    /**
     * The link to the original Reddit post.
     */
    private final String link;

    /**
     * The subreddit which this meme originates from.
     */
    private final String subreddit;

    /**
     * The number of upvotes on the Reddit post.
     */
    private final int upvotes;

    /**
     * The number of downvotes on the Reddit post.
     */
    private final int downvotes;

    /**
     * The number of comments on the Reddit post.
     */
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

    /**
     * @return The title of this Meme.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return The direct URL to the image of this Meme.
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return The link to the original Reddit post.
     */
    public String getLink() {
        return link;
    }

    /**
     * @return The subreddit that this Meme was posted in.
     */
    public String getSubreddit() {
        return subreddit;
    }

    /**
     * @return The number of upvotes the Reddit post of this meme has.
     */
    public int getUpvotes() {
        return upvotes;
    }

    /**
     * @return The number of downvotes the Reddit post of this meme has.
     */
    public int getDownvotes() {
        return downvotes;
    }

    /**
     * @return The number of comments the Reddit post of this meme has.
     */
    public int getComments() {
        return comments;
    }
}
