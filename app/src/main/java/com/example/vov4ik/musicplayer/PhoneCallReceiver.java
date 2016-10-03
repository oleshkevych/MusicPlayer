package com.example.vov4ik.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneCallReceiver extends BroadcastReceiver {
        private static TelephonyManager telManager;
    private static boolean trigger = false;
    private static Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("test", "CALL_STATE_STARTED");
        PhoneCallReceiver.context = context;
        PhoneCallReceiver.telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (PlayService.isPlayingNow()||trigger) {
            PhoneCallReceiver.telManager.listen(PhoneCallReceiver.phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        }else{
            PhoneCallReceiver.telManager.listen(PhoneCallReceiver.phoneListener, PhoneStateListener.LISTEN_NONE);
        }
    }
//        public static void startListener(Context context) {
//            PhoneCallReceiver.telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//            PhoneCallReceiver.telManager.listen(PhoneCallReceiver.phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
//
//        }

    public static void stopListener() {
        if(PhoneCallReceiver.telManager!=null)
        PhoneCallReceiver.telManager.listen(PhoneCallReceiver.phoneListener, PhoneStateListener.LISTEN_NONE);
    }

        private static final PhoneStateListener phoneListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                try {
                    switch (state) {
                        case TelephonyManager.CALL_STATE_RINGING: {
//                            PlayService.pausePlaying();
                            Intent intent1 = new Intent(context, PlayService.class);
                            intent1.setAction(PlayService.PAUSE_ACTION);
                            context.startService(intent1);
                            Log.d("test", "CALL_STATE_RINGING");
                            trigger = true;
                            break;
                        }
                        case TelephonyManager.CALL_STATE_OFFHOOK: {
                            if (PlayService.isPlayingNow()) {
//                                PlayService.pausePlaying();
                                Intent intent1 = new Intent(context, PlayService.class);
                                intent1.setAction(PlayService.PAUSE_ACTION);
                                context.startService(intent1);
                                trigger = true;
                            }
                            Log.d("test", "CALL_STATE_OFFHOOK");

                            break;
                        }
                        case TelephonyManager.CALL_STATE_IDLE: {
                            if(trigger) {
//                                PlayService.startPlaying();
                                Intent intent1 = new Intent(context, PlayService.class);
                                intent1.setAction(PlayService.PLAY_ACTION);
                                context.startService(intent1);
                                trigger = false;
                            }
                            Log.d("test",  "CALL_STATE_IDLE");
                            break;
                        }
                        default: {
                        }
                    }
                } catch (Exception ex) {
                    Log.d("test", "RESET ERROR" + ex);
                }
//                super.onCallStateChanged(state, incomingNumber);
            }
        };

//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }


}
