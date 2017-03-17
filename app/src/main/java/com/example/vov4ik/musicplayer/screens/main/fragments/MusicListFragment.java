package com.example.vov4ik.musicplayer.screens.main.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.vov4ik.musicplayer.data.model.MusicFile;
import com.example.vov4ik.musicplayer.screens.main.MainActivity;
import com.example.vov4ik.musicplayer.service.PlayService;
import com.example.vov4ik.musicplayer.R;
import com.example.vov4ik.musicplayer.data.local.DbConnector;
import com.example.vov4ik.musicplayer.data.model.MusicItemsList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vov4ik on 8/14/2016.
 */
public abstract class MusicListFragment extends Fragment implements ISelectableFragment {//, View.OnClickListener, View.OnLongClickListener {



    public List<MusicFile> getSelectedPlaylist() {
        return getMusicItemsList().getAdapter() != null ? getMusicItemsList().getAdapter().getSelectedMusicFiles() : new ArrayList<MusicFile>();
    }

    public boolean isInFolder() {
        return getMusicItemsList().getAdapter() != null && getMusicItemsList().getAdapter().isInFolder();
    }

    public boolean isSelecting() {
        return getMusicItemsList().getAdapter() != null && getMusicItemsList().getAdapter().isSelecting();
    }

    public void showFolder() {
        getMusicItemsList().getAdapter().setUpFolderValues();
    }

    public int getNumberOfPlaylist() {
        return getMusicItemsList().getNumberOfPlaylist();
    }

    public RecyclerViewAdapter getAdapter() {
        return getMusicItemsList().getAdapter();
    }

    public void setUpRecycler(int fragment) {
        getMusicItemsList().setRecyclerView((RecyclerView) getMusicItemsList().getRootView().findViewById(R.id.recycler_view));
        getMusicItemsList().getRecyclerView().setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        getMusicItemsList().getRecyclerView().setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        getMusicItemsList().setAdapter(new RecyclerViewAdapter(fragment));
        getMusicItemsList().getRecyclerView().setAdapter(getMusicItemsList().getAdapter());
        getMusicItemsList().getAdapter().setOnClickListener(mOnClickListener);
    }

    final RecyclerViewAdapter.OnClickListener mOnClickListener = new RecyclerViewAdapter.OnClickListener() {
        @Override
        public void onFileClick(MusicFile musicFile, List<MusicFile> musicFiles) {
            onClick(musicFile, musicFiles);
        }

    };

//    public void reloadForPlaylist() {
//        getMusicItemsList().setPlaylistChanges(true);
//        getMusicItemsList().setCheckingTrigger(false);
//        getMusicItemsList().setFolderName(DbConnector.getPlaylist(getMusicItemsList().getContext()));
//        getMusicItemsList().setPathPlaylist(DbConnector.getPlaylistFiles(getMusicItemsList().getContext()));
//        showPlaylist(getMusicItemsList().getFolderName());
//        getMusicItemsList().setCheckedList(new ArrayList<String>());
//        getMusicItemsList().setNewSelectedPaths(new ArrayList<String>());
//        getMusicItemsList().setSelectedPlaylist(new ArrayList<String>());
//        getMusicItemsList().setSelectedPlaylist(new ArrayList<String>());
//        getMusicItemsList().setNumberOfPlaylist(0);
//    }

    public void unselectMusicItems() {

        if (getMusicItemsList().getAdapter() != null)
            getMusicItemsList().getAdapter().unselect();
    }

    protected abstract MusicItemsList getMusicItemsList();

//    public void showPlaylist(List<String> list) {
//
//        List<List<String>> arrayList = new ArrayList<>();
//        if (getMusicItemsList().isFolderTrigger()) {
//            arrayList.add(getMusicItemsList().getPathPlaylist().get(getMusicItemsList().getNumberOfFolder()));
//        }
////        getMusicItemsList().getRecyclerView().setHasFixedSize(true);
////        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
////        getMusicItemsList().getRecyclerView().setLayoutManager(mLayoutManager);
//        mAdapter.updateAdapter(list,
//                arrayList, getMusicItemsList().getCheckedList(), getMusicItemsList().isCheckingTrigger(),
//                getMusicItemsList().isFolderTrigger(), getMusicItemsList().getNumberOfPlaylist(), true, getMusicItemsList().isAllSongsFragment());
//    }


    public void onClick(MusicFile musicFile, List<MusicFile> musicFiles) {

        sendListPathsAction(musicFiles);
        sendStartPlayAction(musicFile);

        Log.d("Test", musicFiles.toString());
    }

    private void sendListPathsAction(List<MusicFile> musicFiles) {
        Intent intent = new Intent(PlayService.SET_MUSIC_FILES_ACTION);
        intent.putParcelableArrayListExtra(PlayService.LIST_EXTRA, (ArrayList<? extends Parcelable>) musicFiles);
        sendBroadcastIntent(intent);
    }

    private void sendStartPlayAction(MusicFile musicFile) {
        Intent intent = new Intent(PlayService.START_FILE_ACTION);
        intent.putExtra(PlayService.FILE_EXTRA, musicFile);
        sendBroadcastIntent(intent);
    }

    private void sendBroadcastIntent(Intent intent) {
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerBroadcastReceiver();
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(MainActivity.UPDATE_MUSIC_FILES);
        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    public void onPause() {
        unregisterBroadcastReceiver();
        super.onPause();
    }

    private void unregisterBroadcastReceiver() {
        LocalBroadcastManager.getInstance(getActivity())
                .unregisterReceiver(mBroadcastReceiver);
    }

    final BroadcastReceiver mBroadcastReceiver
            = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case MainActivity.UPDATE_MUSIC_FILES:
                    updateContent();
                    break;
            }
        }
    };

    public void updateContent() {
        getMusicItemsList().getAdapter().updateMusic(DbConnector.getMusicFiles(getActivity()));
    }


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if ((!menuVisible) && (getMusicItemsList().getRootView() != null)) {
            unselectMusicItems();
        }
    }
}
