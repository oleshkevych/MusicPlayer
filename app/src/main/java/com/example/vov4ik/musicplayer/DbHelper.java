package com.example.vov4ik.musicplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vov4ik on 8/3/2016.
 */
public class DbHelper extends SQLiteOpenHelper {

    final static int DB_VERSION = 1;
    final static String DB_NAME = "OwnHomeMadePlay.db";

    final static String TABLE_FOLDER = "folder";
    final static String COLUMN_NAME_ID_FOLDER = "ID_folder";
    final static String COLUMN_FOLDER_NAME = "name_of_folder";
    final static String CREATE_TABLE_FOLDER = "CREATE TABLE IF NOT EXISTS "+TABLE_FOLDER+" (" +
            COLUMN_NAME_ID_FOLDER + " INTEGER PRIMARY KEY, "+ COLUMN_FOLDER_NAME + " TEXT" + ");";

    final static String TABLE_ALBUM = "album";
    final static String COLUMN_NAME_ID_ALBUM = "ID_album";
    final static String COLUMN_ALBUM_NAME = "name_of_album";
    final static String CREATE_TABLE_ALBUM = "CREATE TABLE IF NOT EXISTS "+TABLE_ALBUM + " (" +
            COLUMN_NAME_ID_ALBUM + " INTEGER PRIMARY KEY, "+ COLUMN_ALBUM_NAME + " TEXT" + ");";

    final static String TABLE_ARTIST = "artist";
    final static String COLUMN_NAME_ID_ARTIST = "ID_artist";
    final static String COLUMN_ARTIST_NAME = "name_of_artist";
    final static String CREATE_TABLE_ARTIST = "CREATE TABLE IF NOT EXISTS "+TABLE_ARTIST+" (" +
            COLUMN_NAME_ID_ARTIST + " INTEGER PRIMARY KEY, "+ COLUMN_ARTIST_NAME + " TEXT" + ");";

    final static String TABLE_MAIN_FOLDER = "main_folder";
    final static String COLUMN_NAME_ID_MAIN_FOLDER = "ID_main_folder";
    final static String COLUMN_MAIN_FOLDER_NAME = "name_of_main_folder";
    final static String CREATE_TABLE_MAIN_FOLDER = "CREATE TABLE IF NOT EXISTS "+TABLE_MAIN_FOLDER+" (" +
            COLUMN_NAME_ID_MAIN_FOLDER + " INTEGER PRIMARY KEY, "+ COLUMN_MAIN_FOLDER_NAME + " TEXT" + ");";

    final static String TABLE_FILE = "file";
    final static String COLUMN_ID_FOLDER = "ID_file";
    final static String COLUMN_ID_ALBUM = "ID_album";
    final static String COLUMN_ID_ARTIST = "ID_artist";
    final static String COLUMN_ID_MAIN_FOLDER = "ID_main_folder";
    final static String COLUMN_FILE_NAME = "name_of_file";
    final static String COLUMN_PATH = "path_to_file";
    final static String CREATE_TABLE_FILE = "CREATE TABLE IF NOT EXISTS "+TABLE_FILE+" (" +
            COLUMN_FILE_NAME + " TEXT, " + COLUMN_PATH +  " TEXT, "+ COLUMN_ID_FOLDER + " INTEGER, "+ COLUMN_ID_ALBUM + " INTEGER, " +
            COLUMN_ID_ARTIST + " INTEGER, "+ COLUMN_ID_MAIN_FOLDER + " INTEGER"+ ");";

    final static String TABLE_PLAY_SERVICE = "play_service";
    final static String COLUMN_NAME_ID_PLAY_SERVICE = "ID_play_service";
    final static String COLUMN_PATH_PLAY_SERVICE = "path_for_play_service";
    final static String COLUMN_PLAYED_TIME = "last_played_time_for_play_service";
    final static String CREATE_TABLE_PLAY_SERVICE = "CREATE TABLE IF NOT EXISTS "+TABLE_PLAY_SERVICE+" (" +
            COLUMN_NAME_ID_PLAY_SERVICE + " INTEGER PRIMARY KEY, "+  COLUMN_PATH_PLAY_SERVICE + " TEXT, " +
            COLUMN_PLAYED_TIME + " INTEGER);";


    private Context mContext;
    private SQLiteDatabase mDatabase;
    private String folderTop = "..GoToRoot";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
        mDatabase = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_FILE);
        db.execSQL(CREATE_TABLE_FOLDER);
        db.execSQL(CREATE_TABLE_ALBUM);
        db.execSQL(CREATE_TABLE_ARTIST);
        db.execSQL(CREATE_TABLE_MAIN_FOLDER);
        db.execSQL(CREATE_TABLE_PLAY_SERVICE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    private DbHelper open() {
        mDatabase = getWritableDatabase();
        mDatabase.execSQL(CREATE_TABLE_FOLDER);
        mDatabase.execSQL(CREATE_TABLE_FILE);
        mDatabase.execSQL(CREATE_TABLE_ALBUM);
        mDatabase.execSQL(CREATE_TABLE_ARTIST);
        mDatabase.execSQL(CREATE_TABLE_MAIN_FOLDER);
        mDatabase.execSQL(CREATE_TABLE_PLAY_SERVICE);
        return this;
    }


    public void delete() {
        Log.d("Test", "DELETE DB opened");
        open();
        mDatabase.delete(TABLE_FILE, null, null);
        mDatabase.delete(TABLE_FOLDER, null, null);
        mDatabase.delete(TABLE_ALBUM, null, null);
        mDatabase.delete(TABLE_ARTIST, null, null);
        mDatabase.delete(TABLE_MAIN_FOLDER, null, null);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_FILE);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_FOLDER);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ALBUM);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTIST);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_MAIN_FOLDER);
    }

    public void filler(List<String> folders, List<String[]> files, List<String[]> paths, List<String> albums, List<List<String>> pathAlbums,
                       List<String> artists, List<List<String>> pathArtists, List<String> mainFolders, List<List<String>> pathMainFolders) {
        open();
        for (String album: albums){
            ContentValues values = new ContentValues();
            values.put(COLUMN_ALBUM_NAME, album);
            mDatabase.insert(
                    DbHelper.TABLE_ALBUM,
                    null,
                    values);
        }
        for (String artist: artists){
            ContentValues values = new ContentValues();
            values.put(COLUMN_ARTIST_NAME, artist);
            mDatabase.insert(
                    DbHelper.TABLE_ARTIST,
                    null,
                    values);
        }
        for (String folder: mainFolders){
            ContentValues values = new ContentValues();
            values.put(COLUMN_MAIN_FOLDER_NAME, folder);
            mDatabase.insert(
                    DbHelper.TABLE_MAIN_FOLDER,
                    null,
                    values);
        }
        for (int i = 0; i < folders.size(); i++) {
            ContentValues valuesFolder = new ContentValues();
            valuesFolder.put(COLUMN_FOLDER_NAME, folders.get(i));
            mDatabase.insert(
                    DbHelper.TABLE_FOLDER,
                    null,
                    valuesFolder);

            for (int j = 0; j<files.get(i).length; j++){
                String path = paths.get(i)[j];
                ContentValues valuesFile = new ContentValues();
                if (files.get(i)[j].equals("..goToRoot")){
                    valuesFile.put(COLUMN_FILE_NAME, files.get(i)[j]);
                    valuesFile.put(COLUMN_PATH, paths.get(i)[j]);
                    valuesFile.put(COLUMN_ID_FOLDER, i);
                    valuesFile.put(COLUMN_ID_ALBUM, 0);
                    valuesFile.put(COLUMN_ID_ARTIST, 0);
                    valuesFile.put(COLUMN_ID_MAIN_FOLDER, 0);
                }else {
                    valuesFile.put(COLUMN_FILE_NAME, files.get(i)[j]);
                    valuesFile.put(COLUMN_PATH, paths.get(i)[j]);
                    valuesFile.put(COLUMN_ID_FOLDER, i);
                    valuesFile.put(COLUMN_ID_ALBUM, finderID(path, "album", pathAlbums, albums));
                    valuesFile.put(COLUMN_ID_ARTIST, finderID(path, "artist", pathArtists, artists));
                    valuesFile.put(COLUMN_ID_MAIN_FOLDER, finderID(path, "mainFolder", pathMainFolders, mainFolders));
                }
                mDatabase.insert(
                        DbHelper.TABLE_FILE,
                        null,
                        valuesFile);
            }
        } mDatabase.close();
    }
    private int finderID(String path, String sectionName, List<List<String>> sectionPaths, List<String> section){
        int number = 0;
        for(int i = 0; i<sectionPaths.size(); i++){
            if(sectionPaths.get(i).contains(path)){
                number = i;
            }
        }
        String searchingName = section.get(number);
        int searchingID = 0; Cursor cursor;
        if (sectionName.equals("album")) {
            cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_ALBUM, null);
        }else if (sectionName.equals("artist")){
            cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_ARTIST, null);
        }else if (sectionName.equals("mainFolder")){
            cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_MAIN_FOLDER, null);
        }else{
            cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_MAIN_FOLDER, null);
            Log.d("Test", "BLAAAAAAAAAAAAAAAAAAAAAA"+ sectionName);
        }
        cursor.moveToFirst();
        do{
            if(cursor.getString(1).equals(searchingName)){
                searchingID = cursor.getInt(0);
                break;
            }
        }while(cursor.moveToNext());
        cursor.close();
        return searchingID;
    }
    public List<String> getFolders(){
        open();
        List<String> folders = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM "+ TABLE_FOLDER, null);
        cursor.moveToFirst();
        do{
            folders.add(cursor.getString(1));
        }while(cursor.moveToNext());
        cursor.close();
        mDatabase.close();
        return folders;
    }
    public List<String[]> getFiles(){
        open();
        List<String[]> files = new ArrayList<>();
        Cursor cursorFolder = mDatabase.rawQuery("SELECT * FROM "+ TABLE_FOLDER, null);
        int count = cursorFolder.getCount();
        for(int i = 0; i<count; i++){
            Cursor cursorFile = mDatabase.rawQuery("SELECT * FROM "+ TABLE_FILE+ " WHERE "
                    + COLUMN_ID_FOLDER + "=" + i, null);
            String[] fileNamesArray = new String[cursorFile.getCount()];
            cursorFile.moveToFirst();
            int j=0;
            do{
                fileNamesArray[j] = cursorFile.getString(0);
                j++;
            }while (cursorFile.moveToNext());
            cursorFile.close();
            files.add(fileNamesArray);
        }
        cursorFolder.close();
        mDatabase.close();
        return files;
    }
    public List<String[]> getPaths(){
        open();
        List<String[]> path = new ArrayList<>();
        Cursor cursorFolder = mDatabase.rawQuery("SELECT * FROM "+ TABLE_FOLDER, null);
        int count = cursorFolder.getCount();
        for(int i = 0; i<count; i++){
            Cursor cursorFile = mDatabase.rawQuery("SELECT * FROM "+ TABLE_FILE+ " WHERE "
                    + COLUMN_ID_FOLDER + "=" + i, null);
            String[] pathArray = new String[cursorFile.getCount()];
            cursorFile.moveToFirst();
            int j=0;
            do{
                pathArray[j] = cursorFile.getString(1);
                j++;
            }while (cursorFile.moveToNext());
            cursorFile.close();
            path.add(pathArray);
        }
        cursorFolder.close();
        mDatabase.close();
        return path;
    }
    public List<String> getAlbums(){
        open();
        List<String> albums = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM "+ TABLE_ALBUM, null);
        cursor.moveToFirst();
        do{
            albums.add(cursor.getString(1));
        }while(cursor.moveToNext());
        cursor.close();
        mDatabase.close();
        return albums;
    }
    public List<String[]> getPathAlbums(){
        open();
        List<String[]> path = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM "+ TABLE_ALBUM, null);
        cursor.moveToFirst();
        int count = 1;
        do{
            Cursor cursorFile = mDatabase.rawQuery("SELECT * FROM "+ TABLE_FILE+ " WHERE "
                    + COLUMN_ID_ALBUM + "=" + count, null);
            String[] pathArray = new String[cursorFile.getCount()+1];
            cursorFile.moveToFirst();
            int j=1;
            pathArray[0] = folderTop;
            do{
                pathArray[j] = cursorFile.getString(1);
                j++;
            }while (cursorFile.moveToNext());
            cursorFile.close();
            path.add(pathArray);
            count++;
        }while (cursor.moveToNext());
        cursor.close();
        mDatabase.close();
        return path;
    }
    public List<String[]> getNameAlbums(){
        open();
        List<String[]> path = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM "+ TABLE_ALBUM, null);
        cursor.moveToFirst();
        int count = 1;
        do{
            Cursor cursorFile = mDatabase.rawQuery("SELECT * FROM "+ TABLE_FILE+ " WHERE "
                    + COLUMN_ID_ALBUM + "=" + count, null);
            String[] pathArray = new String[cursorFile.getCount()+1];
            cursorFile.moveToFirst();
            int j=1;
            pathArray[0] = folderTop;
            do{
                pathArray[j] = cursorFile.getString(0);
                j++;
            }while (cursorFile.moveToNext());
            cursorFile.close();
            path.add(pathArray);
            count++;
        }while (cursor.moveToNext());
        cursor.close();
        mDatabase.close();
        return path;
    }
    public List<String> getArtist(){
        open();
        List<String> artists = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM "+ TABLE_ARTIST, null);
        cursor.moveToFirst();
        do{
            artists.add(cursor.getString(1));
        }while(cursor.moveToNext());
        cursor.close();
        mDatabase.close();
        return artists;
    }
    public List<String[]> getPathArtist(){
        open();
        List<String[]> path = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM "+ TABLE_ARTIST, null);
        cursor.moveToFirst();
        int count = 1;
        do{
            Cursor cursorFile = mDatabase.rawQuery("SELECT * FROM "+ TABLE_FILE+ " WHERE "
                    + COLUMN_ID_ARTIST + "=" + count, null);
            String[] pathArray = new String[cursorFile.getCount()+1];
            cursorFile.moveToFirst();
            int j=1;
            pathArray[0] = folderTop;
            do{
                pathArray[j] = cursorFile.getString(1);
                j++;
            }while (cursorFile.moveToNext());
            cursorFile.close();
            path.add(pathArray);
            count++;
        }while (cursor.moveToNext());
        cursor.close();
        mDatabase.close();
        return path;
    }
    public List<String[]> getNameArtist(){
        open();
        List<String[]> path = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM "+ TABLE_ARTIST, null);
        cursor.moveToFirst();
        int count = 1;
        do{
            Cursor cursorFile = mDatabase.rawQuery("SELECT * FROM "+ TABLE_FILE+ " WHERE "
                    + COLUMN_ID_ARTIST + "=" + count, null);
            String[] pathArray = new String[cursorFile.getCount()+1];
            cursorFile.moveToFirst();
            int j=1;
            pathArray[0] = folderTop;
            do{
                pathArray[j] = cursorFile.getString(0);
                j++;
            }while (cursorFile.moveToNext());
            cursorFile.close();
            path.add(pathArray);
            count++;
        }while (cursor.moveToNext());
        cursor.close();
        mDatabase.close();
        return path;
    }
    public List<String> getMainFolders(){
        open();
        List<String> mainFolders = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM "+ TABLE_MAIN_FOLDER, null);
        cursor.moveToFirst();
        do{
            mainFolders.add(cursor.getString(1));
        }while(cursor.moveToNext());
        cursor.close();
        mDatabase.close();
        return mainFolders;
    }
    public List<String[]> getPathMainFolders(){
        open();
        List<String[]> path = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM "+ TABLE_MAIN_FOLDER, null);
        cursor.moveToFirst();
        int count = 1;
        do{
            Cursor cursorFile = mDatabase.rawQuery("SELECT * FROM "+ TABLE_FILE+ " WHERE "
                    + COLUMN_ID_MAIN_FOLDER + "=" + count, null);
//            Cursor cursorFile11 =  mDatabase.rawQuery("SELECT * FROM "+ TABLE_FILE, null);
//            Cursor cursorFile1 =  mDatabase.rawQuery("SELECT * FROM "+ TABLE_MAIN_FOLDER, null);
            String[] pathArray = new String[cursorFile.getCount()+1];
            cursorFile.moveToFirst();
//            cursorFile11.moveToFirst();
//            cursorFile1.moveToFirst();
//            Log.d("Test", count + " COUNT");
//            Log.d("Test", "TITLE " + cursorFile11.getString(0));
//            Log.d("Test", "PATH "+cursorFile11.getString(1));
//            Log.d("Test", "Folder "+cursorFile11.getInt(2));
//            Log.d("Test", "Album "+cursorFile11.getInt(3));
//            Log.d("Test", "Artist "+cursorFile11.getInt(4));
//            Log.d("Test", "MainFolder "+cursorFile11.getInt(5));
//            do {
//                Log.d("Test", "MainFolder " + cursorFile1.getInt(0));
//                Log.d("Test", "MainFolder " + cursorFile1.getString(1));
//            }while(cursorFile1.moveToNext());
            int j=1;
            pathArray[0] = folderTop;
                do {
                    pathArray[j] = cursorFile.getString(1);
                    j++;
                } while (cursorFile.moveToNext());

            cursorFile.close();
            path.add(pathArray);
            count++;
        }while (cursor.moveToNext());
        cursor.close();
        mDatabase.close();
        return path;
    }
    public List<String[]> getNameMainFolders(){
        open();
        List<String[]> path = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM "+ TABLE_MAIN_FOLDER, null);
        cursor.moveToFirst();
        int count = 1;
        do{
            Cursor cursorFile = mDatabase.rawQuery("SELECT * FROM "+ TABLE_FILE+ " WHERE "
                    + COLUMN_ID_MAIN_FOLDER + "=" + count, null);
            String[] pathArray = new String[cursorFile.getCount()+1];
            cursorFile.moveToFirst();
            int j=1;
            pathArray[0] = folderTop;
            do{
                pathArray[j] = cursorFile.getString(0);
                j++;
            }while (cursorFile.moveToNext());
            cursorFile.close();
            path.add(pathArray);
            count++;
        }while (cursor.moveToNext());
        cursor.close();
        mDatabase.close();
        return path;
    }

    //Working with DB for Service

    public List<String> getLastPlayList() {
        open();
        List<String> list = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_PLAY_SERVICE, null);
        cursor.moveToFirst();
        do{
            list.add(cursor.getString(1));
        }while(cursor.moveToNext());
        cursor.close();
        mDatabase.close();
        return list;
    }

    public int getLastPlayedTime(){
        open();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_PLAY_SERVICE, null);
        cursor.moveToFirst();
        int lastTime = cursor.getInt(2);
        cursor.close();
        mDatabase.close();
        return lastTime;
    }

    public void setLastPlayListAndTime(List<String> list, int time){
        open();
        mDatabase.delete(TABLE_PLAY_SERVICE, null, null);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAY_SERVICE);
        mDatabase.execSQL(CREATE_TABLE_PLAY_SERVICE);
        for (int i = 0; i<list.size(); i++){
            ContentValues values = new ContentValues();
            values.put(COLUMN_PATH_PLAY_SERVICE, list.get(i));
            if(i==0){
                values.put(COLUMN_PLAYED_TIME, time);
            }else{
                values.put(COLUMN_PLAYED_TIME, 0);
            }
            mDatabase.insert(
                    DbHelper.TABLE_PLAY_SERVICE,
                    null,
                    values);
        }
        mDatabase.close();
    }


}
