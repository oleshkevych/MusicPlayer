package com.example.vov4ik.musicplayer;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vov4ik on 7/31/2016.
 */
public class PagerAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;
    List<TabConstructor> list;
    Map<String, Fragment> fragments = new LinkedHashMap<>();
    List<Fragment> f;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, Context context) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.list = DbConnector.getAllTabs(context);
        fragments.put("Album",new AlbumsFragment());
        fragments.put("All Songs", new AllSongsFragment());
        fragments.put("Artist", new ArtistsFragment());
        fragments.put("Folder(All Content)",new FolderAllIncludeFragment());
        fragments.put("Folder", new FolderFragment());
        fragments.put("Playlist", new PlaylistFragment());
        for(TabConstructor t: list){
            if(!t.isVisibility()){
                fragments.remove(t.getName());
            }
        }
        f = new ArrayList<>(fragments.values());
        if(f.size()==0){
            f.add(fragments.get("Folder(All Content)"));
        }
    }

    @Override
    public Fragment getItem(int position) {


        return f.get(position);

//        switch (position) {
//            case 0:
//                if(list.get(0).isVisibility()){
//                    return  new AlbumsFragment();
//                }
//            case 1:
//                if(list.get(1).isVisibility()){
//                    return  new AllSongsFragment();
//                }
//            case 2:
//                if(list.get(2).isVisibility()){
//                    return  new ArtistsFragment();
//                }
//            case 3:
//                if(list.get(3).isVisibility()){
//                    return  new FolderAllIncludeFragment();
//                }
//            case 4:
//                if(list.get(4).isVisibility()){
//                    return  new FolderFragment();
//                }
//            case 5:
//                if(list.get(5).isVisibility()){
//                    return  new PlaylistFragment();
//                }
//            default:
//                return new FolderAllIncludeFragment();
//        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}