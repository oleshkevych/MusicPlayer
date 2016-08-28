package com.example.vov4ik.musicplayer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PlayerActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    final static String EXTRA_FOR_CLICKED_FILE = "extra for clicked file";
    final static String EXTRA_FOR_PATHS = "extra for paths";
    private List<String> path = new ArrayList<>();
    private List<String> musicFilesName = new ArrayList<>();
    private String clickedFile;
    private String playingNow;
    private AsyncTask async;
    private boolean asyncStop = false;


    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private List<String> removeItemList = new ArrayList<>();
    private boolean checkingTrigger = false;

    public void setPath(List<String> newPath){
        //Add paths from Service if it is working
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        for(String s: newPath) {
            Log.d("Test", "LOOP " + s);
            File f = new File(s);
            if(!f.isDirectory()) {
                path.add(s);

                mmr.setDataSource(f.getPath());
                String title = (mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
                if ((title == null) || (title.equals(""))) {
                    musicFilesName.add(f.getName());
                } else {
                    musicFilesName.add(title);
                }

            }
        }
        mmr.release();
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




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_player);

        mContentView = findViewById(R.id.fullscreen_content);
        Intent intent = getIntent();
        Log.d("Test", "Player " + Arrays.toString(intent.getStringArrayExtra(EXTRA_FOR_PATHS)));
        List<String> newPathList = new ArrayList<String>();
        if((intent.getStringArrayExtra(EXTRA_FOR_PATHS))!=null) {
            newPathList = new ArrayList<String>(Arrays.asList(intent.getStringArrayExtra(EXTRA_FOR_PATHS)));

            for (int i = 0; i < newPathList.size(); i++) {
                if (newPathList.get(i).equals("..GoToRoot")) {
                    newPathList.remove(i);
                }
            }

            clickedFile = intent.getStringExtra(EXTRA_FOR_CLICKED_FILE);

        if(clickedFile.equals("ADD")) {
            setPath(PlayService.getPath());
            PlayService.addPaths(newPathList);
        }else{
            PlayService.setLastPlayedTime(0);
            PlayService.playFile(clickedFile);
            PlayService.setPath(newPathList);
        }
            setPath(newPathList);
        }else {
            if(PlayService.isShuffle()){
                setPath(PlayService.getShufflePath());
            }else {
                setPath(PlayService.getPath());
            }
        }
        showViews();
///-------------




        int[] ID = new int[]{0, R.id.layoutForButton2,R.id.layoutForButton3,R.id.layoutForButton4,R.id.layoutForButton5};
        final LinearLayout[] l= new LinearLayout[5];
        for(int i = 1; i<ID.length; i++){
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
                    PlayService.startPlaying();
                }else{
                    PlayService.pausePlaying();
                }
                buttonChanger();
                progressWriter();
                showViews();

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
                PlayService.nextSong();
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
                PlayService.previous();
                progressWriter();

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
                    PlayService.setPath(path);
                    PlayService.setShuffle(true);
                    musicFilesName = new ArrayList<String>();
                    path = new ArrayList<String>();
                    setPath(PlayService.getShufflePath());
                    shuffle.setBackground(getResources().getDrawable(R.drawable.shuffle_on));
                }
                showViews();
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
                async = new FetchTask().execute();
                PlayService.setLastPlayedTime(progressChanged);
                if (PlayService.isPlayingNow()) {
                    PlayService.startPlaying();
                }
            }
        });
        progressWriter();
        async = new FetchTask().execute();

        Button remove = (Button) findViewById(R.id.removeButton);
        assert remove != null;
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameLayout main = (FrameLayout)findViewById(R.id.menu_frame);
                assert main != null;
                main.setVisibility(FrameLayout.VISIBLE);
                LinearLayout remove = (LinearLayout)findViewById(R.id.remove_layout);
                assert remove != null;
                remove.setVisibility(LinearLayout.INVISIBLE);
                checkingTrigger = false;
                List<String> newPath = new ArrayList<String>();
                path.removeAll(removeItemList);
                newPath = path;
                path = new ArrayList<String>();
                musicFilesName = new ArrayList<String>();
                setPath(newPath);
                showViews();
                PlayService.setPath(path);

                removeItemList = new ArrayList<String>();
            }
        });
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
        String current =  cur/ 60000 + " : " + (((cur / 1000) % 60 >= 10) ? ((cur / 1000) % 60) : ("0" + (cur / 1000) % 60));
        String duration = dur / 60000 + " : " + (((dur / 1000) % 60 >= 10) ? ((dur / 1000) % 60) : ("0" + (dur / 1000) % 60));

        currentTime.setText(current);

        durationTime.setText(duration);

        song.setText(PlayService.trekName());

        seekBar.setMax(dur);
        seekBar.setProgress(cur);
        seekBar.setSecondaryProgress(dur);

    }

    @Override
    public boolean onLongClick(View v) {
        checkingTrigger = true;
        v.setBackground(getResources().getDrawable(R.drawable.checked_view_background));
        v.setTag("checked");
        removeItemList.add(path.get(v.getId()));
        FrameLayout main = (FrameLayout)findViewById(R.id.menu_frame);
        assert main != null;
        main.setVisibility(FrameLayout.INVISIBLE);
        LinearLayout remove = (LinearLayout)findViewById(R.id.remove_layout);
        assert remove != null;
        remove.setVisibility(LinearLayout.VISIBLE);
        return true;
    }

    private class FetchTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Thread t = new Thread(){
                @Override
                public void run() {
                    try {
                        while (!isInterrupted()&&!asyncStop) {
                            Thread.sleep(500);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(!playingNow.equals(PlayService.playingFile)){
                                        showViews();
                                    }
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
    }

    @Override
    protected void onResume(){
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




///-----------------


    public void showViews(){
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layoutPlayer);
        assert linearLayout != null;
        linearLayout.removeAllViews();
        playingNow = PlayService.playingFile;
        for (String s : musicFilesName) {
            TextView text = new TextView(linearLayout.getContext());
            text.setText(String.valueOf(s));
            text.setId((musicFilesName).indexOf(s));
            linearLayout.addView(text);
            text.setOnClickListener(this);
            text.setOnLongClickListener(this);
            if (PlayService.isPlayingNow() && path.get((musicFilesName).indexOf(s)).equals(playingNow)) {
                text.setPadding(80, 10, 20, 10);
                text.setTextSize(18);
                text.setBackground(getResources().getDrawable(R.drawable.playing_background));
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) text.getLayoutParams();
                mlp.setMargins(40, 15, 0, 15);
            }else {
                text.setPadding(20, 10, 20, 10);
                text.setTextSize(16);
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) text.getLayoutParams();
                mlp.setMargins(0, 15, 0, 15);
            }
            if(checkingTrigger&&removeItemList.contains(path.get((musicFilesName).indexOf(s)))){
                text.setBackground(getResources().getDrawable(R.drawable.checked_view_background));
                text.setTag("checked");
            }
        }
    }
    @Override
    public void onClick(View v) {
        if(checkingTrigger){
            if ((v.getTag()!=null)&&v.getTag().equals("checked")){
                v.setBackground(null);
                v.setTag(null);
                removeItemList.remove(path.get(v.getId()));
            } else {
                v.setBackground(getResources().getDrawable(R.drawable.checked_view_background));
                removeItemList.add(path.get(v.getId()));
                v.setTag("checked");
                v.setBackground(getResources().getDrawable(R.drawable.checked_view_background));
            }
        }else {
            clickedFile = path.get(v.getId());
            PlayService.setLastPlayedTime(0);
            PlayService.playFile(clickedFile);
            showViews();
        }
    }

    @Override
    protected void onDestroy() {
        async.cancel(true);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
//        moveTaskToBack(true);
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
        if(checkingTrigger){
            FrameLayout main = (FrameLayout)findViewById(R.id.menu_frame);
            assert main != null;
            main.setVisibility(FrameLayout.VISIBLE);
            LinearLayout remove = (LinearLayout)findViewById(R.id.remove_layout);
            assert remove != null;
            remove.setVisibility(LinearLayout.INVISIBLE);
            checkingTrigger = false;
            showViews();
            removeItemList = new ArrayList<String>();
        }else
        super.onBackPressed();
    }





    ////FULL SCREEN METHODS
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(50);
    }

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

}
