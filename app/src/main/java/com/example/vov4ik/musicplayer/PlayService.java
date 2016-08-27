package com.example.vov4ik.musicplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
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
//    private AlarmManager manager;
//    private PendingIntent pendingIntent;
    private static Context context = null;
    private static NotificationManager nm = null;
    private static int color;
    private static boolean pauseTrigger = true;
    private static boolean checkAPI;
    private static boolean shuffle = false;
    private static boolean nextSong = false;
    public static final long DOUBLE_CLICK_DELAY = 150;
    public static long lastPressTime = 0; // oldValue
    public static long myLastPressTime = 0; // oldValue
    public static long newPressTime = System.currentTimeMillis();
    private static List<String> shufflePath = new ArrayList<>();
    private static Notification notification;
    private static final String CLOSE_ACTION = "com.example.vov4ik.musicplayer.PlayService.close";
    private static final String PLAY_ACTION = "com.example.vov4ik.musicplayer.PlayService.play";
    private static final String NEXT_ACTION = "com.example.vov4ik.musicplayer.PlayService.next";
    private static final String PREV_ACTION = "com.example.vov4ik.musicplayer.PlayService.prev";
//    private static final String OPEN_ACTION = "com.example.vov4ik.musicplayer.PlayService.open";

    public static boolean isShuffle() {
        return shuffle;
    }

    public static List<String> getShufflePath() {
        return shufflePath;
    }

    public static void setShuffle(boolean shuffle) {
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

    public static void setPath(List<String> path) {
        PlayService.path = path;
        makeShufflePath();
    }

    public static void addPaths(List<String> pathList){
        path.addAll(pathList);
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
        PlayService.color  = getResources().getColor(R.color.colorIconNotification);
        PlayService.checkAPI =  Build.VERSION.SDK_INT>20;


    }

    @Override
    public void onDestroy() {

        Log.d("Test", "DESTROY!");
        AudioManager mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        ComponentName mReceiverComponent = new ComponentName(context,HeadphonesClickReceiver.class);
        mAudioManager.unregisterMediaButtonEventReceiver(mReceiverComponent);
        context.stopService(new Intent(context, AutoAudioStopper.class));
        PhoneCallReceiver.stopListener();
        if(player != null) {
            lastPlayedTime = player.getCurrentPosition();
            player.stop();
            player.release();
        }
        path.remove(playingFile);
        path.add(0, playingFile);
        DbConnector.setLastPlayListAndTime(getApplicationContext(), path, lastPlayedTime);
        nm.cancel(1);
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//
        if(intent!=null&&intent.getAction()!=null) {
            Log.d("test", intent.getAction() + "");
            if (intent.getAction().equals(PLAY_ACTION)) {
                if (!isPlayingNow()) {
                    startPlaying();
                }else{
                    pausePlaying();
                }
                sendNotification(context);
            } else if (intent.getAction().equals(PREV_ACTION)) {
                previous();
            } else if (intent.getAction().equals(NEXT_ACTION)) {
                nextSong();
            } else if (intent.getAction().equals(CLOSE_ACTION)) {
                stopSelf();
            }

            startForeground(1, notification);
        }
        return START_STICKY; //super.onStartCommand(intent, flags, startId);
    }

    public static void playFile(String filePath){


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
        if((context!=null)&&(nm!=null)) {
            sendNotification(context);
        }
        try {
            player.setDataSource(filePath);
            player.setVolume(100, 100);
            player.prepare();
            player.seekTo(lastPlayedTime);

            player.start();
            pauseTrigger = false;
            if(!AutoAudioStopper.getInstance().focusOn) {
                AutoAudioStopper.getInstance().startFocus();
            }
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    nextSong = true;
                    nextSong();

                }
            });

        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static void playFile(String filePath, Context context, NotificationManager nm){
        if(filePath.equals("START")) {
            addPaths(DbConnector.getLastPlayList(context));
            lastPlayedTime = DbConnector.getLastPlayTime(context);
            playingFile = path.get(0);
        }else {
            playingFile = filePath;
        }
        PlayService.context = context;
        PlayService.nm = nm;
        if((context!=null)&&(nm!=null)) {
            sendNotification(context);
        }
    }

    public static void nextSong(){
        lastPlayedTime = 0;
        List<String> path;
        if(shuffle) {
            path = shufflePath;
        }else{
            path = getPath();
        }
        if(nextSong){
            nextSong = false;
            if ((path.indexOf(playingFile) + 1) < path.size()) {
                playFile(path.get(path.indexOf(playingFile) + 1));
            } else {
                playFile(path.get(0));
            }
        }else
        if(isPlayingNow()) {
            if ((path.indexOf(playingFile) + 1) < path.size()) {
                playFile(path.get(path.indexOf(playingFile) + 1));
            } else {
                playFile(path.get(0));
            }
        }else{
            if ((path.indexOf(playingFile) + 1) < path.size()) {
                playingFile = path.get(path.indexOf(playingFile) + 1);
            }
            sendNotification(context);
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

    public static void pausePlaying(){
        pauseTrigger = true;
        if(player!=null) {
            lastPlayedTime = player.getCurrentPosition();
            player.pause();
            timer();
            sendNotification(context);
        }
        AutoAudioStopper.getInstance().stopFocus();
    }

    public static void startPlaying() {
        playFile(playingFile);
    }

    public static int duration(){
        if(player == null){
            return 0;
        }else {
            return player.getDuration();
        }
    }

    public static void previous(){
        List<String> path;
        if(shuffle) {
            path = shufflePath;
        }else{
            path = getPath();
        }
        if(isPlayingNow()) {
            if (player.getCurrentPosition() < 10000) {
                lastPlayedTime = 0;
                if ((path.indexOf(playingFile) - 1) >= 0) {
                    playFile(path.get(path.indexOf(playingFile) - 1));
                } else {
                    playFile(path.get(path.size() - 1));
                }
            } else {
                lastPlayedTime = 0;
                playFile(playingFile);
            }
        }else{
            if(lastPlayedTime>10000) {
                lastPlayedTime = 0;
            }else{
                lastPlayedTime = 0;
                if ((path.indexOf(playingFile) - 1) >= 0) {
                    playingFile = path.get(path.indexOf(playingFile) - 1);
                } else {
                    playingFile = path.get(path.size() - 1);
                }
            }
            sendNotification(context);
        }
    }

    public static void sendNotification(Context context) {

        String playingFile = PlayService.playingFile;
        Log.d("test", playingFile + "NOTIF");
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        String allTitle;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(playingFile);
        String title = (mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
        String artist  = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        if ((title==null)||(title.equals(""))||(artist == null) || (artist.equals(""))||
                (title.equals(" "))||(artist.equals(" "))) {
//            artist = new File(playingFile).getName();
            allTitle = new File(playingFile).getName();
        }else {
            allTitle = artist+" - "+title;
        }
        mmr.release();

        builder
//                .setContentTitle("Player")
//                .setColor(color)
//                .setContentText(allTitle)
                .setSmallIcon(R.drawable.play_button_png)
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
            rw.setImageViewResource(R.id.icon_notification, R.drawable.default_notification_icon);
//        if(isPlayingNow()){
//            rw.setViewVisibility(R.id.playButtonNotif, -10000);
//            rw.setViewVisibility(R.id.pauseButtonNotif, 1);
//            rw.setViewPadding(R.id.pauseButtonNotif, 0, 0, 4000, 0);
//        }else {
//            rw.setViewVisibility(R.id.playButtonNotif, 1);
//            rw.setViewVisibility(R.id.pauseButtonNotif, -10000);
//        }
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
//        }
    }


    private static void makeShufflePath(){
        shufflePath = new ArrayList<>();
        int lengthOfArray = path.size();

        int[]result = new int[lengthOfArray];
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
            PlayService.shufflePath.remove(playingFile);
            PlayService.shufflePath.add(0, playingFile);
        }
        for(int j = 0; j<result.length; j++) {
        }
    }

    public static void timer(){
        new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            int incr;
                            for (incr = 0; incr <= 90000; incr+=1000) {
                                if (!pauseTrigger){
                                    break;
                                }else if (incr == 90000){
                                    stopService();
                                }
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    Log.d("Test", "sleep failure");
                                }
                            }
                        }
                    }
            ).start();
    }

    private static void stopService() {
        context.stopService(new Intent(context, PlayService.class));
    }

}
