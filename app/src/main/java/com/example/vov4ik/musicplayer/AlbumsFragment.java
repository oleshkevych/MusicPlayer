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

public class AlbumsFragment extends MusicListFragment {//implements View.OnClickListener, View.OnLongClickListener {
/*
    final static String EXTRA_FOR_FILES = "extra for files";
    final static String EXTRA_FOR_PATHS = "extra for paths";
    private  View rootView;
    private List<String> albumsName = null;
    private List<String[]> path = null;
    private List<String[]> filesName = null;
    private boolean albumTrigger = false;
    private int numberOfAlbum;

    private static List<String> checkedList = new ArrayList<>();
    private static boolean checkingTrigger = false;
    private static LinearLayout linearLayout;
    private static List<String> selectedPaths = new ArrayList<>();

    public static boolean isCheckingTrigger() {
        return checkingTrigger;
    }

    public static void setCheckingTrigger(boolean checkingTrigger) {
        AlbumsFragment.checkingTrigger = checkingTrigger;
    }

    public static List<String> getCheckedList() {
        return checkedList;
    }

    public static void setCheckedList(List<String> checkedList) {
        AlbumsFragment.checkedList = checkedList;
    }

    public static LinearLayout getLinearLayout() {
        return linearLayout;
    }

    public static void setLinearLayout(LinearLayout linearLayout) {
        AlbumsFragment.linearLayout = linearLayout;
    }

    public static List<String> getSelectedPaths() {
        return selectedPaths;
    }

    public static void setSelectedPaths(String selectedPaths) {
        AlbumsFragment.selectedPaths.add(selectedPaths);
    }

    public static void setNewSelectedPaths(List<String> selectedPaths) {
        AlbumsFragment.selectedPaths = selectedPaths;
    }
    public static void removeSelectedPaths(String selectedPaths) {
        AlbumsFragment.selectedPaths.remove(selectedPaths);
    }

*/
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

        MusicItemsList musicItemsList = getMusicItemsList();
        musicItemsList.setRootView(inflater.inflate(R.layout.fragment_albums, container, false));
        musicItemsList.setRecyclerView((RecyclerView) getMusicItemsList().getRootView().findViewById(R.id.album_recycler_view));
        musicItemsList.setCheckingTrigger(false);

        Context context = getContext();
        List<String> dbAlbumNamesList = DbConnector.getAlbumFromDb(context);
        List<List<String>> dbFileNamesList = DbConnector.getFileNamesForAlbums(context);
        List<List<String>> dbAlbumPathsList = DbConnector.getPathsForAlbums(context);
        List<String> albumNamesList = new ArrayList<>();
        List<List<String>> albumPathsList = new ArrayList<>();
        List<List<String>> fileNamesList = new ArrayList<>();

        for (String name : dbAlbumNamesList) {
            albumNamesList.add(name);
        }

        Collections.sort(albumNamesList);

        if (dbAlbumNamesList.size() == dbFileNamesList.size() &&
            dbAlbumNamesList.size() == dbAlbumPathsList.size()) {
            for (int i = 0; i < dbAlbumNamesList.size(); i++) {
                int index = albumNamesList.indexOf(dbAlbumNamesList.get(i));
                albumPathsList.add(dbAlbumPathsList.get(index));
                fileNamesList.add(dbFileNamesList.get(index));
            }
        }

        musicItemsList.setFolderName(albumNamesList);
        musicItemsList.setPath(albumPathsList);
        musicItemsList.setMusicFiles(fileNamesList);
        show(musicItemsList.getFolderName());
        return musicItemsList.getRootView();
    }
/*

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }
    private void showFolders(List<String> list){
        getMusicItemsList().setLinearLayout((LinearLayout) rootView.findViewById(R.id.layoutAlbum));
        linearLayout = getMusicItemsList().getLinearLayout(); // TODO: Refactor this
        linearLayout.removeAllViews();
        for (String s : list) {
            TextView text = new TextView(linearLayout.getContext());
            text.setText(String.valueOf(s));
            text.setId((list).indexOf(s));
            ((LinearLayout) linearLayout).addView(text);
            text.setOnClickListener(this);
            text.setOnLongClickListener(this);
            text.setPadding(20, 10, 20, 10);
            text.setTextSize(16);
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) text.getLayoutParams();
            mlp.setMargins(0, 15, 0, 15);
        }
    }

    @Override
    public void onClick(View v) {
        if (!getMusicItemsList().isCheckingTrigger()) {
            if ((!albumTrigger)) {
                numberOfAlbum = v.getId();
                showFolders(Arrays.asList(filesName.get(v.getId())));
                albumTrigger = true;
            } else if (v.getId() == 0) {
                showFolders(albumsName);
                albumTrigger = false;
            } else {
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                intent.putExtra(EXTRA_FOR_FILES, filesName.get(numberOfAlbum));
                intent.putExtra(EXTRA_FOR_PATHS, path.get(numberOfAlbum));
                startActivity(intent);
            }
        } else if (!((albumTrigger) && (v.getId() == 0))) {
            if ((v.getTag()!=null)&&(v.getTag().equals("checked"))){
                v.setBackground(null);
                v.setTag(null);
                List<String> l = getMusicItemsList().getCheckedList();
                l.remove(String.valueOf(v.getId()));
                getMusicItemsList().setCheckedList(l);
                if ((!albumTrigger)) {
                    for (int i = 1; i< path.get(v.getId()).length; i++) {
                        getMusicItemsList().removeSelectedPaths(path.get(v.getId())[i]);
                    }
                } else if (v.getId() != 0) {
                    getMusicItemsList().removeSelectedPaths(path.get(numberOfAlbum)[v.getId()]);
                }
            } else {
                v.setBackground(getResources().getDrawable(R.drawable.checked_view_background));
                List<String> l = getMusicItemsList().getCheckedList();
                l.add(String.valueOf(v.getId()));
                v.setTag("checked");
                getMusicItemsList().setCheckedList(l);
                if ((!albumTrigger)) {
                    for (int i = 1; i< path.get(v.getId()).length; i++) {
                        getMusicItemsList().setSelectedPaths(path.get(v.getId())[i]);
                    }
                } else if (v.getId() != 0) {
                    getMusicItemsList().setSelectedPaths(path.get(numberOfAlbum)[v.getId()]);
                }

            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if(!((v.getId()==0)&&(albumTrigger))) {
            getMusicItemsList().setCheckingTrigger(true);
            v.setBackground(getResources().getDrawable(R.drawable.checked_view_background));
            v.setTag("checked");
            List<String> l = getMusicItemsList().getCheckedList();
            l.add(String.valueOf(v.getId()));
            getMusicItemsList().setCheckedList(l);
            if ((!albumTrigger)) {
                for (int i = 1; i< path.get(v.getId()).length; i++) {
                    getMusicItemsList().setSelectedPaths(path.get(v.getId())[i]);
                }
            } else if (v.getId() != 0) {
                getMusicItemsList().setSelectedPaths(path.get(numberOfAlbum)[v.getId()]);
            }
        }
        return true;
    }
*/
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
