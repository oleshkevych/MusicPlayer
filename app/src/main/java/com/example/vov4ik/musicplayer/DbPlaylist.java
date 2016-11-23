package com.example.vov4ik.musicplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vov4ik on 9/7/2016.
 */
public class DbPlaylist extends SQLiteOpenHelper {

    final static int DB_VERSION = 3;
    final static String DB_NAME = "Playlists.db";

    final static String TABLE_PLAYLIST = "playlist";
    final static String COLUMN_NAME_ID_PLAYLIST = "ID_playlist";
    final static String COLUMN_PLAYLIST_NAME = "name_of_playlist";
    final static String CREATE_TABLE_PLAYLIST = "CREATE TABLE IF NOT EXISTS "+TABLE_PLAYLIST+" (" +
            COLUMN_NAME_ID_PLAYLIST + " INTEGER PRIMARY KEY, "+ COLUMN_PLAYLIST_NAME + " TEXT" + ");";

    final static String TABLE_FILES_IN_PLAYLIST = "playlist_files";
    final static String COLUMN_ID_COUNT = "ID_count_files_in_playlist";
    final static String COLUMN_ID_FILES_IN_PLAYLIST = "ID_files_in_playlist";
    final static String COLUMN_FILES_IN_PLAYLIST_NAME = "name_of_files_in_playlist";
    final static String CREATE_TABLE_FILES_IN_PLAYLIST = "CREATE TABLE IF NOT EXISTS "+TABLE_FILES_IN_PLAYLIST+" (" +
            COLUMN_ID_COUNT + " INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_FILES_IN_PLAYLIST_NAME + " TEXT, " + COLUMN_ID_FILES_IN_PLAYLIST +
            " INTEGER);";

    private SQLiteDatabase mDatabase;


    public DbPlaylist(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PLAYLIST);
        db.execSQL(CREATE_TABLE_FILES_IN_PLAYLIST);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.delete("playlist", null, null);
        db.delete("playlist_files", null, null);
        onCreate(db);
    }
    private DbPlaylist open() {
        mDatabase = getWritableDatabase();
        mDatabase.execSQL(CREATE_TABLE_PLAYLIST);
        mDatabase.execSQL(CREATE_TABLE_FILES_IN_PLAYLIST);
        return this;
    }

    public void playlistFiller(String playlistName, List<String> path) {
        open();


        Cursor cursor1 = mDatabase.rawQuery("SELECT * FROM " + TABLE_PLAYLIST, null);
        cursor1.moveToFirst();

        boolean add = true;
        try {
        if(cursor1.getCount()>0)
            do {
                add = !cursor1.getString(1).equals(playlistName);
            } while (cursor1.moveToNext());
        }catch (CursorIndexOutOfBoundsException c){}
        ContentValues values = new ContentValues();
        if(add) {
            values.put(COLUMN_PLAYLIST_NAME, playlistName);
            mDatabase.insert(
                    DbPlaylist.TABLE_PLAYLIST,
                    null,
                    values);
        }
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_PLAYLIST, null);
        cursor.moveToFirst();
        int ID = 0;
        do{
            if(cursor.getString(1).equals(playlistName)){
                ID = cursor.getInt(0);
                break;
            }
        }while (cursor.moveToNext());
        cursor.close();
        for(String s: path) {
            values = new ContentValues();
            values.put(COLUMN_FILES_IN_PLAYLIST_NAME, s);
            values.put(COLUMN_ID_FILES_IN_PLAYLIST, ID);
            mDatabase.insert(
                    DbPlaylist.TABLE_FILES_IN_PLAYLIST,
                    null,
                    values);
        }
        cursor.close();
        cursor1.close();
        mDatabase.close();
    }

    public List<String> getPlaylist(){
        open();
        List<String> playlists = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM "+ TABLE_PLAYLIST, null);
        cursor.moveToFirst();
        try {
            do {

                if(playlists.add(cursor.getString(1))){
                    PlaylistFragment.addListOfCurrentIds(cursor.getInt(0));
                }
            } while (cursor.moveToNext());
        }catch (CursorIndexOutOfBoundsException c){
            playlists.add("No Playlists available");
        }
        cursor.close();
        mDatabase.close();
        return playlists;
    }

    public List<List<String>> getPlaylistFiles(){
        open();
        List<List<String>> playlistFiles = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM "+ TABLE_FILES_IN_PLAYLIST, null);
        cursor.moveToFirst();
        try {
//            int counter = 0;
//            do{
//                if(cursor.getInt(cursor.getColumnIndex(COLUMN_ID_FILES_IN_PLAYLIST))>counter&&(!cursor.getString(1).isEmpty())){
//                    counter = cursor.getInt(cursor.getColumnIndex(COLUMN_ID_FILES_IN_PLAYLIST));
//                    Log.d("Test", cursor.getString(1));
//                }
//            }while (cursor.moveToNext());
            for(int i = 0; i<PlaylistFragment.getListOfCurrentIds().size(); i++) {
                List<String> listString = new ArrayList<String>();
                cursor.moveToFirst();
                do {
                    if(cursor.getInt(2)==PlaylistFragment.getListOfCurrentIds().get(i))
                    listString.add(cursor.getString(1));
                } while (cursor.moveToNext());
                playlistFiles.add(listString);
            }
        }catch (CursorIndexOutOfBoundsException c){
            List<String> list = new ArrayList<>();
            list.add("Make Playlists");
            playlistFiles.add(list);
        }
        cursor.close();
        mDatabase.close();
        PlaylistFragment.setListOfCurrentIds(new ArrayList<Integer>());
        return playlistFiles;
    }
    public boolean playlistRemover(String playlistName) {
        open();
        int numberOfRemovedPlaylist = 0;
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM "+ TABLE_PLAYLIST, null);
        cursor.moveToFirst();
//        try{
            do{
                if(cursor.getString(1).equals(playlistName)){
                    numberOfRemovedPlaylist = cursor.getInt(0);
                    break;
                }
            }while(cursor.moveToNext());
//        }catch (CursorIndexOutOfBoundsException c){
//
//        }

        mDatabase.delete(TABLE_PLAYLIST, COLUMN_NAME_ID_PLAYLIST + "=" + numberOfRemovedPlaylist, null);
        mDatabase.delete(TABLE_FILES_IN_PLAYLIST, COLUMN_ID_FILES_IN_PLAYLIST + "=" + numberOfRemovedPlaylist, null);
        cursor.close();
        mDatabase.close();
        return true;
    }
    public void playlistFileRemover(String playlist, List<String> fileName) {
        open();
        List<Integer> list = new ArrayList<>();
        int number=-1;
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM "+ TABLE_PLAYLIST, null);
        cursor.moveToFirst();
//        try{
        do{
            if(cursor.getString(1).equals(playlist)){
                number = cursor.getInt(0);
                break;
            }
        }while(cursor.moveToNext());
        cursor = mDatabase.rawQuery("SELECT * FROM "+ TABLE_FILES_IN_PLAYLIST, null);

        for(String s: fileName) {
            cursor.moveToFirst();
            try {
                do {
                    if (cursor.getString(1).equals(s)&&cursor.getInt(2)==number) {
                        list.add(cursor.getInt(0));
                    }
                } while (cursor.moveToNext());
            } catch (IllegalStateException c) {
                Log.d("Test", c.getMessage());
            }
        }
        for(int i: list) {
                int in = mDatabase.delete(TABLE_FILES_IN_PLAYLIST, COLUMN_ID_COUNT + "=" + i, null);
                    Log.d("Test", "deleteDb "+in);

        }
//        for(String s: fileName) {
//            cursor.moveToFirst();
////            try {
//                do {
//                    if (cursor.getInt(cursor.getColumnIndex(COLUMN_ID_FILES_IN_PLAYLIST)) == number && (cursor.getString(1).equals(s))) {
//                        mDatabase.deleteDb(TABLE_FILES_IN_PLAYLIST, COLUMN_ID_COUNT + "=" + cursor.getInt(2), null);
//                        Log.d("Test", s);
//                        break;
//                    }
//                } while (cursor.moveToNext());
////            } catch (CursorIndexOutOfBoundsException c) {
////            }
//        }
        cursor.close();
        mDatabase.close();
    }
}

