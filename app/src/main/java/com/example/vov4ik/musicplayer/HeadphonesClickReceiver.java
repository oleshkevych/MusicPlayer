package com.example.vov4ik.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

/**
 * Created by vov4ik on 8/22/2016.
 */
public class HeadphonesClickReceiver extends BroadcastReceiver {

    public HeadphonesClickReceiver ()
    {
        super ();
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent event = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

            PlayService.lastPressTime = PlayService.newPressTime;
            PlayService.newPressTime = System.currentTimeMillis();
            long delta = PlayService.newPressTime - PlayService.lastPressTime;
            if(delta < PlayService.DOUBLE_CLICK_DELAY){
                PlayService.myLastPressTime += delta;
                Log.d("Test", "HEAD DISCON click" + delta);

                if (79 == event.getKeyCode()) {
                    if(PlayService.getPlayer()!=null) {
                        if (PlayService.isPlayingNow()) {
                            PlayService.pausePlaying();
                            Log.d("Test", "HEAD DISCON pause" );

                        } else {
                            Log.d("Test", "HEAD DISCON start" );

                            PlayService.startPlaying();
                        }
                    }else{
                        PlayService.startPlaying();
                        Log.d("Test", "HEAD DISCON start from else" );

                    }
                }
            }
            if(PlayService.myLastPressTime<200&&PlayService.myLastPressTime>10){
                PlayService.myLastPressTime = 0;
                PlayService.nextSong();
                Log.d("Test", "HEAD DISCON double" );

            }else if(PlayService.myLastPressTime>200){
                PlayService.myLastPressTime =0;
                Log.d("Test", "HEAD DISCON 0" );

            }

        }
    }
}
