package com.example.vov4ik.musicplayer;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FolderFragment extends MusicListFragment {//implements View.OnClickListener, View.OnLongClickListener {

/*    private List<String> folderName = null;
    private List<String[]> path = null;
    private List<String[]> musicFiles = null;
    private  View rootView;
    private boolean folderTrigger = false;
    private int numberOfFolder;
    final static String EXTRA_FOR_FILES = "extra for files";
    final static String EXTRA_FOR_PATHS = "extra for paths";

    private static List<String> checkedList = new ArrayList<>();
    private static boolean checkingTrigger = false;
    //private static LinearLayout linearLayout;
    private static List<String> selectedPaths = new ArrayList<>();


    public static boolean isCheckingTrigger() {
        return checkingTrigger;
    }

    public static void setCheckingTrigger(boolean checkingTrigger) {
        FolderFragment.checkingTrigger = checkingTrigger;
    }

    public static List<String> getCheckedList() {
        return checkedList;
    }

    public static void setCheckedList(List<String> checkedList) {
        FolderFragment.checkedList = checkedList;
    }

    public static LinearLayout getLinearLayout() {
        return linearLayout;
    }

    public static void setLinearLayout(LinearLayout linearLayout) {
        FolderFragment.linearLayout = linearLayout;
    }

    public static List<String> getSelectedPaths() {
        return selectedPaths;
    }

    public static void setSelectedPaths(String selectedPaths) {
        FolderFragment.selectedPaths.add(selectedPaths);
    }

    public static void setNewSelectedPaths(List<String> selectedPaths) {
        FolderFragment.selectedPaths = selectedPaths;
    }
    public static void removeSelectedPaths(String selectedPaths) {
        FolderFragment.selectedPaths.remove(selectedPaths);
    }*/

    private static MusicItemsList musicItemsList;

    protected MusicItemsList getMusicItemsList()
    {
        return musicItemsList;
    }

    public FolderFragment()
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
        getMusicItemsList().setRootView(inflater.inflate(R.layout.fragment_folder, container, false));
        getMusicItemsList().setRecyclerView((RecyclerView) getMusicItemsList().getRootView().findViewById(R.id.folder_recycler_view));
        getMusicItemsList().setCheckingTrigger(false);

        List<String> names = DbConnector.getFoldersFromDb(getContext());
        List<List<String>> m = DbConnector.getFilesNamesForFolders(getContext());
        List<List<String>> p = DbConnector.getPathsForFolders(getContext());
        List<String> n = new ArrayList<>();
        List<List<String>> p1 = new ArrayList<>();
        List<List<String>> m1 = new ArrayList<>();
        for(int i = 0; i < names.size(); i++){
            n.add(names.get(i));
        }
        Collections.sort(names);
        Log.d("tetst", names.size() + " " + m.size() + " " + p.size() + " ");
        for(int i = 0; i < names.size(); i++){
            int index = n.indexOf(names.get(i));
            p1.add(p.get(index));
            m1.add(m.get(index));
        }

        getMusicItemsList().setFolderName(names);
        getMusicItemsList().setPath(p1);
        getMusicItemsList().setMusicFiles(m1);
        show(getMusicItemsList().getFolderName());
        return getMusicItemsList().getRootView();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }



    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if((!menuVisible)&&(getMusicItemsList().getRootView()!=null)){
            getMusicItemsList().setCheckingTrigger(false);
            if(getMusicItemsList().isFolderTrigger()) {
                show((getMusicItemsList().getMusicFiles().get(getMusicItemsList().getNumberOfFolder())));
            }else {
                show(getMusicItemsList().getFolderName());
            }
        }
    }
}
