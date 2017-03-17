package com.example.vov4ik.musicplayer.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by vov4ik on 9/24/2016.
 */
public class MusicFile extends RealmObject implements Parcelable {

    @PrimaryKey
    private int mId;

    private String mTitle;
    private String mPath;
    private String mAlbum;
    private String mArtist;
    private String mFolder;
    private String mMainFolder;
    private String mTrackNumber;

    public MusicFile(String title,
                     String path,
                     String album,
                     String artist,
                     String folder,
                     String mainFolder,
                     String trackNumber) {

        mTitle = title;
        mPath = path;
        mAlbum = album;
        mArtist = artist;
        mFolder = folder;
        mMainFolder = mainFolder;
        mTrackNumber = trackNumber;
    }

    public MusicFile(int id,
                     String title,
                     String path,
                     String album,
                     String artist,
                     String folder,
                     String mainFolder,
                     String trackNumber) {

        mId = id;
        mTitle = title;
        mPath = path;
        mAlbum = album;
        mArtist = artist;
        mFolder = folder;
        mMainFolder = mainFolder;
        mTrackNumber = trackNumber;
    }

    public MusicFile(Parcel in) {

        mId = in.readInt();
        mTitle = in.readString();
        mPath = in.readString();
        mAlbum = in.readString();
        mArtist = in.readString();
        mFolder = in.readString();
        mMainFolder = in.readString();
        mTrackNumber = in.readString();
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public void setAlbum(String album) {
        this.mAlbum = album;
    }

    public String getArtist() {
        return mArtist;
    }

    public void setArtist(String artist) {
        this.mArtist = artist;
    }

    public String getFolder() {
        return mFolder;
    }

    public void setFolder(String folder) {
        this.mFolder = folder;
    }

    public String getMainFolder() {
        return mMainFolder;
    }

    public void setMainFolder(String mainFolder) {
        this.mMainFolder = mainFolder;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        this.mPath = path;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getTrackNumber() {
        return mTrackNumber;
    }

    public void setTrackNumber(String trackNumber) {
        this.mTrackNumber = trackNumber;
    }

    public MusicFile() {
    }

    @Override
    public String toString() {
        return "MusicFile{" +
                "mId=" + mId +
                ", mTitle='" + mTitle + '\'' +
                ", mPath='" + mPath + '\'' +
                ", mAlbum='" + mAlbum + '\'' +
                ", mArtist='" + mArtist + '\'' +
                ", mFolder='" + mFolder + '\'' +
                ", mMainFolder='" + mMainFolder + '\'' +
                ", mTrackNumber='" + mTrackNumber + '\'' +
                '}';
    }

    public static final Creator<MusicFile> CREATOR = new Creator<MusicFile>() {
        @Override
        public MusicFile createFromParcel(Parcel in) {
            return new MusicFile(in);
        }

        @Override
        public MusicFile[] newArray(int size) {
            return new MusicFile[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mTitle);
        dest.writeString(mPath);
        dest.writeString(mAlbum);
        dest.writeString(mArtist);
        dest.writeString(mFolder);
        dest.writeString(mMainFolder);
        dest.writeString(mTrackNumber);
    }

    public static final class Builder {

        private int mId;
        private String mTitle;
        private String mPath;
        private String mAlbum;
        private String mArtist;
        private String mFolder;
        private String mMainFolder;
        private String mTrackNumber;

        public Builder setId(int id) {
            mId = id;
            return this;
        }

        public Builder setTitle(String title) {
            mTitle = title;
            return this;
        }

        public Builder setPath(String path) {
            mPath = path;
            return this;
        }

        public Builder setAlbum(String album) {
            mAlbum = album;
            return this;
        }

        public Builder setArtist(String artist) {
            mArtist = artist;
            return this;
        }

        public Builder setFolder(String folder) {
            mFolder = folder;
            return this;
        }

        public Builder setMainFolder(String mainFolder) {
            mMainFolder = mainFolder;
            return this;
        }

        public Builder setTrackNumber(String trackNumber) {
            mTrackNumber = trackNumber;
            return this;
        }

        public MusicFile create() {
            return new MusicFile(mId, mTitle, mPath, mAlbum, mArtist, mFolder, mMainFolder, mTrackNumber);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MusicFile musicFile = (MusicFile) o;

        if (mId != musicFile.mId) return false;
        if (mTitle == null || !mTitle.equals(musicFile.mTitle)) return false;
        if (mPath == null || !mPath.equals(musicFile.mPath)) return false;
        if (mAlbum != null ? !mAlbum.equals(musicFile.mAlbum) : musicFile.mAlbum != null)
            return false;
        if (mArtist != null ? !mArtist.equals(musicFile.mArtist) : musicFile.mArtist != null)
            return false;
        if (mFolder != null ? !mFolder.equals(musicFile.mFolder) : musicFile.mFolder != null)
            return false;
        return mMainFolder.equals(musicFile.mMainFolder);

    }

    @Override
    public int hashCode() {
        int result = mId;
        result = 31 * result + mTitle.hashCode();
        result = 31 * result + mPath.hashCode();
        result = 31 * result + (mAlbum != null ? mAlbum.hashCode() : 0);
        result = 31 * result + (mArtist != null ? mArtist.hashCode() : 0);
        result = 31 * result + (mFolder != null ? mFolder.hashCode() : 0);
        result = 31 * result + mMainFolder.hashCode();
        return result;
    }
}
