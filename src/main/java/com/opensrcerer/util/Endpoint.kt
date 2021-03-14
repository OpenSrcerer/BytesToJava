package com.opensrcerer.util

enum class Endpoint (val endpointName: String, val declaredMethod: String) {
    INFO("/info/", "mapToTokenInfo"), WORD("/word/", "mapToString"),
    TEXT("/text/", "mapToString"), MADLIBS("/madlibs/", "mapToMadLib"),
    MEME("/meme/", "mapToRedditMeme"), LYRICS("/lyrics/", "mapToSongLyrics"),
    REDDIT("/reddit/", "mapToRedditPosts");
}