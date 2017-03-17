package com.example.vov4ik.musicplayer.receivers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.vov4ik.musicplayer.service.PlayService;

/**
 * Created by vov4ik on 8/22/2016.
 */
public class DisconnectingHeadphonesReceiver extends android.content.BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Test", "HEAD DISCON " + intent.getAction());

        if (intent.getAction().equals(
                android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)){
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(PlayService.PAUSE_ACTION));
        }
    }
}
