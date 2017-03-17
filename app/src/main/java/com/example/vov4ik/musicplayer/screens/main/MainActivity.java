package com.example.vov4ik.musicplayer.screens.main;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.CursorIndexOutOfBoundsException;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vov4ik.musicplayer.data.model.MusicFile;
import com.example.vov4ik.musicplayer.data.model.PlayerControls;
import com.example.vov4ik.musicplayer.service.PlayService;
import com.example.vov4ik.musicplayer.screens.player.PlayerActivity;
import com.example.vov4ik.musicplayer.R;
import com.example.vov4ik.musicplayer.screens.search.SearchActivity;
import com.example.vov4ik.musicplayer.screens.tab.TabCheckerActivity;
import com.example.vov4ik.musicplayer.data.model.TabConstructor;
import com.example.vov4ik.musicplayer.data.local.DatabaseUpdater;
import com.example.vov4ik.musicplayer.data.local.DbConnector;
import com.example.vov4ik.musicplayer.service.AutoAudioStopper;
import com.example.vov4ik.musicplayer.screens.main.fragments.ISelectableFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_WRITE_STORAGE = 112;
    public static final String UPDATE_MUSIC_FILES = "com.example.vov4ik.musicplayer.screens.main.UPDATE_MUSIC_FILES";

    private PagerAdapter mAdapter;
    private ViewPager mViewPager;
    private List<String> playlistList = new ArrayList<>();
    private boolean mIsTimeChanging;

    private Runnable getRestartMainActivityRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                sendBroadcastIntent(new Intent(UPDATE_MUSIC_FILES));
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
            return;
        }

        initialize();
    }

    private void initialize() {
        if (!isServiceRunning(PlayService.class)) {
            DatabaseUpdater dbUpdater = new DatabaseUpdater(getApplicationContext());
            dbUpdater.updateDatabase(getRestartMainActivityRunnable());
            startService(PlayService.getIntent(MainActivity.this));
            sendBroadcastIntent(new Intent(UPDATE_MUSIC_FILES));
        }

        processActivityIntent();
        setupToolbar();
        setupTabs();
        setupPlaybackControlButtons();
        setupSeekBar();
    }

    private void processActivityIntent() {
        if (getIntent() != null) {
            if (getIntent().getData() != null) {
                if (!isServiceRunning(PlayService.class)) {
                    initialize();
                }
                startRemoteFile(getIntent().getData());
                startActivity(PlayerActivity.getIntent(MainActivity.this));
                finish();
            }
        }
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

    private void setupPlaybackControlButtons() {
        final ImageButton playButton = (ImageButton) findViewById(R.id.playButton);
        assert playButton != null;
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isServiceRunning(PlayService.class)) {
                    startService(PlayService.getIntentForPlaying(MainActivity.this));
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
        ImageButton showPlaylist = (ImageButton) findViewById(R.id.openPlayerList);
        assert showPlaylist != null;
        showPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                startActivity(intent);
                finish();
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

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageView search = (ImageView) toolbar.findViewById(R.id.search_icon);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupTabs() {
        List<String> listOfMods;
        try {
            listOfMods = DbConnector.getVisibleTabs(this);
        } catch (CursorIndexOutOfBoundsException c) {
            listOfMods = TabConstructor.getListOfTabs();
        }
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        assert tabLayout != null;
        for (String s : listOfMods) {
            tabLayout.addTab(tabLayout.newTab().setText(s));
        }
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        mViewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount(), this);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initialize();
                } else {
                    Toast.makeText(this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void updatePlaybackProgress(PlayerControls controls) {
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
        if(!song.getText().equals(controls.getTrackName()))
            song.setText(controls.getTrackName());

        String current = cur / 60000 + " : " + (((cur / 1000) % 60 >= 10) ? ((cur / 1000) % 60) : ("0" + (cur / 1000) % 60));
        String duration = dur / 60000 + " : " + (((dur / 1000) % 60 >= 10) ? ((dur / 1000) % 60) : ("0" + (dur / 1000) % 60));

        currentTime.setText(current);

        durationTime.setText(duration);


        seekBar.setMax(dur);
        seekBar.setProgress(cur);
        seekBar.setSecondaryProgress(dur);
    }

    @Override
    public void onClick(View v) {
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.playListLayoutInMainActivity);
        assert linearLayout != null;
        final ISelectableFragment ff = (ISelectableFragment) mAdapter.getItem(mViewPager.getCurrentItem());
        if(ff.getSelectedPlaylist().size() <= 0){
            Toast.makeText(getApplicationContext(), "Nothing was selected", Toast.LENGTH_SHORT).show();
            return;
        }
        if (v.getId() == 0 || playlistList.get(v.getId()).equals("No Playlists available")) {
            if (playlistList.contains("No Playlists available")) {
                playlistList.remove("No Playlists available");
            }
            final LinearLayout inputLayout = (LinearLayout) findViewById(R.id.inputLayout);
            assert inputLayout != null;
            inputLayout.setVisibility(View.VISIBLE);
            final EditText editText = (EditText) findViewById(R.id.addNewPlaylist);
            assert editText != null;
            Button addButton = (Button) findViewById(R.id.confirm);
            assert addButton != null;
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    if (playlistList.contains(editText.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "This name exist!!", Toast.LENGTH_SHORT).show();
                    } else {
                        playlistList.add(editText.getText().toString());
                        editText.setText("");
                    }
                    inputLayout.setVisibility(View.GONE);
                    showView();
                }
            });

        } else {
            DbConnector.setPlaylist(getApplicationContext(), playlistList.get(v.getId()), ff.getSelectedPlaylist());
            ViewPager pager = (ViewPager) findViewById(R.id.pager);
            assert pager != null;
            pager.setVisibility(ViewPager.VISIBLE);
            linearLayout.setVisibility(LinearLayout.INVISIBLE);
            LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.showPlaylistLayoutInMainActivity);
            assert linearLayout1 != null;
            linearLayout1.removeAllViews();
            playlistList = new ArrayList<>();
            ff.unselectMusicItems();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBroadcastReceiver();
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

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    public void onBackPressed() {
        ISelectableFragment ff = (ISelectableFragment) mAdapter.getItem(mViewPager.getCurrentItem());
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        assert pager != null;
        if (pager.getVisibility() == ViewPager.INVISIBLE) {
            pager.setVisibility(ViewPager.VISIBLE);
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.playListLayoutInMainActivity);
            assert linearLayout != null;
            linearLayout.setVisibility(LinearLayout.INVISIBLE);
            LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.showPlaylistLayoutInMainActivity);
            assert linearLayout1 != null;
            linearLayout1.removeAllViews();
            playlistList = new ArrayList<>();
        }
        if (ff.isSelecting()) {
            ff.unselectMusicItems();
        } else if (ff.isInFolder()) {
            ff.showFolder();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.refreshFilesButton) {
            DatabaseUpdater dbUpdater = new DatabaseUpdater(getApplicationContext());
            if (!dbUpdater.updateDatabase(getRestartMainActivityRunnable())) {
                Toast.makeText(this, "Updating is running!", Toast.LENGTH_SHORT).show();
            }
        }

        if (id == R.id.addSelected) {
            ISelectableFragment ff = (ISelectableFragment) mAdapter.getItem(mViewPager.getCurrentItem());
            if (ff.getSelectedPlaylist().size() > 0) {
                addListAction(ff.getSelectedPlaylist());
                ff.unselectMusicItems();
            } else {
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                CharSequence text = "Make shore, that you have selected some thing! ";
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }

        if (id == R.id.addPlaylistInMenu) {
            ISelectableFragment ff = (ISelectableFragment) mAdapter.getItem(mViewPager.getCurrentItem());
            if (ff.getSelectedPlaylist().size() > 0) {
                ViewPager pager = (ViewPager) findViewById(R.id.pager);
                assert pager != null;
                pager.setVisibility(ViewPager.INVISIBLE);
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.playListLayoutInMainActivity);
                assert linearLayout != null;
                linearLayout.setVisibility(LinearLayout.VISIBLE);
                playlistList.add("+ Add New Playlist");
                playlistList.addAll(DbConnector.getPlaylist(getApplicationContext()));
                showView();
            } else {
                Toast.makeText(getApplicationContext(), "Make shore, that you have selected some thing! ", Toast.LENGTH_SHORT).show();
            }
        }
//        if (id == R.id.removePlaylistInMenu) {
//            ISelectableFragment ff = (ISelectableFragment) mAdapter.getItem(mViewPager.getCurrentItem());
//            if (ff.getSelectedPlaylist().size() > 0) {
//                for (MusicFile s : ff.getSelectedPlaylist()) {
//                    DbConnector.removePlaylist(getApplicationContext(), s);
//                    ff.reloadForPlaylist();
//                }
//            } else if (ff.getSelectedPaths().size() > 0 && ff.getNumberOfPlaylist() > 0) {
//                DbConnector.removeFilesFromPlaylist(getApplicationContext(), ff.getPreviousList().get(ff.getNumberOfPlaylist() - 10), ff.getSelectedPaths());
//                ff.reloadForPlaylist();
//            } else {
//                Toast.makeText(getApplicationContext(), "Please, Select something aft first!", Toast.LENGTH_SHORT).show();
//            }
//        }
        if (id == R.id.startTabActivity) {
            Intent intent = new Intent(this, TabCheckerActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showView() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.showPlaylistLayoutInMainActivity);
        assert linearLayout != null;
        linearLayout.removeAllViews();
        for (String s : playlistList) {
            TextView text = new TextView(linearLayout.getContext());
            text.setText(String.valueOf(s));
            text.setId((playlistList).indexOf(s));
            linearLayout.addView(text);
            text.setOnClickListener(this);
            text.setPadding(20, 10, 20, 10);
            text.setTextSize(18);
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) text.getLayoutParams();
            mlp.setMargins(40, 15, 0, 15);
        }
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(PlayService.ACTION_UPDATE);
        LocalBroadcastManager.getInstance(MainActivity.this)
                .registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    protected void onPause() {
        unregisterBroadcastReceiver();
        super.onPause();
    }

    private void unregisterBroadcastReceiver() {
        LocalBroadcastManager.getInstance(MainActivity.this)
                .unregisterReceiver(mBroadcastReceiver);
    }

    final BroadcastReceiver mBroadcastReceiver
            = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case PlayService.ACTION_UPDATE:
                    if(!mIsTimeChanging) {
                        PlayerControls controls = intent.getParcelableExtra(PlayService.EXTRA_PLAYER_CONTROLS);
                        updatePlaybackProgress(controls);
                        buttonChanger(controls);
                    }
                    break;
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

    private void startRemoteFile(Uri file) {
        Intent intent = new Intent(PlayService.PLAY_URI_ACTION);
        intent.putExtra(PlayService.FILE_URI_EXTRA, file);
        sendBroadcastIntent(intent);
    }

    private void addListAction(List<MusicFile> musicFiles) {
        Intent intent = new Intent(PlayService.ADD_LIST_MUSIC_FILES_ACTION);
        intent.putParcelableArrayListExtra(PlayService.LIST_EXTRA, (ArrayList<? extends Parcelable>) musicFiles);
        sendBroadcastIntent(intent);
    }


    private void sendBroadcastIntent(Intent intent) {
        LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
    }
}
