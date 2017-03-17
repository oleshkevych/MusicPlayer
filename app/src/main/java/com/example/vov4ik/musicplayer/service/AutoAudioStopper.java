package com.example.vov4ik.musicplayer.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.vov4ik.musicplayer.receivers.HeadphonesClickReceiver;

/**
 * Created by vov4ik on 8/22/2016.
 */
public class AutoAudioStopper extends Service {//implements AudioManager.OnAudioFocusChangeListener{

    static Intent getIntent(Context context) {
        return new Intent(context, AutoAudioStopper.class);
    }

    public static final String START_FOCUS = "com.example.vov4ik.musicplayer.service.START_FOCUS";
    public static final String STOP_FOCUS = "com.example.vov4ik.musicplayer.service.STOP_FOCUS";
    public static final String CLOSE = "com.example.vov4ik.musicplayer.service.CLOSE";

    private AudioManager mAudioManager;
    private boolean mIsFocusOn = false;

    public AutoAudioStopper() {
    }

    AFListener afListenerMusic;


    @Override
    public void onCreate() {
        super.onCreate();
        registerBroadcastReceiver();
        mAudioManager = (AudioManager) getApplicationContext().getSystemService(AUDIO_SERVICE);
        ComponentName mReceiverComponent = new ComponentName(AutoAudioStopper.this, HeadphonesClickReceiver.class);
        mAudioManager.registerMediaButtonEventReceiver(mReceiverComponent);
    }

    private void startFocus() {
        if(!mIsFocusOn) {
            mIsFocusOn = true;
            afListenerMusic = new AFListener();
            int requestResult = mAudioManager.requestAudioFocus(afListenerMusic,
                    AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            Log.d("Test", "Sound request focus, result: " + requestResult);
        }
    }

    private void close(){
        if (afListenerMusic != null)
            mAudioManager.abandonAudioFocus(afListenerMusic);
        unregisterBroadcastReceiver();
        stopSelf();
    }

    @Override
    public void onDestroy() {
        Log.d("Test", "CALL DESTROY");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void stopFocus() {
        if(mIsFocusOn) {
            mIsFocusOn = false;
//        ComponentName mReceiverComponent = new ComponentName(context,HeadphonesClickReceiver.class);
//        mAudioManager.unregisterMediaButtonEventReceiver(mReceiverComponent);
            if (afListenerMusic != null)
                mAudioManager.abandonAudioFocus(afListenerMusic);
        }
    }

    class AFListener implements AudioManager.OnAudioFocusChangeListener {


        AFListener() {

        }

        @Override
        public void onAudioFocusChange(int focusChange) {
            String event = "";
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                    event = "AUDIOFOCUS_LOSS";
                    broadcastPlayControl(PlayService.PAUSE_FOCUS_ACTION);
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    broadcastPlayControl(PlayService.PAUSE_FOCUS_ACTION);
                    event = "AUDIOFOCUS_LOSS_TRANSIENT";
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
//                    PlayService.getPlayer().setVolume(40, 40);
                    broadcastPlayControl(PlayService.VOLUME_LOW_ACTION);
                    event = "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK";
                    break;
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
//                    PlayService.getPlayer().setVolume(40, 40);
                    broadcastPlayControl(PlayService.VOLUME_LOW_ACTION);
                    event = "AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK";
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    broadcastPlayControl(PlayService.VOLUME_UP_ACTION);
                    broadcastPlayControl(PlayService.PLAY_ACTION);
                    event = "AUDIOFOCUS_GAIN";
                    break;
            }
            Log.d("Test", " onAudioFocusChange: " + event + " " + focusChange);
        }
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(START_FOCUS);
        filter.addAction(STOP_FOCUS);
        filter.addAction(CLOSE);
        LocalBroadcastManager.getInstance(AutoAudioStopper.this)
                .registerReceiver(mBroadcastReceiver, filter);
    }

    private void unregisterBroadcastReceiver() {
        LocalBroadcastManager.getInstance(AutoAudioStopper.this)
                .unregisterReceiver(mBroadcastReceiver);
    }

    final BroadcastReceiver mBroadcastReceiver
            = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case START_FOCUS:
                    startFocus();
                    break;
                case STOP_FOCUS:
                    stopFocus();
                    break;
                case CLOSE:
                   close();
                    break;
            }
        }
    };

    private void broadcastPlayControl(String action) {
        Intent intent = new Intent(action);
        sendBroadcastIntent(intent);
    }

    private void sendBroadcastIntent(Intent intent) {
        LocalBroadcastManager.getInstance(AutoAudioStopper.this).sendBroadcast(intent);
    }
}
