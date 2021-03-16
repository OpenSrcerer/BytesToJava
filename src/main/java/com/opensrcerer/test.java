package com.opensrcerer;

import javax.security.auth.login.LoginException;

public class test {
    public static void main(String[] args) throws LoginException {
        BTJ btj = BTJ.getBTJ("NO9n.SSOqeeJwMnwZNwm8lIB0"); // Initialize BTJ with Token


        /*
        btj.getMeme().queue(e -> System.out.println(e.getLink()));
        btj.getRedditPosts("dankmemes").queue(e -> System.out.println(e.get(0).getTitle()));*/

        btj.getMeme().queue(e -> {
            System.out.println(e.getSubreddit()  + " - " + e.getLink());
            System.out.println(e.getUrl());
        });
    }
}
