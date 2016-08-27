package com.example.vov4ik.musicplayer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;

/**
 * Created by vov4ik on 8/21/2016.
 */
public class NotificationClass extends Service{
    private Context context;
    private NotificationManager nm;
    private NotificationCompat.Builder builder;
    private PendingIntent pIntent;
    private int color;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        this.color  = getResources().getColor(R.color.colorPrimary);
        this.context = getApplicationContext();
        this.nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        this.builder = new NotificationCompat.Builder(this);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        builder.setContentIntent(pIntent);
        this.pIntent = PendingIntent.getActivity(this, 0, intent, 0);

    }
    public void sendNotification() {
        String playingFile = PlayService.playingFile;
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Log.d("test", playingFile + " NOTIFICATION");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
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
                .setSmallIcon(R.drawable.play_button_png);
//        Intent intent = new Intent(this, MainActivity.class);
//        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        nm.notify(1, builder.build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
