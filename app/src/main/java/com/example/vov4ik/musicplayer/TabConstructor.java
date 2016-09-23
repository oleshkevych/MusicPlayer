package com.example.vov4ik.musicplayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by vov4ik on 9/23/2016.
 */
public class TabConstructor {
    private final static List<String> listOfTabs = new ArrayList<>(Arrays.asList(new String[]{"Album","All Songs", "Artist",  "Folder(All Content)", "Folder","Playlist"}));
    private String name;
    private boolean visibility;

    public String getName() {
        return name;
    }

    public static List<String> getListOfTabs() {
        return listOfTabs;
    }

    public TabConstructor(String name, boolean visibility) {
        this.name = name;
        this.visibility = visibility;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TabConstructor() {
    }

    @Override
    public String toString() {
        return "TabConstructor{" +
                "name='" + name + '\'' +
                ", visibility=" + visibility +
                '}';
    }
}
