package com.example.playlist_management.model;

public class Song {
    private int id;
    private String title;
    private String artist;
    private String album;
    private String genre;
    private int durationSeconds;
    private String playlistCode;

    public Song() {
    }

    public Song(int id, String title, String artist, String album, String genre, int durationSeconds, String playlistCode) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.durationSeconds = durationSeconds;
        this.playlistCode = playlistCode;
    }

    /** Returns duration formatted as m:ss */
    public String getFormattedDuration() {
        int m = durationSeconds / 60;
        int s = durationSeconds % 60;
        return String.format("%d:%02d", m, s);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }
    public String getAlbum() { return album; }
    public void setAlbum(String album) { this.album = album; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }
    public String getPlaylistCode() { return playlistCode; }
    public void setPlaylistCode(String playlistCode) { this.playlistCode = playlistCode; }
}
