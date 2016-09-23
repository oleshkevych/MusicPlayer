package com.example.vov4ik.musicplayer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vov4ik on 8/14/2016.
 */
public class MusicItemsList {

    private List<String> checkedList = new ArrayList<>();
    private boolean checkingTrigger = false;
    private RecyclerView recyclerView;
    private List<String> selectedPaths = new ArrayList<String>();
    private View rootView;
    private List<String> folderName = null;
    private List<String[]> path = null;
    private List<String[]> musicFiles = null;
    private boolean folderTrigger = false;
    private int numberOfFolder;
    private List<String> selectedPlaylist = new ArrayList<String>();
    private int numberOfPlaylist=-5;
    private List<List<String>> pathPlaylist = null;
    private boolean playlistChanges = false;
    private List<List<String>> namePlaylist = null;
    private Context context;
    private boolean allSongsFragment = false;

    public boolean isAllSongsFragment() {
        return allSongsFragment;
    }

    public void setAllSongsFragment(boolean allSongsFragment) {
        this.allSongsFragment = allSongsFragment;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
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

    public List<String[]> getPath() {
        return path;
    }

    public void setPath(List<String[]> path) {
        this.path = path;
    }

    public List<String> getFolderName() {
        return folderName;
    }

    public void setFolderName(List<String> folderName) {
        this.folderName = folderName;
    }

    public int getNumberOfFolder() {
        return numberOfFolder;
    }

    public void setNumberOfFolder(int numberOfFolder) {
        this.numberOfFolder = numberOfFolder;
    }

    public boolean isFolderTrigger() {
        return folderTrigger;
    }

    public void setFolderTrigger(boolean folderTrigger) {
        this.folderTrigger = folderTrigger;
    }

    public List<String[]> getMusicFiles() {
        return musicFiles;
    }

    public void setMusicFiles(List<String[]> musicFiles) {
        this.musicFiles = musicFiles;
    }

    public View getRootView() {
        return rootView;
    }

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }

    public boolean isCheckingTrigger() {
        return checkingTrigger;
    }

    public void setCheckingTrigger(boolean checkingTrigger) {
        this.checkingTrigger = checkingTrigger;
    }

    public List<String> getCheckedList() {
        return checkedList;
    }

    public void setCheckedList(List<String> checkedList) {
        this.checkedList = checkedList;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public List<String> getSelectedPaths() {
        return selectedPaths;
    }

    public void setSelectedPaths(String selectedPaths) {
        this.selectedPaths.add(selectedPaths);
    }

    public void setNewSelectedPaths(List<String> selectedPaths) {
        this.selectedPaths = selectedPaths;
    }
    public void removeSelectedPaths(String selectedPaths) {
        this.selectedPaths.remove(selectedPaths);
    }

}
