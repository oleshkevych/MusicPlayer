package com.example.vov4ik.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

/**
 * Created by vov4ik on 9/19/2016.
 */
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
        List<String> list = DbConnector.getAllSongsPaths(getContext());
        getMusicItemsList().setPath(Arrays.asList(new String[][]{list.toArray(new String[list.size()])}));
        list = DbConnector.getAllSongsNames(getContext());
        getMusicItemsList().setMusicFiles(Arrays.asList(new String[][]{list.toArray(new String[list.size()])}));
        show(list);
        getMusicItemsList().setNumberOfFolder(0);
        getMusicItemsList().setAllSongsFragment(true);
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
            PlayService.setPath(Arrays.asList(getMusicItemsList().getPath().get(getMusicItemsList().getNumberOfFolder())[position]));
            if(PlayService.getPlayer()!=null) {
                PlayService.setLastPlayedTime(0);
                PlayService.startPlaying();
            } else {
                Intent intent1 = new Intent(MainActivity.getContext(), PlayService.class);
                MainActivity.getContext().startService(intent1);
                PlayService.setLastPlayedTime(0);
                PlayService.startPlaying();
            }
        }else {
            if (getMusicItemsList().getCheckedList().contains(String.valueOf(position))){
                List<String> l =  getMusicItemsList().getCheckedList();
                l.remove(String.valueOf(position));
                getMusicItemsList().setCheckedList(l);
                getMusicItemsList().removeSelectedPaths(getMusicItemsList().getPath().get(getMusicItemsList().getNumberOfFolder())[position]);
            } else {
                List<String> l =  getMusicItemsList().getCheckedList();
                l.add(String.valueOf(position));
                getMusicItemsList().setCheckedList(l);
                getMusicItemsList().setSelectedPaths(getMusicItemsList().getPath().get(getMusicItemsList().getNumberOfFolder())[position]);
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
            getMusicItemsList().setSelectedPaths(getMusicItemsList().getPath().get(getMusicItemsList().getNumberOfFolder())[position]);
            RecyclerView.Adapter mAdapter = new RecyclerViewAdapter(this, Arrays.asList(getMusicItemsList().getMusicFiles().get(getMusicItemsList().getNumberOfFolder())),
                            getMusicItemsList().getPath(), getMusicItemsList().getCheckedList(), getMusicItemsList().isCheckingTrigger(),
                            getMusicItemsList().isFolderTrigger(),getMusicItemsList().getNumberOfFolder(), false, MainActivity.getContext());
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
