package com.example.vov4ik.musicplayer.receivers;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.example.vov4ik.musicplayer.screens.main.MainActivity;
import com.example.vov4ik.musicplayer.service.PlayService;

/**
 * Created by vov4ik on 8/22/2016.
 */
public class HeadphonesClickReceiver extends BroadcastReceiver {

    static int d = 0;

    @Override
    public void onReceive(final Context context, Intent intent) {
        String intentAction = intent.getAction();
        if (!Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
            return;
        }
        KeyEvent event = (KeyEvent) intent
                .getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        if (event == null) {
            return;
        }
        int action = event.getAction();
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_HEADSETHOOK:
                Log.e("ACTION ", action+"");
                if (action == KeyEvent.ACTION_DOWN) {
                    d++;
                    Handler handler = new Handler();
                    Runnable r = new Runnable() {

                        @Override
                        public void run() {
                            // single click *******************************
                            if (d == 1) {
                                Toast.makeText(context, "single click!", Toast.LENGTH_SHORT).show();
                                if(!isServiceRunning(context)) {
                                    context.startService(PlayService.getIntentForPlaying(context));
                                }else {
                                    onClick(context);
                                }
                            }
                            // double click *********************************
                            if (d == 2) {
                                Toast.makeText(context, "Double click!!", Toast.LENGTH_SHORT).show();
                                onNextClick(context);
                            }
                            if (d == 3) {
                               onPreviousClick(context);
                            }
                            d = 0;
                        }
                    };
                    if (d == 1) {
                        handler.postDelayed(r, 600);
                    }
                }
                break;
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

    private void onClick(Context context) {
        Intent intent = new Intent(PlayService.CLICK_ACTION);
        sendBroadcastIntent(intent, context);
    }

    private void onNextClick(Context context) {
        Intent intent = new Intent(PlayService.NEXT_ACTION);
        sendBroadcastIntent(intent, context);
    }

    private void onPreviousClick(Context context) {
        Intent intent = new Intent(PlayService.PREV_ACTION);
        sendBroadcastIntent(intent, context);
    }

    private void sendBroadcastIntent(Intent intent, Context context) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
