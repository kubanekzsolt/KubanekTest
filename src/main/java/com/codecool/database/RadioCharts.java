package com.codecool.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RadioCharts {

    private static final String NO_RESULT = "";

    private final String url;
    private final String user;
    private final String password;

    public RadioCharts(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public String getMostPlayedSong() {
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql =
                    "SELECT song, times_aired "
                            + "FROM music_broadcast";
            Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(sql);
            List<Song> songs = new ArrayList<>();
            while (results.next()) {
                String title = results.getString(1);
                int aired = results.getInt(2);
                Song song = getSong(songs, title);
                song.addAirTime(aired);
            }
            if (songs.size() == 0) {
                return NO_RESULT;
            }
            return selectMostPlayed(songs);
        } catch (SQLException e) {
            return NO_RESULT;
        }
    }

    private String selectMostPlayed(List<Song> songs) {
        int max = Integer.MIN_VALUE;
        String winner = "";
        for (int i = 0; i < songs.size(); i++) {
            Song song = songs.get(i);
            if (song.getTimesAired() > max) {
                max = song.getTimesAired();
                winner = song.getTitle();
            }
        }
        return winner;
    }

    private Song getSong(List<Song> songs, String title) {
        Song newSong = new Song(title, 0);
        final int whereIsSong = songs.indexOf(newSong);
        if (whereIsSong == -1) {
            songs.add(newSong);
            return newSong;
        }
        return songs.get(whereIsSong);
    }

    public String getMostActiveArtist() {
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql =
                    "SELECT artist, song "
                            + "FROM music_broadcast";
            Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(sql);
            List<Artist> artists = new ArrayList<>();
            while (results.next()) {
                String name = results.getString(1);
                String title = results.getString(2);
                Artist artist = getArtist(artists, name);
                artist.addSongTitle(title);
            }
            if (artists.size() == 0) {
                return NO_RESULT;
            }
            return selectMostActive(artists);
        } catch (SQLException e) {
            return NO_RESULT;
        }
    }

    private String selectMostActive(List<Artist> artists) {
        int max = Integer.MIN_VALUE;
        String winner = "";
        for (int i = 0; i < artists.size(); i++) {
            Artist artist = artists.get(i);
            if (artist.getAmountOfSongs() > max) {
                max = artist.getAmountOfSongs();
                winner = artist.getName();
            }
        }
        return winner;
    }

    private Artist getArtist(List<Artist> artists, String name) {
        Artist newArtist = new Artist(name);
        final int whereIsArtist = artists.indexOf(newArtist);
        if (whereIsArtist == -1) {
            artists.add(newArtist);
            return newArtist;
        }
        return artists.get(whereIsArtist);
    }
}