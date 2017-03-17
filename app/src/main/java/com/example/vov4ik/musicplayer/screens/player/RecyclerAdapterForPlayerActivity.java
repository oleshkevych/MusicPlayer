package com.example.vov4ik.musicplayer.screens.player;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.vov4ik.musicplayer.data.model.MusicFile;
import com.example.vov4ik.musicplayer.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vov4ik on 9/12/2016.
 */
class RecyclerAdapterForPlayerActivity extends RecyclerView.Adapter<RecyclerAdapterForPlayerActivity.ViewHolder> {


    private List<MusicFile> mMusicFiles;
    private List<MusicFile> mSelectedMusicFiles;
    private boolean mIsCheckboxVisible;

    private LayoutInflater mInflater;
    private boolean mIsNoBackground;
    private MusicFile mPlayingTrack;
    private boolean mIsPlayingNow;


    interface OnClickListener {

        void onMusicClick(MusicFile clickedMusicFile);

        void onLongClick(MusicFile selectedFile);
    }

    private OnClickListener mOnClickListener;


    RecyclerAdapterForPlayerActivity(Context context) {
        mInflater = LayoutInflater.from(context);
        mMusicFiles = new ArrayList<>();
        mSelectedMusicFiles = new ArrayList<>();
    }

    void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    List<MusicFile> getSelectedMusicFiles() {
        return mSelectedMusicFiles;
    }

    MusicFile getPlayingTrack() {
        return mPlayingTrack;
    }

    boolean isCheckboxVisible() {
        return mIsCheckboxVisible;
    }

    void setPlayingFile(MusicFile musicFile, boolean isPlayingNow, boolean isNoBackground) {
        if (mPlayingTrack == null || isPlayingNow != mIsPlayingNow) {
            mIsPlayingNow = isPlayingNow;
            mPlayingTrack = musicFile;
            notifyItemChanged(mMusicFiles.indexOf(mPlayingTrack));
            return;
        }
        if (!musicFile.equals(mPlayingTrack)) {
            mPlayingTrack = musicFile;
        }
        if (isNoBackground != mIsNoBackground) {
            mIsNoBackground = isNoBackground;
        }
        notifyDataSetChanged();
    }

//    void stopPlayingFile(){
//        mIsNoBackground = false;
//        mIsPlayingNow = false;
//        notifyDataSetChanged();
//    }

    void setUpAdapter(List<MusicFile> musicFiles) {
        mIsCheckboxVisible = false;
        mSelectedMusicFiles = new ArrayList<>();
        mMusicFiles = musicFiles;
        notifyDataSetChanged();
    }

    void setSelectedVisible(MusicFile selected) {
        mIsCheckboxVisible = true;
        mSelectedMusicFiles = new ArrayList<>();
        mSelectedMusicFiles.add(selected);
        notifyDataSetChanged();
    }

    void setSelectedAll() {
        mSelectedMusicFiles = new ArrayList<>();
        mIsCheckboxVisible = true;
        for (MusicFile musicFile : mMusicFiles)
            mSelectedMusicFiles.add(musicFile);
        notifyDataSetChanged();
    }

    void unselect() {
        mIsCheckboxVisible = false;
        mSelectedMusicFiles.clear();
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerAdapterForPlayerActivity.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflater
                .inflate(R.layout.item_music_file, parent, false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        try {
            holder.names.setText(mMusicFiles.get(position).getTitle());
            holder.path.setText((new File(mMusicFiles.get(position).getPath())).getParentFile().getPath());
            if (mIsNoBackground) {

                holder.mView.setBackground(holder.mView.getContext().getResources().getDrawable(R.drawable.background_if_this_is_present));
            }
            if (mIsPlayingNow && mMusicFiles.get(position).equals(mPlayingTrack)) {
                holder.mView.setBackground(holder.mView.getContext().getResources().getDrawable(R.drawable.playing_background));
            } else {
                holder.mView.setBackground(null);
            }
            if (mIsCheckboxVisible) {
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.checkBox.setOnCheckedChangeListener(null);
                holder.checkBox.setChecked(mSelectedMusicFiles.contains(mMusicFiles.get(position)));
                holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        holder.onClick(buttonView, isChecked);
                    }
                });
            } else {
                holder.checkBox.setVisibility(View.GONE);
                holder.mView.setOnLongClickListener(holder);
            }
        } catch (Exception e) {
            Log.e("Holder exception ", e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return mMusicFiles.size();
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
        }

        @Override
        public void onClick(View v) {
            if (mIsCheckboxVisible) {
                if (mSelectedMusicFiles.contains(mMusicFiles.get(getAdapterPosition()))) {
                    mSelectedMusicFiles.remove(mMusicFiles.get(getAdapterPosition()));
                    checkBox.setChecked(false);
                } else {
                    mSelectedMusicFiles.add(mMusicFiles.get(getAdapterPosition()));

                    checkBox.setChecked(true);
                }
            } else {
                mOnClickListener.onMusicClick(mMusicFiles.get(getAdapterPosition()));
                Log.e("Test", "Click " + mMusicFiles.get(getAdapterPosition()).getTitle());

            }
        }

        public void onClick(View v, boolean isChecked) {
            if (isChecked) {
                mSelectedMusicFiles.add(mMusicFiles.get(getAdapterPosition()));
            } else {
                mSelectedMusicFiles.remove(mMusicFiles.get(getAdapterPosition()));
            }
        }

        @Override
        public boolean onLongClick(View v) {
            mOnClickListener.onLongClick(mMusicFiles.get(getAdapterPosition()));
            return true;
        }
    }
}
