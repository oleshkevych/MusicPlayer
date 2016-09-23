package com.example.vov4ik.musicplayer;

import java.util.List;

/**
 * Created by vov4ik on 8/14/2016.
 */
public interface ISelectableFragment {
    List<String> getSelectedPaths();
    void unselectMusicItems();
    boolean isCheckingTrigger();
    void show(List<String> list);
    List<String> getPreviousList();
    void setFolderTrigger(boolean folderTrigger);
    boolean isFolderTrigger();
    List<String> getSelectedPlaylist();
    int getNumberOfPlaylist();
    void reloadForPlaylist();
    boolean isAllSongsFragment();


}
