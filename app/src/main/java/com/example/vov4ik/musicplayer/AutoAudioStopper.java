package com.example.vov4ik.musicplayer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by vov4ik on 8/22/2016.
 */
public class AutoAudioStopper extends Service {//implements AudioManager.OnAudioFocusChangeListener{
    private static AutoAudioStopper ourInstance = new AutoAudioStopper();

    public static AutoAudioStopper getInstance() {
        return ourInstance;
    }

    private AudioManager mAudioManager;
    public boolean focusOn = false;
    private Context context;

    AFListener afListenerMusic;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setAudioManager(AudioManager audioManager) {
        mAudioManager = audioManager;
    }

    private AutoAudioStopper() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public void startFocus(){
        focusOn = true;
        afListenerMusic = new AFListener();
        int requestResult = mAudioManager.requestAudioFocus(afListenerMusic,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        Log.d("Test", "Sound request focus, result: " + requestResult);
//        this.mAudioManager = mAudioManager;
//        this.mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);


    }
//    @Override
//    public void onAudioFocusChange(int focusChange) {
//        if(PlayService.isPlayingNow()) {
//            if (focusChange <= 0) {
//                PlayService.pausePlaying();
//                Log.d("Test", "CALL");
//            } else {
//                PlayService.startPlaying();
//                Log.d("Test", "CALL FINISH");
//            }
//        }
//    }


    @Override
    public void onDestroy() {
        Log.d("Test", "CALL DESTROY");
        if (afListenerMusic != null)
            mAudioManager.abandonAudioFocus(afListenerMusic);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public void stopFocus() {
        focusOn = false;
//        ComponentName mReceiverComponent = new ComponentName(context,HeadphonesClickReceiver.class);
//        mAudioManager.unregisterMediaButtonEventReceiver(mReceiverComponent);
        if (afListenerMusic != null)
            mAudioManager.abandonAudioFocus(afListenerMusic);
    }

    class AFListener implements AudioManager.OnAudioFocusChangeListener {


        public AFListener() {

        }

        @Override
        public void onAudioFocusChange(int focusChange) {
            String event = "";
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                    event = "AUDIOFOCUS_LOSS";
                    PlayService.getPlayer().pause();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    PlayService.getPlayer().pause();
                    event = "AUDIOFOCUS_LOSS_TRANSIENT";
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    PlayService.getPlayer().setVolume(40, 40);
                    event = "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK";
                    break;
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                    PlayService.getPlayer().setVolume(40, 40);
                    event = "AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK";
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    PlayService.getPlayer().start();
                    event = "AUDIOFOCUS_GAIN";
                    break;
            }
            Log.d("Test", " onAudioFocusChange: " + event+ " "+ focusChange);
        }
    }
}
