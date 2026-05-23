package com.example.playlist_management.repository;

import com.example.playlist_management.model.Playlist;
import com.example.playlist_management.util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlaylistRepository {
    public List<Playlist> findAll() throws SQLException {
        String sql = "SELECT code, name FROM playlists ORDER BY code";
        List<Playlist> playlists = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                playlists.add(new Playlist(
                        rs.getString("code"),
                        rs.getString("name")
                ));
            }
        }
        return playlists;
    }
}
