package com.example.vov4ik.musicplayer.screens.main.fragments;

import com.example.vov4ik.musicplayer.data.model.MusicFile;

import java.util.List;

/**
 * Created by vov4ik on 8/14/2016.
 */
public interface ISelectableFragment {

    public RecyclerViewAdapter getAdapter();
//    List<String> getSelectedPaths();
    void unselectMusicItems();
    boolean isInFolder();
    boolean isSelecting();
    void showFolder();

    //    void show(List<String> list);
//    List<String> getPreviousList();
//    void setFolderTrigger(boolean folderTrigger);
//    boolean isFolderTrigger();
    List<MusicFile> getSelectedPlaylist();
    int getNumberOfPlaylist();
//    void reloadForPlaylist();
//    boolean isAllSongsFragment();


}
