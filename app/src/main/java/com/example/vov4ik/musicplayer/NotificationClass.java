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
    private static Context context;
    private static NotificationManager nm;
    private static NotificationCompat.Builder builder;
    private static PendingIntent pIntent;
    private static int color;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        color  = getResources().getColor(R.color.colorPrimary);
        context = getApplicationContext();
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(this);
        Intent intent = new Intent(this, MainActivity.class);
        pIntent = PendingIntent.getActivity(this, 0, intent, 0);

    }
    public static void sendNotification() {
        String playingFile = PlayService.playingFile;
//        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Log.d("test", playingFile + " NOTIFICATION");
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
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
        builder.setContentIntent(pIntent);
        nm.notify(1, builder.build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
