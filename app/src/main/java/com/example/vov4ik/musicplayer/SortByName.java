package com.example.vov4ik.musicplayer;

import java.util.Comparator;

/**
 * Created by vov4ik on 10/2/2016.
 */
public class SortByName implements Comparator<MusicFile> {
    @Override
    public int compare(MusicFile o1, MusicFile o2) {
        return o1.getTitle().compareToIgnoreCase(o2.getTitle());
    }

}
