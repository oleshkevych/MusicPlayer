package com.example.vov4ik.musicplayer;

import android.content.Context;
import android.database.CursorIndexOutOfBoundsException;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by vov4ik on 8/3/2016.
 */
public class DbConnector {

//    private List<String[]> path = new ArrayList<String[]>();
//    private List<String[]> musicFiles = new ArrayList<>();
//
//    private List<String> folders = new ArrayList<String>();
//    private List<String> albums = new ArrayList<String>();
//    private List<String> artists = new ArrayList<String>();
//    private List<String> mainFolders = new ArrayList<String>();
//
//    private List<List<String>> pathAlbums = new ArrayList<>();
//    private List<List<String>> pathArtist = new ArrayList<>();
//    private List<List<String>> pathForMainFolders = new ArrayList<>();
//    private int folderCounter = 0;
//    private File[] startPaths;
//    private List<String> mainPath = new ArrayList<>();
//    private String stringPath;

    private List<MusicFile> listMusicFiles = new ArrayList<>();
    private List<MusicFile> listMusicFilesExisted = new ArrayList<>();
    private List<String> pathsExisted = new ArrayList<>();


//    public String getStringPath() {
//        return stringPath;
//    }
//
//    public void setStringPath(String stringPath) {
//        this.stringPath = stringPath;
//    }

    public void fillerForDb(Context context){

        String dirPath = Environment.getRootDirectory().getParent();
//        Log.d("Test", "START1 "+dirPath);
//        Log.d("Test", "START2" + Environment.getExternalStorageDirectory().getParentFile().getParent());
//        Log.d("Test", "START3" + Environment.getDataDirectory());
//        Log.d("Test", "START4" + Environment.getDownloadCacheDirectory());
//        Log.d("Test", "START5" + Environment.getRootDirectory());
        try {
            listMusicFilesExisted = getMusicFilesForSearch(context);
            for (MusicFile m : listMusicFilesExisted) {
                pathsExisted.add(m.getPath());
            }
        }catch(CursorIndexOutOfBoundsException c){
            Log.d("Error", c.getMessage());
        }
        Log.d("Test", "START");
        File f = new File(dirPath);
        File[] startPaths = f.listFiles();
        folderMethod(startPaths, 0);
        Log.d("Test", "ALL INCLUDE");
        folderAllIncludeMethod();
        Log.d("Test", "ALBUM");
//        albumMethod();
        Log.d("Test", "ARTIST");
//        artistMethod();

        Log.d("Test", "DELETE");
        DbConnector.deleteDb(context);
        Log.d("Test", "FILL");
//        new DbHelper(context).filler(folders, musicFiles, path, albums, pathAlbums, artists, pathArtist, mainFolders, pathForMainFolders);
        new  DbHelper(context).musicFileFiller(listMusicFiles);
        Log.d("Test", "FINISH"+ listMusicFiles.size());
//        Intent intent = new Intent(context, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        context.startActivity(intent);
    }

//    private void folderMethod(File[] files){
//        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//        if (files!=null)
//        for(int i=0; i < files.length; i++){
//            File file = files[i];
//            if(file.isDirectory()){
//                folderCounter++;
//                if(folderCounter<15) {
//                    folderMethod(file.listFiles());
//                    Log.d("Test", "folderMethod " + file.getPath());
//                }else{
//                    folderCounter = 0;
//                }
//            } else {
//                if ((FileChecker.checker(file))&&(!folders.contains((new File(file.getParent()).getName())))) {
//                    folders.add((new File(file.getParent()).getName()));
//                    List<String> s = new ArrayList<>();
//                    List<String> sPath = new ArrayList<>();
//                    s.add("..goToRoot");
//                    sPath.add(file.getParentFile().getPath());
//                    for (File names : new File(file.getParent()).listFiles()) {
//                        if(!names.isDirectory()&&(FileChecker.checker(names))) {
//                            folderCounter = 0;
//                            mmr.setDataSource(names.getPath());
//                            String title = (mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
//                            if ((title==null)||(title.equals(""))) {
//                                s.add(names.getName());
//                            }else{
//                                s.add(title);
//                            }
//                            sPath.add(names.getPath());
//                        }
//
//                    }
//                    String[] strings = new String[s.size()];
//                    String[] strings1 = new String[sPath.size()];
//                    path.add(sPath.toArray(strings1));
//                    musicFiles.add(s.toArray(strings));
//                }
//            }
//        }
//        mmr.release();
//        List<String> folders1 = new ArrayList<String>();
//        List<String[]> path1 = new ArrayList<>();
//        List<String[]> musicFiles1 = new ArrayList<>();
//        for(int i = 0; i < folders.size(); i++){
//            folders1.add(folders.get(i));
//            path1.add(path.get(i));
//            musicFiles1.add(musicFiles.get(i));
//        }
//        Collections.sort(folders);
//        path.removeAll(path1);
//        musicFiles.removeAll(musicFiles1);
//
//        for(int i = 0; i < folders.size(); i++){
//            int index = folders1.indexOf(folders.get(i));
//            path.add(path1.get(index));
//            musicFiles.add(musicFiles1.get(index));
//        }
//    }
private void folderMethod(File[] files, int counter){
    if (files != null)
        for(int i=0; i < files.length; i++) {
//            int folderCounter = 0;
            File file = files[i];

            if (!((file.isDirectory())&&(file.getName().equals("sys") || (file.getName().equals("system"))||(file.getName().equals("proc"))||file.getName().equals("mnt")||(file.getName().equals("d"))))){
                if (!file.isHidden()) {
                    if (file.isDirectory()) {
//                        folderCounter++;

                        if (counter < 10) {
                            Log.d("Test", "folderMethod " + file.getPath());
                            Log.d("Test", "folderMethod " + counter);
                            folderMethod(file.listFiles(), counter++);
                        } else {
//                            folderCounter = 0;
                            counter = 0;

                        }
                    } else {
//                        Log.d("Test", "folderMethod " + file.getPath());
                        if (FileChecker.checker(file)) {
                            MusicFile musicFile = new MusicFile();
                            counter = 0;
                            if(pathsExisted.contains(file.getPath())){
                                musicFile = listMusicFilesExisted.get(pathsExisted.indexOf(file.getPath()));
                            }else {
                                musicFile.setFolder((new File(file.getParent()).getName()));
                                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                                mmr.setDataSource(file.getPath());
                                String title = (mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
                                musicFile.setArtist(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
                                musicFile.setAlbum(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
                                mmr.release();
                                if ((title == null) || (title.equals(""))) {
                                    musicFile.setTitle(file.getName());
                                } else {
                                    musicFile.setTitle(title);
                                }
                                musicFile.setPath(file.getPath());
                            }
                            listMusicFiles.add(musicFile);
//                            Log.d("MMM", musicFile.toString());
                        }
                    }
                }
            }
        }
    }
    private void folderAllIncludeMethod(){
        List<String> mainPath = new ArrayList<>();
        File parentFileForAll = new File(Environment.getRootDirectory().getParent());//new File(Environment.getExternalStorageDirectory().getParentFile().getParent());
        for(File f: parentFileForAll.listFiles()){
            mainPath.add(f.getPath());
        }
//        List<String> mainPath1 = new ArrayList<>();
//        File p1 =
//        for(File f: p1.listFiles()){
//            mainPath1.add(f.getPath());
//        }
//        Log.d("Test1", mainPath.toString());
//        Log.d("Test2", mainPath1.toString());

        for(int i = 0; i<listMusicFiles.size(); i++){
                File file = new File(listMusicFiles.get(i).getPath());
                if (!file.isDirectory()){
                    getParent(listMusicFiles.get(i).getPath(), i, mainPath);
                }

        }
    }

    private void getParent(String s, int i, List<String> mainPath){
        File f = new File(s);
        if(!mainPath.contains(f.getParentFile().getParentFile().getPath())) {
            if (mainPath.contains(f.getParentFile().getParentFile().getParentFile().getPath())) {
                if(f.isDirectory()) {
                    listMusicFiles.get(i).setMainFolder(f.getName());
                }else{
                    listMusicFiles.get(i).setMainFolder("RootFiles");
                }
            } else {
                getParent(f.getParentFile().getPath(), i, mainPath);
            }
        }
    }
//    private void albumMethod(){
//        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//        for(String[] pArray: path) {
//            for(String p: pArray) {
//                if(!(new File(p).isDirectory())){
//                    mmr.setDataSource(p);
//                    String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
//                    if (album == null||album.equals("")||album.equals(" ")){
//                        album = "no album";
//                    }
//                    if (!albums.contains(album)) {
//                        albums.add(album);
//                        List<String> sPath = new ArrayList<>();
//                        sPath.add(p);
//                        pathAlbums.add(sPath);
//                    } else {
//                        int number = albums.indexOf(album);
//                        pathAlbums.get(number).add(p);
//                    }
//                }
//            }
//        }
//        for(int i = 0; i < pathAlbums.size(); i++) {
//            for(String s:pathAlbums.get(i)){
//                if(new File(s).isDirectory()){
//                    pathAlbums.remove(i);
//                }
//            }
//            if (pathAlbums.get(i).size() < 2) {
//                if (!albums.contains("OneSongAlbums")) {
//                    albums.add("OneSongAlbums");
//                    pathAlbums.add(pathAlbums.get(i));
//                    pathAlbums.remove(i);
//                    albums.remove(i);
//                    i--;
//                } else {
//                    pathAlbums.get(albums.indexOf("OneSongAlbums")).add(pathAlbums.get(i).get(0));
//                    pathAlbums.remove(i);
//                    albums.remove(i);
//                    i--;
//                }
//            }
//        }
//        List<String> albums1 = new ArrayList<String>();
//        List<List<String>> pathAlbums1 = new ArrayList<>();
//        for(int i = 0; i < albums.size(); i++){
//            albums1.add(albums.get(i));
//            pathAlbums1.add(pathAlbums.get(i));
//        }
//        Collections.sort(albums);
//        pathAlbums.removeAll(pathAlbums1);
//
//        for(int i = 0; i < albums.size(); i++){
//            int index = albums1.indexOf(albums.get(i));
//            pathAlbums.add(pathAlbums1.get(index));
//        }
//    }
//    private void artistMethod(){
//        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//        for(String[] pArray: path) {
//            for(String p: pArray) {
//                if(!(new File(p).isDirectory())) {
//                    mmr.setDataSource(p);
//                    String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
//                    if (artist == null||artist.equals("")||artist.equals(" ")){
//                        artist = "no artist";
//                    }
//                    if (!artists.contains(artist)) {
//                        artists.add(artist);
//                        List<String> sPath = new ArrayList<>();
//                        sPath.add(p);
//                        pathArtist.add(sPath);
//                    } else {
//                        int number = artists.indexOf(artist);
//                        pathArtist.get(number).add(p);
//                    }
//                }
//            }
//        }
//        for(int i = 0; i < pathArtist.size(); i++){
//            for(String s:pathArtist.get(i)){
//                if(new File(s).isDirectory()){
//                    pathArtist.remove(i);
//                }
//            }
//            if(pathArtist.get(i).size()<2){
//                if(!artists.contains("OneSongAlbums")){
//                    artists.add("OneSongAlbums");
//                    pathArtist.add(pathArtist.get(i));
//                    pathArtist.remove(i);
//                    artists.remove(i);
//                    i--;
//                } else {
//                    pathArtist.get(artists.indexOf("OneSongAlbums")).add(pathArtist.get(i).get(0));
//                    pathArtist.remove(i);
//                    artists.remove(i);
//                    i--;
//                }
//            }
//        }
//        List<String> artists1 = new ArrayList<String>();
//        List<List<String>> pathArtist1 = new ArrayList<>();
//        for(int i = 0; i < artists.size(); i++){
//            artists1.add(artists.get(i));
//            pathArtist1.add(pathArtist.get(i));
//        }
//        Collections.sort(artists);
//        pathArtist.removeAll(pathArtist1);
//        for(int i = 0; i < artists.size(); i++){
//            int index = artists1.indexOf(artists.get(i));
//            pathArtist.add(pathArtist1.get(index));
//        }
//    }
//    private void folderAllIncludeMethod(){
//
//        File parentFileForAll = new File(Environment.getExternalStorageDirectory().getParentFile().getParent());
//        for(File f: parentFileForAll.listFiles()){
//            mainPath.add(f.getPath());
//        }
//
//        for(String[] pathArray: path){
//            for(String s: pathArray){
//                setStringPath(s);
//                File file = new File(s);
//                if (!file.isDirectory()){
//                    getParent(s);
//                }
//            }
//        }
//        List<String> mainFolders1 = new ArrayList<String>();
//        List<List<String>> pathForMainFolders1 = new ArrayList<>();
//        for(int i = 0; i < mainFolders.size(); i++){
//            mainFolders1.add(mainFolders.get(i));
//            pathForMainFolders1.add(pathForMainFolders.get(i));
//        }
//        Collections.sort(mainFolders);
//        pathForMainFolders.removeAll(pathForMainFolders1);
//
//        for(int i = 0; i < mainFolders.size(); i++){
//            int index = mainFolders1.indexOf(mainFolders.get(i));
//            pathForMainFolders.add(pathForMainFolders1.get(index));
//        }
//    }
//    private void getParent(String s){
//        File f = new File(s);
//        if(!mainPath.contains(f.getParentFile().getPath())) {
//            if (mainPath.contains(f.getParentFile().getParentFile().getPath())) {
//                if(f.isDirectory()) {
//                    if (!mainFolders.contains(f.getName())) {
//                        mainFolders.add(f.getName());
//
//                        List<String> sPath = new ArrayList<>();
//                        sPath.add(getStringPath());
//                        pathForMainFolders.add(sPath);
//                    } else {
//                        int number = mainFolders.indexOf(f.getName());
//                        pathForMainFolders.get(number).add(getStringPath());
//                    }
//                }else{
//                    if (!mainFolders.contains("RootFiles")) {
//                        mainFolders.add("RootFiles");
//                        List<String> sPath = new ArrayList<>();
//                        sPath.add(getStringPath());
//                        pathForMainFolders.add(sPath);
//                    } else {
//                        int number = mainFolders.indexOf("RootFiles");
//                        pathForMainFolders.get(number).add(getStringPath());
//                    }
//                }
//            } else {
//                getParent(f.getParentFile().getPath());
//            }
//        }
//    }
    public static List<String> getFoldersFromDb(Context context){
        return new DbHelper(context).getFolders();
    }

    public static List<List<String>> getFilesNamesForFolders(Context context){
        return new DbHelper(context).getFilesForFolder();
    }

    public static List<List<String>> getPathsForFolders(Context context){
        return new DbHelper(context).getPathsForFolder();
    }
    public static List<String> getAlbumFromDb(Context context){
        return new DbHelper(context).getAlbums();
    }

    public static List<List<String>> getPathsForAlbums(Context context){
        return new DbHelper(context).getPathsForAlbums();
    }
    public static List<List<String>> getFileNamesForAlbums(Context context){
        return new DbHelper(context).getFilesForAlbums();
    }
    public static List<String> getArtistFromDb(Context context){
        return new DbHelper(context).getArtist();
    }

    public static List<List<String>> getPathsForArtists(Context context){
        return new DbHelper(context).getPathsForArtists();
    }
    public static List<List<String>> getFileNamesForArtists(Context context){
        return new DbHelper(context).getFilesForArtists();
    }
    public static List<String> getMainFoldersFromDb(Context context){
        return new DbHelper(context).getMainFolders();
    }

    public static List<List<String>> getPathsForMainFolders(Context context){
        return new DbHelper(context).getPathsForMainFolders();
    }
    public static List<List<String>> getFileNamesForMainFolders(Context context){
        return new DbHelper(context).getFilesForMainFolders();
    }
    public static List<String> getAllSongsPaths(Context context){
        return new DbHelper(context).getAllSongsPaths();
    }
    public static List<String> getAllSongsNames(Context context){
        return new DbHelper(context).getAllSongsNames();
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
    public static int getLastPlayNumber(Context context){
        return new DbHelper(context).getLastPlayedNumber();
    }
    public static boolean getLastPlayState(Context context){
        return new DbHelper(context).getLastPlayedState();
    }
    public static void setLastPlayList(Context context, List<String> list){
        new DbHelper(context).setLastPlayList(list);
    }
    public static void setPlaylistAttributes(Context context, int time, int number, boolean shuffle){
        new DbHelper(context).setPlaylistAttributes(time, number, shuffle);
    }
    public static void setPlaylist(Context context,String playlist, List<String> paths){
        new DbPlaylist(context).playlistFiller(playlist, paths);
    }
    public static List<String> getPlaylist(Context context){
        return new DbPlaylist(context).getPlaylist();
    }
    public static List<List<String>> getPlaylistFiles(Context context){
        return new DbPlaylist(context).getPlaylistFiles();
    }
    public static void removePlaylist(Context context,String playlist){
        new DbPlaylist(context).playlistRemover(playlist);
    }
    public static void removeFilesFromPlaylist(Context context, String playlist, List<String> files){
        new DbPlaylist(context).playlistFileRemover(playlist, files);
    }
    public static void tabsFiller(Context context, List<TabConstructor> tabs){
        new DbTab(context).tabsFiller(tabs);
    }
    public static List<String> getVisibleTabs(Context context){
        return new DbTab(context).getVisibleTabs();
    }
    public static List<TabConstructor> getAllTabs(Context context){
        return new DbTab(context).getAllTabs();
    }
    public static List<MusicFile> getMusicFilesForSearch(Context context){
        return new DbHelper(context).getMusicFilesForSearch();
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