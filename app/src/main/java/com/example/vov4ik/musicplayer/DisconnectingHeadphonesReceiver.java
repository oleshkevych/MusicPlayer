package com.example.vov4ik.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by vov4ik on 8/22/2016.
 */
public class DisconnectingHeadphonesReceiver extends android.content.BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(
                android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)){
            PlayService.pausePlaying();
        }

        Log.d("Test", "HEAD DISCON " + intent.getAction());
    }
}
