package com.example.playlist_management.repository;

import com.example.playlist_management.model.Song;
import com.example.playlist_management.util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SongRepository {
    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM songs";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;
        }
    }

    public List<Song> findPage(int limit, int offset) throws SQLException {
        String sql = "SELECT s.id, s.title, s.artist, s.album, s.genre, s.duration_seconds, s.playlist_code " +
                "FROM songs s ORDER BY s.id DESC LIMIT ? OFFSET ?";
        List<Song> songs = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);
            statement.setInt(2, offset);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    songs.add(mapRow(rs));
                }
            }
        }
        return songs;
    }

    public List<Song> findAll() throws SQLException {
        String sql = "SELECT id, title, artist, album, genre, duration_seconds, playlist_code FROM songs ORDER BY id DESC";
        List<Song> songs = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                songs.add(mapRow(rs));
            }
        }
        return songs;
    }

    public void insert(Song song) throws SQLException {
        String sql = "INSERT INTO songs (title, artist, album, genre, duration_seconds, playlist_code) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindSong(statement, song, false);
            statement.executeUpdate();
        }
    }

    public void update(Song song) throws SQLException {
        String sql = "UPDATE songs SET title = ?, artist = ?, album = ?, genre = ?, duration_seconds = ?, playlist_code = ? WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindSong(statement, song, true);
            statement.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM songs WHERE id = ?")) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    private void bindSong(PreparedStatement st, Song song, boolean includeId) throws SQLException {
        st.setString(1, song.getTitle());
        st.setString(2, song.getArtist());
        st.setString(3, song.getAlbum());
        st.setString(4, song.getGenre());
        st.setInt(5, song.getDurationSeconds());
        st.setString(6, song.getPlaylistCode());
        if (includeId) {
            st.setInt(7, song.getId());
        }
    }

    private Song mapRow(ResultSet rs) throws SQLException {
        Song s = new Song();
        s.setId(rs.getInt("id"));
        s.setTitle(rs.getString("title"));
        s.setArtist(rs.getString("artist"));
        s.setAlbum(rs.getString("album"));
        s.setGenre(rs.getString("genre"));
        s.setDurationSeconds(rs.getInt("duration_seconds"));
        // safely read playlist_code — null is fine
        s.setPlaylistCode(rs.getString("playlist_code"));
        return s;
    }
}