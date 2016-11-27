package com.example.vov4ik.musicplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// TODO: Make this class thread safe.
public class DbHelper extends SQLiteOpenHelper {

    //region Declarations

    private final static int DB_VERSION = 5;
    private final static String DB_NAME = "OwnHomeMadePlay.db";
    private final static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ";
    private final static String DROP_TABLE = "DROP TABLE IF EXISTS ";

    // Database table names
    private final static String FOLDERS = "Folders";
    private final static String ALBUMS = "Albums";
    private final static String ARTISTS = "Artists";
    private final static String MAIN_FOLDERS = "MainFolders";
    private final static String FILES = "Files";
    private final static String PLAY_SERVICES = "PlayServices";
    private final static String PLAY_SERVICE_ATTRIBUTES = "PlayServiceAttributes";

    // Database column names
    private final static String ID = "Id";
    private final static String NAME = "Name";
    private final static String PATH = "Path";
    private final static String TRACK_NUMBER = "TrackNumber";
    private final static String LAST_PLAYED_TIME = "LastPlayedTime";
    private final static String LAST_PLAYED_NUMBER = "LastPlayedNumber";
    private final static String LAST_PLAYED_SHUFFLE_STATE = "LastPlayedShuffleState";

    private SQLiteDatabase mDatabase;

    //endregion

    //region Common db operations

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

    private static String getDependentIdColumnName(String tableName) {
        return String.format("[%1$s.%2$s]", tableName, ID);
    }

    private static String getLeftJoinQuery(String tableName) {
        return " LEFT JOIN " + tableName + " ON " + FILES + "." + getDependentIdColumnName(tableName) + "=" + tableName + "." + ID;
    }

    private static String getNameColumnAlias(String tableName) {
        return tableName + "." + NAME + " AS " + tableName + NAME;
    }

    private static void createDbIfNotExist(SQLiteDatabase db) {
        // TODO: Add more constraints to db schema
        db.execSQL(CREATE_TABLE + FILES + " (" +
                NAME + " TEXT, " +
                PATH + " TEXT, " +
                TRACK_NUMBER + " STRING, " +
                getDependentIdColumnName(FOLDERS) + " INTEGER, " +
                getDependentIdColumnName(ALBUMS) + " INTEGER, " +
                getDependentIdColumnName(ARTISTS) + " INTEGER, " +
                getDependentIdColumnName(MAIN_FOLDERS) + " INTEGER" + ");");
        db.execSQL(CREATE_TABLE + FOLDERS + " (" +
                ID + " INTEGER PRIMARY KEY, " +
                NAME + " TEXT" + ");");
        db.execSQL(CREATE_TABLE + ALBUMS + " (" +
                ID + " INTEGER PRIMARY KEY, " +
                NAME + " TEXT" + ");");
        db.execSQL(CREATE_TABLE + ARTISTS + " (" +
                ID + " INTEGER PRIMARY KEY, " +
                NAME + " TEXT" + ");");
        db.execSQL(CREATE_TABLE + MAIN_FOLDERS + " (" +
                ID + " INTEGER PRIMARY KEY, " +
                NAME + " TEXT" + ");");
        db.execSQL(CREATE_TABLE + PLAY_SERVICES + " (" +
                ID + " INTEGER PRIMARY KEY, " +
                PATH + " TEXT);");
        db.execSQL(CREATE_TABLE + PLAY_SERVICE_ATTRIBUTES + " (" +
                LAST_PLAYED_TIME + " INTEGER, " +
                LAST_PLAYED_NUMBER + " INTEGER, " +
                LAST_PLAYED_SHUFFLE_STATE + " BOOLEAN);");
    }

    public void deleteDb() {
        mDatabase.execSQL(DROP_TABLE + FILES);
        mDatabase.execSQL(DROP_TABLE + FOLDERS);
        mDatabase.execSQL(DROP_TABLE + ALBUMS);
        mDatabase.execSQL(DROP_TABLE + ARTISTS);
        mDatabase.execSQL(DROP_TABLE + MAIN_FOLDERS);
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

        long startTime = System.currentTimeMillis();
        int album_id,
                artist_id,
                folder_id,
                main_folder_id;
        HashMap<String, Integer> albums = new HashMap<>();
        HashMap<String, Integer> artists = new HashMap<>();
        HashMap<String, Integer> folders = new HashMap<>();
        HashMap<String, Integer> mainFolders = new HashMap<>();

        for (MusicFile musicFile : musicFilesList) {
            album_id = addItemIfNotExist(albums, musicFile.getAlbum(), ALBUMS);
            artist_id = addItemIfNotExist(artists, musicFile.getArtist(), ARTISTS);
            main_folder_id = addItemIfNotExist(mainFolders, musicFile.getMainFolder(), MAIN_FOLDERS);
            folder_id = addItemIfNotExist(folders, musicFile.getFolder(), FOLDERS);
            ContentValues valuesFile = new ContentValues();
            valuesFile.put(NAME, musicFile.getTitle());
            valuesFile.put(PATH, musicFile.getPath());
            valuesFile.put(TRACK_NUMBER, musicFile.getTrackNumber());
            valuesFile.put(getDependentIdColumnName(FOLDERS), folder_id);
            valuesFile.put(getDependentIdColumnName(ALBUMS), album_id);
            valuesFile.put(getDependentIdColumnName(ARTISTS), artist_id);
            valuesFile.put(getDependentIdColumnName(MAIN_FOLDERS), main_folder_id);
            mDatabase.insert(FILES, null, valuesFile);
        }

        mDatabase.close();
        long endTime = System.currentTimeMillis();
        Log.i(Constants.LOG_TAG, "Database filled with data in " + (endTime - startTime) + " ms.");
    }

    public List<MusicFile> getMusicFilesForSearch() {
        createDbIfNotExist(mDatabase);
        List<MusicFile> items = new ArrayList<>();

        /* Query can look like this:
        SELECT
            Files.Name AS FilesName,
            Path,
            TrackNumber,
            Folders.Name AS FoldersName,
            Albums.Name AS AlbumsName,
            Artists.Name AS ArtistsName,
            MainFolders.Name AS MainFoldersName
        FROM Files
           LEFT JOIN Folders ON Files.[Folders.Id] = Folders.Id
           LEFT JOIN Albums ON Files.[Albums.Id] = Albums.Id
           LEFT JOIN Artists ON Files.[Artists.Id] = Artists.Id
           LEFT JOIN MainFolders ON Files.[MainFolders.Id] = MainFolders.Id
        */
        String query = "SELECT " +
                getNameColumnAlias(FILES) + ", " +
                PATH + ", " +
                TRACK_NUMBER + ", " +
                getNameColumnAlias(FOLDERS) + ", " +
                getNameColumnAlias(ALBUMS) + ", " +
                getNameColumnAlias(ARTISTS) + ", " +
                getNameColumnAlias(MAIN_FOLDERS) +
                " FROM " + FILES +
                getLeftJoinQuery(FOLDERS) +
                getLeftJoinQuery(ALBUMS) +
                getLeftJoinQuery(ARTISTS) +
                getLeftJoinQuery(MAIN_FOLDERS);
        Log.d(Constants.LOG_TAG, "Fetching items by query: \n" + query);
        long startTime = System.currentTimeMillis();
        Cursor cursor = mDatabase.rawQuery(query, null);

        while (cursor.moveToNext()) {
            MusicFile item = new MusicFile();
            item.setTitle(cursor.getString(0));
            item.setPath(cursor.getString(1));
            item.setTrackNumber(cursor.getString(2));
            item.setFolder(cursor.getString(3));
            item.setAlbum(cursor.getString(4));
            item.setArtist(cursor.getString(5));
            item.setMainFolder(cursor.getString(6));
            items.add(item);
        }

        cursor.close();
        mDatabase.close();
        long endTime = System.currentTimeMillis();
        Log.i(Constants.LOG_TAG, "Data fetched and processed in " + (endTime - startTime) + " ms.");
        return items;
    }

    //endregion

    //region Fetch dependent lists

    public List<List<String>> getFilesForFolder() {
        return fetchDependentItems(FOLDERS, NAME);
    }

    public List<List<String>> getPathsForFolder() {
        return fetchDependentItems(FOLDERS, PATH);
    }

    public List<List<String>> getFilesForAlbums() {
        return fetchDependentItems(ALBUMS, NAME);
    }

    public List<List<String>> getPathsForAlbums() {
        return fetchDependentItems(ALBUMS, PATH);
    }

    public List<List<String>> getFilesForArtists() {
        return fetchDependentItems(ARTISTS, NAME);
    }

    public List<List<String>> getPathsForArtists() {
        return fetchDependentItems(ARTISTS, PATH);
    }

    public List<List<String>> getFilesForMainFolders() {
        return fetchDependentItems(MAIN_FOLDERS, NAME);
    }

    public List<List<String>> getPathsForMainFolders() {
        return fetchDependentItems(MAIN_FOLDERS, PATH);
    }

    private List<List<String>> fetchDependentItems(String sourceTable, String filesTableField) {
        createDbIfNotExist(mDatabase);
        List<List<String>> items = new ArrayList<>();

        // The query could look like this:
        // "SELECT Folders.Name, Files.Name FROM Folders JOIN Files ON Folders.Id = Files.[Folders.Id] ORDER BY Folders.Name, Files.TrackNumber", null);
        String query = String.format(
                "SELECT %1$s.%3$s, %2$s.%6$s FROM %1$s JOIN %2$s ON %1$s.%4$s = %2$s.%7$s ORDER BY %1$s.%3$s, %2$s.%5$s",
                sourceTable,
                FILES,
                NAME,
                ID,
                TRACK_NUMBER,
                filesTableField,
                getDependentIdColumnName(sourceTable));

        Log.d(Constants.LOG_TAG, "Fetching items by query: \n" + query);
        long startTime = System.currentTimeMillis();
        Cursor cursor = mDatabase.rawQuery(query, null);

        // Tracks item name from the source table
        String currentItemName = null;

        // List of sub-items related to current item name.
        ArrayList<String> currentSubItems = null;

        while (cursor.moveToNext()) {
            if (!cursor.getString(0).equals(currentItemName)) {
                // As all records are sorted by item name from source table,
                // when we hit this condition, all sub-items related to the record from source table
                // has already been processed. So we switch to processing of another item and its sub-items.
                currentSubItems = new ArrayList<>();
                items.add(currentSubItems);
                currentItemName = cursor.getString(0);
            }

            currentSubItems.add(cursor.getString(1));
        }

        cursor.close();
        mDatabase.close();
        long endTime = System.currentTimeMillis();
        Log.i(Constants.LOG_TAG, "Data fetched and processed in " + (endTime - startTime) + " ms.");
        return items;
    }

    //endregion

    //region Fetch simple lists

    public List<String> getAllSongsPaths() {
        return getSortedItemNames(FILES, PATH);
    }

    public List<String> getAllSongsNames() {
        return getSortedItemNames(FILES, NAME);
    }

    public List<String> getFolders() {
        return getSortedItemNames(FOLDERS, NAME);
    }

    public List<String> getAlbums() {
        return getSortedItemNames(ALBUMS, NAME);
    }

    public List<String> getArtist() {
        return getSortedItemNames(ARTISTS, NAME);
    }

    public List<String> getMainFolders() {
        return getSortedItemNames(MAIN_FOLDERS, NAME);
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
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + PLAY_SERVICES, null);
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
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + PLAY_SERVICE_ATTRIBUTES, null);
        cursor.moveToFirst();
        int lastTime = cursor.getInt(0);
        cursor.close();
        mDatabase.close();
        return lastTime;
    }

    public int getLastPlayedNumber() {
        createDbIfNotExist(mDatabase);
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + PLAY_SERVICE_ATTRIBUTES, null);
        cursor.moveToFirst();
        int lastNumber = cursor.getInt(1);
        cursor.close();
        mDatabase.close();
        return lastNumber;
    }

    public boolean getLastPlayedState() {
        createDbIfNotExist(mDatabase);
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + PLAY_SERVICE_ATTRIBUTES, null);
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
        mDatabase.delete(PLAY_SERVICES, null, null);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + PLAY_SERVICES);
        mDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + PLAY_SERVICES + " (" +
                ID + " INTEGER PRIMARY KEY, " + PATH + " TEXT);");
        for (int i = 0; i < list.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(PATH, list.get(i));
            mDatabase.insert(
                    PLAY_SERVICES,
                    null,
                    values);
        }
        mDatabase.close();
    }

    public void setPlaylistAttributes(int time, int number, boolean shuffle) {
        createDbIfNotExist(mDatabase);
        mDatabase.delete(PLAY_SERVICE_ATTRIBUTES, null, null);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + PLAY_SERVICE_ATTRIBUTES);
        mDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + PLAY_SERVICE_ATTRIBUTES + " (" +
                LAST_PLAYED_TIME + " INTEGER, " + LAST_PLAYED_NUMBER + " INTEGER, " +
                LAST_PLAYED_SHUFFLE_STATE + " BOOLEAN);");
        ContentValues values = new ContentValues();
        values.put(LAST_PLAYED_TIME, time);
        values.put(LAST_PLAYED_NUMBER, number);
        if (shuffle) {
            values.put(LAST_PLAYED_SHUFFLE_STATE, 1);
        } else {
            values.put(LAST_PLAYED_SHUFFLE_STATE, 0);
        }
        mDatabase.insert(PLAY_SERVICE_ATTRIBUTES, null, values);
        mDatabase.close();
    }

    //endregion
}
