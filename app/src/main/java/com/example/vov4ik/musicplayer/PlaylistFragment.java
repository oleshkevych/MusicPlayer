package com.example.vov4ik.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PlaylistFragment extends MusicListFragment implements View.OnLongClickListener, View.OnClickListener {

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
        getMusicItemsList().setLinearLayout((LinearLayout) rootView.findViewById(R.id.layoutPlaylist));
        getMusicItemsList().setContext(getContext());
        getMusicItemsList().setCheckingTrigger(false);
        getMusicItemsList().setFolderName(DbConnector.getPlaylist(getContext()));

        playlistNames = (getMusicItemsList().getFolderName());
        path = DbConnector.getPlaylistFiles(getContext());
        nameMaker();
        show(playlistNames);
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
    public boolean onLongClick(View v) {
        if(getMusicItemsList().isPlaylistChanges()) {
            playlistNames = (getMusicItemsList().getFolderName());
            path = getMusicItemsList().getPathPlaylist();
            nameMaker();
            getMusicItemsList().setPlaylistChanges(false);
        }
        if (!((v.getId() == 0) && (playlistTrigger))) {
            getMusicItemsList().setCheckingTrigger(true);
            v.setBackground(MainActivity.getContext().getResources().getDrawable(R.drawable.checked_view_background));
            v.setTag("checked");
            if ((!playlistTrigger)) {
                List<String> newPath = path.get(v.getId());
                newPath.remove(0);
                getMusicItemsList().addSelectedPlaylist(playlistNames.get(v.getId()));
                for(String s: newPath) {
                    getMusicItemsList().setSelectedPaths(s);
                }
            } else if (v.getId() != 0) {
                getMusicItemsList().setNumberOfPlaylist(numberOfPlaylist + 10);
                getMusicItemsList().setSelectedPaths(path.get(numberOfPlaylist).get(v.getId()));
            }
        }
    return true;
    }

    @Override
    public void onClick(View v) {
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
            if ((!playlistTrigger)) {
                numberOfPlaylist = v.getId();
                show(filesName.get(v.getId()));
                playlistTrigger = true;
            } else if (v.getId() == 0) {
                show(playlistNames);
                playlistTrigger = false;
            } else {
                Intent intent = new Intent(MainActivity.getContext(), PlayerActivity.class);
                intent.putExtra(EXTRA_FOR_FILES, filesName.get(numberOfPlaylist).toArray(new String[filesName.get(numberOfPlaylist).size()]));
                intent.putExtra(EXTRA_FOR_CLICKED_FILE, path.get(numberOfPlaylist).get(v.getId()));
                intent.putExtra(EXTRA_FOR_PATHS, path.get(numberOfPlaylist).toArray(new String[path.get(numberOfPlaylist).size()]));
                if(isAdded()) {
                    startActivity(intent);
                }else{
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    MainActivity.getContext().startActivity(intent);
                }
            }
        } else if (!((playlistTrigger) && (v.getId() == 0))) {
            if ((v.getTag()!=null)&&(v.getTag().equals("checked"))){
                v.setBackground(null);
                v.setTag(null);
                if ((!playlistTrigger)) {
                    List<String> newPath = path.get(v.getId());
                    newPath.remove(0);
                    getMusicItemsList().removeSelectedPlaylist(playlistNames.get(v.getId()));
                    for(String s: newPath) {
                        getMusicItemsList().removeSelectedPaths(s);
                    }
                } else if (v.getId() != 0) {
                    getMusicItemsList().removeSelectedPaths(path.get(numberOfPlaylist).get(v.getId()));
                }

            } else {
                v.setBackground(MainActivity.getContext().getResources().getDrawable(R.drawable.checked_view_background));
                v.setTag("checked");
                if ((!playlistTrigger)) {
                    List<String> newPath = path.get(v.getId());
                    newPath.remove(0);
                    getMusicItemsList().addSelectedPlaylist(playlistNames.get(v.getId()));
                    for(String s: newPath) {
                        getMusicItemsList().setSelectedPaths(s);
                    }
                } else if (v.getId() != 0) {
                    getMusicItemsList().setSelectedPaths(path.get(numberOfPlaylist).get(v.getId()));
                }

            }
        }
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
                show(filesName.get(numberOfPlaylist));
            }else {
                show(playlistNames);
            }
        }
    }
}
