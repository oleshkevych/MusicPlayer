package com.example.vov4ik.musicplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by vov4ik on 8/3/2016.
 */
public class DbHelper extends SQLiteOpenHelper {

    final static int DB_VERSION = 5;
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

    final static String TABLE_All_SONGS = "all_songs";
    final static String COLUMN_NAME_ID_All_SONGS = "ID_all_songs";
    final static String COLUMN_All_SONGS_PATH = "path_of_all_songs";
    final static String COLUMN_All_SONGS_NAME = "name_of_all_songs";
    final static String CREATE_TABLE_All_SONGS = "CREATE TABLE IF NOT EXISTS "+TABLE_All_SONGS+" (" +
            COLUMN_NAME_ID_All_SONGS + " INTEGER PRIMARY KEY, "+ COLUMN_All_SONGS_PATH + " TEXT, "+COLUMN_All_SONGS_NAME + " TEXT" + ");";

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
    final static String CREATE_TABLE_PLAY_SERVICE = "CREATE TABLE IF NOT EXISTS "+TABLE_PLAY_SERVICE+" (" +
            COLUMN_NAME_ID_PLAY_SERVICE + " INTEGER PRIMARY KEY, "+  COLUMN_PATH_PLAY_SERVICE + " TEXT);";

    final static String TABLE_PLAY_SERVICE_ATTRIBUTES = "play_service_attribute";
    final static String COLUMN_PLAYED_TIME = "last_played_time_for_play_service";
    final static String COLUMN_PLAYED_NUMBER = "last_played_number_for_play_service";
    final static String COLUMN_PLAYED_SHUFFLE = "last_played_shuffle_state_for_play_service";
    final static String CREATE_TABLE_PLAY_SERVICE_ATTRIBUTES = "CREATE TABLE IF NOT EXISTS "+ TABLE_PLAY_SERVICE_ATTRIBUTES +" (" +
            COLUMN_PLAYED_TIME + " INTEGER, " + COLUMN_PLAYED_NUMBER + " INTEGER, " +
            COLUMN_PLAYED_SHUFFLE + " BOOLEAN);";





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
        db.execSQL(CREATE_TABLE_PLAY_SERVICE_ATTRIBUTES);
        db.execSQL(CREATE_TABLE_All_SONGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

//        delete();
//        mDatabase.delete(TABLE_PLAY_SERVICE, null, null);
//        mDatabase.delete(TABLE_PLAY_SERVICE_ATTRIBUTES, null, null);
        onCreate(db);
    }
    private DbHelper open() {
        mDatabase = getWritableDatabase();
        mDatabase.execSQL(CREATE_TABLE_FOLDER);
        mDatabase.execSQL(CREATE_TABLE_FILE);
        mDatabase.execSQL(CREATE_TABLE_ALBUM);
        mDatabase.execSQL(CREATE_TABLE_ARTIST);
        mDatabase.execSQL(CREATE_TABLE_MAIN_FOLDER);
        mDatabase.execSQL(CREATE_TABLE_PLAY_SERVICE);
        mDatabase.execSQL(CREATE_TABLE_All_SONGS);
        return this;
    }


    public void delete() {
        Log.d("Test", "DELETE DB opened");
//        open();
        mDatabase.delete(TABLE_FILE, null, null);
        mDatabase.delete(TABLE_FOLDER, null, null);
        mDatabase.delete(TABLE_ALBUM, null, null);
        mDatabase.delete(TABLE_ARTIST, null, null);
        mDatabase.delete(TABLE_MAIN_FOLDER, null, null);
        mDatabase.delete(TABLE_All_SONGS, null, null);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_All_SONGS);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_FILE);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_FOLDER);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ALBUM);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTIST);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_MAIN_FOLDER);
    }

//    public void filler(List<String> folders, List<String[]> files, List<String[]> paths, List<String> albums, List<List<String>> pathAlbums,
//                       List<String> artists, List<List<String>> pathArtists, List<String> mainFolders, List<List<String>> pathMainFolders) {
//        open();
//
//        for (String album: albums){
//            ContentValues values = new ContentValues();
//            values.put(COLUMN_ALBUM_NAME, album);
//            mDatabase.insert(
//                    DbHelper.TABLE_ALBUM,
//                    null,
//                    values);
//        }
//        for (int i = 0; i< paths.size(); i++){
//            for(int j = 1; j<paths.get(i).length; j++) {
//                ContentValues values = new ContentValues();
//                values.put(COLUMN_All_SONGS_PATH, paths.get(i)[j]);
//                values.put(COLUMN_All_SONGS_NAME, files.get(i)[j]);
//                mDatabase.insert(
//                        DbHelper.TABLE_All_SONGS,
//                        null,
//                        values);
//            }
//        }
//        for (String artist: artists){
//            ContentValues values = new ContentValues();
//            values.put(COLUMN_ARTIST_NAME, artist);
//            mDatabase.insert(
//                    DbHelper.TABLE_ARTIST,
//                    null,
//                    values);
//        }
//        for (String folder: mainFolders){
//            ContentValues values = new ContentValues();
//            values.put(COLUMN_MAIN_FOLDER_NAME, folder);
//            mDatabase.insert(
//                    DbHelper.TABLE_MAIN_FOLDER,
//                    null,
//                    values);
//        }
//        for (int i = 0; i < folders.size(); i++) {
//            ContentValues valuesFolder = new ContentValues();
//            valuesFolder.put(COLUMN_FOLDER_NAME, folders.get(i));
//            mDatabase.insert(
//                    DbHelper.TABLE_FOLDER,
//                    null,
//                    valuesFolder);
//
//            for (int j = 0; j<files.get(i).length; j++){
//                String path = paths.get(i)[j];
//                ContentValues valuesFile = new ContentValues();
//                if (files.get(i)[j].equals("..goToRoot")){
//                    valuesFile.put(COLUMN_FILE_NAME, files.get(i)[j]);
//                    valuesFile.put(COLUMN_PATH, paths.get(i)[j]);
//                    valuesFile.put(COLUMN_ID_FOLDER, i);
//                    valuesFile.put(COLUMN_ID_ALBUM, 0);
//                    valuesFile.put(COLUMN_ID_ARTIST, 0);
//                    valuesFile.put(COLUMN_ID_MAIN_FOLDER, 0);
//                }else {
//                    valuesFile.put(COLUMN_FILE_NAME, files.get(i)[j]);
//                    valuesFile.put(COLUMN_PATH, paths.get(i)[j]);
//                    valuesFile.put(COLUMN_ID_FOLDER, i);
//                    valuesFile.put(COLUMN_ID_ALBUM, finderID(path, "album", pathAlbums, albums));
//                    valuesFile.put(COLUMN_ID_ARTIST, finderID(path, "artist", pathArtists, artists));
//                    valuesFile.put(COLUMN_ID_MAIN_FOLDER, finderID(path, "mainFolder", pathMainFolders, mainFolders));
//                }
//                mDatabase.insert(
//                        DbHelper.TABLE_FILE,
//                        null,
//                        valuesFile);
//            }
//        } mDatabase.close();
//    }
//    private int finderID(String path, String sectionName, List<List<String>> sectionPaths, List<String> section){
//        int number = 0;
//        for(int i = 0; i<sectionPaths.size(); i++){
//            if(sectionPaths.get(i).contains(path)){
//                number = i;
//            }
//        }
//        String searchingName = section.get(number);
//        int searchingID = 0; Cursor cursor;
//        if (sectionName.equals("album")) {
//            cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_ALBUM, null);
//        }else if (sectionName.equals("artist")){
//            cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_ARTIST, null);
//        }else if (sectionName.equals("mainFolder")){
//            cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_MAIN_FOLDER, null);
//        }else{
//            cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_MAIN_FOLDER, null);
//            Log.d("Test", "BLAAAAAAAAAAAAAAAAAAAAAA"+ sectionName);
//        }
//        cursor.moveToFirst();
//        do{
//            if(cursor.getString(1).equals(searchingName)){
//                searchingID = cursor.getInt(0);
//                break;
//            }
//        }while(cursor.moveToNext());
//        cursor.close();
//        return searchingID;
//    }
    public void musicFileFiller(List<MusicFile> musicFilesList) {
        open();
        long album_id, artist_id, folder_id, main_folder_id;
        List<String> albums = new ArrayList<>();
        List<String> artists = new ArrayList<>();
        List<String> folders = new ArrayList<>();
        List<String> mainFolders = new ArrayList<>();
        Collections.sort(musicFilesList, new SortByName());

        for(MusicFile musicFile: musicFilesList){
            if(albums.contains(musicFile.getAlbum())){
                album_id = albums.indexOf(musicFile.getAlbum())+1;
            }else {
                ContentValues values = new ContentValues();
                values.put(COLUMN_ALBUM_NAME, musicFile.getAlbum());
                album_id = mDatabase.insert(
                        DbHelper.TABLE_ALBUM,
                        null,
                        values);
                albums.add(musicFile.getAlbum());
            }

            ContentValues valuesAll = new ContentValues();
            valuesAll.put(COLUMN_All_SONGS_PATH, musicFile.getPath());
            valuesAll.put(COLUMN_All_SONGS_NAME, musicFile.getTitle());
            mDatabase.insert(
                    DbHelper.TABLE_All_SONGS,
                    null,
                    valuesAll);

            if(artists.contains(musicFile.getArtist())){
                artist_id = artists.indexOf(musicFile.getArtist())+1;
            }else {
                ContentValues values = new ContentValues();
                values.put(COLUMN_ARTIST_NAME, musicFile.getArtist());
                artist_id = mDatabase.insert(
                        DbHelper.TABLE_ARTIST,
                        null,
                        values);
                artists.add(musicFile.getArtist());
            }
            if(mainFolders.contains(musicFile.getMainFolder())){
                main_folder_id = mainFolders.indexOf(musicFile.getMainFolder())+1;
            }else {
                ContentValues values = new ContentValues();
                values.put(COLUMN_MAIN_FOLDER_NAME, musicFile.getMainFolder());
                main_folder_id = mDatabase.insert(
                        DbHelper.TABLE_MAIN_FOLDER,
                        null,
                        values);
                mainFolders.add(musicFile.getMainFolder());
            }

            if(folders.contains(musicFile.getFolder())){
                folder_id = folders.indexOf(musicFile.getFolder())+1;
            }else {
                ContentValues valuesFolder = new ContentValues();
                valuesFolder.put(COLUMN_FOLDER_NAME, musicFile.getFolder());
                folder_id = mDatabase.insert(
                        DbHelper.TABLE_FOLDER,
                        null,
                        valuesFolder);
                folders.add(musicFile.getFolder());
            }
            ContentValues valuesFile = new ContentValues();
//            valuesFile = new ContentValues();
            valuesFile.put(COLUMN_FILE_NAME, musicFile.getTitle());
            valuesFile.put(COLUMN_PATH, musicFile.getPath());
            valuesFile.put(COLUMN_ID_FOLDER, folder_id);
            valuesFile.put(COLUMN_ID_ALBUM, album_id);
            valuesFile.put(COLUMN_ID_ARTIST, artist_id);
            valuesFile.put(COLUMN_ID_MAIN_FOLDER, main_folder_id);
            mDatabase.insert(
                    DbHelper.TABLE_FILE,
                    null,
                    valuesFile);
        }
        mDatabase.close();

    }

    public List<MusicFile> getMusicFilesForSearch(){
        open();
        List<MusicFile> list = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM "+ TABLE_FILE, null);
        cursor.moveToFirst();
        Cursor c;
        do{
            if (!cursor.getString(0).equals("..goToRoot")) {
                MusicFile m = new MusicFile();
                m.setTitle(cursor.getString(0));
                m.setPath(cursor.getString(1));
              c = mDatabase.rawQuery("SELECT * FROM " + TABLE_FOLDER + " WHERE " + COLUMN_NAME_ID_FOLDER + " = " + String.valueOf(cursor.getInt(2)), null);
                c.moveToFirst();
                m.setFolder(c.getString(1));
                c.close();
                c = mDatabase.rawQuery("SELECT * FROM " + TABLE_ALBUM + " WHERE " + COLUMN_NAME_ID_ALBUM + " = " + String.valueOf(cursor.getInt(3)), null);
                c.moveToFirst();
                m.setAlbum(c.getString(1));
                c.close();
                c = mDatabase.rawQuery("SELECT * FROM " + TABLE_ARTIST + " WHERE " + COLUMN_NAME_ID_ARTIST + " = " + String.valueOf(cursor.getInt(4)), null);
                c.moveToFirst();
                m.setArtist(c.getString(1));
                c.close();
                c = mDatabase.rawQuery("SELECT * FROM " + TABLE_MAIN_FOLDER + " WHERE " + COLUMN_NAME_ID_MAIN_FOLDER + " = " + String.valueOf(cursor.getInt(5)), null);
                c.moveToFirst();
                m.setMainFolder(c.getString(1));
                c.close();
                list.add(m);
            }
        }while(cursor.moveToNext());

        cursor.close();
        return list;
    }
    public List<String> getAllSongsPaths(){
        open();
        List<String> songs = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_All_SONGS, null);
        cursor.moveToFirst();
        try {
            do{
                if(!(cursor.getString(2)).equals("..goToRoot")) {
                    songs.add(cursor.getString(1));
                }
            }while(cursor.moveToNext());
        }catch (CursorIndexOutOfBoundsException c){
            songs.add("Error database");
        }
        cursor.close();
        mDatabase.close();
        return songs;
    }
    public List<String> getAllSongsNames(){
        open();
        List<String> songs = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_All_SONGS, null);
        cursor.moveToFirst();
        try {
            do{
                if(!(cursor.getString(2)).equals("..goToRoot")) {
                    songs.add(cursor.getString(2));
                }
            }while(cursor.moveToNext());
        }catch (CursorIndexOutOfBoundsException c){
            songs.add("Error database");
        }
        cursor.close();
        mDatabase.close();
        return songs;
    }
    public List<List<String>> getFilesForFolder(){
        open();
        List<List<String>> files = new ArrayList<>();
        Cursor cursorFolder = mDatabase.rawQuery("SELECT * FROM "+ TABLE_FOLDER, null);
        int count = cursorFolder.getCount();
        try {
            for (int i = 0; i < count; i++) {
                Cursor cursorFile = mDatabase.rawQuery("SELECT * FROM " + TABLE_FILE + " WHERE "
                        + COLUMN_ID_FOLDER + "=" + (i+1), null);
                List<String> fileNamesList = new ArrayList<>();
                cursorFile.moveToFirst();
                do {
                    fileNamesList.add(cursorFile.getString(0));
                } while (cursorFile.moveToNext());
                cursorFile.close();
                files.add(fileNamesList);
            }
        }catch (CursorIndexOutOfBoundsException c){
            files.add(new ArrayList<String>(Arrays.asList(new String[]{"Error database"})));
        }
        cursorFolder.close();
        mDatabase.close();
        return files;
    }
    public List<List<String>> getPathsForFolder(){
        open();
        List<List<String>> paths = new ArrayList<>();
        Cursor cursorFolder = mDatabase.rawQuery("SELECT * FROM " + TABLE_FOLDER, null);
        int count = cursorFolder.getCount();
        try {
            for (int i = 0; i < count; i++) {
                Cursor cursorFile = mDatabase.rawQuery("SELECT * FROM " + TABLE_FILE + " WHERE "
                        + COLUMN_ID_FOLDER + "=" + (i+1), null);
                List<String> fileNamesList = new ArrayList<>();
                cursorFile.moveToFirst();
                do {
                    fileNamesList.add(cursorFile.getString(1));
                } while (cursorFile.moveToNext());
                cursorFile.close();
                paths.add(fileNamesList);
            }
        }catch (CursorIndexOutOfBoundsException c){
            paths.add(new ArrayList<String>(Arrays.asList(new String[]{"Error database"})));
        }
        cursorFolder.close();
        mDatabase.close();
        return paths;
    }
    public List<String> getFolders(){
        open();
        List<String> folders = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_FOLDER, null);
        cursor.moveToFirst();
        try {
            do{
                folders.add(cursor.getString(1));
            }while(cursor.moveToNext());
        }catch (CursorIndexOutOfBoundsException c){
            folders.add("Error database");
        }
        cursor.close();
        mDatabase.close();
        return folders;
    }
    public List<List<String>> getFilesForAlbums(){
        open();
        List<List<String>> files = new ArrayList<>();
        Cursor cursorFolder = mDatabase.rawQuery("SELECT * FROM "+ TABLE_ALBUM, null);
        int count = cursorFolder.getCount();
        try {
            for (int i = 0; i < count; i++) {
                Cursor cursorFile = mDatabase.rawQuery("SELECT * FROM " + TABLE_FILE + " WHERE "
                        + COLUMN_ID_ALBUM + "=" + (i+1), null);
                List<String> fileNamesList = new ArrayList<>();
                cursorFile.moveToFirst();
                do {
                    fileNamesList.add(cursorFile.getString(0));
                } while (cursorFile.moveToNext());
                cursorFile.close();
                files.add(fileNamesList);
            }
        }catch (CursorIndexOutOfBoundsException c){
            files.add(new ArrayList<String>(Arrays.asList(new String[]{"Error database"})));
        }
        cursorFolder.close();
        mDatabase.close();
        return files;
    }
    public List<List<String>> getPathsForAlbums(){
        open();
        List<List<String>> paths = new ArrayList<>();
        Cursor cursorFolder = mDatabase.rawQuery("SELECT * FROM " + TABLE_ALBUM, null);
        int count = cursorFolder.getCount();
        try {
            for (int i = 0; i < count; i++) {
                Cursor cursorFile = mDatabase.rawQuery("SELECT * FROM " + TABLE_FILE + " WHERE "
                        + COLUMN_ID_ALBUM + "=" + (i+1), null);
                List<String> fileNamesList = new ArrayList<>();
                cursorFile.moveToFirst();
                do {
                    fileNamesList.add(cursorFile.getString(1));
                } while (cursorFile.moveToNext());
                cursorFile.close();
                paths.add(fileNamesList);
            }
        }catch (CursorIndexOutOfBoundsException c){
            paths.add(new ArrayList<String>(Arrays.asList(new String[]{"Error database"})));
        }
        cursorFolder.close();
        mDatabase.close();
        return paths;
    }
    public List<String> getAlbums(){
        open();
        List<String> albums = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_ALBUM, null);
        cursor.moveToFirst();
        try {
            do {
                if(cursor.getString(1)!=null) {
                    albums.add(cursor.getString(1));
                }else{
                    albums.add("");
                }
            } while (cursor.moveToNext());
        }catch (CursorIndexOutOfBoundsException c){
            albums.add("Error database");
        }
        cursor.close();
        mDatabase.close();
        return albums;
    }
    public List<List<String>> getFilesForArtists(){
        open();
        List<List<String>> files = new ArrayList<>();
        Cursor cursorFolder = mDatabase.rawQuery("SELECT * FROM "+ TABLE_ARTIST, null);
        int count = cursorFolder.getCount();
        try {
            for (int i = 0; i < count; i++) {
                Cursor cursorFile = mDatabase.rawQuery("SELECT * FROM " + TABLE_FILE + " WHERE "
                        + COLUMN_ID_ARTIST + "=" + (i+1), null);
                List<String> fileNamesList = new ArrayList<>();
                cursorFile.moveToFirst();
                do {
                    fileNamesList.add(cursorFile.getString(0));
                } while (cursorFile.moveToNext());
                cursorFile.close();
                files.add(fileNamesList);
            }
        }catch (CursorIndexOutOfBoundsException c){
            files.add(new ArrayList<String>(Arrays.asList(new String[]{"Error database"})));
        }
        cursorFolder.close();
        mDatabase.close();
        return files;
    }
    public List<List<String>> getPathsForArtists(){
        open();
        List<List<String>> paths = new ArrayList<>();
        Cursor cursorFolder = mDatabase.rawQuery("SELECT * FROM " + TABLE_ARTIST, null);
        int count = cursorFolder.getCount();
        try {
            for (int i = 0; i < count; i++) {
                Cursor cursorFile = mDatabase.rawQuery("SELECT * FROM " + TABLE_FILE + " WHERE "
                        + COLUMN_ID_ARTIST + "=" + (i+1), null);
                List<String> fileNamesList = new ArrayList<>();
                cursorFile.moveToFirst();
                do {
                    fileNamesList.add(cursorFile.getString(1));
                } while (cursorFile.moveToNext());
                cursorFile.close();
                paths.add(fileNamesList);
            }
        }catch (CursorIndexOutOfBoundsException c){
            paths.add(new ArrayList<String>(Arrays.asList(new String[]{"Error database"})));
        }
        cursorFolder.close();
        mDatabase.close();
        return paths;
    }
    public List<String> getArtist(){
        open();
        List<String> artists = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_ARTIST, null);
        cursor.moveToFirst();
        try {
            do {
                if(cursor.getString(1) != null) {
                    artists.add(cursor.getString(1));
                }else{
                    artists.add("");
                }
            } while (cursor.moveToNext());
        }catch (CursorIndexOutOfBoundsException c){
            artists.add("Error database");
        }
        cursor.close();
        mDatabase.close();
        return artists;
    }
    public List<List<String>> getFilesForMainFolders(){
        open();
        List<List<String>> files = new ArrayList<>();
        Cursor cursorFolder = mDatabase.rawQuery("SELECT * FROM "+ TABLE_MAIN_FOLDER, null);
        int count = cursorFolder.getCount();
        Cursor cursorFile;
        try {
            for (int i = 0; i < count; i++) {
                cursorFile = mDatabase.rawQuery("SELECT * FROM " + TABLE_FILE + " WHERE "
                        + COLUMN_ID_MAIN_FOLDER + " = " + (i+1), null);
                List<String> fileNamesList = new ArrayList<>();
                cursorFile.moveToFirst();
                do {
                    fileNamesList.add(cursorFile.getString(0));
                } while (cursorFile.moveToNext());
                cursorFile.close();
                files.add(fileNamesList);
            }
        }catch (CursorIndexOutOfBoundsException ci){
            files.add(new ArrayList<String>(Arrays.asList(new String[]{"Error database"})));
        }
        cursorFolder.close();
        mDatabase.close();
        return files;
    }
    public List<List<String>> getPathsForMainFolders(){
        open();
        List<List<String>> paths = new ArrayList<>();
        Cursor cursorFolder = mDatabase.rawQuery("SELECT * FROM " + TABLE_MAIN_FOLDER, null);
        int count = cursorFolder.getCount();
        Cursor cursorFile;
        try {
            for (int i = 0; i < count; i++) {
                cursorFile = mDatabase.rawQuery("SELECT * FROM " + TABLE_FILE + " WHERE "
                        + COLUMN_ID_MAIN_FOLDER + "=" + (i+1), null);
                List<String> fileNamesList = new ArrayList<>();
                cursorFile.moveToFirst();
                do {
                    fileNamesList.add(cursorFile.getString(1));
                } while (cursorFile.moveToNext());
                cursorFile.close();
                paths.add(fileNamesList);
                Log.d("te3r"+count, fileNamesList.toString());
            }
        }catch (CursorIndexOutOfBoundsException c){
            Log.d("Error", c.getMessage());
            paths.add(new ArrayList<>(Arrays.asList(new String[]{"Error database"})));
        }
        cursorFolder.close();
        mDatabase.close();
        return paths;
    }
    public List<String> getMainFolders(){
        open();
        List<String> mainFolders = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_MAIN_FOLDER, null);
        cursor.moveToFirst();
        try {
            do {
                mainFolders.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }catch (CursorIndexOutOfBoundsException c){
            mainFolders.add("Error database");
        }
        cursor.close();
        mDatabase.close();
        return mainFolders;
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
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_PLAY_SERVICE_ATTRIBUTES, null);
        cursor.moveToFirst();
        int lastTime = cursor.getInt(0);
        cursor.close();
        mDatabase.close();
        return lastTime;
    }
    public int getLastPlayedNumber(){
        open();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_PLAY_SERVICE_ATTRIBUTES, null);
        cursor.moveToFirst();
        int lastNumber = cursor.getInt(1);
        cursor.close();
        mDatabase.close();
        return lastNumber;
    }
    public boolean getLastPlayedState(){
        open();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_PLAY_SERVICE_ATTRIBUTES, null);
        cursor.moveToFirst();
        boolean lastState = false;
        if(cursor.getInt(2) == 1) {
            lastState = true;
        }
        cursor.close();
        mDatabase.close();
        return lastState;
    }

    public void setLastPlayList(List<String> list) {
        open();
        Log.d("Test", "DATABASE is writing current trek list");
        mDatabase.delete(TABLE_PLAY_SERVICE, null, null);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAY_SERVICE);
        mDatabase.execSQL(CREATE_TABLE_PLAY_SERVICE);
        for (int i = 0; i < list.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_PATH_PLAY_SERVICE, list.get(i));
            mDatabase.insert(
                    DbHelper.TABLE_PLAY_SERVICE,
                    null,
                    values);
        }
        mDatabase.close();
    }
    public void setPlaylistAttributes(int time, int number, boolean shuffle){
        open();
        mDatabase.delete(TABLE_PLAY_SERVICE_ATTRIBUTES, null, null);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAY_SERVICE_ATTRIBUTES);
        mDatabase.execSQL(CREATE_TABLE_PLAY_SERVICE_ATTRIBUTES);
        ContentValues values = new ContentValues();
        values.put(COLUMN_PLAYED_TIME, time);
        values.put(COLUMN_PLAYED_NUMBER, number);
        if(shuffle) {
            values.put(COLUMN_PLAYED_SHUFFLE, 1);
        }else{
            values.put(COLUMN_PLAYED_SHUFFLE, 0);
        }
        mDatabase.insert(
                DbHelper.TABLE_PLAY_SERVICE_ATTRIBUTES,
                null,
                values);
        mDatabase.close();
    }
}




