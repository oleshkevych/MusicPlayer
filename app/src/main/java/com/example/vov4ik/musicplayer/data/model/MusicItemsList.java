package com.example.vov4ik.musicplayer.data.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.vov4ik.musicplayer.screens.main.fragments.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vov4ik on 8/14/2016.
 */
public class MusicItemsList {

//    private RecyclerView recyclerView;
    private View rootView;
    private List<String> selectedPlaylist = new ArrayList<String>();
    private int numberOfPlaylist=-5;
    private List<List<String>> pathPlaylist = null;
    private boolean playlistChanges = false;
    private List<List<String>> namePlaylist = null;
    private RecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    public RecyclerViewAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(RecyclerViewAdapter adapter) {
        mAdapter = adapter;
    }

    public boolean isPlaylistChanges() {
        return playlistChanges;
    }

    public void setPlaylistChanges(boolean playlistChanges) {
        this.playlistChanges = playlistChanges;
    }

    public List<List<String>> getNamePlaylist() {
        return namePlaylist;
    }

    public void setNamePlaylist(List<List<String>> namePlaylist) {
        this.namePlaylist = namePlaylist;
    }

    public List<List<String>> getPathPlaylist() {
        return pathPlaylist;
    }

    public void setPathPlaylist(List<List<String>> pathPlaylist) {
        this.pathPlaylist = pathPlaylist;
    }

    public List<String> getSelectedPlaylist() {
        return selectedPlaylist;
    }

    public int getNumberOfPlaylist() {
        return numberOfPlaylist;
    }

    public void setNumberOfPlaylist(int numberOfPlaylist) {
        this.numberOfPlaylist = numberOfPlaylist;
    }

    public void setSelectedPlaylist(List<String> selectedPlaylist) {
        this.selectedPlaylist = selectedPlaylist;
    }
    public void addSelectedPlaylist(String selectedPlaylist) {
        this.selectedPlaylist.add(selectedPlaylist);
    }

    public void removeSelectedPlaylist(String selectedPlaylist) {
        this.selectedPlaylist.remove(selectedPlaylist);
    }

    public View getRootView() {
        return rootView;
    }

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }

//    public RecyclerView getRecyclerView() {
//        return recyclerView;
//    }
//
//    public void setRecyclerView(RecyclerView recyclerView) {
//        this.recyclerView = recyclerView;
//    }

}
