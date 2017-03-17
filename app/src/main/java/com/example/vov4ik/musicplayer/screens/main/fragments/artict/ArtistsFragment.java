package com.example.vov4ik.musicplayer.screens.main.fragments.artict;

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


public class ArtistsFragment extends MusicListFragment {//implements View.OnClickListener, View.OnLongClickListener {

private static MusicItemsList musicItemsList;

    protected MusicItemsList getMusicItemsList()
    {
        return musicItemsList;
    }

    public ArtistsFragment()
    {
        if (musicItemsList == null)
        {
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
        getMusicItemsList().setRootView(inflater.inflate(R.layout.fragment_artists, container, false));
//        getMusicItemsList().setRecyclerView((RecyclerView) getMusicItemsList().getRootView().findViewById(R.id.artist_recycler_view));
        setUpRecycler(RecyclerViewAdapter.ARTIST_FRAGMENT);
        updateContent();
        return getMusicItemsList().getRootView();

    }
}
