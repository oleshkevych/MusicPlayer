package com.example.vov4ik.musicplayer;

import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vov4ik on 8/14/2016.
 */
public class MusicItemsList {

    private List<String> checkedList = new ArrayList<>();
    private boolean checkingTrigger = false;
    private LinearLayout linearLayout;
    private List<String> selectedPaths = new ArrayList<String>();
    private View rootView;
    private List<String> folderName = null;
    private List<String[]> path = null;
    private List<String[]> musicFiles = null;
    private boolean folderTrigger = false;
    private int numberOfFolder;


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

    public LinearLayout getLinearLayout() {
        return linearLayout;
    }

    public void setLinearLayout(LinearLayout linearLayout) {
        this.linearLayout = linearLayout;
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
