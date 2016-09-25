package com.example.vov4ik.musicplayer;

/**
 * Created by vov4ik on 9/24/2016.
 */
public class MusicFile {
    private String title;
    private String path;
    private String album;
    private String artist;
    private String folder;
    private String mainFolder;

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getMainFolder() {
        return mainFolder;
    }

    public void setMainFolder(String mainFolder) {
        this.mainFolder = mainFolder;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MusicFile(String album, String artist, String folder, String mainFolder, String path, String title) {
        this.album = album;
        this.artist = artist;
        this.folder = folder;
        this.mainFolder = mainFolder;
        this.path = path;
        this.title = title;
    }

    public MusicFile() {
    }


}
