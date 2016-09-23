package com.example.vov4ik.musicplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.RemoteViews;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by vov4ik on 8/17/2016.
 */
public class PlayService extends Service {
    private static List<String> path = new ArrayList<>();
    private static int lastPlayedTime;
    private static MediaPlayer player =  null;
    public static String playingFile;
    private static Context context = null;
    private static NotificationManager nm = null;
    private static int color;
    private static boolean checkAPI;
    private static boolean shuffle = false;
    private static boolean pauseTrigger = false;
    private static boolean nextSong = false;
    public static long myLastPressTime = 0; // oldValue
    private static List<String> shufflePath = new ArrayList<>();
    private static Notification notification;
    private static final String CLOSE_ACTION = "com.example.vov4ik.musicplayer.PlayService.close";
    private static final String PLAY_ACTION = "com.example.vov4ik.musicplayer.PlayService.play";
    private static final String NEXT_ACTION = "com.example.vov4ik.musicplayer.PlayService.next";
    private static final String PREV_ACTION = "com.example.vov4ik.musicplayer.PlayService.prev";
    private static int trekNumber;
    private static  int[]result;

    public static int[] getResult() {
        return result;
    }

    public static int getTrekNumber() {
        return trekNumber;
    }

    public static void setTrekNumber(int trekNumber) {
        PlayService.trekNumber = trekNumber;
    }

    public static boolean isShuffle() {
        return shuffle;
    }

    public static List<String> getShufflePath() {
        return shufflePath;
    }

    public static void setShuffle(boolean shuffle) {
        if(shuffle){
            for(int i = 0; i<result.length; i++){
                if(result[i] == trekNumber){
                    trekNumber = i;
                }
            }
        }else{
            trekNumber = path.indexOf(playingFile);
        }
        PlayService.shuffle = shuffle;
    }

    public static MediaPlayer getPlayer() {
        return player;
    }

    public static List<String> getPath() {
        return path;
    }

    public static void setLastPlayedTime(int lastPlayedTime) {
        PlayService.lastPlayedTime = lastPlayedTime;
    }

    public static void setPlayingFile(String playingFile) {
        PlayService.playingFile = playingFile;
    }

    public static void setPath(List<String> path) {
        List<String> p = new ArrayList<>();
        for (int i = 0; i < path.size(); i++) {
            File f = new File(path.get(i));
            if ((!path.get(i).equals("..GoToRoot"))&&(!f.isDirectory())) {
                p.add(path.get(i));
            }
        }
        PlayService.path = p;
        makeShufflePath();
    }

    public static void addPaths(List<String> pathList){
        List<String> p = new ArrayList<>();
        for (int i = 0; i < pathList.size(); i++) {
            File f = new File(pathList.get(i));
            if ((!pathList.get(i).equals("..GoToRoot"))&&(!f.isDirectory())) {
                p.add(pathList.get(i));
            }
        }
        path.addAll(p);
        makeShufflePath();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setPath(DbConnector.getLastPlayList(getApplicationContext()));
        setTrekNumber(DbConnector.getLastPlayNumber(getApplicationContext()));
        setLastPlayedTime(DbConnector.getLastPlayTime(getApplicationContext()));
        PlayService.color  = getResources().getColor(R.color.colorIconNotification);
        PlayService.checkAPI =  Build.VERSION.SDK_INT>20;


    }

    @Override
    public void onDestroy() {

        Log.d("Test", "DESTROY!");
        this.stopService(new Intent(this, AutoAudioStopper.class));
        PhoneCallReceiver.stopListener();
        if(player != null) {
            lastPlayedTime = player.getCurrentPosition();
            player.stop();
            player.release();
        }
        if(isShuffle()){
            path = shufflePath;
        }
        player = null;
        path.remove(playingFile);
        path.add(0, playingFile);
        //DbConnector.setLastPlayListAndTime(getApplicationContext(), path, lastPlayedTime);
        nm.cancel(1);
        stopForeground(true);
        nm.cancelAll();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Test", " "+path.toString());
//        Log.d("test", "intent == "+ intent+" "+flags);
//        Log.d("test", "intentACTION == "+ intent.getAction());
        PlayService.nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        PlayService.context = this;
        if(intent!=null&&intent.getAction()!=null) {
            if (intent.getAction().equals(PLAY_ACTION)) {
                if( intent.getIntExtra("NUMBER", 10000)!=10000 ){
                    setTrekNumber(intent.getIntExtra("NUMBER", 0));
                }
                if(player == null){
                    AudioManager am = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                    AutoAudioStopper.getInstance().setAudioManager(am);
                    AutoAudioStopper.getInstance().setContext(this);
//                    Log.d("Test", "start with null");
                }
                if (!isPlayingNow()) {
//                    Log.d("Test", "start  play");
                    startPlaying();
                }else{
//                    Log.d("Test", "start  pause");
                    pausePlaying();
                }
                sendNotification(context);
            } else if (intent.getAction().equals(PREV_ACTION)) {
                previous();
                sendNotification(context);
            } else if (intent.getAction().equals(NEXT_ACTION)) {
                nextSong();
                sendNotification(context);
            } else if (intent.getAction().equals(CLOSE_ACTION)) {
                stopSelf();
                sendNotification(context);
            }

            startForeground(1, notification);
        }
        return START_NOT_STICKY;//START_STICKY; //super.onStartCommand(intent, flags, startId);
    }
    private static void playFile(String filePath){
//        Log.d("Test", " "+playingFile);
//        Log.d("Test", " "+filePath);

        if(filePath == null) {
            try {
                setPath(DbConnector.getLastPlayList(context));
                setTrekNumber(DbConnector.getLastPlayNumber(context));
                PlayService.lastPlayedTime = DbConnector.getLastPlayTime(context);
                PlayService.playingFile = path.get(0);
            }catch(RuntimeException r){
            }
        }
        if(player == null){
            player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }else{
            try {
                player.reset();
            }catch (IllegalStateException e){
                Log.d("test", "RESET ERROR"+ e);
                player = new MediaPlayer();
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }
        }

        playingFile = filePath;
        try {
            if (filePath != null) {
                player.setDataSource(filePath);
                player.setVolume(100, 100);
                player.prepare();
                player.seekTo(lastPlayedTime);

                player.start();
                pauseTrigger = false;
                if (!AutoAudioStopper.getInstance().focusOn) {
                    AutoAudioStopper.getInstance().startFocus();
                }
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        nextSong = true;
                        nextSong();

                    }
                });
                if ((context != null) && (nm != null)) {
                    sendNotification(context);
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


//    public static void playFile(String filePath, Context context, NotificationManager nm){
//        if(filePath.equals("START")) {
//            setPath(DbConnector.getLastPlayList(context));
//            PlayService.lastPlayedTime = DbConnector.getLastPlayTime(context);
//            PlayService.playingFile = path.get(0);
//        }else {
//            PlayService.playingFile = filePath;
//        }
//        PlayService.context = context;
//        PlayService.nm = nm;
//        if((context!=null)&&(nm!=null)) {
//            sendNotification(context);
//        }
//        timer();
//    }

    public static void nextSong(){
        lastPlayedTime = 0;
        List<String> path;
        if(shuffle) {
            path = shufflePath;
        }else{
            path = getPath();
        }
        if(path.size()>0) {
            if (nextSong) {
                nextSong = false;
//                if ((path.indexOf(playingFile) + 1) < path.size()) {
//                    playFile(path.get(path.indexOf(playingFile) + 1));
//                } else {
//                    playFile(path.get(0));
//                }
//            } else if (isPlayingNow()) {
//                if ((path.indexOf(playingFile) + 1) < path.size()) {
//                    playFile(path.get(path.indexOf(playingFile) + 1));
//                } else {
//                    playFile(path.get(0));
//                }
//            } else {
//                if ((path.indexOf(playingFile) + 1) < path.size()) {
//                    playingFile = path.get(path.indexOf(playingFile) + 1);
//                }
                if ((getTrekNumber() + 1) < path.size()) {
                    playFile(path.get(getTrekNumber() + 1));
                    setTrekNumber(getTrekNumber()+1);
                } else {
                    playFile(path.get(0));
                    setTrekNumber(0);
                }
            } else if (isPlayingNow()) {
                if ((getTrekNumber() + 1) < path.size()) {
                    playFile(path.get(getTrekNumber() + 1));
                    setTrekNumber(getTrekNumber() + 1);
                } else {
                    setTrekNumber(0);
                    playFile(path.get(0));
                }
            } else {
                if ((getTrekNumber() + 1) < path.size()) {
                    playingFile = path.get(getTrekNumber() + 1);
                    setTrekNumber(getTrekNumber()+1);
                }
                sendNotification(context);
            }
        }
    }

    public static void log(){
        Log.d("test", player.getCurrentPosition() + "");
    }

    public static boolean isPlayingNow(){
        try {
            return player != null && player.isPlaying();
        }catch (IllegalStateException e){
            return false;
        }
    }

    public static void pausePlaying() {
        pauseTrigger = true;
        if(player!= null) {
            lastPlayedTime = player.getCurrentPosition();
            player.pause();
//            Log.d("test", "pause "+lastPlayedTime+" "+getTrekNumber()+" "+path.size());
            DbConnector.setLastPlayListAndTime(context, path, lastPlayedTime, getTrekNumber());
            timer();
            sendNotification(context);
        }
        AutoAudioStopper.getInstance().stopFocus();
    }

    public static void startPlaying() {
        List<String> path;
        if(shuffle) {
            path = shufflePath;
        }else{
            path = getPath();
        }
//        Log.d("Test", getTrekNumber()+" "+path.size());
        try {
            playFile(path.get(getTrekNumber()));
        }catch(IndexOutOfBoundsException i){
            try {
                setPath(DbConnector.getLastPlayList(context));
                setTrekNumber(DbConnector.getLastPlayNumber(context));
                setLastPlayedTime(DbConnector.getLastPlayTime(context));
                playFile(path.get(getTrekNumber()));
            }catch(IndexOutOfBoundsException i1){
                setPath(DbConnector.getAllSongsPaths(context));
                Random r = new Random();
                setTrekNumber(r.nextInt(PlayService.path.size() - 1));
            }


        }
        sendNotification(context);
    }

    public static int duration(){
        if(player == null||!isPlayingNow()){
            if(playingFile!=null) {
                MediaPlayer p = new MediaPlayer();
                try {
                    int d;
                    p.setDataSource(playingFile);
                    p.prepare();
                    d = p.getDuration();
                    p.stop();
                    p.release();
                    return d;
                } catch (IOException e) {
                    Log.d("test", e.toString());
                    return 0;
                }
            }else{ return 0;}
        }else {
            return player.getDuration();
        }
    }
    public static int currentTime(){
        if(player == null){
            return lastPlayedTime;
        }else {
            if(isPlayingNow()) {
                return player.getCurrentPosition();
            }else{
                return lastPlayedTime;
            }
        }
    }

    public static String trekName(){
        String playingFile = PlayService.playingFile;
        String title, artist;
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(playingFile);
            title = (mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
            artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            mmr.release();
        }catch(IllegalArgumentException e){
            title = "file";
            artist = "no";
        }
        if ((title==null)||(title.equals(""))||(artist == null) || (artist.equals(""))||
                (title.equals(" "))||(artist.equals(" "))) {
            return  new File(playingFile).getName();
        }else {
            return  artist+" - "+title;
        }
    }


    public static void previous(){
        List<String> path;
        if(shuffle) {
            path = shufflePath;
        }else{
            path = getPath();
        }
        if(path.size()>0) {
            if (isPlayingNow()) {
//                if (player.getCurrentPosition() < 10000) {
//                    lastPlayedTime = 0;
//                    if ((path.indexOf(playingFile) - 1) >= 0) {
//                        playFile(path.get(path.indexOf(playingFile) - 1));
//                    } else {
//                        playFile(path.get(path.size() - 1));
//                    }
//                } else {
//                    lastPlayedTime = 0;
//                    playFile(playingFile);
//                }
//            } else {
//                if (lastPlayedTime > 10000) {
//                    lastPlayedTime = 0;
//                } else {
//                    lastPlayedTime = 0;
//                    if ((path.indexOf(playingFile) - 1) >= 0) {
//                        playingFile = path.get(path.indexOf(playingFile) - 1);
//                    } else {
//                        playingFile = path.get(path.size() - 1);
//                    }
//                }
                if (player.getCurrentPosition() < 10000) {
                    lastPlayedTime = 0;
                    if ((getTrekNumber() - 1) >= 0) {
                        playFile(path.get(getTrekNumber() - 1));
                        setTrekNumber(getTrekNumber() - 1);
                    } else {
                        playFile(path.get(path.size() - 1));
                        setTrekNumber((path.size() - 1));
                    }
                } else {
                    lastPlayedTime = 0;
                    playFile(playingFile);
                }
            } else {
                if (lastPlayedTime > 10000) {
                    lastPlayedTime = 0;
                } else {
                    lastPlayedTime = 0;
                    if ((getTrekNumber() - 1) >= 0) {
                        playingFile = path.get(getTrekNumber() - 1);
                        setTrekNumber(getTrekNumber()-1);
                    } else {
                        playingFile = path.get(path.size() - 1);
                        setTrekNumber((path.size() - 1));
                    }
                }
                sendNotification(context);
            }
        }
    }

    public static void sendNotification(Context context) {

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        String allTitle = trekName();
        builder
//                .setContentTitle("Player")
//                .setColor(color)
//                .setContentText(allTitle)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(false)
                .setOngoing(true);

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
        builder.setContentIntent(pIntent);

        RemoteViews rw = new RemoteViews(context.getPackageName(), R.layout.notification);
//            rw.setTextViewText(R.id.notification_artist, artist);
            rw.setTextViewText(R.id.notification_trek, allTitle);
        Bitmap icon = getImage();
        if(icon!=null){
            rw.setImageViewBitmap(R.id.icon_notification, icon);
        }else {
            rw.setImageViewResource(R.id.icon_notification, R.drawable.default_notification_icon);
        }
        if(isPlayingNow()){
            rw.setViewVisibility(R.id.playButtonNotif, Button.INVISIBLE);
            rw.setViewVisibility(R.id.pauseButtonNotif, Button.VISIBLE);
            builder.setSmallIcon(R.drawable.pause_png);
        }else {
            rw.setViewVisibility(R.id.playButtonNotif, Button.VISIBLE);
            rw.setViewVisibility(R.id.pauseButtonNotif, Button.INVISIBLE);
            builder.setSmallIcon(R.drawable.play_button_png);
        }
        Intent closeIntent = new Intent(context, PlayService.class);
        closeIntent.setAction(CLOSE_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(context, 0,
                closeIntent, 0);

        Intent previousIntent = new Intent(context, PlayService.class);
        previousIntent.setAction(PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(context, 0,
                previousIntent, 0);

        Intent playIntent = new Intent(context, PlayService.class);
        playIntent.setAction(PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(context, 0,
                playIntent, 0);

        Intent nextIntent = new Intent(context, PlayService.class);
        nextIntent.setAction(NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(context, 0,
                nextIntent, 0);
//        Intent openIntent = new Intent(context, MainActivity.class);
////        openIntent.setAction(OPEN_ACTION);
////        openIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent popenIntent = PendingIntent.getService(context, 0,
//                openIntent, 0);


        rw.setOnClickPendingIntent(R.id.closeNotif, pcloseIntent);
//        rw.setOnClickPendingIntent(R.id.icon_notification, popenIntent);
//        rw.setOnClickPendingIntent(R.id.notification_trek, popenIntent);
        rw.setOnClickPendingIntent(R.id.playButtonNotif, pplayIntent);
        rw.setOnClickPendingIntent(R.id.pauseButtonNotif, pplayIntent);
        rw.setOnClickPendingIntent(R.id.nextButtonNotif, pnextIntent);
        rw.setOnClickPendingIntent(R.id.previousButtonNotif, ppreviousIntent);

        notification = builder.getNotification();
        notification.contentView = rw;


        nm.notify(1, notification);
        timer();
//        }
    }

    public static Bitmap getImage(){
        Bitmap image;
            try {
                MediaMetadataRetriever mData = new MediaMetadataRetriever();
                mData.setDataSource(playingFile);
                try {
                    byte art[] = mData.getEmbeddedPicture();
                    image = BitmapFactory.decodeByteArray(art, 0, art.length);
                } catch (Exception e) {
                    image = null;
                }

                mData.release();
            } catch (IllegalArgumentException e) {
                image = null;
            }
        return image;
    }

    private static void makeShufflePath(){
        PlayService.shufflePath = new ArrayList<>();
        int lengthOfArray = path.size();

        result = new int[lengthOfArray];
        for (int i = 0; i < lengthOfArray; i++) {
            result[i] = i;
        }
        Random rnd = new Random();
        for (int i = lengthOfArray - 1; i > 1; i--)
        {
            int index = rnd.nextInt(i + 1);
            int a = result[index];
            result[index] = result[i];
            result[i] = a;
        }

        for(int j = 0; j<result.length; j++) {
            PlayService.shufflePath.add(j, path.get(result[j]));
        }
        if (playingFile!=null&&!playingFile.equals("")) {
            int index = 0;
            for (int j = 0; j<result.length; j++) {
                if(PlayService.shufflePath.indexOf(playingFile) == result[j]) {
                    index = j;
                    break;
                }
            }
            int a = result[index];
            result[index] = result[0];
            result[0] = a;
            PlayService.shufflePath.remove(playingFile);
            PlayService.shufflePath.add(0, playingFile);

        }
    }

    public static void timer(){
        if (!isPlayingNow()) {
            new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            int incr=0;
                            while(true) {
                                if (isPlayingNow()||player==null) {
                                    break;
                                } else if (incr > 300000) {
                                    stopService();
                                    incr = 0;
                                }
                                incr+=1000;
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    Log.d("Test", "sleep failure");
                                }
                            }
                        }
                    }
            ).start();
        }
    }

    private static void stopService() {
        context.stopService(new Intent(context, PlayService.class));
    }

}
