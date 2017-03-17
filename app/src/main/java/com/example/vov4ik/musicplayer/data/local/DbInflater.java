package com.example.vov4ik.musicplayer.data.local;

import android.content.Context;
import android.database.CursorIndexOutOfBoundsException;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.Log;

import com.example.vov4ik.musicplayer.data.local.realm.DbHelperRealm;
import com.example.vov4ik.musicplayer.data.model.Constants;
import com.example.vov4ik.musicplayer.data.model.MusicFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Volodymyr Oleshkevych on 3/12/2017.
 * Copyright (c) 2017, Rolique. All rights reserved.
 */
public class DbInflater {

    private static final int MAX_FOLDER_SCAN_DEPTH = 10;
    private static final String MP3_FILE_EXTENSION = ".mp3";
    private static final HashSet<String> nonMusicFolders = new HashSet<>(Arrays.asList(
            "sys",
            "system",
            "proc",
            "mnt",
            "d",
            "AudioBooks"));

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

//        try {
//            Log.d(Constants.LOG_TAG, "Obtaining existing music files from database.");
//            List<MusicFile> existingMusicFilesFromDb = new DbConnector().getMusicFiles(context);
//            if (existingMusicFilesFromDb != null && existingMusicFilesFromDb.size() > 0) {
//                for (MusicFile m : existingMusicFilesFromDb) {
//                    existingMusicFiles.put(m.getPath(), m);
//                }
//
//                Log.d(Constants.LOG_TAG, "Got " + listMusicFiles.size() + " music files.");
//            }
//        } catch (CursorIndexOutOfBoundsException ex) {
//            Log.e(Constants.LOG_TAG, ex.getMessage(), ex);
//        }

        Log.d(Constants.LOG_TAG, "Start filesystem scanning.");
        scanForMusicFiles(rootFolderFiles, 0);
        inflateMainFolders();
        Log.d(Constants.LOG_TAG, "Filesystem scanning complete. Found " + listMusicFiles.size() + " music files.");
//        Log.d(Constants.LOG_TAG, "Deleting old database.");
//        DbConnector.deleteDb(context);
        Log.d(Constants.LOG_TAG, "Creating new database with found files. Time start " + new Date().getTime());
//        new DbHelperRealm(context).musicFileFiller(listMusicFiles);
        DbHelperRealm realm = DbHelperRealm.getInstants(context);
        realm.update(listMusicFiles);

        Log.d(Constants.LOG_TAG, "Database created successfully. Time finish " + new Date().getTime());
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

    public MusicFile createMusicFile(File file) {
        String filePath = file.getPath();
        if (existingMusicFiles.containsKey(filePath)) {
            return existingMusicFiles.get(filePath);
        }

        MusicFile musicFile = new MusicFile();
        musicFile.setFolder(file.getParentFile().getName());
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(filePath);
        String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);

        musicFile.setArtist(artist == null ? "Empty value" : artist);
        musicFile.setAlbum(album == null ? "Empty value" : album);
        musicFile.setTrackNumber(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER));
        mmr.release();
        musicFile.setTitle(title == null || title.isEmpty() ? file.getName() : title);
        musicFile.setPath(filePath);
//        musicFile.setMainFolder(musicFile.getFolder());
        return musicFile;
    }

    private void inflateMainFolders() {
        List<String> mainPath = new ArrayList<>();
        File deviceRoot = new File("/");
        File[] rootFolderFiles = deviceRoot.listFiles();
        if (rootFolderFiles == null) {
            Log.e(Constants.LOG_TAG, "Error accessing device filesystem. Check if app has proper permissions.");
            return;
        }
        for (File f : rootFolderFiles) {
            mainPath.add(f.getPath());
        }
        for (int i = 0; i < listMusicFiles.size(); i++) {
            File file = new File(listMusicFiles.get(i).getPath());
            if (!file.isDirectory()) {
                getParent(listMusicFiles.get(i).getPath(), i, mainPath);
            }
            if(listMusicFiles.get(i).getMainFolder() == null){
                listMusicFiles.get(i).setMainFolder("Top Of Files");
            }
        }

    }

    private void getParent(String s, int i, List<String> mainPath) {
        File f = new File(s);
        if (!mainPath.contains(f.getParentFile().getParentFile().getPath())) {
            if (mainPath.contains(f.getParentFile().getParentFile().getParentFile().getPath())) {
                if (f.isDirectory()) {
                    listMusicFiles.get(i).setMainFolder(f.getName());
                } else {
                    listMusicFiles.get(i).setMainFolder("RootFiles");
                }
            } else {
                getParent(f.getParentFile().getPath(), i, mainPath);
            }
        }
    }
}
