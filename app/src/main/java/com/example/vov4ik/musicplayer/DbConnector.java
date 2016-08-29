package com.example.vov4ik.musicplayer;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by vov4ik on 8/3/2016.
 */
public class DbConnector {

    private List<String[]> path = new ArrayList<String[]>();
    private List<String[]> musicFiles = new ArrayList<>();

    private List<String> folders = new ArrayList<String>();
    private List<String> albums = new ArrayList<String>();
    private List<String> artists = new ArrayList<String>();
    private List<String> mainFolders = new ArrayList<String>();

    private List<List<String>> pathAlbums = new ArrayList<>();
    private List<List<String>> pathArtist = new ArrayList<>();
    private List<List<String>> pathForMainFolders = new ArrayList<>();


    private List<String> mainPath = new ArrayList<>();
    private String stringPath;

    public String getStringPath() {
        return stringPath;
    }

    public void setStringPath(String stringPath) {
        this.stringPath = stringPath;
    }

    public void fillerForDb(Context context){
        String dirPath = Environment.getExternalStorageDirectory().getParentFile().getParent();

        Log.d("Test", "START");
        File f = new File(dirPath);
        File[] files = f.listFiles();
        folderMethod(files);
        Log.d("Test", "ALL INCLUDE");
        folderAllIncludeMethod();
        Log.d("Test", "ALBUM");
        albumMethod();
        Log.d("Test", "ARTIST");
        artistMethod();

        Log.d("Test", "DELETE");
        DbConnector.deleteDb(context);
        Log.d("Test", "FILL");
        new DbHelper(context).filler(folders, musicFiles, path, albums, pathAlbums, artists, pathArtist, mainFolders, pathForMainFolders);
        Log.d("Test", "FINISH");
    }

    private void folderMethod(File[] files){
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        if (files!=null)
        for(int i=0; i < files.length; i++){
            File file = files[i];
            if(file.isDirectory()){
                folderMethod(file.listFiles());
            } else {
                if ((FileChecker.checker(file))&&(!folders.contains((new File(file.getParent()).getName())))) {
                    folders.add((new File(file.getParent()).getName()));
                    List<String> s = new ArrayList<>();
                    List<String> sPath = new ArrayList<>();
                    s.add("..goToRoot");
                    sPath.add(file.getParentFile().getPath());
                    for (File names : new File(file.getParent()).listFiles()) {
                        if(!names.isDirectory()&&(FileChecker.checker(names))) {
                            mmr.setDataSource(names.getPath());
                            String title = (mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
                            if ((title==null)||(title.equals(""))) {
                                s.add(names.getName());
                            }else{
                                s.add(title);
                            }
                            sPath.add(names.getPath());
                        }

                    }
                    String[] strings = new String[s.size()];
                    String[] strings1 = new String[sPath.size()];
                    path.add(sPath.toArray(strings1));
                    musicFiles.add(s.toArray(strings));
                }
            }
        }
    }

    private void albumMethod(){
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        for(String[] pArray: path) {
            for(String p: pArray) {
                if(!(new File(p).isDirectory())){
                    mmr.setDataSource(p);
                    String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                    if (album == null||album.equals("")||album.equals(" ")){
                        album = "no album";
                    }
                    if (!albums.contains(album)) {
                        albums.add(album);
                        List<String> sPath = new ArrayList<>();
                        sPath.add(p);
                        pathAlbums.add(sPath);
                    } else {
                        int number = albums.indexOf(album);
                        pathAlbums.get(number).add(p);
                    }
                }
            }
        }
        for(int i = 0; i < pathAlbums.size(); i++) {
            for(String s:pathAlbums.get(i)){
                if(new File(s).isDirectory()){
                    pathAlbums.remove(i);
                }
            }
            if (pathAlbums.get(i).size() < 2) {
                if (!albums.contains("OneSongAlbums")) {
                    albums.add("OneSongAlbums");
                    pathAlbums.add(pathAlbums.get(i));
                    pathAlbums.remove(i);
                    albums.remove(i);
                    i--;
                } else {
                    pathAlbums.get(albums.indexOf("OneSongAlbums")).add(pathAlbums.get(i).get(0));
                    pathAlbums.remove(i);
                    albums.remove(i);
                    i--;
                }
            }
        }
        List<String> albums1 = new ArrayList<String>();
        List<List<String>> pathAlbums1 = new ArrayList<>();
        for(int i = 0; i < albums.size(); i++){
            albums1.add(albums.get(i));
            pathAlbums1.add(pathAlbums.get(i));
        }
        Collections.sort(albums);
        pathAlbums.removeAll(pathAlbums1);

        for(int i = 0; i < albums.size(); i++){
            int index = albums1.indexOf(albums.get(i));
            pathAlbums.add(pathAlbums1.get(index));
        }
    }
    private void artistMethod(){
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        for(String[] pArray: path) {
            for(String p: pArray) {
                if(!(new File(p).isDirectory())) {
                    mmr.setDataSource(p);
                    String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                    if (artist == null||artist.equals("")||artist.equals(" ")){
                        artist = "no artist";
                    }
                    if (!artists.contains(artist)) {
                        artists.add(artist);
                        List<String> sPath = new ArrayList<>();
                        sPath.add(p);
                        pathArtist.add(sPath);
                    } else {
                        int number = artists.indexOf(artist);
                        pathArtist.get(number).add(p);
                    }
                }
            }
        }
        for(int i = 0; i < pathArtist.size(); i++){
            for(String s:pathArtist.get(i)){
                if(new File(s).isDirectory()){
                    pathArtist.remove(i);
                }
            }
            if(pathArtist.get(i).size()<2){
                if(!artists.contains("OneSongAlbums")){
                    artists.add("OneSongAlbums");
                    pathArtist.add(pathArtist.get(i));
                    pathArtist.remove(i);
                    artists.remove(i);
                    i--;
                } else {
                    pathArtist.get(artists.indexOf("OneSongAlbums")).add(pathArtist.get(i).get(0));
                    pathArtist.remove(i);
                    artists.remove(i);
                    i--;
                }
            }
        }
        List<String> artists1 = new ArrayList<String>();
        List<List<String>> pathArtist1 = new ArrayList<>();
        for(int i = 0; i < artists.size(); i++){
            artists1.add(artists.get(i));
            pathArtist1.add(pathArtist.get(i));
        }
        Collections.sort(artists);
        pathArtist.removeAll(pathArtist1);
        for(int i = 0; i < artists.size(); i++){
            int index = artists1.indexOf(artists.get(i));
            pathArtist.add(pathArtist1.get(index));
        }
    }
    private void folderAllIncludeMethod(){

        File parentFileForAll = new File(Environment.getExternalStorageDirectory().getParentFile().getParent());
        for(File f: parentFileForAll.listFiles()){
            mainPath.add(f.getPath());
        }

        for(String[] pathArray: path){
            for(String s: pathArray){
                setStringPath(s);
                File file = new File(s);
                if (!file.isDirectory()){
                    getParent(s);
                }
            }
        }
    }
    private void getParent(String s){
        File f = new File(s);
        if(!mainPath.contains(f.getParentFile().getPath())) {
            if (mainPath.contains(f.getParentFile().getParentFile().getPath())) {
                if(f.isDirectory()) {
                    if (!mainFolders.contains(f.getName())) {
                        mainFolders.add(f.getName());

                        List<String> sPath = new ArrayList<>();
                        sPath.add(getStringPath());
                        pathForMainFolders.add(sPath);
                    } else {
                        int number = mainFolders.indexOf(f.getName());
                        pathForMainFolders.get(number).add(getStringPath());
                    }
                }else{
                    if (!mainFolders.contains("RootFiles")) {
                        mainFolders.add("RootFiles");
                        List<String> sPath = new ArrayList<>();
                        sPath.add(getStringPath());
                        pathForMainFolders.add(sPath);
                    } else {
                        int number = mainFolders.indexOf("RootFiles");
                        pathForMainFolders.get(number).add(getStringPath());
                    }
                }
            } else {
                getParent(f.getParentFile().getPath());
            }
        }
    }
    public static List<String> getFoldersFromDb(Context context){
        return new DbHelper(context).getFolders();
    }

    public static List<String[]> getFilesNamesFromDb(Context context){
        return new DbHelper(context).getFiles();
    }

    public static List<String[]> getPathsFromDb(Context context){
        return new DbHelper(context).getPaths();
    }
    public static List<String> getAlbumFromDb(Context context){
        return new DbHelper(context).getAlbums();
    }

    public static List<String[]> getAlbumPathsFromDb(Context context){
        return new DbHelper(context).getPathAlbums();
    }
    public static List<String[]> getAlbumNamesFromDb(Context context){
        return new DbHelper(context).getNameAlbums();
    }
    public static List<String> getArtistFromDb(Context context){
        return new DbHelper(context).getArtist();
    }

    public static List<String[]> getArtistPathsFromDb(Context context){
        return new DbHelper(context).getPathArtist();
    }
    public static List<String[]> getArtistNamesFromDb(Context context){
        return new DbHelper(context).getNameArtist();
    }
    public static List<String> getMainFoldersFromDb(Context context){
        return new DbHelper(context).getMainFolders();
    }

    public static List<String[]> getMainFoldersPathsFromDb(Context context){
        return new DbHelper(context).getPathMainFolders();
    }
    public static List<String[]> getMainFoldersNamesFromDb(Context context){
        return new DbHelper(context).getNameMainFolders();
    }
    public static void deleteDb(Context context){
        new DbHelper(context).delete();
    }
    public static List<String> getLastPlayList(Context context){
        return new DbHelper(context).getLastPlayList();
    }
    public static int getLastPlayTime(Context context){
        return new DbHelper(context).getLastPlayedTime();
    }
    public static void setLastPlayListAndTime(Context context, List<String> list, int time){
        new DbHelper(context).setLastPlayListAndTime(list, time);
        Log.d("test", "Load");
    }

}
///////////////////////////////////For simple file manager
/*
   boolean isSDPresent = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);

        if (isSDPresent) {
            File file[] = Environment.getExternalStorageDirectory().listFiles();
            if (file != null) {
                for (int i = 0; i < file.length; i++) {
                    Log.d("Test", "SSS " + file[i].getAbsolutePath());
                }
                files = file;
            }
        }
        root = Environment.getExternalStorageDirectory().getParentFile().getParent();

        getDir(root);


    private void getDir(String dirPath){
        item = new ArrayList<String>();
        path = new ArrayList<String>();
        File f = new File(dirPath);
        File[] files = f.listFiles();

        if(!dirPath.equals(root))
        {
            item.add(root);
            path.add(root);
            item.add("../");
            path.add(f.getParent());
        }

        for(int i=0; i < files.length; i++)
        {
            File file = files[i];

            if(!file.isHidden() && file.canRead()){
                path.add(file.getPath());
                if(file.isDirectory()){
                    item.add(file.getName() + "/");
                }else{
                    item.add(file.getName());
                }
            }
        }
        if(rootView!=null) {
            LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.layoutArtist);
            for (String s : item) {
                TextView text = new TextView(linearLayout.getContext());
                text.setText(String.valueOf(s));
                text.setId((item).indexOf(s));
                linearLayout.addView(text);
                text.setOnClickListener(this);
                text.setPadding(20, 10, 20, 10);
                text.setTextSize(16);
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) text.getLayoutParams();
                mlp.setMargins(0, 15, 0, 15);
            }
        }
    }


    @Override
    public void onClick(View v) {
        Log.d("Test", "SSS " + path.get(v.getId()));
        File file = new File(path.get(v.getId()));

        if (file.isDirectory())
        {
            if(file.canRead()){
                LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.layoutArtist);
                linearLayout.removeAllViews();
                getDir(path.get(v.getId()));
            }else{
                new AlertDialog.Builder(getContext())
                        .setIcon(R.color.black_overlay)
                        .setTitle("[" + file.getName() + "] folder can't be read!")
                        .setPositiveButton("OK", null).show();
            }
        }else {
            new AlertDialog.Builder(getContext())
                    .setIcon(R.color.black_overlay)
                    .setTitle("[" + file.getName() + "]")
                    .setPositiveButton("OK", null).show();

        }


    }

 */