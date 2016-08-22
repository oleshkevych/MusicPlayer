package com.example.vov4ik.musicplayer;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PlayerActivity extends AppCompatActivity implements View.OnClickListener {

    final static String EXTRA_FOR_CLICKED_FILE = "extra for clicked file";
    final static String EXTRA_FOR_PATHS = "extra for paths";
    private List<String> path = new ArrayList<>();
    private List<String> musicFilesName = new ArrayList<>();
    private String clickedFile;


    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;

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
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            setPath(PlayService.getPath());
            PlayService.addPaths(newPathList);
        }else{
            PlayService.setLastPlayedTime(0);
            PlayService.playFile(clickedFile);
            PlayService.setPath(newPathList);
        }
            setPath(newPathList);
        }else {
            setPath(PlayService.getPath());
        }
        showViews();
    }

    private void showViews(){
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layoutPlayer);
        linearLayout.removeAllViews();
        for (String s : musicFilesName) {
            TextView text = new TextView(linearLayout.getContext());
            text.setText(String.valueOf(s));
            text.setId((musicFilesName).indexOf(s));
            linearLayout.addView(text);
            text.setOnClickListener(this);
            text.setPadding(20, 10, 20, 10);
            text.setTextSize(16);
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) text.getLayoutParams();
            mlp.setMargins(0, 15, 0, 15);
            if (PlayService.isPlayingNow() && s.equals(clickedFile)) {
                text.setPadding(80, 10, 20, 10);
            }
        }
    }
    @Override
    public void onClick(View v) {
        clickedFile = path.get(v.getId());
        PlayService.setLastPlayedTime(0);
        PlayService.playFile(clickedFile);
        showViews();
    }

    @Override
    public void onBackPressed() {
//        moveTaskToBack(true);
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
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
