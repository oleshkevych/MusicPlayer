package com.example.vov4ik.musicplayer;

import android.content.Context;
import android.content.Intent;

/**
 * Created by vov4ik on 8/22/2016.
 */
public class DisconnectingHeadphonesReceiver extends android.content.BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(
                android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)){
            if(PlayService.isPlayingNow()) {
                Intent intent1 = new Intent(context, PlayService.class);
                intent1.setAction(PlayService.PAUSE_ACTION);
                context.startService(intent1);
            }
        }

//        Log.d("Test", "HEAD DISCON " + intent.getAction());
    }
}
