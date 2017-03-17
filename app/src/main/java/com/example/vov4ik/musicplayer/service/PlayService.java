package com.example.vov4ik.musicplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.example.vov4ik.musicplayer.R;
import com.example.vov4ik.musicplayer.data.local.DbConnector;
import com.example.vov4ik.musicplayer.data.local.DbInflater;
import com.example.vov4ik.musicplayer.data.model.MusicFile;
import com.example.vov4ik.musicplayer.data.model.PlayerControls;
import com.example.vov4ik.musicplayer.screens.main.MainActivity;
import com.example.vov4ik.musicplayer.screens.player.PlayerActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by vov4ik on 8/17/2016.
 */
public class PlayService extends Service {

    public static final String CLOSE_ACTION = "com.example.vov4ik.musicplayer.service.PlayService.CLOSE";
    public static final String NEXT_ACTION = "com.example.vov4ik.musicplayer.service.PlayService.NEXT";
    public static final String PREV_ACTION = "com.example.vov4ik.musicplayer.service.PlayService.PREVIOUS";
    public static final String PAUSE_ACTION = "com.example.vov4ik.musicplayer.service.PlayService.PAUSE";
    public static final String START_SERVICE_ACTION = "com.example.vov4ik.musicplayer.service.PlayService.START";
    public static final String START_SERVICE_FOR_PLAYING_ACTION = "com.example.vov4ik.musicplayer.service.PlayService.START_SERVICE_FOR_PLAYING_ACTION";
    public static final String PLAY_ACTION = "com.example.vov4ik.musicplayer.service.PlayService.PLAY";
    public static final String PLAY_URI_ACTION = "com.example.vov4ik.musicplayer.service.PlayService.PLAY_URI_ACTION";
    public static final String ADD_LIST_PATHS_ACTION = "com.example.vov4ik.musicplayer.service.PlayService.ADD_LIST";
    public static final String ADD_LIST_MUSIC_FILES_ACTION = "com.example.vov4ik.musicplayer.service.PlayService.ADD_LIST_MUSIC_FILES_ACTION";
    public static final String SEND_LIST_ACTION = "com.example.vov4ik.musicplayer.service.PlayService.SEND_LIST_ACTION";
    public static final String PLAY_TIME_CHANGED_ACTION = "com.example.vov4ik.musicplayer.service.PlayService.PLAY_TIME_CHANGED_ACTION";
    public static final String SET_SHUFFLE_CHANGED_ACTION = "com.example.vov4ik.musicplayer.service.PlayService.SET_SHUFFLE_CHANGED_ACTION";
    public static final String SET_MUSIC_FILES_ACTION = "com.example.vov4ik.musicplayer.service.PlayService.SET_MUSIC_FILES_ACTION";
    public static final String START_FILE_ACTION = "com.example.vov4ik.musicplayer.service.PlayService.START_FILE_ACTION";
    public static final String CLICK_ACTION = "com.example.vov4ik.musicplayer.service.PlayService.CLICK_ACTION";
    public static final String PHONE_STATE_CHANGED_ACTION = "com.example.vov4ik.musicplayer.service.PlayService.PHONE_STATE_CHANGED_ACTION";
    public static final String PLAY_FILE_ACTION = "com.example.vov4ik.musicplayer.service.PlayService.PLAY_FILE_ACTION";

    //Audio focus listener
    public static final String PLAY_FOCUS_ACTION = "com.example.vov4ik.musicplayer.service.PlayService.PLAY_FOCUS_ACTION";
    public static final String PAUSE_FOCUS_ACTION = "com.example.vov4ik.musicplayer.service.PlayService.PAUSE_FOCUS_ACTION";
    public static final String VOLUME_LOW_ACTION = "com.example.vov4ik.musicplayer.service.PlayService.VOLUME_LOW_ACTION";
    public static final String VOLUME_UP_ACTION = "com.example.vov4ik.musicplayer.service.PlayService.VOLUME_UP_ACTION";

    public static final String FILE_EXTRA = "com.example.vov4ik.musicplayer.service.PlayService.FILE_EXTRA";
    public static final String LIST_EXTRA = "com.example.vov4ik.musicplayer.service.PlayService.LIST_EXTRA";
    public static final String NEW_TIME_EXTRA = "com.example.vov4ik.musicplayer.service.PlayService.NEW_TIME_EXTRA";
    public static final String FILE_URI_EXTRA = "com.example.vov4ik.musicplayer.service.PlayService.FILE_URI_EXTRA";
    public static final String CALL_STATE_EXTRA = "com.example.vov4ik.musicplayer.service.PlayService.CALL_STATE_EXTRA";

    public static final String ACTION_UPDATE = "com.example.vov4ik.musicplayer.service.PlayService.ACTION_UPDATE";
    public static final String ACTION_SEND_MUSIC_FILES = "com.example.vov4ik.musicplayer.service.PlayService.ACTION_SEND_MUSIC_FILES";

    public static final String EXTRA_PLAYER_CONTROLS = "com.example.vov4ik.musicplayer.service.PlayService.EXTRA_PLAYER_CONTROLS";
    public static final String EXTRA_LIST_MUSIC_FILES = "com.example.vov4ik.musicplayer.service.PlayService.EXTRA_LIST_MUSIC_FILES";


    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, PlayService.class);
        intent.setAction(START_SERVICE_ACTION);
        return intent;
    }

    public static Intent getIntentForPlaying(Context context) {
        Intent intent = new Intent(context, PlayService.class);
        intent.setAction(START_SERVICE_FOR_PLAYING_ACTION);
        return intent;
    }


    private final int TIME_SLEEP = 1000;

    private List<MusicFile> mMusicFiles;

    private int mLastPlayedTime;
    private int mDuration;
    private MediaPlayer mPlayer;
    private MusicFile mPlayingFile;
    private NotificationManager mNotificationManager;
    private boolean mIsShuffle;
    private boolean mIsPlayingBefore;
    private boolean mIsNextSong;
    private List<MusicFile> mShufflePaths;
    private Notification mNotification;
    private Thread mThread;
    private boolean mTrigger = true;
    private boolean mIsImage;

    private void setPath(MusicFile musicFiles) {
        setPath(Arrays.asList(musicFiles));
    }

    private void setPath(List<MusicFile> musicFiles) {
        setMusicFiles(musicFiles);
        makeShufflePath();
    }

    private void addPaths(List<MusicFile> musicFiles) {
        addMusicFiles(musicFiles);
        makeShufflePath();
    }

    private void setMusicFiles(List<MusicFile> musicFiles) {
        mMusicFiles = musicFiles;
    }

    private void addOneMusicFile(MusicFile musicFile) {
        if (mMusicFiles == null) mMusicFiles = new ArrayList<>();
        mMusicFiles.add(musicFile);
    }

    private void addMusicFiles(List<MusicFile> musicFiles) {
        if (mMusicFiles == null) mMusicFiles = new ArrayList<>();
        mMusicFiles.addAll(musicFiles);
    }

    private void setShuffleMode(boolean isShuffle) {
        mIsShuffle = isShuffle;
        broadcastUpdatePlayerActivity();
    }

    private void makeShufflePath() {
        mShufflePaths = new ArrayList<>();
        int lengthOfArray = mMusicFiles.size();

        int[] mShuffleIdsArray = new int[lengthOfArray];
        for (int i = 0; i < lengthOfArray; i++) {
            mShuffleIdsArray[i] = i;
        }
        Random rnd = new Random();
        for (int i = lengthOfArray - 1; i > 1; i--) {
            int index = rnd.nextInt(i + 1);
            int a = mShuffleIdsArray[index];
            mShuffleIdsArray[index] = mShuffleIdsArray[i];
            mShuffleIdsArray[i] = a;
        }

        for (int j = 0; j < mShuffleIdsArray.length; j++) {
            mShufflePaths.add(j, mMusicFiles.get(mShuffleIdsArray[j]));
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timerTread();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Test", "Onstartcommand " + Thread.currentThread().getName());
//        Log.d("Test", " "+mPaths.toString());
        if (intent != null) {
            Log.d("Test", "Main A 1");
        }
        if (intent != null && intent.getAction() != null) {
            Log.d("Test", "Onstartcommand A " + intent.getAction());
        }
        if (intent != null && intent.getStringExtra("command") != null) {
            Log.d("Test", "Onstartcommand A " + intent.getAction());
        }
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case START_SERVICE_ACTION:
                    registerBroadcastReceiver();
                    setPreviousValues();
                    startService(AutoAudioStopper.getIntent(PlayService.this));
                    break;
                case START_SERVICE_FOR_PLAYING_ACTION:
                    registerBroadcastReceiver();
                    setPreviousValues();
                    startService(AutoAudioStopper.getIntent(PlayService.this));
                    play();
                    break;
                case CLOSE_ACTION:
                    stopService();
                    return START_NOT_STICKY;
                case NEXT_ACTION:
                    nextSong();
                    break;
                case PREV_ACTION:
                    previous();
                    break;
                case PAUSE_ACTION:
                    pausePlaying();
                    break;
                case PLAY_ACTION:
                    play();
                    break;
            }


            sendNotification();
            startForeground(1, mNotification);
            return START_STICKY;
        }
        return START_STICKY;
    }

    private void headphonesClicked() {
        if (isPlayingNow()) {
            pausePlaying();
        } else {
            play();
        }
    }

    private void startFromUri(Uri file) {
        MusicFile musicFile = convertToMusicFile(file.getPath());
        setPath(musicFile);
        startFromNew(musicFile);
    }

    private MusicFile convertToMusicFile(String path) {
        return new DbInflater().createMusicFile(new File(path));

    }

    private void startFromNew(MusicFile musicFile) {
        if (mMusicFiles != null) mMusicFiles.clear();
        if (mShufflePaths != null) mShufflePaths.clear();
        mLastPlayedTime = 0;
        addOneMusicFile(musicFile);
        setShuffleMode(false);
        mPlayingFile = musicFile;
        playFile();
    }

    private void setPreviousValues() {
        try {
            mLastPlayedTime = DbConnector.getLastPlayTime(PlayService.this);
            setPath(DbConnector.getLastPlayList(PlayService.this));
            mPlayingFile = mMusicFiles.get(DbConnector.getLastPlayNumber(PlayService.this));
            mIsShuffle = DbConnector.getLastPlayState(PlayService.this);
        } catch (IndexOutOfBoundsException i1) {
            Log.e("Error", "Previous Values didn't be found");
        }
        if(mPlayingFile == null){
            setPath(DbConnector.getMusicFiles(PlayService.this));
            Random r = new Random();
            mLastPlayedTime = 0;
            mIsShuffle = true;
            mPlayingFile = mMusicFiles.get(r.nextInt(mMusicFiles.size() - 1));
        }
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(mPlayingFile.getPath());
        mDuration = Integer.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        mmr.release();
    }

    private void startFromPrevious() {
        setPreviousValues();
        playFile();
    }

    private void play() {
        if (mPlayingFile != null) {
            playFile();
        } else {
            startFromPrevious();
        }
    }

    private void audioFocusPlay(){
        if(isPlayingNow()) return;
        play();
    }

    private void playFile() {

        mIsNextSong = false;
//        if ((mThread != null) && mThread.isAlive()) {
//            mThread.interrupt();
//        }
//        try {
//            mThread = new Thread(
//                    new Runnable() {
//                        @Override
//                        public void run() {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        } else {
            try {
//                                    mPlayer.stop();
                mPlayer.reset();
            } catch (IllegalStateException e) {
                Log.e("test", "RESET ERROR" + e);
                mPlayer = new MediaPlayer();
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }
        }

        try {
//                                Uri u = Uri.fromFile(new File(mPlayingFile.getPath()));
            mPlayer.setDataSource(mPlayingFile.getPath());
            mPlayer.setWakeMode(PlayService.this, PowerManager.PARTIAL_WAKE_LOCK);
            mPlayer.setScreenOnWhilePlaying(false);
            setVolume(1.0f);
            mPlayer.prepare();
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mPlayer.seekTo(mLastPlayedTime);
                    mPlayer.start();
                    mDuration = duration();
                    broadcastAudioFocusAction(AutoAudioStopper.START_FOCUS);
                    sendNotification();
                }
            });

            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mIsNextSong = true;
                    nextSong();

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            playFile();
        }

//                        }
//                    }
//            );
//            mThread.start();
//        } catch (Exception e1) {
//            e1.printStackTrace();
//            mThread.interrupt();
//            playFile();
//        }
    }

    private void nextSong() {
        mLastPlayedTime = 0;
        List<MusicFile> path;
        if (mIsShuffle) {
            path = mShufflePaths;
        } else {
            path = mMusicFiles;
        }
        if (path.size() > 0) {
            int trekNumber = path.indexOf(mPlayingFile);
            if ((trekNumber + 1) < path.size()) {
                mPlayingFile = path.get(trekNumber + 1);
            } else {
                mPlayingFile = path.get(0);
            }
            if (isPlayingNow() || mIsNextSong)
                playFile();
        }
    }

    private void previous() {
        if (mPlayingFile == null) return;
        List<MusicFile> path;
        if (mIsShuffle) {
            path = mShufflePaths;
        } else {
            path = mMusicFiles;
        }
        int trekNumber = path.indexOf(mPlayingFile);
        if (isPlayingNow()) {
            if (mLastPlayedTime > 10000) {
                mLastPlayedTime = 0;
            } else {
                mLastPlayedTime = 0;
                if ((trekNumber - 1) >= 0) {
                    mPlayingFile = path.get(trekNumber - 1);
                } else {
                    mPlayingFile = path.get(path.size() - 1);
                }
            }
            playFile();
        } else {
            mLastPlayedTime = 0;
            if ((trekNumber - 1) >= 0) {
                mPlayingFile = path.get(trekNumber - 1);
            } else {
                mPlayingFile = path.get(path.size() - 1);
            }
        }

    }

    private void setVolume(float level) {
        if (mPlayer != null)
            mPlayer.setVolume(level, level);
    }

    private boolean isPlayingNow() {
        try {
            return mPlayer != null && mPlayer.isPlaying();
        } catch (IllegalStateException e) {
            return false;
        }
    }

    private void onPauseMediaPlayer() {
        if (mPlayer != null) {
            mLastPlayedTime = mPlayer.getCurrentPosition();
            mPlayer.pause();
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            sendNotification();


            int trekNumber = mMusicFiles.indexOf(mPlayingFile);
            if (mIsShuffle) {
                trekNumber = mShufflePaths.indexOf(mPlayingFile);
            }
            DbConnector.setLastPlayList(PlayService.this, mMusicFiles);
            DbConnector.setPlaylistAttributes(PlayService.this, mLastPlayedTime, trekNumber, mIsShuffle);
        }
    }

    private void pausePlayingFocus() {
        onPauseMediaPlayer();
    }


    private void pausePlaying() {
        onPauseMediaPlayer();
        broadcastAudioFocusAction(AutoAudioStopper.STOP_FOCUS);
    }

    private int duration() {
        if (mPlayer != null) {
            return mPlayer.getDuration();
        }
        return 0;
    }

    private int currentTime() {
        if (mPlayer == null) {
            return mLastPlayedTime;
        }
        mLastPlayedTime = mPlayer.getCurrentPosition();
        return mLastPlayedTime;
    }

    private String trekName() {
        try {
            String title, artist;
            try {
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(mPlayingFile.getPath());
                title = (mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
                artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                mmr.release();
            } catch (IllegalArgumentException e) {
                title = "file";
                artist = "no";
            }
            if ((title == null) || (title.equals("")) || (artist == null) || (artist.equals("")) ||
                    (title.equals(" ")) || (artist.equals(" "))) {
                return new File(mPlayingFile.getPath()).getName();
            } else {
                return artist + " - " + title;
            }
        } catch (Exception c) {
            Log.d("Error", c.getMessage());
            setPreviousValues();
            return " - ";
        }
    }

    private void setNewTime(int newTime) {
        mLastPlayedTime = newTime;
        if (isPlayingNow())
            mPlayer.seekTo(mLastPlayedTime);
    }

    private void sendNotification() {

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(PlayService.this);
        String songTitle = trekName();
        builder
//                .setContentTitle("Player")
//                .setColor(color)
//                .setContentText(allTitle)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(false)
                .setOngoing(true);

        Intent intent = new Intent(PlayService.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(PlayService.this, 0, intent, 0);
        builder.setContentIntent(pIntent);

        RemoteViews rw = new RemoteViews(PlayService.this.getPackageName(), R.layout.notification);
//            rw.setTextViewText(R.id.notification_artist, artist);
        rw.setTextViewText(R.id.notification_trek, songTitle);
        Bitmap icon = getImage();
        if (icon != null) {
            mIsImage = true;
            rw.setImageViewBitmap(R.id.icon_notification, icon);
        } else {
            mIsImage = false;
            rw.setImageViewResource(R.id.icon_notification, R.drawable.default_notification_icon);
        }
        if (isPlayingNow()) {
            rw.setViewVisibility(R.id.playButtonNotif, View.GONE);
            rw.setViewVisibility(R.id.pauseButtonNotif, View.VISIBLE);
            builder.setSmallIcon(R.drawable.ic_pause_white_24dp);
        } else {
            rw.setViewVisibility(R.id.playButtonNotif, View.VISIBLE);
            rw.setViewVisibility(R.id.pauseButtonNotif, View.GONE);
            builder.setSmallIcon(R.drawable.ic_play_white_24dp);
        }
        Intent closeIntent = new Intent(PlayService.this, PlayService.class);
        closeIntent.setAction(CLOSE_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(PlayService.this, 0,
                closeIntent, 0);

        Intent previousIntent = new Intent(PlayService.this, PlayService.class);
        previousIntent.setAction(PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(PlayService.this, 0,
                previousIntent, 0);

        Intent playIntent = new Intent(PlayService.this, PlayService.class);
        playIntent.setAction(PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(PlayService.this, 0,
                playIntent, 0);

        Intent pauseIntent = new Intent(PlayService.this, PlayService.class);
        pauseIntent.setAction(PAUSE_ACTION);
        PendingIntent ppauseIntent = PendingIntent.getService(PlayService.this, 0,
                pauseIntent, 0);

        Intent nextIntent = new Intent(PlayService.this, PlayService.class);
        nextIntent.setAction(NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(PlayService.this, 0,
                nextIntent, 0);
        Intent openIntent = new Intent(PlayService.this, PlayerActivity.class);
        openIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent popenIntent = PendingIntent.getService(PlayService.this, 0,
                openIntent, 0);


        rw.setOnClickPendingIntent(R.id.closeNotif, pcloseIntent);
        rw.setOnClickPendingIntent(R.id.icon_notification, popenIntent);
        rw.setOnClickPendingIntent(R.id.playButtonNotif, pplayIntent);
        rw.setOnClickPendingIntent(R.id.pauseButtonNotif, ppauseIntent);
        rw.setOnClickPendingIntent(R.id.nextButtonNotif, pnextIntent);
        rw.setOnClickPendingIntent(R.id.previousButtonNotif, ppreviousIntent);


        builder.setCustomContentView(rw);
        mNotification = builder.build();
//        mNotification.contentView = rw;


        mNotificationManager.notify(1, mNotification);

    }

    private Bitmap getImage() {
        Bitmap image;
        try {
            MediaMetadataRetriever mData = new MediaMetadataRetriever();
            mData.setDataSource(mPlayingFile.getPath());
            try {
                byte art[] = mData.getEmbeddedPicture();
                image = BitmapFactory.decodeByteArray(art, 0, art.length);
            } catch (Exception e) {
                image = null;
            }

            mData.release();
        } catch (IllegalArgumentException e) {
            image = null;
        }
        return image;
    }

    private void broadcastUpdatePlayerActivity() {
        Intent intent = new Intent(ACTION_SEND_MUSIC_FILES);

        List<MusicFile> fileList = new ArrayList<MusicFile>();
        if (mMusicFiles != null && mMusicFiles.size() > 0) {
            if (mIsShuffle) {
                fileList = mShufflePaths;
            } else {
                fileList = mMusicFiles;
            }
        }
        intent.putParcelableArrayListExtra(EXTRA_LIST_MUSIC_FILES, (ArrayList<? extends Parcelable>) fileList);
        sendBroadcastIntent(intent);
    }

    private void timerTread() {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        mTrigger = true;
                        while (mTrigger) {
//                            if (isPlayingNow()) {
                            PlayerControls playerControls = new PlayerControls(
                                    isPlayingNow(),
                                    mIsShuffle,
                                    trekName(),
                                    mDuration,
                                    isPlayingNow() ? currentTime() : mLastPlayedTime,
                                    mIsImage,
                                    isPlayingNow() ? mPlayingFile : new MusicFile());
                            broadcastUpdate(playerControls);
//                            }
                            try {
                                Thread.sleep(TIME_SLEEP);
                            } catch (InterruptedException e) {
                                Log.d("Test", "sleep failure");
                            }
                        }
                    }
                }
        ).start();
    }

    private void onPhoneCallStateChanged(boolean isCallingTrigger) {
        if (mIsPlayingBefore && isCallingTrigger) {
            play();
            mIsPlayingBefore = false;
        } else if (!isCallingTrigger && isPlayingNow()) {
            pausePlaying();
            mIsPlayingBefore = true;
        }
    }

    private void stopService() {
        broadcastAudioFocusAction(AutoAudioStopper.CLOSE);
        unregisterBroadcastReceiver();
        if ((mThread != null) && mThread.isAlive()) {
            mThread.interrupt();
        }
        mTrigger = false;
        if (mPlayer != null) {
            int trekNumber = mMusicFiles.indexOf(mPlayingFile);
            if (mIsShuffle) {
                trekNumber = mShufflePaths.indexOf(mPlayingFile);
            }
            if (isPlayingNow()) {
                pausePlaying();
            }
            PlayerControls playerControls = new PlayerControls(
                    false,
                    mIsShuffle,
                    trekName(),
                    mDuration,
                    mLastPlayedTime,
                    mIsImage,
                    mPlayingFile);
            broadcastUpdate(playerControls);
            DbConnector.setLastPlayList(PlayService.this, mMusicFiles);
            DbConnector.setPlaylistAttributes(PlayService.this, mLastPlayedTime, trekNumber, mIsShuffle);
        }
        stopForeground(true);
        mNotificationManager.cancel(1);
        mNotificationManager.cancelAll();
        stopSelf();
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(PlayService.CLOSE_ACTION);
        filter.addAction(PlayService.NEXT_ACTION);
        filter.addAction(PlayService.PREV_ACTION);
        filter.addAction(PlayService.PAUSE_ACTION);
        filter.addAction(PlayService.PLAY_ACTION);
        filter.addAction(PlayService.PLAY_TIME_CHANGED_ACTION);
        filter.addAction(PlayService.ADD_LIST_PATHS_ACTION);
        filter.addAction(PlayService.ADD_LIST_MUSIC_FILES_ACTION);
        filter.addAction(PlayService.SEND_LIST_ACTION);
        filter.addAction(PlayService.START_FILE_ACTION);
        filter.addAction(PlayService.SET_SHUFFLE_CHANGED_ACTION);
        filter.addAction(PlayService.SET_MUSIC_FILES_ACTION);
        filter.addAction(PLAY_URI_ACTION);
        filter.addAction(CLICK_ACTION);
        filter.addAction(PHONE_STATE_CHANGED_ACTION);
        filter.addAction(PLAY_FILE_ACTION);
        filter.addAction(PAUSE_FOCUS_ACTION);
        filter.addAction(VOLUME_LOW_ACTION);
        filter.addAction(VOLUME_UP_ACTION);
        filter.addAction(PLAY_FOCUS_ACTION);
        LocalBroadcastManager.getInstance(PlayService.this)
                .registerReceiver(mBroadcastReceiver, filter);
    }

    private void unregisterBroadcastReceiver() {
        LocalBroadcastManager.getInstance(PlayService.this)
                .unregisterReceiver(mBroadcastReceiver);
    }

    final BroadcastReceiver mBroadcastReceiver
            = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case CLOSE_ACTION:
                    stopService();
                    break;
                case NEXT_ACTION:
                    nextSong();
                    break;
                case PREV_ACTION:
                    previous();
                    break;
                case PAUSE_ACTION:
                    pausePlaying();
                    break;
                case PLAY_ACTION:
                    play();
                    break;
                case ADD_LIST_MUSIC_FILES_ACTION:
                    List<MusicFile> musicList = intent.getParcelableArrayListExtra(LIST_EXTRA);
                    addPaths(musicList);
                    break;
                case SEND_LIST_ACTION:
                    broadcastUpdatePlayerActivity();
                    break;
                case PLAY_TIME_CHANGED_ACTION:
                    setNewTime(intent.getIntExtra(NEW_TIME_EXTRA, 0));
                    break;
                case SET_SHUFFLE_CHANGED_ACTION:
                    setShuffleMode(!mIsShuffle);
                    break;
                case SET_MUSIC_FILES_ACTION:
                    List<MusicFile> files = intent.getParcelableArrayListExtra(LIST_EXTRA);
                    setPath(files);
                    break;
                case START_FILE_ACTION:
                    mLastPlayedTime = 0;
                    mPlayingFile = intent.getParcelableExtra(FILE_EXTRA);
                    play();
                    break;
                case PLAY_URI_ACTION:
                    mLastPlayedTime = 0;
                    Uri fileUri = intent.getParcelableExtra(FILE_URI_EXTRA);
                    startFromUri(fileUri);
                    break;
                case PAUSE_FOCUS_ACTION:
                    pausePlayingFocus();
                    break;
                case VOLUME_LOW_ACTION:
                    setVolume(0.2f);
                    break;
                case VOLUME_UP_ACTION:
                    setVolume(1.0f);
                    break;
                case PLAY_FOCUS_ACTION:
                    audioFocusPlay();
                    break;
                case CLICK_ACTION:
                    headphonesClicked();
                    break;
                case PHONE_STATE_CHANGED_ACTION:
                    onPhoneCallStateChanged(intent.getBooleanExtra(CALL_STATE_EXTRA, true));
                    break;
                case PLAY_FILE_ACTION:
                    MusicFile musicFile = intent.getParcelableExtra(FILE_EXTRA);
                    startFromNew(musicFile);
                    break;
            }
        }
    };

    private void broadcastUpdate(PlayerControls playerControls) {
        Intent intent = new Intent(ACTION_UPDATE);
        intent.putExtra(EXTRA_PLAYER_CONTROLS, playerControls);
        sendBroadcastIntent(intent);
    }

    private void broadcastAudioFocusAction(String action) {
        sendBroadcastIntent(new Intent(action));
    }

    private void sendBroadcastIntent(Intent intent) {
        LocalBroadcastManager.getInstance(PlayService.this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        Log.d("Test", "DESTROY!");
        super.onDestroy();
    }
}
