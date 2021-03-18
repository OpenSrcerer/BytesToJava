package github.opensrcerer.util

enum class Endpoint (val endpointName: String, val declaredMethod: String) {
    INFO("/info/", "mapToTokenInfo"), WORD("/word/", "mapToRandomWord"),
    TEXT("/text/", "mapToRandomText"), MADLIBS("/madlibs/", "mapToMadLib"),
    MEME("/meme/", "mapToRedditMeme"), LYRICS("/lyrics/", "mapToSongLyrics"),
    REDDIT("/reddit/", "mapToRedditPosts");
}