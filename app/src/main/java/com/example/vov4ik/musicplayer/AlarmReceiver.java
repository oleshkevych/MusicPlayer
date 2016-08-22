package com.example.vov4ik.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by vov4ik on 8/19/2016.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(PlayService.getPlayer()!=null&&!PlayService.isPlayingNow()){
            context.stopService(new Intent(context, PlayService.class));
            Log.d("Test", "ALARM MANAGER WORKED");
        }

    }

}
