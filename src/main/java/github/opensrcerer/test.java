package github.opensrcerer;

import github.opensrcerer.requestEntities.RedditPost;

import javax.security.auth.login.LoginException;

public class test {
    public static void main(String[] args) throws LoginException {
        BTJ btj = BTJ.getBTJ("NO9n.SSOqeeJwMnwZNwm8lIB0"); // Initialize BTJ with Token

        btj.getRedditPosts("tiresaretheenemy", 10).queue(redditPosts -> {
            for (RedditPost post : redditPosts) {
                System.out.println(post.getTitle());
                System.out.println(post.getSubreddit());
                System.out.println(post.getLink());
                System.out.println(post.getUrl());
                System.out.println(post.getUpvotes());
                System.out.println(post.getDownvotes());
                System.out.println("--------------");
            }
        });
    }
}
