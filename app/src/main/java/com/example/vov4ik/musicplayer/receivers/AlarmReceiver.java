package com.example.vov4ik.musicplayer.receivers;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.vov4ik.musicplayer.service.PlayService;

/**
 * Created by vov4ik on 8/19/2016.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (isServiceRunning(context)) {
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(PlayService.CLOSE_ACTION));
            Log.d("Test", "ALARM MANAGER WORKED");
        }

    }
    private boolean isServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (PlayService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
