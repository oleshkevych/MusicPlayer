package com.example.vov4ik.musicplayer.data.local;

import android.content.Context;
import android.database.CursorIndexOutOfBoundsException;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.Log;

import com.example.vov4ik.musicplayer.data.local.db.DbPlaylist;
import com.example.vov4ik.musicplayer.data.local.db.DbTab;
import com.example.vov4ik.musicplayer.data.local.realm.DbHelperRealm;
import com.example.vov4ik.musicplayer.data.model.MusicFile;
import com.example.vov4ik.musicplayer.data.model.TabConstructor;
import com.example.vov4ik.musicplayer.data.local.db.DbHelper;
import com.example.vov4ik.musicplayer.data.model.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class DbConnector {

//start music player
    public static List<MusicFile> getMusicFiles(Context context) {
        return DbHelperRealm.getInstants(context).getAllMusicFiles();
    }

    public static List<MusicFile> getLastPlayList(Context context) {
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

    public static void setLastPlayList(Context context, List<MusicFile> list) {
        new DbHelper(context).setLastPlayList(list);
    }

    public static void setPlaylistAttributes(Context context, int time, int number, boolean shuffle) {
        new DbHelper(context).setPlaylistAttributes(time, number, shuffle);
    }
//finish music player

//start playlist
    public static void setPlaylist(Context context, String playlist, List<MusicFile> paths) {
        new DbPlaylist(context).playlistFiller(playlist, paths);
    }

    public static List<String> getPlaylist(Context context) {
        return new DbPlaylist(context).getPlaylist();
    }

    public static List<List<String>> getPlaylistFiles(Context context) {
        return new DbPlaylist(context).getPlaylistFiles();
    }

//    public static void removePlaylist(Context context, MusicFile playlist) {
//        new DbPlaylist(context).playlistRemover(playlist);
//    }

    public static void removeFilesFromPlaylist(Context context, String playlist, List<String> files) {
        new DbPlaylist(context).playlistFileRemover(playlist, files);
    }
//finish playlist


//start player components

    public static void tabsFiller(Context context, List<TabConstructor> tabs) {
        new DbTab(context).tabsFiller(tabs);
    }

    public static List<String> getVisibleTabs(Context context) {
        return new DbTab(context).getVisibleTabs();
    }

    public static List<TabConstructor> getAllTabs(Context context) {
        return new DbTab(context).getAllTabs();
    }
//finish player components
}