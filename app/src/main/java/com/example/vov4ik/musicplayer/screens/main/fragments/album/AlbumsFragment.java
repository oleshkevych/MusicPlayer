package com.example.vov4ik.musicplayer.screens.main.fragments.album;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vov4ik.musicplayer.R;
import com.example.vov4ik.musicplayer.data.local.DbConnector;
import com.example.vov4ik.musicplayer.data.model.MusicItemsList;
import com.example.vov4ik.musicplayer.screens.main.MainActivity;
import com.example.vov4ik.musicplayer.screens.main.fragments.MusicListFragment;
import com.example.vov4ik.musicplayer.screens.main.fragments.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlbumsFragment extends MusicListFragment {

private static MusicItemsList musicItemsList;

    protected MusicItemsList getMusicItemsList()
    {
        return musicItemsList;
    }

    public AlbumsFragment()
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

        getMusicItemsList().setRootView(inflater.inflate(R.layout.fragment_albums, container, false));
//        getMusicItemsList().setRecyclerView((RecyclerView) getMusicItemsList().getRootView().findViewById(R.id.album_recycler_view));

        setUpRecycler(RecyclerViewAdapter.ALBUM_FRAGMENT);
        updateContent();

        return getMusicItemsList().getRootView();
    }
}
