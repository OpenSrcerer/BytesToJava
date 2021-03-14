package com.opensrcerer.requestEntities;

/**
 * Class that encapsulates the data received from JSON requests on the
 * Reddit endpoint.
 */
public class RedditPost {

    /**
     * The title of this Meme.
     */
    private final String title;

    /**
     * The direct URL to the image of this post.
     */
    private final String url;

    /**
     * The link to the original Reddit post.
     */
    private final String link;

    /**
     * The subreddit which this post originates from.
     */
    private final String subreddit;

    /**
     * Optional text for this post.
     */
    private final String text;

    /**
     * The number of upvotes on this Reddit post.
     */
    private final int upvotes;

    /**
     * The number of downvotes on this Reddit post.
     */
    private final int downvotes;

    /**
     * The number of comments on this Reddit post.
     */
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

    /**
     * @return The title of this RedditPost.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return The direct URL to the image of this RedditPost.
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return The link to the original Reddit Post.
     */
    public String getLink() {
        return link;
    }

    /**
     * @return The subreddit that this RedditPost was posted in.
     */
    public String getSubreddit() {
        return subreddit;
    }

    /**
     * @return The optional text for this RedditPost.
     */
    public String getText() {
        return text;
    }

    /**
     * @return The number of upvotes this RedditPost.
     */
    public int getUpvotes() {
        return upvotes;
    }

    /**
     * @return The number of downvotes this RedditPost.
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
