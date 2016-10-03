package com.example.vov4ik.musicplayer;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vov4ik on 8/14/2016.
 */
public abstract class MusicListFragment extends Fragment implements ISelectableFragment{//, View.OnClickListener, View.OnLongClickListener {

    final static String EXTRA_FOR_CLICKED_FILE = "extra for clicked file";
    final static String EXTRA_FOR_PATHS = "extra for paths";
    public boolean isCheckingTrigger() {
        return getMusicItemsList().isCheckingTrigger();
    }

    public List<String> getPreviousList(){
        return getMusicItemsList().getFolderName();
    }
    public List<String> getSelectedPaths()
    {
        return getMusicItemsList().getSelectedPaths();
    }
    public List<String> getSelectedPlaylist()
    {
        return getMusicItemsList().getSelectedPlaylist();
    }
    public int getNumberOfPlaylist() {
        return getMusicItemsList().getNumberOfPlaylist();
    }

    public boolean isFolderTrigger() {
        return getMusicItemsList().isFolderTrigger();
    }
    public boolean isAllSongsFragment() {
        return getMusicItemsList().isAllSongsFragment();
    }

    public void setFolderTrigger(boolean folderTrigger) {
        getMusicItemsList().setFolderTrigger(folderTrigger);
    }

    public void reloadForPlaylist(){
        getMusicItemsList().setPlaylistChanges(true);
        getMusicItemsList().setCheckingTrigger(false);
        getMusicItemsList().setFolderName(DbConnector.getPlaylist(getMusicItemsList().getContext()));
        getMusicItemsList().setPathPlaylist(DbConnector.getPlaylistFiles(getMusicItemsList().getContext()));
        showPlaylist(getMusicItemsList().getFolderName());
        getMusicItemsList().setCheckedList(new ArrayList<String>());
        getMusicItemsList().setNewSelectedPaths(new ArrayList<String>());
        getMusicItemsList().setSelectedPlaylist(new ArrayList<String>());
        getMusicItemsList().setSelectedPlaylist(new ArrayList<String>());
        getMusicItemsList().setNumberOfPlaylist(0);
    }

    public void unselectMusicItems()
    {
        List<String> list;
        getMusicItemsList().setCheckingTrigger(false);
//        list = getMusicItemsList().getCheckedList();
//        for(String s: list) {
//            int i = Integer.parseInt(s);
//            View v = getMusicItemsList().getLinearLayout().findViewById(i);
//            v.setBackground(null);
//            v.setTag(null);
//        }
        getMusicItemsList().setCheckedList(new ArrayList<String>());
        getMusicItemsList().setNewSelectedPaths(new ArrayList<String>());
        getMusicItemsList().setSelectedPlaylist(new ArrayList<String>());
        if(getMusicItemsList().getRecyclerView().getId()!=(R.id.playlist_recycler_view)) {
            if(getMusicItemsList().isFolderTrigger()) {
                RecyclerView.Adapter mAdapter = new RecyclerViewAdapter(this, (getMusicItemsList().getMusicFiles().get(getMusicItemsList().getNumberOfFolder())),
                        getMusicItemsList().getPath(), getMusicItemsList().getCheckedList(), getMusicItemsList().isCheckingTrigger(),
                        getMusicItemsList().isFolderTrigger(), getMusicItemsList().getNumberOfFolder(), false, MainActivity.getContext(), getMusicItemsList().isAllSongsFragment());
                getMusicItemsList().getRecyclerView().setAdapter(mAdapter);
            }else{
                RecyclerView.Adapter mAdapter = new RecyclerViewAdapter(this, getMusicItemsList().getFolderName(),
                        getMusicItemsList().getPath(), getMusicItemsList().getCheckedList(), getMusicItemsList().isCheckingTrigger(),
                        getMusicItemsList().isFolderTrigger(), getMusicItemsList().getNumberOfFolder(), false, MainActivity.getContext(), getMusicItemsList().isAllSongsFragment());
                getMusicItemsList().getRecyclerView().setAdapter(mAdapter);
            }
        }else{
            showPlaylist(getMusicItemsList().getFolderName());
        }
    }

    protected abstract MusicItemsList getMusicItemsList();
    public void showPlaylist(List<String> list){

        List<List<String>> arrayList = new ArrayList<>();
        if(getMusicItemsList().isFolderTrigger()) {
            arrayList.add(getMusicItemsList().getPathPlaylist().get(getMusicItemsList().getNumberOfFolder()));
        }
        getMusicItemsList().getRecyclerView().setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        getMusicItemsList().getRecyclerView().setLayoutManager(mLayoutManager);
        RecyclerView.Adapter mAdapter = new RecyclerViewAdapter(this, list,
                arrayList, getMusicItemsList().getCheckedList(), getMusicItemsList().isCheckingTrigger(),
                getMusicItemsList().isFolderTrigger(),getMusicItemsList().getNumberOfPlaylist(), true, MainActivity.getContext(), getMusicItemsList().isAllSongsFragment());
        getMusicItemsList().getRecyclerView().setAdapter(mAdapter);
    }

    public void show(List<String> list){

        Log.d("Test", "showPlaylist " + list.size());
//        getMusicItemsList().getLinearLayout().removeAllViews();
//        for (String s : list) {
//            TextView text = new TextView(getMusicItemsList().getLinearLayout().getContext());
//            text.setText(String.valueOf(s));
//            text.setId((list).indexOf(s));
//            ((LinearLayout) getMusicItemsList().getLinearLayout()).addView(text);
//            text.setOnClickListener(this);
//            text.setOnLongClickListener(this);
//            text.setPadding(20, 10, 20, 10);
//            text.setTextSize(16);
//            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) text.getLayoutParams();
//            mlp.setMargins(0, 15, 0, 15);
//        }

        getMusicItemsList().getRecyclerView().setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        getMusicItemsList().getRecyclerView().setLayoutManager(mLayoutManager);

            // specify an adapter (see also next example)
        RecyclerView.Adapter mAdapter = new RecyclerViewAdapter(this, list,
                getMusicItemsList().getPath(), getMusicItemsList().getCheckedList(), getMusicItemsList().isCheckingTrigger(),
                getMusicItemsList().isFolderTrigger(),getMusicItemsList().getNumberOfFolder(), false, MainActivity.getContext(), getMusicItemsList().isAllSongsFragment());
        getMusicItemsList().getRecyclerView().setAdapter(mAdapter);

//    public void longClick(int position){
//        checked = new ArrayList<>();
//        checked.add(String.valueOf(position));
//        mAdapter = new MyAdapter(this, musicFiles, folderName, checked, true);
//        mRecyclerView.setAdapter(mAdapter);
//    }

}
 //   @Override
    public void onClick(int position) {
        if (!getMusicItemsList().isCheckingTrigger()) {
            if ((!getMusicItemsList().isFolderTrigger())) {
                getMusicItemsList().setNumberOfFolder(position);
                getMusicItemsList().setFolderTrigger(true);
                show(getMusicItemsList().getMusicFiles().get(position));
            } else if (position == 0) {
                getMusicItemsList().setFolderTrigger(false);
                show(getMusicItemsList().getFolderName());
            } else {
//                Intent intent = new Intent(getContext(), PlayerActivity.class);
//                intent.putExtra(EXTRA_FOR_CLICKED_FILE, getMusicItemsList().getPath().get(getMusicItemsList().getNumberOfFolder())[(position)]);
//                intent.putExtra(EXTRA_FOR_PATHS, getMusicItemsList().getPath().get(getMusicItemsList().getNumberOfFolder()));
//                startActivity(intent);
                if(PlayService.getPlayer() == null){
                    Intent intent1 = new Intent(MainActivity.getContext(), PlayService.class);
                    MainActivity.getContext().startService(intent1);
                }
                PlayService.setTrekNumber(position-1);
                PlayService.setPath(getMusicItemsList().getPath().get(getMusicItemsList().getNumberOfFolder()));
//                if(PlayService.getPlayer()!=null) {
//                    PlayService.setLastPlayedTime(0);
//                    PlayService.startPlaying(getMusicItemsList().getPath().get(getMusicItemsList().getNumberOfFolder())[position]);
//                } else {
                PlayService.setLastPlayedTime(0);
                PlayService.setClickedOnTheSong(true);
                Intent intent1 = new Intent(MainActivity.getContext(), PlayService.class);
                intent1.setAction(PlayService.PLAY_ACTION);
                intent1.putExtra("CLICKED_SONG", getMusicItemsList().getPath().get(getMusicItemsList().getNumberOfFolder()).get(position-1));
                MainActivity.getContext().startService(intent1);
//                    PlayService.startPlaying(getMusicItemsList().getPath().get(getMusicItemsList().getNumberOfFolder())[position]);
//                }
                getMusicItemsList().setFolderTrigger(false);
                show(getMusicItemsList().getFolderName());
            }
        }else if (!((getMusicItemsList().isFolderTrigger()) && (position == 0))) {
            if (getMusicItemsList().getCheckedList().contains(String.valueOf(position))){
                List<String> l =  getMusicItemsList().getCheckedList();
                l.remove(String.valueOf(position));
                getMusicItemsList().setCheckedList(l);
                if ((!getMusicItemsList().isFolderTrigger())) {
                    for (int i = 1; i< getMusicItemsList().getPath().get(position).size(); i++) {
                        getMusicItemsList().removeSelectedPaths(getMusicItemsList().getPath().get(position).get(i));
                    }
                } else  {
                    getMusicItemsList().removeSelectedPaths(getMusicItemsList().getPath().get(getMusicItemsList().getNumberOfFolder()).get(position-1));
                }

            } else {
                List<String> l =  getMusicItemsList().getCheckedList();
                l.add(String.valueOf(position));
                getMusicItemsList().setCheckedList(l);
                if ((!getMusicItemsList().isFolderTrigger())) {
                    for (int i = 1; i< getMusicItemsList().getPath().get(position).size(); i++) {
                        getMusicItemsList().setSelectedPaths(getMusicItemsList().getPath().get(position).get(i));
                    }
                } else {
                    getMusicItemsList().setSelectedPaths(getMusicItemsList().getPath().get(getMusicItemsList().getNumberOfFolder()).get(position-1));
                }
            }
        }
        Log.d("Test", getMusicItemsList().getCheckedList().toString());
    }


   //@Override
    public boolean onLongClick(int position) {
//        Log.d("Test",(v.getId()==0)+" "+
        try {
            if (!((position == 0) && (getMusicItemsList().isFolderTrigger()))) {
                getMusicItemsList().setCheckingTrigger(true);
                List<String> l = getMusicItemsList().getCheckedList();
                l.add(String.valueOf(position));
                getMusicItemsList().setCheckedList(l);
                if ((!getMusicItemsList().isFolderTrigger())) {
                    for (int i = 1; i < getMusicItemsList().getPath().get(position).size(); i++) {
                        getMusicItemsList().setSelectedPaths(getMusicItemsList().getPath().get(position).get(i));
                    }
                    RecyclerView.Adapter mAdapter = new RecyclerViewAdapter(this, getMusicItemsList().getFolderName(),
                            getMusicItemsList().getPath(), getMusicItemsList().getCheckedList(), getMusicItemsList().isCheckingTrigger(),
                            getMusicItemsList().isFolderTrigger(),getMusicItemsList().getNumberOfFolder(), false, MainActivity.getContext(), getMusicItemsList().isAllSongsFragment());
                    getMusicItemsList().getRecyclerView().setAdapter(mAdapter);
                } else if (position != 0) {
                    getMusicItemsList().setSelectedPaths(getMusicItemsList().getPath().get(getMusicItemsList().getNumberOfFolder()).get(position));
                    RecyclerView.Adapter mAdapter = new RecyclerViewAdapter(this, (getMusicItemsList().getMusicFiles().get(getMusicItemsList().getNumberOfFolder())),
                            getMusicItemsList().getPath(), getMusicItemsList().getCheckedList(), getMusicItemsList().isCheckingTrigger(),
                            getMusicItemsList().isFolderTrigger(),getMusicItemsList().getNumberOfFolder(), false, MainActivity.getContext(), getMusicItemsList().isAllSongsFragment());
                    getMusicItemsList().getRecyclerView().setAdapter(mAdapter);
                }
            }
            Log.d("Test", getMusicItemsList().getCheckedList().toString());
            return true;
        }catch(IllegalStateException il){
            return true;
        }
    }

}
