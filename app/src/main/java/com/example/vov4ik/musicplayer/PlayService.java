package com.example.vov4ik.musicplayer;

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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vov4ik on 8/17/2016.
 */
public class PlayService extends Service {
    private static List<String> path = new ArrayList<>();
    private static int lastPlayedTime;
//    private static boolean pauseTrigger = false;
    private static MediaPlayer player =  null;
    public static String playingFile;
//    private AlarmManager manager;
//    private PendingIntent pendingIntent;
    private static Context context = null;
    private static NotificationManager nm = null;
    private static int color;
    private static boolean checkAPI;
    private static boolean nextSong = false;
    public static final long DOUBLE_CLICK_DELAY = 150;
    public static long lastPressTime = 0; // oldValue
    public static long myLastPressTime = 0; // oldValue
    public static long newPressTime = System.currentTimeMillis();



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
    }

    public static void addPaths(List<String> pathList){
        path.addAll(pathList);
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
//        addPaths(DbConnector.getLastPlayList(getApplicationContext()));
//        lastPlayedTime = DbConnector.getLastPlayTime(getApplicationContext());
//        playingFile = path.get(0);
//        player = new MediaPlayer();
//        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
//
////      START NotificationClass.sendNotification();
//        Intent alarmIntent = new Intent(PlayService.this, AlarmReceiver.class);
//        pendingIntent = PendingIntent.getBroadcast(PlayService.this, 0, alarmIntent, 0);
//        int interval = 1800000;
//        manager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), interval, pendingIntent);
////AUDIO FOCUS




    }

    @Override
    public void onDestroy() {
        Log.d("Test", "DESTROY!");
//        if(manager !=null)
//        manager.cancel(pendingIntent);
//        AlarmReceiver.pendingIntent.cancel();
//        Intent intent1 = new Intent(this, AutomaticAudioStopper.class);
//        stopService(intent1);
        AudioManager mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        ComponentName mReceiverComponent = new ComponentName(context,HeadphonesClickReceiver.class);
        mAudioManager.unregisterMediaButtonEventReceiver(mReceiverComponent);
        context.stopService(new Intent(context, AutoAudioStopper.class));
        if(player != null) {
            lastPlayedTime = player.getCurrentPosition();
            player.stop();
            player.release();
        }
        path.remove(playingFile);
        path.add(0, playingFile);
        DbConnector.setLastPlayListAndTime(getApplicationContext(), path, lastPlayedTime);
        nm.cancel(1);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
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
            if(AutoAudioStopper.getInstance().focusOn) {
                AutoAudioStopper.getInstance().startFocus();
            }
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    nextSong = true;
                    nextSong();

                }
            });

        } catch (IOException e) {
            e.printStackTrace();
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
//        if(player == null){
//            player = new MediaPlayer();
//        }else{
//            player.reset();
//        }


        if((context!=null)&&(nm!=null)) {
            sendNotification(context);
        }
//        try {
//            player.setDataSource(filePath);
//            player.setVolume(100, 100);
//            player.prepare();
//            player.seekTo(lastPlayedTime);
//            player.start();
//            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mp) {
//                    nextSong();
//                }
//            });
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public static void nextSong(){
        lastPlayedTime = 0;
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
//        pauseTrigger = true;
        lastPlayedTime = player.getCurrentPosition();
        player.pause();
        sendNotification(context);
        AutoAudioStopper.getInstance().stopFocus();
    }

    public static void startPlaying() {
//        if(!pauseTrigger){
//            lastPlayedTime = 0;
//        }
//        pauseTrigger = false;
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
        String title1  = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        if ((title==null)||(title.equals(""))||(title1 == null) || (title1.equals(""))||
                (title.equals(" "))||(title1.equals(" "))) {
            allTitle = new File(playingFile).getName();
        }else {
            allTitle = title+" - "+title1;
        }
        mmr.release();
        builder.setContentTitle("Player")
                .setAutoCancel(false)
                .setColor(color)
                .setContentText(allTitle)
                .setSmallIcon(R.drawable.play_button_png)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(false)
                .setOngoing(true);

       /* if(checkAPI){
            builder
                            // Add media control buttons that invoke intents in your media service
                    .addAction(R.drawable.ic_prev, "Previous", prevPendingIntent) // #0
                    .addAction(R.drawable.ic_pause, "Pause", pausePendingIntent)  // #1
                    .addAction(R.drawable.ic_next, "Next", nextPendingIntent)     // #2
                            // Apply the media style template
                    .setStyle(new Notification.MediaStyle()
                            .setShowActionsInCompactView(1 *//* #1: pause button *//*)
                            .setMediaSession(mMediaSession.getSessionToken())
                            .setLargeIcon(albumArtBitmap)
                    );
        }*/
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
        builder.setContentIntent(pIntent);
//        if(isPlayingNow()){
//            new Thread(
//                    new Runnable() {
//                        @Override
//                        public void run() {
//                            int incr;
//                            for (incr = 0; incr <= player.getDuration(); incr+=1000) {
//                                builder.setProgress(player.getDuration(), incr, false);
//                                nm.notify(1, builder.build());
//                                try {
//                                    Thread.sleep(1000);
//                                } catch (InterruptedException e) {
//                                    Log.d("Test", "sleep failure");
//                                }
//                            }
//                        }
//                    }
//            ).start();
//        }else {
        nm.notify(1, builder.build());
//        }
    }

//    public void onPrepared(MediaPlayer player) {
//        player.start();
//    }
}
