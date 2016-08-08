package com.example.vov4ik.musicplayer;

import java.io.File;

/**
 * Created by vov4ik on 8/3/2016.
 */
public class FileChecker {
    public static Boolean checker(File file) {
//        MimeTypeMap map = MimeTypeMap.getSingleton();
//        String ext = MimeTypeMap.getFileExtensionFromUrl(file.getPath().replace(" ", ""));
//        String type = map.getMimeTypeFromExtension(ext);
//        if((type != null)&& (type.contains("audio/"))) {
//            if((file.getName().substring(file.getName().lastIndexOf(".")).equalsIgnoreCase(".mp3"))) {
//               return true;
//            }
//        }
//        if((type != null) && (type.contains("audio/"))){
//            if(!(file.getName().length()>4)&&(file.getName().substring(file.getName().length()-4).equalsIgnoreCase(".mp3"))) {
//                Log.d("Test", " NE MP3    " + file.getName());
//            }
//        }else
//        if((file.getName().length()>4)&&(file.getName().substring(file.getName().length()-4).equalsIgnoreCase(".mp3"))){
//            Log.d("Test", "         "+file.getName());
//        }
//        return (type != null) && (type.contains("audio/"));
        return((file.getName().length()>4)&&(file.getName().substring(file.getName().length()-4).equalsIgnoreCase(".mp3")));

    }
}
