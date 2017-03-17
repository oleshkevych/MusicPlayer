package com.example.vov4ik.musicplayer.screens.main.fragments;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.vov4ik.musicplayer.R;
import com.example.vov4ik.musicplayer.data.model.MusicFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by vov4ik on 9/11/2016.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    public static final int ALBUM_FRAGMENT = 1;
    public static final int ALL_SONGS_FRAGMENT = 2;
    public static final int ARTIST_FRAGMENT = 3;
    public static final int FOLDER_ALL_INCLUDE = 4;
    public static final int FOLDER = 5;
    public static final int PLAYLIST = 6;

    private List<String> mTitles;
    private Map<String, List<MusicFile>> mSortedViewMap;
    private List<String> mPath;
    private List<MusicFile> mSelectedMusicFiles;
    private boolean mIsCheckboxVisible;
    private String mPlayingTrack;
    private boolean mIsPlayingNow;
    private boolean mIsInFolder;
    private int mCurrentFragment;
    private List<Integer> mSelectedPathsId;
    private String mOpenedDirectory;

    interface OnClickListener {

        void onFileClick(MusicFile clickedMusicFile, List<MusicFile> musicFiles);

    }

    private RecyclerViewAdapter.OnClickListener mOnClickListener;

    void setOnClickListener(RecyclerViewAdapter.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public RecyclerViewAdapter(int fragment) {
        mPath = new ArrayList<>();
        mTitles = new ArrayList<>();
        mSelectedMusicFiles = new ArrayList<>();
        mCurrentFragment = fragment;
        mSortedViewMap = new TreeMap<>();
        mSelectedPathsId = new ArrayList<>();
    }

    private void setUpAdapter(@NonNull List<MusicFile> musicFiles) {

        fillCollections(musicFiles);
        if (mCurrentFragment == ALL_SONGS_FRAGMENT) {
            setUpInsideFolderValues("");
        }else
        if (mCurrentFragment != PLAYLIST) {
            setUpFolderValues();
        }

    }

    private void fillCollections(List<MusicFile> musicFiles){
        mSortedViewMap = new TreeMap<>();
        switch (mCurrentFragment) {
            case ALBUM_FRAGMENT:
                for (MusicFile musicFile : musicFiles) {
                    if (mSortedViewMap.containsKey(musicFile.getAlbum())) {
                        mSortedViewMap.get(musicFile.getAlbum()).add(musicFile);
                    } else {
                        mSortedViewMap.put(musicFile.getAlbum(), new ArrayList<MusicFile>(Arrays.asList(musicFile)));
                    }
                }
                break;
            case ARTIST_FRAGMENT:
                for (MusicFile musicFile : musicFiles) {
                    if (mSortedViewMap.containsKey(musicFile.getArtist())) {
                        mSortedViewMap.get(musicFile.getArtist()).add(musicFile);
                    } else {
                        mSortedViewMap.put(musicFile.getArtist(), new ArrayList<MusicFile>(Arrays.asList(musicFile)));
                    }
                }
                break;
            case FOLDER:
                for (MusicFile musicFile : musicFiles) {
                    if (mSortedViewMap.containsKey(musicFile.getFolder())) {
                        mSortedViewMap.get(musicFile.getFolder()).add(musicFile);
                    } else {
                        mSortedViewMap.put(musicFile.getFolder(), new ArrayList<MusicFile>(Arrays.asList(musicFile)));
                    }
                }
                break;
            case FOLDER_ALL_INCLUDE:
                for (MusicFile musicFile : musicFiles) {
                    if (mSortedViewMap.containsKey(musicFile.getMainFolder())) {
                        mSortedViewMap.get(musicFile.getMainFolder()).add(musicFile);
                    } else {
                        mSortedViewMap.put(musicFile.getMainFolder(), new ArrayList<MusicFile>(Arrays.asList(musicFile)));
                    }
                }
                break;
            case PLAYLIST:
                break;
            case ALL_SONGS_FRAGMENT:
                mTitles = new ArrayList<>();
                mPath = new ArrayList<>();
                for (MusicFile m : musicFiles) {
                    mTitles.add(m.getTitle());
                    mPath.add(new File(m.getPath()).getParentFile().getPath());
                }
                mIsInFolder = true;
                mOpenedDirectory = "";
                mSortedViewMap.put(mOpenedDirectory, musicFiles);
                notifyDataSetChanged();
                break;
        }
        sortMap();
    }

    private void sortMap(){
        Map<String, List<MusicFile>> sortedMap = new TreeMap<String, List<MusicFile>>();
        List<String> keys = new ArrayList<>(mSortedViewMap.keySet());
        Collections.sort(keys, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.toLowerCase().compareTo(o2.toLowerCase());
            }
        });
//        for(String key: keys) {
//            sortedMap.put(key, new ArrayList<MusicFile>());
//        }
//        switch (mCurrentFragment) {
//            case ALBUM_FRAGMENT:
                for(String key: keys){
                    List<MusicFile> sortedMusicFiles = mSortedViewMap.get(key);
                    Collections.sort(sortedMusicFiles, new Comparator<MusicFile>() {
                        @Override
                        public int compare(MusicFile o1, MusicFile o2) {
                            return o1.getTitle().toLowerCase().compareTo(o2.getTitle().toLowerCase());
                        }
                    });
                    sortedMap.put(key, sortedMusicFiles);
                }
//                break;
//            case ARTIST_FRAGMENT:
//                for(String key: keys){
//                    List<MusicFile> sortedMusicFiles = mSortedViewMap.get(key);
//                    Collections.sort(sortedMusicFiles, new Comparator<MusicFile>() {
//                        @Override
//                        public int compare(MusicFile o1, MusicFile o2) {
//                            return o1.getArtist().toLowerCase().compareTo(o2.getArtist().toLowerCase());
//                        }
//                    });
//                    sortedMap.put(key, sortedMusicFiles);
////                    sortedMap.get(key).addAll(sortedMusicFiles);
//                }
//                break;
//            case FOLDER:
//                for(String key: keys){
//                    List<MusicFile> sortedMusicFiles = mSortedViewMap.get(key);
//                    Collections.sort(sortedMusicFiles, new Comparator<MusicFile>() {
//                        @Override
//                        public int compare(MusicFile o1, MusicFile o2) {
//                            return o1.getFolder().toLowerCase().toLowerCase().compareTo(o2.getFolder().toLowerCase());
//                        }
//                    });
//                    sortedMap.put(key, sortedMusicFiles);
//                }
//                break;
//            case FOLDER_ALL_INCLUDE:
//                for(String key: keys){
//                    List<MusicFile> sortedMusicFiles = mSortedViewMap.get(key);
//                    Collections.sort(sortedMusicFiles, new Comparator<MusicFile>() {
//                        @Override
//                        public int compare(MusicFile o1, MusicFile o2) {
//                            return o1.getMainFolder().toLowerCase().compareTo(o2.getMainFolder().toLowerCase());
//                        }
//                    });
//                    sortedMap.put(key, sortedMusicFiles);
//                }
//                break;
//            case PLAYLIST:
//                break;
//            case ALL_SONGS_FRAGMENT:
//                for(String key: keys){
//                    List<MusicFile> sortedMusicFiles = mSortedViewMap.get(key);
//                    Collections.sort(sortedMusicFiles, new Comparator<MusicFile>() {
//                        @Override
//                        public int compare(MusicFile o1, MusicFile o2) {
//                            return o1.getTitle().toLowerCase().compareTo(o2.getTitle().toLowerCase());
//                        }
//                    });
//                    sortedMap.put(key, sortedMusicFiles);
//                }
//                break;
//        }
        mSortedViewMap = sortedMap;
    }

    void setPlayingFile(String playingPath, boolean isPlayingNow) {
        mPlayingTrack = playingPath;
        mIsPlayingNow = isPlayingNow;
        notifyDataSetChanged();
    }

    void stopPlayingFile() {
        mIsPlayingNow = false;
        notifyDataSetChanged();
    }

    void unselect() {
        mIsCheckboxVisible = false;
        mSelectedMusicFiles.clear();
        mSelectedPathsId.clear();
        notifyDataSetChanged();
    }

    boolean isInFolder(){
        return mCurrentFragment != ALL_SONGS_FRAGMENT && mIsInFolder;
    }

    boolean isSelecting(){
        return mIsCheckboxVisible;
    }


    public void updateMusic(@NonNull List<MusicFile> musicFiles) {
        if(mPath.size() > 0){
            fillCollections(musicFiles);
        }else{
            setUpAdapter(musicFiles);
        }


    }

    List<MusicFile> getSelectedMusicFiles() {
        return mSelectedMusicFiles;
    }

    void setUpFolderValues() {
        mTitles = new ArrayList<>();
        mPath = new ArrayList<>();
        mIsInFolder = false;
        Set<String> values = mSortedViewMap.keySet();
        for (String s : values) {
            mTitles.add(s);
            mPath.add("Records: " + mSortedViewMap.get(s).size());
        }
        notifyDataSetChanged();
    }

    private void setUpInsideFolderValues(String key) {
        mTitles = new ArrayList<>();
        mPath = new ArrayList<>();
        mIsInFolder = true;
        List<MusicFile> values = mSortedViewMap.get(key);
        for (MusicFile m : values) {
            mTitles.add(m.getTitle());
            mPath.add(new File(m.getPath()).getParentFile().getPath());
        }
        if(mCurrentFragment != ALL_SONGS_FRAGMENT) {
            mPath.add(0, "..go To Root");
            mTitles.add(0, "..go To Root");
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_music_file, parent, false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.names.setText(mTitles.get(position));
//        if (!mIsPlaylist) {
//            holder.path.setText(mPath.get(position));
//        } else if (mIsFolderTrigger) {
        holder.path.setText(mPath.get(position));
//        }
//        if (mIsPlayingNow && mPath.get(position).equals(mPlayingTrack)) {
//            holder.mView.setBackground(holder.mView.getContext().getResources().getDrawable(R.drawable.playing_background));
//            holder.names.setTextColor(holder.mView.getContext().getResources().getColor(R.color.colorWhite));
//            holder.path.setTextColor(holder.mView.getContext().getResources().getColor(R.color.colorWhite));
//        }

        if (mIsCheckboxVisible) {
            holder.checkBox.setOnCheckedChangeListener(null);
            holder.checkBox.setChecked(mSelectedPathsId.contains(position));
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (mIsCheckboxVisible) {
                        holder.onClick(isChecked);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        try {
            return mTitles.size();
        } catch (NullPointerException n) {
            return 0;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        View mView;
        TextView names;
        TextView path;
        CheckBox checkBox;


        ViewHolder(View v) {
            super(v);
            mView = v;
            names = (TextView) v.findViewById(R.id.musicFileName);
            path = (TextView) v.findViewById(R.id.path);
            checkBox = (CheckBox) v.findViewById(R.id.checkBox);
            v.setOnClickListener(this);
            if (getAdapterPosition() != 0) {
                if (mIsCheckboxVisible) {
                    checkBox.setVisibility(View.VISIBLE);
                } else {
                    checkBox.setVisibility(View.GONE);
                    v.setOnLongClickListener(this);
                }
            }
        }

        @Override
        public void onClick(View v) {
            if (mIsCheckboxVisible) {
                if (mIsInFolder) {
                    if (getAdapterPosition() == 0 && mCurrentFragment != ALL_SONGS_FRAGMENT) {
                        mIsCheckboxVisible = false;
                        mSelectedPathsId = new ArrayList<>();
                        mSelectedMusicFiles = new ArrayList<>();
                        setUpFolderValues();
                        return;
                    }
                    if (mSelectedPathsId.contains(getAdapterPosition())) {
                        mSelectedPathsId.remove(mSelectedPathsId.indexOf(getAdapterPosition()));
                        MusicFile musicFile = mSortedViewMap.get(mOpenedDirectory).get(getAdapterPosition());
                        if (musicFile != null)
                            mSelectedMusicFiles.remove(musicFile);
                        checkBox.setChecked(false);
                    } else {
                        mSelectedPathsId.add(getAdapterPosition());
                        MusicFile musicFile = mSortedViewMap.get(mOpenedDirectory).get(getAdapterPosition())/*getMusicFile(String.valueOf(path.getText()))*/;
                        if (musicFile != null)
                            mSelectedMusicFiles.add(musicFile);
                        checkBox.setChecked(true);
                    }
                } else {
                    if (mSelectedPathsId.contains(getAdapterPosition())) {
                        mSelectedPathsId.remove(mSelectedPathsId.indexOf(getAdapterPosition()));
                        checkBox.setChecked(false);
                        String key = String.valueOf(names.getText());
                        mSelectedMusicFiles.removeAll(mSortedViewMap.get(key));

                    } else {
                        mSelectedPathsId.add(getAdapterPosition());
                        checkBox.setChecked(true);
                        String key = String.valueOf(names.getText());
                        mSelectedMusicFiles.addAll(mSortedViewMap.get(key));
                    }
                }
            } else {

                if (mIsInFolder) {
                    if (getAdapterPosition() == 0 && mCurrentFragment != ALL_SONGS_FRAGMENT) {
                        setUpFolderValues();
                        return;
                    }
                    int position = getAdapterPosition();
                    if(mCurrentFragment != ALL_SONGS_FRAGMENT)
                        position--;
                    MusicFile musicFile = mSortedViewMap.get(mOpenedDirectory).get(position);
                    if (musicFile != null)
                        mOnClickListener.onFileClick(musicFile, mSortedViewMap.get(mOpenedDirectory));
                } else {
                    mOpenedDirectory = String.valueOf(names.getText());
                    setUpInsideFolderValues(mOpenedDirectory);
                }

            }
        }

        public void onClick(boolean isChecked) {
            if (isChecked) {
                mSelectedPathsId.add(getAdapterPosition());
                MusicFile musicFile = mSortedViewMap.get(mOpenedDirectory).get(getAdapterPosition());
                if (musicFile != null)
                    mSelectedMusicFiles.add(musicFile);
            } else {
                mSelectedPathsId.remove(getAdapterPosition());
                MusicFile musicFile = mSortedViewMap.get(mOpenedDirectory).get(getAdapterPosition());
                if (musicFile != null)
                    mSelectedMusicFiles.remove(musicFile);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            mIsCheckboxVisible = true;
            mSelectedPathsId = new ArrayList<>();
            mSelectedMusicFiles = new ArrayList<>();
            notifyItemRangeChanged(getAdapterPosition() - 10, getAdapterPosition() + 10);
            return true;
        }
    }


}