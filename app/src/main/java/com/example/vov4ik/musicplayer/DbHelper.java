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
import java.util.HashMap;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {

    private final static int DB_VERSION = 5;
    private final static String DB_NAME = "OwnHomeMadePlay.db";
    private final static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ";
    private final static String DROP_TABLE = "DROP TABLE IF EXISTS ";

    private final static String TABLE_FOLDER = "Folders";
    private final static String TABLE_ALBUM = "Albums";
    private final static String TABLE_ARTIST = "Artists";
    private final static String TABLE_MAIN_FOLDER = "MainFolders";
    private final static String TABLE_FILE = "Files";
    private final static String TABLE_PLAY_SERVICE = "PlayServices";
    private final static String TABLE_PLAY_SERVICE_ATTRIBUTES = "PlayServiceAttributes";

    private final static String ID = "Id";
    private final static String NAME = "Name";
    private final static String PATH = "Path";

    private final static String COLUMN_ID_FOLDER = "Folder.Id";
    private final static String COLUMN_ID_ALBUM = "Album.Id";
    private final static String COLUMN_ID_ARTIST = "Artist.Id";
    private final static String COLUMN_ID_MAIN_FOLDER = "MainFolder.Id";
    private final static String COLUMN_PLAYED_TIME = "LastPlayedTime";
    private final static String COLUMN_PLAYED_NUMBER = "LastPlayedNumber";
    private final static String COLUMN_PLAYED_SHUFFLE = "LastPlayedShuffleState";

    private SQLiteDatabase mDatabase;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mDatabase = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createDbIfNotExist(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        createDbIfNotExist(db);
    }

    private static void createDbIfNotExist(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE + TABLE_FILE + " (" +
                "[" + NAME + "] TEXT, " +
                "[" + PATH + "] TEXT, " +
                "[" + COLUMN_ID_FOLDER + "] INTEGER, " +
                "[" + COLUMN_ID_ALBUM + "] INTEGER, " +
                "[" + COLUMN_ID_ARTIST + "] INTEGER, " +
                "[" + COLUMN_ID_MAIN_FOLDER + "] INTEGER" + ");");
        db.execSQL(CREATE_TABLE + TABLE_FOLDER + " (" +
                ID + " INTEGER PRIMARY KEY, " +
                NAME + " TEXT" + ");");
        db.execSQL(CREATE_TABLE + TABLE_ALBUM + " (" +
                ID + " INTEGER PRIMARY KEY, " +
                NAME + " TEXT" + ");");
        db.execSQL(CREATE_TABLE + TABLE_ARTIST + " (" +
                ID + " INTEGER PRIMARY KEY, " +
                NAME + " TEXT" + ");");
        db.execSQL(CREATE_TABLE + TABLE_MAIN_FOLDER + " (" +
                ID + " INTEGER PRIMARY KEY, " +
                NAME + " TEXT" + ");");
        db.execSQL(CREATE_TABLE + TABLE_PLAY_SERVICE + " (" +
                ID + " INTEGER PRIMARY KEY, " +
                PATH + " TEXT);");
        db.execSQL(CREATE_TABLE + TABLE_PLAY_SERVICE_ATTRIBUTES + " (" +
                COLUMN_PLAYED_TIME + " INTEGER, " +
                COLUMN_PLAYED_NUMBER + " INTEGER, " +
                COLUMN_PLAYED_SHUFFLE + " BOOLEAN);");
    }

    public void deleteDb() {
        mDatabase.execSQL(DROP_TABLE + TABLE_FILE);
        mDatabase.execSQL(DROP_TABLE + TABLE_FOLDER);
        mDatabase.execSQL(DROP_TABLE + TABLE_ALBUM);
        mDatabase.execSQL(DROP_TABLE + TABLE_ARTIST);
        mDatabase.execSQL(DROP_TABLE + TABLE_MAIN_FOLDER);
    }

    private int addItemIfNotExist(HashMap<String, Integer> itemsMap, String itemName, String tableName) {
        int item_id;
        if (itemsMap.containsKey(itemName)) {
            item_id = itemsMap.get(itemName);
        } else {
            item_id = itemsMap.size();
            ContentValues values = new ContentValues();
            values.put(NAME, itemName);
            values.put(ID, item_id);
            mDatabase.insert(tableName, null, values);
            itemsMap.put(itemName, item_id);
        }

        return item_id;
    }

    public void musicFileFiller(List<MusicFile> musicFilesList) {
        createDbIfNotExist(mDatabase);

        int album_id,
                artist_id,
                folder_id,
                main_folder_id;
        HashMap<String, Integer> albums = new HashMap<>();
        HashMap<String, Integer> artists = new HashMap<>();
        HashMap<String, Integer> folders = new HashMap<>();
        HashMap<String, Integer> mainFolders = new HashMap<>();

        for (MusicFile musicFile : musicFilesList) {
            album_id = addItemIfNotExist(albums, musicFile.getAlbum(), TABLE_ALBUM);
            artist_id = addItemIfNotExist(artists, musicFile.getArtist(), TABLE_ARTIST);
            main_folder_id = addItemIfNotExist(mainFolders, musicFile.getMainFolder(), TABLE_MAIN_FOLDER);
            folder_id = addItemIfNotExist(folders, musicFile.getFolder(), TABLE_FOLDER);
            ContentValues valuesFile = new ContentValues();
            valuesFile.put(NAME, musicFile.getTitle());
            valuesFile.put(PATH, musicFile.getPath());
            valuesFile.put(COLUMN_ID_FOLDER, folder_id);
            valuesFile.put(COLUMN_ID_ALBUM, album_id);
            valuesFile.put(COLUMN_ID_ARTIST, artist_id);
            valuesFile.put(COLUMN_ID_MAIN_FOLDER, main_folder_id);
            mDatabase.insert(TABLE_FILE, null, valuesFile);
        }

        mDatabase.close();
    }

    public List<MusicFile> getMusicFilesForSearch() {
        createDbIfNotExist(mDatabase);
        List<MusicFile> list = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_FILE, null);
        cursor.moveToFirst();
        Cursor c;
        do {
            if (!cursor.getString(0).equals("..goToRoot")) {
                MusicFile m = new MusicFile();
                m.setTitle(cursor.getString(0));
                m.setPath(cursor.getString(1));
                c = mDatabase.rawQuery("SELECT * FROM " + TABLE_FOLDER + " WHERE " + ID + " = " + String.valueOf(cursor.getInt(2)), null);
                c.moveToFirst();
                m.setFolder(c.getString(1));
                c.close();
                c = mDatabase.rawQuery("SELECT * FROM " + TABLE_ALBUM + " WHERE " + ID + " = " + String.valueOf(cursor.getInt(3)), null);
                c.moveToFirst();
                m.setAlbum(c.getString(1));
                c.close();
                c = mDatabase.rawQuery("SELECT * FROM " + TABLE_ARTIST + " WHERE " + ID + " = " + String.valueOf(cursor.getInt(4)), null);
                c.moveToFirst();
                m.setArtist(c.getString(1));
                c.close();
                c = mDatabase.rawQuery("SELECT * FROM " + TABLE_MAIN_FOLDER + " WHERE " + ID + " = " + String.valueOf(cursor.getInt(5)), null);
                c.moveToFirst();
                m.setMainFolder(c.getString(1));
                c.close();
                list.add(m);
            }
        } while (cursor.moveToNext());

        cursor.close();
        return list;
    }

    public List<List<String>> getFilesForFolder() {
        createDbIfNotExist(mDatabase);
        List<List<String>> files = new ArrayList<>();
        Cursor cursorFolder = mDatabase.rawQuery("SELECT * FROM " + TABLE_FOLDER, null);
        int count = cursorFolder.getCount();
        try {
            for (int i = 0; i < count; i++) {
                Cursor cursorFile = mDatabase.rawQuery("SELECT * FROM " + TABLE_FILE + " WHERE "
                        + COLUMN_ID_FOLDER + "=" + (i + 1), null);
                List<String> fileNamesList = new ArrayList<>();
                cursorFile.moveToFirst();
                do {
                    fileNamesList.add(cursorFile.getString(0));
                } while (cursorFile.moveToNext());
                cursorFile.close();
                files.add(fileNamesList);
            }
        } catch (CursorIndexOutOfBoundsException c) {
            files.add(new ArrayList<String>(Arrays.asList(new String[]{"Error database"})));
        }
        cursorFolder.close();
        mDatabase.close();
        return files;
    }

    public List<List<String>> getPathsForFolder() {
        createDbIfNotExist(mDatabase);
        List<List<String>> paths = new ArrayList<>();
        Cursor cursorFolder = mDatabase.rawQuery("SELECT * FROM " + TABLE_FOLDER, null);
        int count = cursorFolder.getCount();
        try {
            for (int i = 0; i < count; i++) {
                Cursor cursorFile = mDatabase.rawQuery("SELECT * FROM " + TABLE_FILE + " WHERE "
                        + COLUMN_ID_FOLDER + "=" + (i + 1), null);
                List<String> fileNamesList = new ArrayList<>();
                cursorFile.moveToFirst();
                do {
                    fileNamesList.add(cursorFile.getString(1));
                } while (cursorFile.moveToNext());
                cursorFile.close();
                paths.add(fileNamesList);
            }
        } catch (CursorIndexOutOfBoundsException c) {
            paths.add(new ArrayList<>(Arrays.asList(new String[]{"Error database"})));
        }
        cursorFolder.close();
        mDatabase.close();
        return paths;
    }

    public List<List<String>> getFilesForAlbums() {
        createDbIfNotExist(mDatabase);
        List<List<String>> files = new ArrayList<>();
        Cursor cursorFolder = mDatabase.rawQuery("SELECT * FROM " + TABLE_ALBUM, null);
        int count = cursorFolder.getCount();
        try {
            for (int i = 0; i < count; i++) {
                Cursor cursorFile = mDatabase.rawQuery("SELECT * FROM " + TABLE_FILE + " WHERE "
                        + COLUMN_ID_ALBUM + "=" + (i + 1), null);
                List<String> fileNamesList = new ArrayList<>();
                cursorFile.moveToFirst();
                do {
                    fileNamesList.add(cursorFile.getString(0));
                } while (cursorFile.moveToNext());
                cursorFile.close();
                files.add(fileNamesList);
            }
        } catch (CursorIndexOutOfBoundsException c) {
            files.add(new ArrayList<String>(Arrays.asList(new String[]{"Error database"})));
        }
        cursorFolder.close();
        mDatabase.close();
        return files;
    }

    public List<List<String>> getPathsForAlbums() {
        createDbIfNotExist(mDatabase);
        List<List<String>> paths = new ArrayList<>();
        Cursor cursorFolder = mDatabase.rawQuery("SELECT * FROM " + TABLE_ALBUM, null);
        int count = cursorFolder.getCount();
        try {
            for (int i = 0; i < count; i++) {
                Cursor cursorFile = mDatabase.rawQuery("SELECT * FROM " + TABLE_FILE + " WHERE "
                        + COLUMN_ID_ALBUM + "=" + (i + 1), null);
                List<String> fileNamesList = new ArrayList<>();
                cursorFile.moveToFirst();
                do {
                    fileNamesList.add(cursorFile.getString(1));
                } while (cursorFile.moveToNext());
                cursorFile.close();
                paths.add(fileNamesList);
            }
        } catch (CursorIndexOutOfBoundsException c) {
            paths.add(new ArrayList<>(Arrays.asList(new String[]{"Error database"})));
        }
        cursorFolder.close();
        mDatabase.close();
        return paths;
    }

    public List<List<String>> getFilesForArtists() {
        createDbIfNotExist(mDatabase);
        List<List<String>> files = new ArrayList<>();
        Cursor cursorFolder = mDatabase.rawQuery("SELECT * FROM " + TABLE_ARTIST, null);
        int count = cursorFolder.getCount();
        try {
            for (int i = 0; i < count; i++) {
                Cursor cursorFile = mDatabase.rawQuery("SELECT * FROM " + TABLE_FILE + " WHERE "
                        + COLUMN_ID_ARTIST + "=" + (i + 1), null);
                List<String> fileNamesList = new ArrayList<>();
                cursorFile.moveToFirst();
                do {
                    fileNamesList.add(cursorFile.getString(0));
                } while (cursorFile.moveToNext());
                cursorFile.close();
                files.add(fileNamesList);
            }
        } catch (CursorIndexOutOfBoundsException c) {
            files.add(new ArrayList<>(Arrays.asList(new String[]{"Error database"})));
        }
        cursorFolder.close();
        mDatabase.close();
        return files;
    }

    public List<List<String>> getPathsForArtists() {
        createDbIfNotExist(mDatabase);
        List<List<String>> paths = new ArrayList<>();
        Cursor cursorFolder = mDatabase.rawQuery("SELECT * FROM " + TABLE_ARTIST, null);
        int count = cursorFolder.getCount();
        try {
            for (int i = 0; i < count; i++) {
                Cursor cursorFile = mDatabase.rawQuery("SELECT * FROM " + TABLE_FILE + " WHERE "
                        + COLUMN_ID_ARTIST + "=" + (i + 1), null);
                List<String> fileNamesList = new ArrayList<>();
                cursorFile.moveToFirst();
                do {
                    fileNamesList.add(cursorFile.getString(1));
                } while (cursorFile.moveToNext());
                cursorFile.close();
                paths.add(fileNamesList);
            }
        } catch (CursorIndexOutOfBoundsException c) {
            paths.add(new ArrayList<String>(Arrays.asList(new String[]{"Error database"})));
        }
        cursorFolder.close();
        mDatabase.close();
        return paths;
    }

    public List<List<String>> getFilesForMainFolders() {
        createDbIfNotExist(mDatabase);
        List<List<String>> files = new ArrayList<>();
        Cursor cursorFolder = mDatabase.rawQuery("SELECT * FROM " + TABLE_MAIN_FOLDER, null);
        int count = cursorFolder.getCount();
        Cursor cursorFile;
        try {
            for (int i = 0; i < count; i++) {
                cursorFile = mDatabase.rawQuery("SELECT * FROM " + TABLE_FILE + " WHERE "
                        + COLUMN_ID_MAIN_FOLDER + " = " + (i + 1), null);
                List<String> fileNamesList = new ArrayList<>();
                cursorFile.moveToFirst();
                do {
                    fileNamesList.add(cursorFile.getString(0));
                } while (cursorFile.moveToNext());
                cursorFile.close();
                files.add(fileNamesList);
            }
        } catch (CursorIndexOutOfBoundsException ci) {
            files.add(new ArrayList<String>(Arrays.asList(new String[]{"Error database"})));
        }
        cursorFolder.close();
        mDatabase.close();
        return files;
    }

    public List<List<String>> getPathsForMainFolders() {
        createDbIfNotExist(mDatabase);
        List<List<String>> paths = new ArrayList<>();
        Cursor cursorFolder = mDatabase.rawQuery("SELECT * FROM " + TABLE_MAIN_FOLDER, null);
        int count = cursorFolder.getCount();
        Cursor cursorFile;
        try {
            for (int i = 0; i < count; i++) {
                cursorFile = mDatabase.rawQuery("SELECT * FROM " + TABLE_FILE + " WHERE "
                        + COLUMN_ID_MAIN_FOLDER + "=" + (i + 1), null);
                List<String> fileNamesList = new ArrayList<>();
                cursorFile.moveToFirst();
                do {
                    fileNamesList.add(cursorFile.getString(1));
                } while (cursorFile.moveToNext());
                cursorFile.close();
                paths.add(fileNamesList);
                Log.d("te3r" + count, fileNamesList.toString());
            }
        } catch (CursorIndexOutOfBoundsException c) {
            Log.d("Error", c.getMessage());
            paths.add(new ArrayList<>(Arrays.asList(new String[]{"Error database"})));
        }
        cursorFolder.close();
        mDatabase.close();
        return paths;
    }

    //region Fetch simple lists

    public List<String> getAllSongsPaths() {
        return getSortedItemNames(TABLE_FILE, PATH);
    }

    public List<String> getAllSongsNames() {
        return getSortedItemNames(TABLE_FILE, NAME);
    }

    public List<String> getFolders() {
        return getSortedItemNames(TABLE_FOLDER, NAME);
    }

    public List<String> getAlbums() {
        return getSortedItemNames(TABLE_ALBUM, NAME);
    }

    public List<String> getArtist() {
        return getSortedItemNames(TABLE_ARTIST, NAME);
    }

    public List<String> getMainFolders() {
        return getSortedItemNames(TABLE_MAIN_FOLDER, NAME);
    }

    private List<String> getSortedItemNames(String itemTable, String itemColumn) {
        createDbIfNotExist(mDatabase);
        List<String> items = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT " + itemColumn + " FROM " + itemTable + " ORDER BY " + itemColumn, null);
        while (cursor.moveToNext()) {
            items.add(cursor.getString(0));
        }
        cursor.close();
        mDatabase.close();
        return items;
    }

    //endregion

    //region Working with DB for Service

    public List<String> getLastPlayList() {
        createDbIfNotExist(mDatabase);
        List<String> list = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_PLAY_SERVICE, null);
        cursor.moveToFirst();
        do {
            list.add(cursor.getString(1));
        } while (cursor.moveToNext());
        cursor.close();
        mDatabase.close();
        return list;
    }

    public int getLastPlayedTime() {
        createDbIfNotExist(mDatabase);
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_PLAY_SERVICE_ATTRIBUTES, null);
        cursor.moveToFirst();
        int lastTime = cursor.getInt(0);
        cursor.close();
        mDatabase.close();
        return lastTime;
    }

    public int getLastPlayedNumber() {
        createDbIfNotExist(mDatabase);
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_PLAY_SERVICE_ATTRIBUTES, null);
        cursor.moveToFirst();
        int lastNumber = cursor.getInt(1);
        cursor.close();
        mDatabase.close();
        return lastNumber;
    }

    public boolean getLastPlayedState() {
        createDbIfNotExist(mDatabase);
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_PLAY_SERVICE_ATTRIBUTES, null);
        cursor.moveToFirst();
        boolean lastState = false;
        if (cursor.getInt(2) == 1) {
            lastState = true;
        }
        cursor.close();
        mDatabase.close();
        return lastState;
    }

    public void setLastPlayList(List<String> list) {
        createDbIfNotExist(mDatabase);
        Log.d("Test", "DATABASE is writing current trek list");
        mDatabase.delete(TABLE_PLAY_SERVICE, null, null);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAY_SERVICE);
        mDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_PLAY_SERVICE + " (" +
                ID + " INTEGER PRIMARY KEY, " + PATH + " TEXT);");
        for (int i = 0; i < list.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(PATH, list.get(i));
            mDatabase.insert(
                    TABLE_PLAY_SERVICE,
                    null,
                    values);
        }
        mDatabase.close();
    }

    public void setPlaylistAttributes(int time, int number, boolean shuffle) {
        createDbIfNotExist(mDatabase);
        mDatabase.delete(TABLE_PLAY_SERVICE_ATTRIBUTES, null, null);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAY_SERVICE_ATTRIBUTES);
        mDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_PLAY_SERVICE_ATTRIBUTES + " (" +
                COLUMN_PLAYED_TIME + " INTEGER, " + COLUMN_PLAYED_NUMBER + " INTEGER, " +
                COLUMN_PLAYED_SHUFFLE + " BOOLEAN);");
        ContentValues values = new ContentValues();
        values.put(COLUMN_PLAYED_TIME, time);
        values.put(COLUMN_PLAYED_NUMBER, number);
        if (shuffle) {
            values.put(COLUMN_PLAYED_SHUFFLE, 1);
        } else {
            values.put(COLUMN_PLAYED_SHUFFLE, 0);
        }
        mDatabase.insert(TABLE_PLAY_SERVICE_ATTRIBUTES, null, values);
        mDatabase.close();
    }

    //endregion
}




