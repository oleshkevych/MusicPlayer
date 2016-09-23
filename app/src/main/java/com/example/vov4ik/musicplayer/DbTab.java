package com.example.vov4ik.musicplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vov4ik on 9/23/2016.
 */
public class DbTab extends SQLiteOpenHelper {

    final static int DB_VERSION = 1;
    final static String DB_NAME = "TabDb.db";

    final static String TABLE_TABS = "tabs";
    final static String COLUMN_ID_TAB = "ID_tab";
    final static String COLUMN_NAME_TAB = "name_of_tab";
    final static String COLUMN_VISIBILITY_TAB = "visibility_of_tab";
    final static String CREATE_TABLE_TAB = "CREATE TABLE IF NOT EXISTS " + TABLE_TABS + " (" +
            COLUMN_ID_TAB + " INTEGER PRIMARY KEY, " + COLUMN_NAME_TAB + " TEXT, " + COLUMN_VISIBILITY_TAB + " INTEGER);";


    private SQLiteDatabase mDatabase;


    public DbTab(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TAB);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.delete("tabs", null, null);
        onCreate(db);
    }

    private DbTab open() {
        mDatabase = getWritableDatabase();
        mDatabase.execSQL(CREATE_TABLE_TAB);
        return this;
    }

    public void tabsFiller(List<TabConstructor> tabs) {
        open();
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TABS);
        mDatabase.execSQL(CREATE_TABLE_TAB);
        ContentValues values = new ContentValues();
        for (TabConstructor tab : tabs) {
            values.put(COLUMN_NAME_TAB, tab.getName());
            if (tab.isVisibility()) {
                values.put(COLUMN_VISIBILITY_TAB, 1);
            } else {
                values.put(COLUMN_VISIBILITY_TAB, 0);
            }
            mDatabase.insert(
                    DbTab.TABLE_TABS,
                    null,
                    values);
        }
        mDatabase.close();
    }

    public List<TabConstructor> getAllTabs() {
        open();
        List<TabConstructor> tabs = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM "+ TABLE_TABS, null);
        cursor.moveToFirst();
        do {
            TabConstructor tc = new TabConstructor();
            tc.setName(cursor.getString(1));
            if(cursor.getInt(2) == 0){
                tc.setVisibility(false);
            }else{
                tc.setVisibility(true);
            }
            tabs.add(tc);
        } while (cursor.moveToNext());
        cursor.close();
        mDatabase.close();
        return tabs;
    }
    public List<String> getVisibleTabs() {
        open();
        List<String> tabs = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM "+ TABLE_TABS, null);
        cursor.moveToFirst();
        do {
            if(cursor.getInt(2) == 1){
              tabs.add(cursor.getString(1));
            }
        } while (cursor.moveToNext());
        if(tabs.size() == 0){
            tabs.add(TabConstructor.getListOfTabs().get(3));
        }
        cursor.close();
        mDatabase.close();
        return tabs;
    }
}