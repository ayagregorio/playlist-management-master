package com.example.playlist_management.controller;

import com.example.playlist_management.model.Playlist;
import com.example.playlist_management.model.Song;
import com.example.playlist_management.repository.PlaylistRepository;
import com.example.playlist_management.repository.SongRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;

import java.sql.SQLException;

public class PlaylistController {
    private static final int PAGE_SIZE = 10;

    @FXML private TextField idField;
    @FXML private TextField titleField;
    @FXML private TextField artistField;
    @FXML private TextField albumField;
    @FXML private TextField genreField;
    @FXML private TextField durationField;
    @FXML private ComboBox<Playlist> playlistComboBox;
    @FXML private TableView<Song> songTable;
    @FXML private TableColumn<Song, Integer> idColumn;
    @FXML private TableColumn<Song, String> titleColumn;
    @FXML private TableColumn<Song, String> artistColumn;
    @FXML private TableColumn<Song, String> albumColumn;
    @FXML private TableColumn<Song, String> genreColumn;
    @FXML private TableColumn<Song, String> durationColumn;
    @FXML private TableColumn<Song, String> playlistColumn;
    @FXML private Pagination songPagination;

    private final SongRepository repository = new SongRepository();
    private final PlaylistRepository playlistRepository = new PlaylistRepository();
    private final ObservableList<Song> songs = FXCollections.observableArrayList();
    private final ObservableList<Playlist> playlists = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        artistColumn.setCellValueFactory(new PropertyValueFactory<>("artist"));
        albumColumn.setCellValueFactory(new PropertyValueFactory<>("album"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("formattedDuration"));
        playlistColumn.setCellValueFactory(new PropertyValueFactory<>("playlistCode"));

        songTable.setItems(songs);
        playlistComboBox.setItems(playlists);
        songTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> populateForm(selected));
        songPagination.setPageFactory(this::createPage);
        songPagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            try {
                loadPage(newIndex.intValue());
            } catch (SQLException e) {
                showError("Database error", e.getMessage());
            }
        });
        loadPlaylists();
        refreshPagination();
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        if (!validateForm(true)) return;
        try {
            repository.insert(buildSong(false));
            clearForm();
            refreshPagination();
        } catch (Exception e) {
            showError("Unable to add song", e.getMessage());
        }
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        if (!validateForm(false)) return;
        try {
            repository.update(buildSong(true));
            clearForm();
            refreshPagination();
        } catch (Exception e) {
            showError("Unable to update song", e.getMessage());
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        Song selected = songTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No selection", "Choose a song to delete.");
            return;
        }
        try {
            repository.delete(selected.getId());
            clearForm();
            refreshPagination();
        } catch (Exception e) {
            showError("Unable to delete song", e.getMessage());
        }
    }

    @FXML
    private void handleClear(ActionEvent event) {
        clearForm();
    }

    private void refreshPagination() {
        try {
            int total = repository.countAll();
            int pageCount = Math.max(1, (int) Math.ceil(total / (double) PAGE_SIZE));
            songPagination.setPageCount(pageCount);
            int pageIndex = Math.min(songPagination.getCurrentPageIndex(), pageCount - 1);
            songPagination.setCurrentPageIndex(pageIndex);
            loadPage(pageIndex);
        } catch (SQLException e) {
            showError("Database error", e.getMessage());
        }
    }

    private javafx.scene.Node createPage(int pageIndex) {
        return new Region();
    }

    private void loadPage(int pageIndex) throws SQLException {
        int offset = pageIndex * PAGE_SIZE;
        songs.setAll(repository.findPage(PAGE_SIZE, offset));
        songTable.getSelectionModel().clearSelection();
    }

    private Song buildSong(boolean includeId) {
        Song song = new Song();
        if (includeId) {
            song.setId(Integer.parseInt(idField.getText().trim()));
        }
        song.setTitle(titleField.getText().trim());
        song.setArtist(artistField.getText().trim());
        song.setAlbum(albumField.getText().trim());
        song.setGenre(genreField.getText().trim());
        song.setDurationSeconds(Integer.parseInt(durationField.getText().trim()));
        song.setPlaylistCode(playlistComboBox.getValue() != null ? playlistComboBox.getValue().getCode() : null);
        return song;
    }

    private boolean validateForm(boolean allowEmptyId) {
        if (!allowEmptyId && idField.getText().trim().isEmpty()) {
            showError("Missing selection", "Select a song from the table first.");
            return false;
        }
        if (titleField.getText().trim().isEmpty()
                || artistField.getText().trim().isEmpty()
                || durationField.getText().trim().isEmpty()) {
            showError("Missing data", "Title, artist, and duration are required.");
            return false;
        }
        try {
            int dur = Integer.parseInt(durationField.getText().trim());
            if (dur <= 0) throw new NumberFormatException();
            if (!allowEmptyId && !idField.getText().trim().isEmpty()) {
                Integer.parseInt(idField.getText().trim());
            }
        } catch (NumberFormatException e) {
            showError("Invalid number", "Duration must be a positive number of seconds.");
            return false;
        }
        return true;
    }

    private void populateForm(Song song) {
        if (song == null) return;
        idField.setText(String.valueOf(song.getId()));
        titleField.setText(song.getTitle());
        artistField.setText(song.getArtist());
        albumField.setText(song.getAlbum() != null ? song.getAlbum() : "");
        genreField.setText(song.getGenre() != null ? song.getGenre() : "");
        durationField.setText(String.valueOf(song.getDurationSeconds()));
        playlistComboBox.setValue(null);
        playlistComboBox.getItems().stream()
                .filter(p -> p.getCode().equals(song.getPlaylistCode()))
                .findFirst()
                .ifPresent(playlistComboBox::setValue);
    }

    private void clearForm() {
        idField.clear();
        titleField.clear();
        artistField.clear();
        albumField.clear();
        genreField.clear();
        durationField.clear();
        playlistComboBox.setValue(null);
        songTable.getSelectionModel().clearSelection();
    }

    private void loadPlaylists() {
        try {
            playlists.setAll(playlistRepository.findAll());
        } catch (SQLException e) {
            showError("Database error", e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
