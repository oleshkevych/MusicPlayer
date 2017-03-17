package com.example.vov4ik.musicplayer.screens.main.fragments.playlist;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.example.vov4ik.musicplayer.service.PlayService;
import com.example.vov4ik.musicplayer.R;
import com.example.vov4ik.musicplayer.screens.main.fragments.RecyclerViewAdapter;
import com.example.vov4ik.musicplayer.data.local.DbConnector;
import com.example.vov4ik.musicplayer.data.model.MusicItemsList;
import com.example.vov4ik.musicplayer.screens.main.fragments.MusicListFragment;

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
//        getMusicItemsList().setRecyclerView((RecyclerView) rootView.findViewById(R.id.playlist_recycler_view));
//        getMusicItemsList().setContext(getContext());
//        getMusicItemsList().setCheckingTrigger(false);
//        getMusicItemsList().setFolderName(DbConnector.getPlaylist(getContext()));
//
//        playlistNames = (getMusicItemsList().getFolderName());
//        path = DbConnector.getPlaylistFiles(getContext());
//        nameMaker();
//        showPlaylist(playlistNames);
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

//    @Override
//    public boolean onLongClick(int position) {
//        if(getMusicItemsList().isPlaylistChanges()) {
//            playlistNames = (getMusicItemsList().getFolderName());
//            path = getMusicItemsList().getPathPlaylist();
//            nameMaker();
//            getMusicItemsList().setPlaylistChanges(false);
//        }
//        if (!((position == 0) && (playlistTrigger))) {
//            List<String> l =  getMusicItemsList().getCheckedList();
//            l.add(String.valueOf(position));
//            getMusicItemsList().setCheckedList(l);
//            getMusicItemsList().setCheckingTrigger(true);
//            if ((!playlistTrigger)) {
//                List<String> newPath = path.get(position);
//                newPath.remove(0);
//                getMusicItemsList().addSelectedPlaylist(playlistNames.get(position));
//                for(String s: newPath) {
//                    getMusicItemsList().setSelectedPaths(s);
//                }
//                List<List<String>> arrayList = new ArrayList<>();
//                getAdapter().updateAdapter(playlistNames,
//                        arrayList, getMusicItemsList().getCheckedList(), getMusicItemsList().isCheckingTrigger(),
//                        getMusicItemsList().isFolderTrigger(), numberOfPlaylist, true, getMusicItemsList().isAllSongsFragment());
//            } else if (position != 0) {
//                getMusicItemsList().setNumberOfPlaylist(numberOfPlaylist + 10);
//                getMusicItemsList().setSelectedPaths(path.get(numberOfPlaylist).get(position));
//                List<List<String>> arrayList = new ArrayList<>();
//                arrayList.add(getMusicItemsList().getPathPlaylist().get(getMusicItemsList().getNumberOfFolder()));
//                getAdapter().updateAdapter(filesName.get(numberOfPlaylist),
//                        arrayList, getMusicItemsList().getCheckedList(), getMusicItemsList().isCheckingTrigger(),
//                        getMusicItemsList().isFolderTrigger(), numberOfPlaylist, true, getMusicItemsList().isAllSongsFragment());
//            }
//        }
//        Log.d("Test", getMusicItemsList().getCheckedList().toString());
//        return true;
//    }
//
//    @Override
//    public void onClick(int position) {
//        if(getMusicItemsList().isPlaylistChanges()) {
//            playlistNames = (getMusicItemsList().getFolderName());
//            path = getMusicItemsList().getPathPlaylist();
//            nameMaker();
//            getMusicItemsList().setPlaylistChanges(false);
//        }
//        Log.d("Test", "path"+playlistNames.size());
//        Log.d("Test", "path"+path.size());
//        Log.d("Test", "path"+filesName.size());
////                Log.d("Test", "path name "+ Arrays.toString(path.get(numberOfPlaylist).toArray(new String[path.get(numberOfPlaylist).size()])));
//        if (!getMusicItemsList().isCheckingTrigger()) {
//            if ((!playlistTrigger)&&(!playlistNames.get(0).equals("No Playlists available"))) {
//                numberOfPlaylist = position;
//                getMusicItemsList().setNumberOfFolder(numberOfPlaylist);
//                playlistTrigger = true;
//                getMusicItemsList().setFolderTrigger(true);
//                showPlaylist(filesName.get(position));
//            } else if (position == 0) {
//                playlistTrigger = false;
//                getMusicItemsList().setFolderTrigger(false);
//                showPlaylist(playlistNames);
//            } else {
//                    sendListPathsAction(path.get(numberOfPlaylist));
//                    sendStartPlayAction(position-1);
//            }
//        } else if (!((playlistTrigger) && (position == 0))) {
//            if (getMusicItemsList().getCheckedList().contains(String.valueOf(position))){
//                if ((!playlistTrigger)) {
//                    List<String> newPath = path.get(position);
//                    newPath.remove(0);
//                    getMusicItemsList().removeSelectedPlaylist(playlistNames.get(position));
//                    for(String s: newPath) {
//                        getMusicItemsList().removeSelectedPaths(s);
//                    }
//                } else if (position != 0) {
//                    getMusicItemsList().removeSelectedPaths(path.get(numberOfPlaylist).get(position));
//                }
//
//            } else {
//                if ((!playlistTrigger)) {
//                    List<String> newPath = path.get(position);
//                    newPath.remove(0);
//                    getMusicItemsList().addSelectedPlaylist(playlistNames.get(position));
//                    for(String s: newPath) {
//                        getMusicItemsList().setSelectedPaths(s);
//                    }
//                } else if (position != 0) {
//                    getMusicItemsList().setSelectedPaths(path.get(numberOfPlaylist).get(position));
//                }
//
//            }
//        }
//        Log.d("Test", getMusicItemsList().getCheckedList().toString());
//    }
//
//    private void nameMaker() {
//            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//            filesName = new ArrayList<>();
//            for(int i = 0; i<path.size(); i++) {
//                path.get(i).add(0, "..GoToRoot");
//                List<String> list = new ArrayList<>();
//                for (String p : path.get(i)) {
//                    if(p.equals("Make Playlists")){
//                        list.add("..GoToRoot");
//                        list.add("Make Playlists");
//                    }else if(!p.equals("..GoToRoot")) {
//                        File f = new File(p);
//                        String title;
//                        try {
//                            mmr.setDataSource(f.getPath());
//                            title = (mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
//                        } catch (IllegalArgumentException e) {
//                            title = "Refresh the Database";
//                        }
//                        if ((title == null) || (title.equals("")) || (title.equals("Refresh the Database!"))) {
//                            list.add(f.getName());
//                        } else {
//                            list.add(title);
//                        }
//                    }else{
//                        list.add("..GoToRoot");
//                    }
//                }
//                filesName.add(list);
//            }
//            getMusicItemsList().setPathPlaylist(path);
//            getMusicItemsList().setNamePlaylist(filesName);
//            mmr.release();
//    }
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//    }
//    @Override
//    public void onPrepareOptionsMenu(Menu menu) {
//        menu.findItem(R.id.addPlaylistInMenu).setVisible(false);
//        menu.findItem(R.id.removePlaylistInMenu).setVisible(true);
//        super.onPrepareOptionsMenu(menu);
//    }
//    @Override
//    public void setMenuVisibility(boolean menuVisible) {
//        super.setMenuVisibility(menuVisible);
//        if((!menuVisible)&&(rootView!=null)){
//            getMusicItemsList().setCheckingTrigger(false);
//            if(playlistTrigger) {
//                showPlaylist(filesName.get(numberOfPlaylist));
//            }else {
//                showPlaylist(playlistNames);
//            }
//        }
//    }
//
//    private void sendListPathsAction(List<String> paths) {
//        Intent intent = new Intent(PlayService.SET_PATHS_ACTION);
//        intent.putExtra(PlayService.LIST_EXTRA, paths.toArray());
//        sendBroadcastIntent(intent);
//    }
//
//    private void sendStartPlayAction(int position) {
//        Intent intent = new Intent(PlayService.START_PLAY_FILE_ACTION);
//        intent.putExtra(PlayService.FILE_NUMBER_EXTRA, position);
//        sendBroadcastIntent(intent);
//    }
//
//    private void sendBroadcastIntent(Intent intent) {
//        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
//    }
}
