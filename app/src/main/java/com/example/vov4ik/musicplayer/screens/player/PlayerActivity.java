package com.example.vov4ik.musicplayer.screens.player;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vov4ik.musicplayer.data.model.MusicFile;
import com.example.vov4ik.musicplayer.data.model.PlayerControls;
import com.example.vov4ik.musicplayer.service.AutoAudioStopper;
import com.example.vov4ik.musicplayer.service.PlayService;
import com.example.vov4ik.musicplayer.R;
import com.example.vov4ik.musicplayer.data.local.DbConnector;
import com.example.vov4ik.musicplayer.screens.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PlayerActivity extends AppCompatActivity implements View.OnClickListener {


    public static Intent getIntent(Context context) {
        return new Intent(context, PlayerActivity.class);
    }

    private static final int UI_ANIMATION_DELAY = 300;

    private final Handler mHideHandler = new Handler();
    private View mContentView;

    public List<String> mPlaylistList = new ArrayList<>();
    private boolean mIsTimeChanging;
    private List<MusicFile> mMusicFiles;
    private RecyclerAdapterForPlayerActivity mAdapter;

    public void setMusicFiles(List<MusicFile> musicFiles) {
        //Add paths from Service if it is working
        mMusicFiles = new ArrayList<>(musicFiles.size());
        mMusicFiles.addAll(musicFiles);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mContentView = findViewById(R.id.fullscreen_content);

        setupPlaybackControlButtons();

        setupSeekBar();
        setupListControlsButton();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.player_activity_recycler_view);
        assert recyclerView != null;
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new RecyclerAdapterForPlayerActivity(PlayerActivity.this);
        mAdapter.setOnClickListener(mOnClickListener);
        recyclerView.setAdapter(mAdapter);
    }

    private void setupPlaybackControlButtons() {
        findViewById(R.id.openPlayerList).setVisibility(View.INVISIBLE);
        final ImageButton playButton = (ImageButton) findViewById(R.id.playButton);
        assert playButton != null;
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isServiceRunning(PlayService.class)) {
                    startService(PlayService.getIntentForPlaying(PlayerActivity.this));
                }else {
                    onPlayClick();
                }

            }
        });
        final ImageButton pauseButton = (ImageButton) findViewById(R.id.pauseButton);
        assert pauseButton != null;
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPauseClick();

            }
        });
        ImageButton nextButton = (ImageButton) findViewById(R.id.nextButton);
        assert nextButton != null;
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextClick();

            }
        });
        final ImageButton previousButton = (ImageButton) findViewById(R.id.previousButton);
        assert previousButton != null;
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPreviousClick();
            }
        });

        final ImageButton shuffleOff = (ImageButton) findViewById(R.id.shuffleOff);
        assert shuffleOff != null;
        shuffleOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuffleOff.setVisibility(View.GONE);
                findViewById(R.id.shuffleOn).setVisibility(View.VISIBLE);
                onShuffleClick();
            }
        });
        final ImageButton shuffleOn = (ImageButton) findViewById(R.id.shuffleOn);
        assert shuffleOn != null;
        shuffleOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuffleOff.setVisibility(View.VISIBLE);
                shuffleOn.setVisibility(View.GONE);
                onShuffleClick();
            }
        });
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void setupSeekBar() {
        SeekBar seekBar = (SeekBar) findViewById(R.id.seek_bar);
        assert seekBar != null;
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    TextView currentTime = (TextView) findViewById(R.id.current_time);
                    assert currentTime != null;

                    String current = progress / 60000 + " : " + (((progress / 1000) % 60 > 10) ? ((progress / 1000) % 60) : ("0" + (progress / 1000) % 60));
                    currentTime.setText(current);

                    seekBar.setProgress(progress);
                    progressChanged = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsTimeChanging = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mIsTimeChanging = false;
                setNewPlayingTime(progressChanged);
            }
        });
    }

    private void setupListControlsButton() {
        final Button checkAllButton = (Button) findViewById(R.id.checkAll);
        assert checkAllButton != null;
        checkAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.setSelectedAll();
            }
        });

        Button remove = (Button) findViewById(R.id.removeButton);
        assert remove != null;
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<MusicFile> selectedList = mAdapter.getSelectedMusicFiles();
                if (selectedList.size() > 0) {
                    for (MusicFile s : selectedList) {
                        mMusicFiles.remove(s);
                    }
                    updateServiceList();
                    FrameLayout main = (FrameLayout) findViewById(R.id.menu_frame);
                    assert main != null;
                    main.setVisibility(FrameLayout.VISIBLE);
                    LinearLayout remove = (LinearLayout) findViewById(R.id.remove_layout);
                    assert remove != null;
                    remove.setVisibility(LinearLayout.GONE);
                    final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.playListLayoutInPLayerActivity);
                    assert linearLayout != null;
                    linearLayout.setVisibility(ViewPager.GONE);
                    ScrollView pager = (ScrollView) findViewById(R.id.fullscreen_content);
                    assert pager != null;
                    pager.setVisibility(View.VISIBLE);
                    mAdapter.setUpAdapter(mMusicFiles);
                }
            }
        });
        Button addToPlayList = (Button) findViewById(R.id.addToPlaylist);
        assert addToPlayList != null;
        addToPlayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<MusicFile> selectedList = mAdapter.getSelectedMusicFiles();
                if (selectedList.size() == 0) {
                    Toast.makeText(getApplicationContext(), "No selected songs!", Toast.LENGTH_SHORT).show();
                } else {
                    ScrollView pager = (ScrollView) findViewById(R.id.fullscreen_content);
                    assert pager != null;
                    pager.setVisibility(ViewPager.GONE);
                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.playListLayoutInPLayerActivity);
                    assert linearLayout != null;
                    linearLayout.setVisibility(LinearLayout.VISIBLE);
                    mPlaylistList.add("+Add Playlist");
                    mPlaylistList.addAll(DbConnector.getPlaylist(getApplicationContext()));
                    showPlaylistView();
                }
            }
        });

    }

    final RecyclerAdapterForPlayerActivity.OnClickListener mOnClickListener = new RecyclerAdapterForPlayerActivity.OnClickListener() {
        @Override
        public void onMusicClick(MusicFile musicFile) {
            onClick(musicFile);
        }

        @Override
        public void onLongClick(MusicFile musicFile) {
            FrameLayout main = (FrameLayout) findViewById(R.id.menu_frame);
            assert main != null;
            main.setVisibility(FrameLayout.GONE);
            LinearLayout remove = (LinearLayout) findViewById(R.id.remove_layout);
            assert remove != null;
            remove.setVisibility(LinearLayout.VISIBLE);
            mAdapter.setSelectedVisible(musicFile);
        }

    };

    private void showPlaylistView() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.showPlaylistLayoutInPLayerActivity);
        assert linearLayout != null;
        linearLayout.removeAllViews();
        Drawable backgroundForAll;
//        if (mIsBackgroundImage) {
        backgroundForAll = getResources().getDrawable(R.drawable.background_if_this_is_present);
//        } else {
//            backgroundForAll = null;
//        }
        for (String s : mPlaylistList) {
            TextView text = new TextView(linearLayout.getContext());
            text.setText(String.valueOf(s));
            text.setId((mPlaylistList).indexOf(s));
            linearLayout.addView(text);
            text.setOnClickListener(this);
            text.setPadding(20, 10, 20, 10);
            text.setTextSize(18);
            text.setBackground(backgroundForAll);
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) text.getLayoutParams();
            mlp.setMargins(40, 15, 0, 15);
        }
    }

    @Override
    public void onClick(View v) {
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.playListLayoutInPLayerActivity);
        assert linearLayout != null;
        {
            if (v.getId() == 0 || mPlaylistList.get(v.getId()).equals("No Playlists available")) {
                if (mPlaylistList.contains("No Playlists available")) {
                    mPlaylistList.remove("No Playlists available");
                }
                final LinearLayout inputLayout = (LinearLayout) findViewById(R.id.inputLayoutInPLayerActivity);
                assert inputLayout != null;
                inputLayout.setVisibility(View.VISIBLE);
                final EditText editText = (EditText) findViewById(R.id.addNewPlaylistInPLayerActivity);
                assert editText != null;
                Button addButton = (Button) findViewById(R.id.confirmInPLayerActivity);
                assert addButton != null;
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        if (mPlaylistList.contains(editText.getText().toString())) {
                            Toast.makeText(getApplicationContext(), "This name exist!!", Toast.LENGTH_SHORT).show();
                        } else {
                            mPlaylistList.add(editText.getText().toString());

                        }
                        inputLayout.setVisibility(View.GONE);
                        showPlaylistView();
                    }
                });

            } else {
                List<MusicFile> selectedList = mAdapter.getSelectedMusicFiles();
                DbConnector.setPlaylist(getApplicationContext(), mPlaylistList.get(v.getId()), selectedList);
                ScrollView pager = (ScrollView) findViewById(R.id.fullscreen_content);
                assert pager != null;
                pager.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(LinearLayout.GONE);
                LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.showPlaylistLayoutInPLayerActivity);
                assert linearLayout1 != null;
                linearLayout1.removeAllViews();
                mPlaylistList = new ArrayList<>();
                FrameLayout main = (FrameLayout) findViewById(R.id.menu_frame);
                assert main != null;
                main.setVisibility(FrameLayout.VISIBLE);
                LinearLayout remove = (LinearLayout) findViewById(R.id.remove_layout);
                assert remove != null;
                remove.setVisibility(LinearLayout.GONE);
                mAdapter.unselect();
            }
        }
    }

    private void backgroundWriter(Bitmap image) {
        FrameLayout main = (FrameLayout) findViewById(R.id.playerActivityMainFrame);
        assert main != null;
        if (image != null) {
            BitmapDrawable ob = new BitmapDrawable(getResources(), image);
            main.setBackground(ob);
        } else {
            main.setBackgroundColor(getResources().getColor(R.color.colorMainBackgroundForPlayerActivity));
        }
    }

    private class GetBitmapTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... musicFilePath) {

            Bitmap image;
            try {
                MediaMetadataRetriever mData = new MediaMetadataRetriever();
                mData.setDataSource(musicFilePath[0]);
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

        @Override
        protected void onPostExecute(Bitmap image) {
            backgroundWriter(image);

        }
    }

    private void buttonChanger(PlayerControls controls) {
        ImageButton playButton = (ImageButton) findViewById(R.id.playButton);
        ImageButton pauseButton = (ImageButton) findViewById(R.id.pauseButton);
        if (controls.isPlaying()) {
            playButton.setVisibility(View.GONE);
            pauseButton.setVisibility(View.VISIBLE);
        } else {
            playButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.GONE);
        }
        if (controls.isShuffle()) {
            findViewById(R.id.shuffleOn).setVisibility(View.VISIBLE);
            findViewById(R.id.shuffleOff).setVisibility(View.GONE);
        } else {
            findViewById(R.id.shuffleOn).setVisibility(View.GONE);
            findViewById(R.id.shuffleOff).setVisibility(View.VISIBLE);
        }
    }

    private void progressWriter(PlayerControls controls) {
        TextView currentTime = (TextView) findViewById(R.id.current_time);
        assert currentTime != null;
        TextView durationTime = (TextView) findViewById(R.id.current_duration);
        assert durationTime != null;
        TextView song = (TextView) findViewById(R.id.trek_name);
        assert song != null;
        SeekBar seekBar = (SeekBar) findViewById(R.id.seek_bar);
        assert seekBar != null;

        int dur = controls.getDuration();
        int cur = controls.getCurrentTime();
        String current = cur / 60000 + " : " + (((cur / 1000) % 60 >= 10) ? ((cur / 1000) % 60) : ("0" + (cur / 1000) % 60));
        String duration = dur / 60000 + " : " + (((dur / 1000) % 60 >= 10) ? ((dur / 1000) % 60) : ("0" + (dur / 1000) % 60));

        currentTime.setText(current);

        durationTime.setText(duration);

        if(!song.getText().equals(controls.getTrackName()))
            song.setText(controls.getTrackName());

        seekBar.setMax(dur);
        seekBar.setProgress(cur);
        seekBar.setSecondaryProgress(dur);

    }

    public void showViews(PlayerControls playerControls) {
        boolean isTrackChanged = !playerControls.getMusicFile().equals(mAdapter.getPlayingTrack());
        if (isTrackChanged) {
            mAdapter.setPlayingFile(playerControls.getMusicFile(), playerControls.isPlaying(), playerControls.isImageExist());
            if (playerControls.isImageExist()) {
                new GetBitmapTask().execute(playerControls.getMusicFile().getPath());
            }
        }
    }

    public void onClick(MusicFile musicFile) {
        sendStartPlayAction(musicFile);
    }

    @Override
    public void onBackPressed() {
//        moveTaskToBack(true);
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.playListLayoutInPLayerActivity);
        assert linearLayout != null;
        if (linearLayout.getVisibility() == ViewPager.VISIBLE) {
            linearLayout.setVisibility(ViewPager.GONE);
            ScrollView pager = (ScrollView) findViewById(R.id.fullscreen_content);
            assert pager != null;
            pager.setVisibility(View.VISIBLE);
            LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.showPlaylistLayoutInPLayerActivity);
            assert linearLayout1 != null;
            linearLayout1.removeAllViews();
            mPlaylistList = new ArrayList<>();
        }
        if (mAdapter.isCheckboxVisible()) {
            FrameLayout main = (FrameLayout) findViewById(R.id.menu_frame);
            assert main != null;
            main.setVisibility(FrameLayout.VISIBLE);
            LinearLayout remove = (LinearLayout) findViewById(R.id.remove_layout);
            assert remove != null;
            remove.setVisibility(LinearLayout.GONE);
            mAdapter.unselect();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBroadcastReceiver();
        sendFullUpdateCall();
//        delayedHide(50);
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(PlayService.ACTION_SEND_MUSIC_FILES);
        filter.addAction(PlayService.ACTION_UPDATE);
        LocalBroadcastManager.getInstance(PlayerActivity.this)
                .registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    protected void onPause() {
        unregisterErrorBroadcastReceiver();
        super.onPause();
    }

    private void unregisterErrorBroadcastReceiver() {
        LocalBroadcastManager.getInstance(PlayerActivity.this)
                .unregisterReceiver(mBroadcastReceiver);
    }

    final BroadcastReceiver mBroadcastReceiver
            = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case PlayService.ACTION_UPDATE:
                    if (!mIsTimeChanging) {
                        PlayerControls controls = intent.getParcelableExtra(PlayService.EXTRA_PLAYER_CONTROLS);
                        progressWriter(controls);
                        buttonChanger(controls);
                        if (mMusicFiles != null && mMusicFiles.size() > 0)
                            showViews(controls);
                    }
                    break;
                case PlayService.ACTION_SEND_MUSIC_FILES:
                    List<MusicFile> musicFiles = intent.getParcelableArrayListExtra(PlayService.EXTRA_LIST_MUSIC_FILES);
                    setMusicFiles(musicFiles);
                    mAdapter.setUpAdapter(mMusicFiles);
            }
        }
    };

    private void onPlayClick() {
        Intent intent = new Intent(PlayService.PLAY_ACTION);
        sendBroadcastIntent(intent);
    }

    private void onPauseClick() {
        Intent intent = new Intent(PlayService.PAUSE_ACTION);
        sendBroadcastIntent(intent);
    }

    private void onNextClick() {
        Intent intent = new Intent(PlayService.NEXT_ACTION);
        sendBroadcastIntent(intent);
    }

    private void onPreviousClick() {
        Intent intent = new Intent(PlayService.PREV_ACTION);
        sendBroadcastIntent(intent);
    }

    private void onShuffleClick() {
        Intent intent = new Intent(PlayService.SET_SHUFFLE_CHANGED_ACTION);
        sendBroadcastIntent(intent);
    }

    private void setNewPlayingTime(int progressChanged) {
        Intent intent = new Intent(PlayService.PLAY_TIME_CHANGED_ACTION);
        intent.putExtra(PlayService.NEW_TIME_EXTRA, progressChanged);
        sendBroadcastIntent(intent);
    }

    private void updateServiceList() {
        Intent intent = new Intent(PlayService.SET_MUSIC_FILES_ACTION);
        intent.putParcelableArrayListExtra(PlayService.LIST_EXTRA, (ArrayList<? extends Parcelable>) mMusicFiles);
        sendBroadcastIntent(intent);
    }

    private void sendFullUpdateCall() {
        Intent intent = new Intent(PlayService.SEND_LIST_ACTION);
        sendBroadcastIntent(intent);
    }

    private void sendStartPlayAction(MusicFile musicFile) {
        Intent intent = new Intent(PlayService.START_FILE_ACTION);
        intent.putExtra(PlayService.FILE_EXTRA, musicFile);
        sendBroadcastIntent(intent);
    }

    private void sendBroadcastIntent(Intent intent) {
        LocalBroadcastManager.getInstance(PlayerActivity.this).sendBroadcast(intent);
    }

 /*   ////FULL SCREEN METHODS
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(50);
    }

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
*/
}
