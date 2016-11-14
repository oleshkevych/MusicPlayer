package com.example.vov4ik.musicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
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

import java.util.ArrayList;
import java.util.List;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static Boolean executeTrigger = false;
    private ViewPager viewPager;
    private static PagerAdapter adapter;
    final static String EXTRA_FOR_CLICKED_FILE = "extra for clicked file";
    final static String EXTRA_FOR_PATHS = "extra for paths";
    private AsyncTask async;
    private boolean asyncStop = false;
    public static List<String> playlistList = new ArrayList<>();
    private static Context context;
    private boolean backgroundExecuteTrigger = false;


    public static PagerAdapter getAdapter() {
        return adapter;
    }

    public static Context getContext() {
        return context;
    }

    public static Boolean getExecuteTrigger() {
        return executeTrigger;
    }

    public static void setExecuteTrigger(Boolean executeTrigger) {
        MainActivity.executeTrigger = executeTrigger;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        MainActivity.context = getApplicationContext();
        setContentView(R.layout.activity_main);

//        new DbConnector().fillerForDb(getContext());



        if(PlayService.getPlayer()==null) {
            Intent intent = new Intent(this, PlayService.class);
            startService(intent);
            Intent intent1 = new Intent(this, AutoAudioStopper.class);
            startService(intent1);

            AudioManager am = (AudioManager) getApplicationContext().getSystemService(AUDIO_SERVICE);

            AutoAudioStopper.getInstance().setAudioManager(am);
            AutoAudioStopper.getInstance().setContext(this);
            ComponentName mReceiverComponent = new ComponentName(this, HeadphonesClickReceiver.class);
            am.registerMediaButtonEventReceiver(mReceiverComponent);
        }
//        if(!PlayService.isPlayingNow()) {
//            PlayService.startPlaying();
//            PlayService.pausePlaying();
//        }

//        asyncStop = false;
//        if(!backgroundExecuteTrigger) {
//            async = new FetchTask().execute();
//        }
        if(getIntent()!=null) {
            Log.d("Test", "Main A INTENT!!!!!");
            String intentAction = getIntent().getAction();
            if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
                Log.d("Test", "Media_Button");
                KeyEvent event = (KeyEvent) getIntent()
                        .getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                Log.d("Test", "Media_Button"+event.getAction());
                Log.d("Test", "Media_Button"+event.getKeyCode());

            }

        }
        if(getIntent()!=null&&getIntent().getStringExtra("command")!=null) {
            Log.d("Test", "Main A "+getIntent().getStringExtra("command"));
        }
        if(getIntent()!=null&&getIntent().getData()!=null) {
            Intent intent = new Intent(this, PlayerActivity.class);
            startActivity(intent);
            Intent intent1 = new Intent(this, PlayService.class);
            List<String> l = new ArrayList<>();
            l.add(getIntent().getData().getPath());
            PlayService.setPath(l);
            PlayService.setTrekNumber(0);
            PlayService.setLastPlayedTime(0);
            PlayService.setClickedOnTheSong(true);
            intent1.putExtra("CLICKED_SONG", getIntent().getData().getPath());
            intent1.setAction(PlayService.PLAY_ACTION);
            startService(intent1);
            finish();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert toolbar != null;
        ImageView search = (ImageView) toolbar.findViewById(R.id.search_icon);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
                finish();
            }
        });
        List<String> listOfMods;
        try {
            listOfMods = DbConnector.getVisibleTabs(getContext());  //{"Album","All Songs", "Artist",  "Folder(All Content)", "Folder","Playlist"};
        }catch(CursorIndexOutOfBoundsException c){
            listOfMods = TabConstructor.getListOfTabs();
        }
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        assert tabLayout != null;
        for (String s: listOfMods) {
            tabLayout.addTab(tabLayout.newTab().setText(s));
        }
//        tabLayout.setTabGravity(TabLayout.MODE_FIXED);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
//        tabLayout.setTabGravity(TabLayout.SCROLL_INDICATOR_RIGHT);

        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount(), getContext());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        int[] ID = new int[]{R.id.layoutForButton1, R.id.layoutForButton2,R.id.layoutForButton3,R.id.layoutForButton4,R.id.layoutForButton5};
        final LinearLayout[] l= new LinearLayout[5];
        for(int i = 0; i<ID.length; i++){
            l[i] = (LinearLayout)findViewById(ID[i]);
            assert l[i] != null;
        }
        final Button playButton = (Button) findViewById(R.id.playButton);

        assert playButton != null;
        playButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                l[2].setBackground(getResources().getDrawable(R.drawable.checked_view_background));
                return false;
            }
        });
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l[2].setBackground(getResources().getDrawable(R.drawable.keys_shape));
                if(!PlayService.isPlayingNow()){
//                    if(PlayService.getPlayer()!=null) {
//                        PlayService.startPlaying();
//
//                    } else {
                        Intent intent1 = new Intent(getApplicationContext(), PlayService.class);
                        intent1.setAction(PlayService.PLAY_ACTION);
                        getApplicationContext().startService(intent1);
//                    }
                }else{
//                    PlayService.pausePlaying();
                    Intent intent1 = new Intent(getApplicationContext(), PlayService.class);
                    intent1.setAction(PlayService.PAUSE_ACTION);
                    getApplicationContext().startService(intent1);
                }
                buttonChanger();
                progressWriter();

            }
        });
        Button nextButton = (Button) findViewById(R.id.nextButton);
        assert nextButton != null;
        nextButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                l[3].setBackground(getResources().getDrawable(R.drawable.checked_view_background));
                return false;
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l[3].setBackground(getResources().getDrawable(R.drawable.keys_shape));
                if(PlayService.getPlayer()!=null) {
//                        PlayService.nextSong();
//                } else {
//                    Intent intent1 = new Intent(getApplicationContext(), PlayService.class);
//                    intent1.setAction("com.example.vov4ik.musicplayer.PlayService.next");
//                    getApplicationContext().startService(intent1);
                    Intent intent1 = new Intent(getApplicationContext(), PlayService.class);
                    intent1.setAction(PlayService.NEXT_ACTION);
                    getApplicationContext().startService(intent1);
                }
                progressWriter();

            }
        });
        final ImageButton previousButton = (ImageButton) findViewById(R.id.previousButton);
        assert previousButton != null;
        previousButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                l[1].setBackground(getResources().getDrawable(R.drawable.checked_view_background));
                return false;
            }
        });
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l[1].setBackground(getResources().getDrawable(R.drawable.keys_shape));
                if(PlayService.getPlayer()!=null) {
//
//                        PlayService.previous();
//
//                } else {
//                    Intent intent1 = new Intent(getApplicationContext(), PlayService.class);
//                    intent1.setAction("com.example.vov4ik.musicplayer.PlayService.prev");
//                    getApplicationContext().startService(intent1);
                    Intent intent1 = new Intent(getApplicationContext(), PlayService.class);
                    intent1.setAction(PlayService.PREV_ACTION);
                    getApplicationContext().startService(intent1);
                }
                progressWriter();
            }
        });
        Button showPlaylist = (Button) findViewById(R.id.openPlayerList);
        assert showPlaylist != null;
        showPlaylist.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                l[0].setBackground(getResources().getDrawable(R.drawable.checked_view_background));
                return false;
            }
        });
        showPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l[0].setBackground(getResources().getDrawable(R.drawable.keys_shape));
                Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                startActivity(intent);
                finish();
            }
        });
        final Button shuffle = (Button) findViewById(R.id.shuffle);
        assert shuffle != null;
        if(PlayService.isShuffle()){
            shuffle.setBackground(getResources().getDrawable(R.drawable.shuffle_on));
        }else{
            shuffle.setBackground(getResources().getDrawable(R.drawable.shuffle_off));
        }
        shuffle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                l[4].setBackground(getResources().getDrawable(R.drawable.checked_view_background));
                return false;
            }
        });
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l[4].setBackground(getResources().getDrawable(R.drawable.keys_shape));
                if (PlayService.isShuffle()) {
                    PlayService.setShuffle(false);
                    shuffle.setBackground(getResources().getDrawable(R.drawable.shuffle_off));
                } else {
                    PlayService.setShuffle(true);
                    shuffle.setBackground(getResources().getDrawable(R.drawable.shuffle_on));
                }
            }
        });

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
                async.cancel(true);
                asyncStop = true;

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                asyncStop = false;
                if (!backgroundExecuteTrigger) {
                    async = new FetchTask().execute();
                }
                if (PlayService.getPlayer() != null) {
                    PlayService.setLastPlayedTime(progressChanged);
                    if (PlayService.isPlayingNow()) {
//                       PlayService.startPlaying();
                        Intent intent1 = new Intent(getApplicationContext(), PlayService.class);
                        intent1.setAction(PlayService.PLAY_ACTION);
                        getApplicationContext().startService(intent1);
                    }
                } else {
                    Intent intent1 = new Intent(getApplicationContext(), PlayService.class);
                    getApplicationContext().startService(intent1);
                    PlayService.setLastPlayedTime(progressChanged);
                }
            }
        });
        progressWriter();
        asyncStop = false;
        if(!backgroundExecuteTrigger) {
            async = new FetchTask().execute();
        }
    }

    private void progressWriter(){
        TextView currentTime = (TextView) findViewById(R.id.current_time);
        assert currentTime != null;
        TextView durationTime = (TextView) findViewById(R.id.current_duration);
        assert durationTime != null;
        TextView song = (TextView) findViewById(R.id.trek_name_main_activity);
        assert song != null;
        SeekBar seekBar = (SeekBar) findViewById(R.id.seek_bar);
        assert seekBar != null;
        int dur = PlayService.duration();
        int cur = PlayService.currentTime();
        song.setText(PlayService.trekName());
        if(!PlayService.isPlayingNow()&&(PlayService.trekName().equals("no - file")||PlayService.trekName().equals("null - null"))){
            try {
                String f = (DbConnector.getLastPlayList(getContext()).get(DbConnector.getLastPlayNumber(getContext())));
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(f);
                String artist = (mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
                String title = (mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
                song.setText(artist + " - " + title);
                dur = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                cur = DbConnector.getLastPlayTime(getContext());
            }catch(CursorIndexOutOfBoundsException c){
                Log.d("Error", c.getMessage());
            }
        }


        String current =  cur/ 60000 + " : " + (((cur / 1000) % 60 >= 10) ? ((cur / 1000) % 60) : ("0" + (cur / 1000) % 60));
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
        final ISelectableFragment ff = (ISelectableFragment) adapter.getItem(viewPager.getCurrentItem());
        if(v.getId()==0 || playlistList.get(v.getId()).equals("No Playlists available")){
            if(playlistList.contains("No Playlists available")){
                playlistList.remove("No Playlists available");
            }
            final LinearLayout inputLayout = (LinearLayout)findViewById(R.id.inputLayout);
            assert inputLayout != null;
            inputLayout.setVisibility(View.VISIBLE);
            final EditText editText = (EditText)findViewById(R.id.addNewPlaylist);
            assert editText != null;
            Button addButton = (Button)findViewById(R.id.confirm);
            assert addButton != null;
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    if(MainActivity.playlistList.contains(editText.getText().toString())){
                        Toast.makeText(getApplicationContext(), "This name exist!!", Toast.LENGTH_SHORT).show();
                    }else {
                        MainActivity.playlistList.add(editText.getText().toString());
                        editText.setText("");
                    }
                    inputLayout.setVisibility(View.GONE);
                    showView();
                }
            });

        }else {
            DbConnector.setPlaylist(getApplicationContext(),playlistList.get(v.getId()), ff.getSelectedPaths());
            ViewPager pager = (ViewPager) findViewById(R.id.pager);
            assert pager != null;
            pager.setVisibility(ViewPager.VISIBLE);
            linearLayout.setVisibility(LinearLayout.INVISIBLE);
            LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.showPlaylistLayoutInMainActivity);
            assert linearLayout1 != null;
            linearLayout1.removeAllViews();
            playlistList = new ArrayList<>();
            if (ff.isCheckingTrigger()) {
                ff.unselectMusicItems();
            }
        }



    }


    private class FetchTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Thread t = new Thread(){
                        @Override
                        public void run() {
                            try {
                                backgroundExecuteTrigger = true;
                                while (!isInterrupted()&&!asyncStop) {
                                    Log.d("Test", "MainActivity "+Thread.currentThread().getName());
                                    Thread.sleep(500);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(PlayService.isPlayingNow()) {
                                                progressWriter();
                                            }
                                            buttonChanger();
                                        }
                                    });
                                }

                            } catch (InterruptedException e) {
                                Log.d("Test", "sleep failure");
                            }
                        }
            };
                t.start();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            backgroundExecuteTrigger = false;
        }
    }

    @Override
    protected void onStop() {
//        asyncStop = true;
//        async.cancel(true);
//        if(!async.isCancelled()){
//            async.cancel(true);
//        }

        super.onStop();
    }

    @Override
    protected void onResume(){
        if(!backgroundExecuteTrigger) {
            asyncStop = false;
            async = new FetchTask().execute();
        }
        super.onResume();
        buttonChanger();
    }

    private void buttonChanger(){
        if(PlayService.isPlayingNow()){
            Button playButton = (Button) findViewById(R.id.playButton);
            assert playButton != null;
            playButton.setBackground(getResources().getDrawable(R.drawable.pause_png));
        }else{
            Button playButton = (Button) findViewById(R.id.playButton);
            assert playButton != null;
            playButton.setBackground(getResources().getDrawable(R.drawable.play_button_png));

        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }
    private class RefreshDb extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            new DbConnector().fillerForDb(getApplicationContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setExecuteTrigger(false);
        }
    }

    @Override
    public void onBackPressed() {
        ISelectableFragment ff = (ISelectableFragment) adapter.getItem(viewPager.getCurrentItem());
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
        if (ff.isCheckingTrigger()){
            ff.unselectMusicItems();
        }else if(ff.isFolderTrigger()&&(!ff.isAllSongsFragment())) {
            ff.show(ff.getPreviousList());
            ff.setFolderTrigger(false);
        }else{
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.refreshFilesButton) {
            if (!getExecuteTrigger()) {
                setExecuteTrigger(true);
                RefreshDb rDb = new RefreshDb();
                rDb.execute();
            }else{
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                CharSequence text = "Updating is running!";
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }
        if (id == R.id.addSelected) {
            ISelectableFragment ff = (ISelectableFragment)adapter.getItem(viewPager.getCurrentItem());
            if (ff.getSelectedPaths().size()>0) {
//                Intent intent = new Intent(this, PlayerActivity.class);
//                intent.putExtra(EXTRA_FOR_PATHS, ff.getSelectedPaths().toArray(new String[ff.getSelectedPaths().size()]));
//                intent.putExtra(EXTRA_FOR_CLICKED_FILE, "ADD");
//                startActivity(intent);
                PlayService.addPaths(ff.getSelectedPaths());
                ff.unselectMusicItems();
            }else{
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                CharSequence text = "Make shore, that you have selected some thing! ";
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }

        if (id == R.id.addPlaylistInMenu) {
            ISelectableFragment ff = (ISelectableFragment)adapter.getItem(viewPager.getCurrentItem());
            if (ff.getSelectedPaths().size()>0) {
                ViewPager pager = (ViewPager) findViewById(R.id.pager);
                assert pager != null;
                pager.setVisibility(ViewPager.INVISIBLE);
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.playListLayoutInMainActivity);
                assert linearLayout != null;
                linearLayout.setVisibility(LinearLayout.VISIBLE);
                playlistList.add("+ Add New Playlist");
                playlistList.addAll(DbConnector.getPlaylist(getApplicationContext()));
               showView();
            }else{
                Toast.makeText(getApplicationContext(), "Make shore, that you have selected some thing! ", Toast.LENGTH_SHORT).show();
            }
        }
        if (id == R.id.removePlaylistInMenu) {
            ISelectableFragment ff = (ISelectableFragment)adapter.getItem(viewPager.getCurrentItem());
            if(ff.getSelectedPlaylist().size()>0){
                for(String s: ff.getSelectedPlaylist()){
                    DbConnector.removePlaylist(getApplicationContext(), s);
                    ff.reloadForPlaylist();
                }
            }else if(ff.getSelectedPaths().size()>0&&ff.getNumberOfPlaylist()>0){
                DbConnector.removeFilesFromPlaylist(getApplicationContext(), ff.getPreviousList().get(ff.getNumberOfPlaylist()-10), ff.getSelectedPaths());
                ff.reloadForPlaylist();
            }
            else{
                Toast.makeText(getApplicationContext(), "Please, Select something aft first!", Toast.LENGTH_SHORT).show();
            }
        }
        if(id == R.id.startTabActivity){
            Intent intent = new Intent(this, TabCheckerActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private void showView(){
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
//    public boolean isMyServiceRunning(Class<?> serviceClass) {
//        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (serviceClass.getName().equals(service.service.getClassName())) {
//                return true;
//            }
//        }
//        return false;
//    }



    @Override
    protected void onDestroy() {
//        AlarmManager manager;
//        PendingIntent pendingIntent;
//        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
//
////      START NotificationClass.sendNotification();
//        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
//        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
//        int interval = 180000;
//        manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, System.currentTimeMillis(), interval, pendingIntent);
        asyncStop = true;
        async.cancel(true);
        if(!async.isCancelled()){
            async.cancel(true);
        }
        Thread.currentThread().interrupt();
        finish();
        super.onDestroy();
    }
}
