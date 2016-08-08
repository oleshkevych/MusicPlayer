package com.example.vov4ik.musicplayer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by vov4ik on 7/31/2016.
 */
public class PagerAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                AlbumsFragment albums = new AlbumsFragment();
                return albums;
            case 1:
                ArtistsFragment artists = new ArtistsFragment();
                return artists;
            case 2:
                FolderAllIncludeFragment folderAllIncludeFragment = new FolderAllIncludeFragment();
                return folderAllIncludeFragment;
            case 3:
                FolderFragment folder = new FolderFragment();
            return folder;
            case 4:
                PlaylistFragment playList = new PlaylistFragment();
                return playList;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}