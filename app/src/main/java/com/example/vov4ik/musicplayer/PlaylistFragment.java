package com.example.vov4ik.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PlaylistFragment extends MusicListFragment {//implements View.OnLongClickListener, View.OnClickListener {

    final static String EXTRA_FOR_CLICKED_FILE = "extra for clicked file";
    final static String EXTRA_FOR_FILES = "extra for files";
    final static String EXTRA_FOR_PATHS = "extra for paths";
    private View rootView;

    private List<String> playlistNames = null;
    private List<List<String>> path = null;
    private List<List<String>> filesName = new ArrayList<>();
    private boolean playlistTrigger = false;
    private int numberOfPlaylist;
    private static List<Integer> listOfCurrentIds = new ArrayList<>();

    public static List<Integer> getListOfCurrentIds() {
        return listOfCurrentIds;
    }

    public static void setListOfCurrentIds(List<Integer> listOfCurrentIds) {
        PlaylistFragment.listOfCurrentIds = listOfCurrentIds;
    }

    public static void addListOfCurrentIds(int i) {
        PlaylistFragment.listOfCurrentIds.add(i);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getMusicItemsList().setRootView(inflater.inflate(R.layout.fragment_playlists, container, false));
        rootView = getMusicItemsList().getRootView();
        getMusicItemsList().setRecyclerView((RecyclerView) rootView.findViewById(R.id.playlist_recycler_view));
        getMusicItemsList().setContext(getContext());
        getMusicItemsList().setCheckingTrigger(false);
        getMusicItemsList().setFolderName(DbConnector.getPlaylist(getContext()));

        playlistNames = (getMusicItemsList().getFolderName());
        path = DbConnector.getPlaylistFiles(getContext());
        nameMaker();
        showPlaylist(playlistNames);
        return rootView;
    }

    private static MusicItemsList musicItemsList;

    protected MusicItemsList getMusicItemsList()
    {
        return musicItemsList;
    }

    public PlaylistFragment()
    {
        if (musicItemsList == null)
        {
            musicItemsList = new MusicItemsList();
        }
    }

    @Override
    public boolean onLongClick(int position) {
        if(getMusicItemsList().isPlaylistChanges()) {
            playlistNames = (getMusicItemsList().getFolderName());
            path = getMusicItemsList().getPathPlaylist();
            nameMaker();
            getMusicItemsList().setPlaylistChanges(false);
        }
        if (!((position == 0) && (playlistTrigger))) {
            List<String> l =  getMusicItemsList().getCheckedList();
            l.add(String.valueOf(position));
            getMusicItemsList().setCheckedList(l);
            getMusicItemsList().setCheckingTrigger(true);
            if ((!playlistTrigger)) {
                List<String> newPath = path.get(position);
                newPath.remove(0);
                getMusicItemsList().addSelectedPlaylist(playlistNames.get(position));
                for(String s: newPath) {
                    getMusicItemsList().setSelectedPaths(s);
                }
                List<String[]> arrayList = new ArrayList<>();
                RecyclerView.Adapter mAdapter = new RecyclerViewAdapter(this, playlistNames,
                        arrayList, getMusicItemsList().getCheckedList(), getMusicItemsList().isCheckingTrigger(),
                        getMusicItemsList().isFolderTrigger(), numberOfPlaylist, true, MainActivity.getContext());
                getMusicItemsList().getRecyclerView().setAdapter(mAdapter);
            } else if (position != 0) {
                getMusicItemsList().setNumberOfPlaylist(numberOfPlaylist + 10);
                getMusicItemsList().setSelectedPaths(path.get(numberOfPlaylist).get(position));
                List<String[]> arrayList = new ArrayList<>();
                arrayList.add(getMusicItemsList().getPathPlaylist().get(getMusicItemsList().getNumberOfFolder()).toArray(new String[getMusicItemsList().getPathPlaylist().get(getMusicItemsList().getNumberOfFolder()).size()]));
                RecyclerView.Adapter mAdapter = new RecyclerViewAdapter(this, filesName.get(numberOfPlaylist),
                        arrayList, getMusicItemsList().getCheckedList(), getMusicItemsList().isCheckingTrigger(),
                        getMusicItemsList().isFolderTrigger(), numberOfPlaylist, true, MainActivity.getContext());
                getMusicItemsList().getRecyclerView().setAdapter(mAdapter);
            }
        }
        Log.d("Test", getMusicItemsList().getCheckedList().toString());
        return true;
    }

    @Override
    public void onClick(int position) {
        if(getMusicItemsList().isPlaylistChanges()) {
            playlistNames = (getMusicItemsList().getFolderName());
            path = getMusicItemsList().getPathPlaylist();
            nameMaker();
            getMusicItemsList().setPlaylistChanges(false);
        }
        Log.d("Test", "path"+playlistNames.size());
        Log.d("Test", "path"+path.size());
        Log.d("Test", "path"+filesName.size());
//                Log.d("Test", "path name "+ Arrays.toString(path.get(numberOfPlaylist).toArray(new String[path.get(numberOfPlaylist).size()])));
        if (!getMusicItemsList().isCheckingTrigger()) {
            if ((!playlistTrigger)&&(!playlistNames.get(0).equals("No Playlists available"))) {
                numberOfPlaylist = position;
                getMusicItemsList().setNumberOfFolder(numberOfPlaylist);
                playlistTrigger = true;
                getMusicItemsList().setFolderTrigger(true);
                showPlaylist(filesName.get(position));
            } else if (position == 0) {
                playlistTrigger = false;
                getMusicItemsList().setFolderTrigger(false);
                showPlaylist(playlistNames);
            } else {
//                Intent intent = new Intent(MainActivity.getContext(), PlayerActivity.class);
//                intent.putExtra(EXTRA_FOR_FILES, filesName.get(numberOfPlaylist).toArray(new String[filesName.get(numberOfPlaylist).size()]));
//                intent.putExtra(EXTRA_FOR_CLICKED_FILE, path.get(numberOfPlaylist).get(position));
//                intent.putExtra(EXTRA_FOR_PATHS, path.get(numberOfPlaylist).toArray(new String[path.get(numberOfPlaylist).size()]));

                PlayService.setTrekNumber(position-1);
                PlayService.setPath(path.get(numberOfPlaylist));
                if(PlayService.getPlayer()!=null) {
                    PlayService.setLastPlayedTime(0);
                    PlayService.startPlaying();
                } else {
                    Intent intent1 = new Intent(MainActivity.getContext(), PlayService.class);
                    MainActivity.getContext().startService(intent1);
                    PlayService.setLastPlayedTime(0);
                    PlayService.startPlaying();
                }
//                if(isAdded()) {
//                    startActivity(intent);
//                }else{
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//                    MainActivity.getContext().startActivity(intent);
//                }
            }
        } else if (!((playlistTrigger) && (position == 0))) {
            if (getMusicItemsList().getCheckedList().contains(String.valueOf(position))){
                if ((!playlistTrigger)) {
                    List<String> newPath = path.get(position);
                    newPath.remove(0);
                    getMusicItemsList().removeSelectedPlaylist(playlistNames.get(position));
                    for(String s: newPath) {
                        getMusicItemsList().removeSelectedPaths(s);
                    }
                } else if (position != 0) {
                    getMusicItemsList().removeSelectedPaths(path.get(numberOfPlaylist).get(position));
                }

            } else {
                if ((!playlistTrigger)) {
                    List<String> newPath = path.get(position);
                    newPath.remove(0);
                    getMusicItemsList().addSelectedPlaylist(playlistNames.get(position));
                    for(String s: newPath) {
                        getMusicItemsList().setSelectedPaths(s);
                    }
                } else if (position != 0) {
                    getMusicItemsList().setSelectedPaths(path.get(numberOfPlaylist).get(position));
                }

            }
        }
        Log.d("Test", getMusicItemsList().getCheckedList().toString());
    }

    private void nameMaker() {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            filesName = new ArrayList<>();
            for(int i = 0; i<path.size(); i++) {
                path.get(i).add(0, "..GoToRoot");
                List<String> list = new ArrayList<>();
                for (String p : path.get(i)) {
                    if(p.equals("Make Playlists")){
                        list.add("..GoToRoot");
                        list.add("Make Playlists");
                    }else if(!p.equals("..GoToRoot")) {
                        File f = new File(p);
                        String title;
                        try {
                            mmr.setDataSource(f.getPath());
                            title = (mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
                        } catch (IllegalArgumentException e) {
                            title = "Refresh the Database";
                        }
                        if ((title == null) || (title.equals("")) || (title.equals("Refresh the Database!"))) {
                            list.add(f.getName());
                        } else {
                            list.add(title);
                        }
                    }else{
                        list.add("..GoToRoot");
                    }
                }
                filesName.add(list);
            }
            getMusicItemsList().setPathPlaylist(path);
            getMusicItemsList().setNamePlaylist(filesName);
            mmr.release();
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
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.addPlaylistInMenu).setVisible(false);
        menu.findItem(R.id.removePlaylistInMenu).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }
    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if((!menuVisible)&&(rootView!=null)){
            getMusicItemsList().setCheckingTrigger(false);
            if(playlistTrigger) {
                showPlaylist(filesName.get(numberOfPlaylist));
            }else {
                showPlaylist(playlistNames);
            }
        }
    }
}
