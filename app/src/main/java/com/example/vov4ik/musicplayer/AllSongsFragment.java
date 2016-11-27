package com.example.vov4ik.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AllSongsFragment extends MusicListFragment {
    final static String EXTRA_FOR_FILES = "extra for files";
    private static MusicItemsList musicItemsList;

    protected MusicItemsList getMusicItemsList()
    {
        return musicItemsList;
    }

    public AllSongsFragment()
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
        // Inflate the layout for this fragment
        getMusicItemsList().setRootView(inflater.inflate(R.layout.fragment_all_songs, container, false));
        getMusicItemsList().setRecyclerView((RecyclerView) getMusicItemsList().getRootView().findViewById(R.id.all_songs_recycler_view));
        getMusicItemsList().setCheckingTrigger(false);
        getMusicItemsList().setFolderTrigger(true);
        List<List<String>> p = new ArrayList<>();
        List<List<String>> m = new ArrayList<>();
        m.add(new ArrayList<String>());
        p.add(new ArrayList<String>());
//        List<MusicFile> musicFiles = MainActivity.getmF();
//        for(MusicFile musicFile: musicFiles){
            m.get(0).addAll(DbConnector.getAllSongsNames(getContext()));
            p.get(0).addAll(DbConnector.getAllSongsPaths(getContext()));
//        }
//        Collections.sort(m.get(0));
//        for(MusicFile musicFile: musicFiles){
//            p.get(0).set(m.get(0).indexOf(musicFile.getTitle()), musicFile.getPath());
//        }


        getMusicItemsList().setPath(p);
        getMusicItemsList().setMusicFiles(m);
        getMusicItemsList().setNumberOfFolder(0);
        getMusicItemsList().setAllSongsFragment(true);
        show(m.get(0));
        Log.d("tset", m.get(0).size()+"");
        return getMusicItemsList().getRootView();
    }
    @Override
    public void onClick(int position) {
        if (!getMusicItemsList().isCheckingTrigger()) {
//                Intent intent = new Intent(getContext(), PlayerActivity.class);
//                intent.putExtra(EXTRA_FOR_CLICKED_FILE, getMusicItemsList().getPath().get(getMusicItemsList().getNumberOfFolder())[(position)]);
//                intent.putExtra(EXTRA_FOR_PATHS, getMusicItemsList().getPath().get(getMusicItemsList().getNumberOfFolder()));
//                intent.putExtra(EXTRA_FOR_FILES,  getMusicItemsList().getMusicFiles().get(getMusicItemsList().getNumberOfFolder()));
//                startActivity(intent);
            PlayService.setTrekNumber(0);
            PlayService.setPath(Arrays.asList(getMusicItemsList().getPath().get(getMusicItemsList().getNumberOfFolder()).get(position)));
            PlayService.setLastPlayedTime(0);
//            if(PlayService.getPlayer()!=null) {
//                PlayService.startPlaying();
//            } else {
                Intent intent1 = new Intent(getContext(), PlayService.class);
                intent1.setAction(PlayService.PLAY_ACTION);
                getContext().startService(intent1);
//                PlayService.startPlaying();
//            }
        }else {
            if (getMusicItemsList().getCheckedList().contains(String.valueOf(position))){
                List<String> l =  getMusicItemsList().getCheckedList();
                l.remove(String.valueOf(position));
                getMusicItemsList().setCheckedList(l);
                getMusicItemsList().removeSelectedPaths(getMusicItemsList().getPath().get(getMusicItemsList().getNumberOfFolder()).get(position));
            } else {
                List<String> l =  getMusicItemsList().getCheckedList();
                l.add(String.valueOf(position));
                getMusicItemsList().setCheckedList(l);
                getMusicItemsList().setSelectedPaths(getMusicItemsList().getPath().get(getMusicItemsList().getNumberOfFolder()).get(position));
            }
        }
    }


    @Override
    public boolean onLongClick(int position) {
//        Log.d("Test",(v.getId()==0)+" "+
        try {
            getMusicItemsList().setCheckingTrigger(true);
            List<String> l = getMusicItemsList().getCheckedList();
            l.add(String.valueOf(position));
            getMusicItemsList().setCheckedList(l);
            getMusicItemsList().setSelectedPaths(getMusicItemsList().getPath().get(getMusicItemsList().getNumberOfFolder()).get(position));
            RecyclerView.Adapter mAdapter = new RecyclerViewAdapter(this, (getMusicItemsList().getMusicFiles().get(getMusicItemsList().getNumberOfFolder())),
                            getMusicItemsList().getPath(), getMusicItemsList().getCheckedList(), getMusicItemsList().isCheckingTrigger(),
                            getMusicItemsList().isFolderTrigger(),getMusicItemsList().getNumberOfFolder(), false, getContext(), getMusicItemsList().isAllSongsFragment());
            getMusicItemsList().getRecyclerView().setAdapter(mAdapter);
            return true;
        }catch(IllegalStateException il){
            return true;
        }
    }
    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if((!menuVisible)&&(getMusicItemsList().getRootView()!=null)){
            getMusicItemsList().setCheckingTrigger(false);
            getMusicItemsList().setFolderTrigger(true);
            try {
                show(DbConnector.getAllSongsNames(getContext()));
            }catch(NullPointerException e){

            }
        }
    }
}
