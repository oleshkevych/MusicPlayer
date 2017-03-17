package com.example.vov4ik.musicplayer.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Volodymyr Oleshkevych on 2/28/2017.
 * Copyright (c) 2017, Rolique. All rights reserved.
 */
public class PlayerControls implements Parcelable{

    private boolean mIsPlaying;
    private boolean mIsShuffle;
    private String mTrackName;
    private MusicFile mMusicFile;
    private int mDuration;
    private int mCurrentTime;
    private boolean mIsImageExist;

    public PlayerControls() {
    }

    public PlayerControls(boolean isPlaying, boolean isShuffle, String trackName, int duration, int currentTime, boolean isImageExist, MusicFile musicFile) {
        mIsPlaying = isPlaying;
        mIsShuffle = isShuffle;
        mTrackName = trackName;
        mDuration = duration;
        mCurrentTime = currentTime;
        mMusicFile = musicFile;
        mIsImageExist = isImageExist;
    }

    public static final Creator<PlayerControls> CREATOR = new Creator<PlayerControls>() {
        @Override
        public PlayerControls createFromParcel(Parcel in) {
            return new PlayerControls(in);
        }

        @Override
        public PlayerControls[] newArray(int size) {
            return new PlayerControls[size];
        }
    };

    public boolean isImageExist() {
        return mIsImageExist;
    }

    public void setImageExist(boolean imageExist) {
        mIsImageExist = imageExist;
    }

    public MusicFile getMusicFile() {
        return mMusicFile;
    }

    public void setMusicFile(MusicFile musicFile) {
        mMusicFile = musicFile;
    }

    public boolean isPlaying() {
        return mIsPlaying;
    }

    public void setPlaying(boolean playing) {
        mIsPlaying = playing;
    }

    public boolean isShuffle() {
        return mIsShuffle;
    }

    public void setShuffle(boolean shuffle) {
        mIsShuffle = shuffle;
    }

    public String getTrackName() {
        return mTrackName;
    }

    public void setTrackName(String trackName) {
        this.mTrackName = trackName;
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public int getCurrentTime() {
        return mCurrentTime;
    }

    public void setCurrentTime(int currentTime) {
        mCurrentTime = currentTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTrackName);
        dest.writeInt(mCurrentTime);
        dest.writeInt(mDuration);
        dest.writeInt(mIsPlaying ? 1 : 0);
        dest.writeInt(mIsShuffle ? 1 : 0);
        dest.writeInt(mIsImageExist ? 1 : 0);
        dest.writeParcelable(mMusicFile, flags);
    }

    public PlayerControls(Parcel in) {

        mTrackName = in.readString();
        mCurrentTime = in.readInt();
        mDuration = in.readInt();
        mIsPlaying = in.readInt() == 1;
        mIsShuffle = in.readInt() == 1;
        mIsImageExist = in.readInt() == 1;
        mMusicFile = in.readParcelable(MusicFile.class.getClassLoader());
    }

    public static final class Builder {

        private boolean mIsPlaying;
        private boolean mIsShuffle;
        private String mTrackName;
        private MusicFile mMusicFile;
        private int mDuration;
        private int mCurrentTime;
        private boolean mIsImageExist;

        public Builder setPlaying(boolean playing) {
            mIsPlaying = playing;
            return this;
        }

        public Builder setImageExist(boolean imageExist) {
            mIsImageExist = imageExist;
            return this;
        }

        public Builder setShuffle(boolean shuffle) {
            mIsShuffle = shuffle;
            return this;
        }

        public Builder setTrackName(String trackName) {
            mTrackName = trackName;
            return this;
        }

        public Builder setMusicFile (MusicFile musicFile) {
            mMusicFile = musicFile;
            return this;
        }

        public Builder setDuration(int duration) {
            mDuration = duration;
            return this;
        }

        public Builder setCurrentTime(int currentTime) {
            mCurrentTime = currentTime;
            return this;
        }
        public PlayerControls create(){
            return new PlayerControls(mIsPlaying, mIsShuffle, mTrackName, mDuration, mCurrentTime, mIsImageExist, mMusicFile);
        }
    }
}
