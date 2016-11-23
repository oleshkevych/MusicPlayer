package com.example.vov4ik.musicplayer;

import android.content.Context;
import android.database.CursorIndexOutOfBoundsException;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class DbConnector {
    //region Declarations

    private static final int MAX_FOLDER_SCAN_DEPTH = 10;
    private static final String MP3_FILE_EXTENSION = ".mp3";
    private static final HashSet<String> nonMusicFolders = new HashSet<>(Arrays.asList(
            "sys",
            "system",
            "proc",
            "mnt",
            "d"));

    private ArrayList<MusicFile> listMusicFiles;
    private HashMap<String, MusicFile> existingMusicFiles;

    //endregion

    public void fillerForDb(Context context) {
        listMusicFiles = new ArrayList<>();
        existingMusicFiles = new HashMap<>();

        File deviceRoot = new File("/");
        File[] rootFolderFiles = deviceRoot.listFiles();
        if (rootFolderFiles == null) {
            Log.e(Constants.LOG_TAG, "Error accessing device filesystem. Check if app has proper permissions.");
            return;
        }

        try {
            Log.d(Constants.LOG_TAG, "Obtaining existing music files from database.");
            List<MusicFile> existingMusicFilesFromDb = getMusicFilesForSearch(context);
            if (existingMusicFilesFromDb != null) {
                for (MusicFile m : existingMusicFilesFromDb) {
                    existingMusicFiles.put(m.getPath(), m);
                }

                Log.d(Constants.LOG_TAG, "Got " + listMusicFiles.size() + " music files.");
            }
        } catch (CursorIndexOutOfBoundsException ex) {
            Log.e(Constants.LOG_TAG, ex.getMessage(), ex);
        }

        Log.d(Constants.LOG_TAG, "Start filesystem scanning.");
        scanForMusicFiles(rootFolderFiles, 0);
        Log.d(Constants.LOG_TAG, "Filesystem scanning complete. Found " + listMusicFiles.size() + " music files.");
        Log.d(Constants.LOG_TAG, "Deleting old database.");
        DbConnector.deleteDb(context);
        Log.d(Constants.LOG_TAG, "Creating new database with found files.");
        new DbHelper(context).musicFileFiller(listMusicFiles);
        Log.d(Constants.LOG_TAG, "Database created successfully.");
    }

    private void scanForMusicFiles(File[] files, int folderScanDepth) {
        if (files == null || folderScanDepth > MAX_FOLDER_SCAN_DEPTH) {
            return;
        }

        ArrayList<File> folders = new ArrayList<>();
        boolean currentFolderHasMp3Files = false;
        for (File file : files) {
            if (isNonMusicDirectory(file) || file.isHidden()) {
                continue;
            }

            if (file.isDirectory()) {
                // Add folders to separate list and process them later
                folders.add(file);
                continue;
            }

            if (isMp3File(file.getName())) {
                currentFolderHasMp3Files = true;
                MusicFile musicFile = createMusicFile(file);
                listMusicFiles.add(musicFile);
            }
        }

        // For folders with music files
        // set lowest possible initial scan depth
        // to be able to scan all nested folders.
        int updatedFolderScanDepth = currentFolderHasMp3Files ? Integer.MIN_VALUE : folderScanDepth + 1;

        // Process folders after all files was processed.
        for (File folder : folders) {
            scanForMusicFiles(folder.listFiles(), updatedFolderScanDepth);
        }
    }

    //region File processing helpers

    private static boolean isNonMusicDirectory(File file) {
        return file.isDirectory() && nonMusicFolders.contains(file.getName());
    }

    private static boolean isMp3File(String fileName) {
        return fileName.endsWith(MP3_FILE_EXTENSION);
    }

    private MusicFile createMusicFile(File file) {
        String filePath = file.getPath();
        if (existingMusicFiles.containsKey(filePath)) {
            return existingMusicFiles.get(filePath);
        }

        MusicFile musicFile = new MusicFile();
        musicFile.setFolder(file.getParentFile().getName());
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(filePath);
        String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        musicFile.setArtist(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
        musicFile.setAlbum(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
        mmr.release();
        musicFile.setTitle(title == null || title.isEmpty() ? file.getName() : title);
        musicFile.setPath(filePath);
        musicFile.setMainFolder(musicFile.getFolder());
        return musicFile;
    }

    //endregion

    //region DbHelpers

    public static List<String> getFoldersFromDb(Context context) {
        return new DbHelper(context).getFolders();
    }

    public static List<List<String>> getFilesNamesForFolders(Context context) {
        return new DbHelper(context).getFilesForFolder();
    }

    public static List<List<String>> getPathsForFolders(Context context) {
        return new DbHelper(context).getPathsForFolder();
    }

    public static List<String> getAlbumFromDb(Context context) {
        return new DbHelper(context).getAlbums();
    }

    public static List<List<String>> getPathsForAlbums(Context context) {
        return new DbHelper(context).getPathsForAlbums();
    }

    public static List<List<String>> getFileNamesForAlbums(Context context) {
        return new DbHelper(context).getFilesForAlbums();
    }

    public static List<String> getArtistFromDb(Context context) {
        return new DbHelper(context).getArtist();
    }

    public static List<List<String>> getPathsForArtists(Context context) {
        return new DbHelper(context).getPathsForArtists();
    }

    public static List<List<String>> getFileNamesForArtists(Context context) {
        return new DbHelper(context).getFilesForArtists();
    }

    public static List<String> getMainFoldersFromDb(Context context) {
        return new DbHelper(context).getMainFolders();
    }

    public static List<List<String>> getPathsForMainFolders(Context context) {
        return new DbHelper(context).getPathsForMainFolders();
    }

    public static List<List<String>> getFileNamesForMainFolders(Context context) {
        return new DbHelper(context).getFilesForMainFolders();
    }

    public static List<String> getAllSongsPaths(Context context) {
        return new DbHelper(context).getAllSongsPaths();
    }

    public static List<String> getAllSongsNames(Context context) {
        return new DbHelper(context).getAllSongsNames();
    }

    public static void deleteDb(Context context) {
        new DbHelper(context).deleteDb();
    }

    public static List<String> getLastPlayList(Context context) {
        return new DbHelper(context).getLastPlayList();
    }

    public static int getLastPlayTime(Context context) {
        return new DbHelper(context).getLastPlayedTime();
    }

    public static int getLastPlayNumber(Context context) {
        return new DbHelper(context).getLastPlayedNumber();
    }

    public static boolean getLastPlayState(Context context) {
        return new DbHelper(context).getLastPlayedState();
    }

    public static void setLastPlayList(Context context, List<String> list) {
        new DbHelper(context).setLastPlayList(list);
    }

    public static void setPlaylistAttributes(Context context, int time, int number, boolean shuffle) {
        new DbHelper(context).setPlaylistAttributes(time, number, shuffle);
    }

    public static void setPlaylist(Context context, String playlist, List<String> paths) {
        new DbPlaylist(context).playlistFiller(playlist, paths);
    }

    public static List<String> getPlaylist(Context context) {
        return new DbPlaylist(context).getPlaylist();
    }

    public static List<List<String>> getPlaylistFiles(Context context) {
        return new DbPlaylist(context).getPlaylistFiles();
    }

    public static void removePlaylist(Context context, String playlist) {
        new DbPlaylist(context).playlistRemover(playlist);
    }

    public static void removeFilesFromPlaylist(Context context, String playlist, List<String> files) {
        new DbPlaylist(context).playlistFileRemover(playlist, files);
    }

    public static void tabsFiller(Context context, List<TabConstructor> tabs) {
        new DbTab(context).tabsFiller(tabs);
    }

    public static List<String> getVisibleTabs(Context context) {
        return new DbTab(context).getVisibleTabs();
    }

    public static List<TabConstructor> getAllTabs(Context context) {
        return new DbTab(context).getAllTabs();
    }

    public static List<MusicFile> getMusicFilesForSearch(Context context) {
        return new DbHelper(context).getMusicFilesForSearch();
    }

    //endregion
}