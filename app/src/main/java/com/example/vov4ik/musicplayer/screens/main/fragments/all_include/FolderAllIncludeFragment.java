package com.example.vov4ik.musicplayer.screens.main.fragments.all_include;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.vov4ik.musicplayer.R;
import com.example.vov4ik.musicplayer.data.local.DbConnector;
import com.example.vov4ik.musicplayer.data.model.MusicItemsList;
import com.example.vov4ik.musicplayer.screens.main.MainActivity;
import com.example.vov4ik.musicplayer.screens.main.fragments.MusicListFragment;
import com.example.vov4ik.musicplayer.screens.main.fragments.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class FolderAllIncludeFragment extends MusicListFragment {//implements View.OnClickListener, View.OnLongClickListener {

    private static MusicItemsList musicItemsList;

    protected MusicItemsList getMusicItemsList() {
        return musicItemsList;
    }

    public FolderAllIncludeFragment() {
        if (musicItemsList == null) {
            musicItemsList = new MusicItemsList();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getMusicItemsList().setRootView(inflater.inflate(R.layout.fragment_folder_all_include, container, false));
        setUpRecycler(RecyclerViewAdapter.FOLDER_ALL_INCLUDE);
        updateContent();
        return getMusicItemsList().getRootView();
    }
}
