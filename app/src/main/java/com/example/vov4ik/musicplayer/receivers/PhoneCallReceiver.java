package com.example.vov4ik.musicplayer.receivers;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.vov4ik.musicplayer.service.PlayService;

public class PhoneCallReceiver extends BroadcastReceiver {

    private TelephonyManager mTelephonyManager;
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("test", "CALL_STATE_STARTED");
        mContext = context;
        mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (isServiceRunning(context)) {
            mTelephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        } else {
            stopListener();
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

    public void stopListener() {
        if (mTelephonyManager != null)
            mTelephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_NONE);
    }

    private final PhoneStateListener phoneListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            try {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING: {
                        Log.d("test", "CALL_STATE_RINGING");
                        onStateChanged(true);
                        break;
                    }
                    case TelephonyManager.CALL_STATE_OFFHOOK: {
                        onStateChanged(true);
                        Log.d("test", "CALL_STATE_OFFHOOK");

                        break;
                    }
                    case TelephonyManager.CALL_STATE_IDLE: {
                        onStateChanged(false);
                        Log.d("test", "CALL_STATE_IDLE");
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

    private void onStateChanged(boolean isCalling) {
        Intent intent = new Intent(PlayService.PHONE_STATE_CHANGED_ACTION);
        intent.putExtra(PlayService.CALL_STATE_EXTRA, isCalling);
        sendBroadcastIntent(intent);
    }

    private void sendBroadcastIntent(Intent intent) {
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

}
