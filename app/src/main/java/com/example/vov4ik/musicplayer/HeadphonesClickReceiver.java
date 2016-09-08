package com.example.vov4ik.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * Created by vov4ik on 8/22/2016.
 */
public class HeadphonesClickReceiver extends BroadcastReceiver {
    static int d = 0;
    public HeadphonesClickReceiver ()
    {
        super ();
    }
    @Override
    public void onReceive(final Context context, Intent intent) {
//        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
//            KeyEvent event = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
//            Log.d("Test", KeyEvent.ACTION_DOWN+"click" +  event.getKeyCode());
//
//            PlayService.lastPressTime = PlayService.newPressTime;
//            PlayService.newPressTime = System.currentTimeMillis();
//            long delta = PlayService.newPressTime - PlayService.lastPressTime;
//            if(delta < PlayService.DOUBLE_CLICK_DELAY){
//                PlayService.myLastPressTime += delta;
//                Log.d("Test", "HEAD DISCON click" + delta);
//
//                if (79 == event.getKeyCode()) {
//                    if(PlayService.getPlayer()!=null) {
//                        if (PlayService.isPlayingNow()) {
//                            PlayService.pausePlaying();
//                            Log.d("Test", "HEAD DISCON pause" );
//                        } else {
//                            Log.d("Test", "HEAD DISCON start");
//                            PlayService.startPlaying();
//                        }
//                    } else{
//                        Intent intent1 = new Intent(context, PlayService.class);
//                        intent1.setAction("com.example.vov4ik.musicplayer.PlayService.play");
//                        context.startService(intent1);
//                    }
//                }
//            }
//            if(PlayService.myLastPressTime<150&&PlayService.myLastPressTime>43){
//                Log.d("Test", "HEAD DISCON doble" + PlayService.myLastPressTime);
//                PlayService.myLastPressTime = 0;
//                PlayService.nextSong();
//                Log.d("Test", "HEAD DISCON double" );
//
//            }
////            else if(PlayService.myLastPressTime>200){
////                PlayService.myLastPressTime =0;
////                Log.d("Test", "HEAD DISCON 0" );
////
////            }
//
//        }
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
                if (action == KeyEvent.ACTION_DOWN) {
                    d++;
                    Handler handler = new Handler();
                    Runnable r = new Runnable() {

                        @Override
                        public void run() {
                            // single click *******************************
                            if (d == 1) {
                                Toast.makeText(context, "single click!", Toast.LENGTH_SHORT).show();
                                if(PlayService.getPlayer()!=null) {
                                    if (PlayService.isPlayingNow()) {
                                        PlayService.pausePlaying();
                                    } else {
                                        PlayService.startPlaying();
                                    }
                                } else{
                                    Intent intent1 = new Intent(context, PlayService.class);
                                    intent1.setAction("com.example.vov4ik.musicplayer.PlayService.play");
                                    context.startService(intent1);
                                }
                            }
                            // double click *********************************
                            if (d == 2) {
                                Toast.makeText(context, "Double click!!", Toast.LENGTH_SHORT).show();
                                if(PlayService.getPlayer()!=null) {
                                    PlayService.myLastPressTime = 0;
                                    PlayService.nextSong();
                                }
                            }
                            if (d == 3) {
                                Toast.makeText(context, "Thre clicks!!", Toast.LENGTH_SHORT).show();
                                if(PlayService.getPlayer()!=null) {
                                    PlayService.previous();
                                }
                            }
                            d = 0;
                        }
                    };
                    if (d == 1) {
                        handler.postDelayed(r, 600);
                    }
                }break;
        }
    }
}
