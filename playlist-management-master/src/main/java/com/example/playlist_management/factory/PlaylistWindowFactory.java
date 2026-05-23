package com.example.playlist_management.factory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;

public final class PlaylistWindowFactory {
    private PlaylistWindowFactory() {
    }

    public static Scene createScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(PlaylistWindowFactory.class.getResource("/com/example/playlist_management/playlist-view.fxml"));
        return new Scene(loader.load());
    }
}
