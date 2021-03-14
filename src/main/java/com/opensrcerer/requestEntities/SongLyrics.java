package com.opensrcerer.requestEntities;

/**
 * Class that encapsulates the data received from JSON requests on the
 * SongLyrics endpoint.
 */
public class SongLyrics implements BTJReturnable {
    /**
     * This Song's title.
     */
    private final String title;

    /**
     * This song's artist.
     */
    private final String artist;

    /**
     * The lyrics of this song, pre-formatted.
     */
    private final String lyrics;

    public SongLyrics(String title, String artist, String lyrics) {
        this.title = title;
        this.artist = artist;
        this.lyrics = lyrics;
    }

    /**
     * @return The title to this song.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return The artist to this song.
     */
    public String getArtist() {
        return artist;
    }

    /**
     * @return The lyrics to this song.
     */
    public String getLyrics() {
        return lyrics;
    }
}
