package com.opensrcerer.entities;

/**
 * Class that encapsulates the data received from JSON requests on the
 * SongLyrics endpoint.
 */
public class SongLyrics {

    private final String title;
    private final String artist;
    private final String lyrics;

    public SongLyrics(String title, String artist, String lyrics) {
        this.title = title;
        this.artist = artist;
        this.lyrics = lyrics;
    }
}
