package com.example.vov4ik.musicplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PlayerActivity extends AppCompatActivity implements View.OnClickListener{//}, View.OnLongClickListener {

    final static String EXTRA_FOR_CLICKED_FILE = "extra for clicked file";
    final static String EXTRA_FOR_PATHS = "extra for paths";
    final static String EXTRA_FOR_FILES = "extra for files";
    protected List<String> path = new ArrayList<>();
    private List<String> musicFilesName = new ArrayList<>();
    private String clickedFile;
    private String playingNow = "n";
    private AsyncTask async;
    private static boolean asyncStop = false;

    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private List<String> removeItemList = new ArrayList<>();
    public static List<String> playlistList = new ArrayList<>();
    private boolean checkingTrigger = false;
    private boolean background = false;
    private boolean allChecked = false;
    private boolean prevBackground = false;
    private List<String> checkedNumbers = new ArrayList<>();
    private List<String> oldPath;
    private List<String> oldNames;

    public  void addMusicFilesName(String musicFilesName) {
        this.musicFilesName.add(musicFilesName);
    }
    public void addMusicFilesName(int i,String musicFilesName) {
        this.musicFilesName.add(i,musicFilesName);
    }

    public void setPath(List<String> newPath, List<String> newNames){
        //Add paths from Service if it is working
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        for(int i = 0; i<newPath.size(); i++) {
            path.add(newPath.get(i));
        }
//        counter = path.indexOf(PlayService.playingFile);
//        if(path.size()>70) {
//            if (counter > 15) {
//                counter = counter - 15;
//            }
//        }else{
//            counter = 0;
//        }
        if (newNames.size() == 0) {
            for (int i = 0; i < newPath.size(); i++) {//(int i = counter; i<newPath.size()&&i<counter+30; i++) {
                File f = new File(newPath.get(i));
                if (!f.isDirectory()) {
                    String title;
                    try {
                        mmr.setDataSource(f.getPath());
                        title = (mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
                    } catch (IllegalArgumentException e) {
                        title = "Refresh the Database";
                    }
                    if ((title == null) || (title.equals("")) || (title.equals("Refresh the Database!"))) {
                        addMusicFilesName(f.getName());
                    } else {
                        addMusicFilesName(title);
                    }
                }
            }
        }else{
            for(int i = 0; i<newPath.size(); i++) {
                addMusicFilesName(newNames.get(i));
            }
        }
        oldPath = path;
        oldNames = musicFilesName;
        mmr.release();
    }



//    private class FetchTaskForPathName extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected Void doInBackground(Void... params) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//                    if(counter!=0) {
//                        for (int i = 0; i < counter; i++) {
//                            File f = new File(path.get(i));
//                            if (!f.isDirectory()) {
//                                String title;
//                                try {
//                                    mmr.setDataSource(f.getPath());
//                                    title = (mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
//                                } catch (IllegalArgumentException e) {
//                                    title = "Refresh the Database";
//                                }
//                                if ((title == null) || (title.equals("")) || (title.equals("Refresh the Database!"))) {
//                                    addMusicFilesName(i, f.getName());
//                                } else {
//                                    addMusicFilesName(i, title);
//                                }
//                            }
//                        }
//                        Log.d("Test", musicFilesName.size()+"");
////                        addFirstViews(counter);
//                    }
//                    if(counter+30>path.size()) {
//                        for (int i = counter + 30; i < path.size(); i++) {
//                            File f = new File(path.get(i));
//                            if (!f.isDirectory()) {
//                                String title;
//                                try {
//                                    mmr.setDataSource(f.getPath());
//                                    title = (mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
//                                } catch (IllegalArgumentException e) {
//                                    title = "Refresh the Database";
//                                }
//                                if ((title == null) || (title.equals("")) || (title.equals("Refresh the Database!"))) {
//                                    addMusicFilesName(f.getName());
//                                } else {
//                                    addMusicFilesName(title);
//                                }
//                            }
//                        }
////                        addLastViews(counter+30);
//                    }
//                    mmr.release();
//                }
//            });
//            return null;
//        }
//    }
//
//    private void addFirstViews(int counter){
//        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layoutPlayer);
//        assert linearLayout!=null;
//        Drawable backgroundForAll;
//        if(background){
//            backgroundForAll = getResources().getDrawable(R.drawable.background_if_this_is_present);
//        }else{
//            backgroundForAll = null;
//        }
//        for (int i =0; i<counter; i++) {
//            TextView text = new TextView(linearLayout.getContext());
//            text.setText(musicFilesName.get(i));
//            text.setId(i);
//            linearLayout.addView(text, i);
//            text.setOnClickListener(this);
//            text.setPadding(30, 10, 20, 10);
//            text.setTextSize(16);
//            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) text.getLayoutParams();
//            mlp.setMargins(0, 15, 0, 15);
//            text.setBackground(backgroundForAll);
//        }
//
//    }
//    private void addLastViews(int counter){
//        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layoutPlayer);
//        assert linearLayout!=null;
//        Drawable backgroundForAll;
//        if(background){
//            backgroundForAll = getResources().getDrawable(R.drawable.background_if_this_is_present);
//        }else{
//            backgroundForAll = null;
//        }
//        for (int i = counter; i<musicFilesName.size(); i++) {
//            TextView text = new TextView(linearLayout.getContext());
//            text.setText(musicFilesName.get(i));
//            text.setId(i);
//            linearLayout.addView(text, i);
//            text.setOnClickListener(this);
//            text.setPadding(30, 10, 20, 10);
//            text.setTextSize(16);
//            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) text.getLayoutParams();
//            mlp.setMargins(0, 15, 0, 15);
//            text.setBackground(backgroundForAll);
//        }
//
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        asyncStop = true;
        setContentView(R.layout.activity_player);
        mContentView = findViewById(R.id.fullscreen_content);
        Intent intent = getIntent();
//        List<String> newPathList = new ArrayList<String>();
//        List<String> newFileList = new ArrayList<String>();
////        if((intent.getStringArrayExtra(EXTRA_FOR_PATHS))!=null) {
//            List<String> newPathList1 = new ArrayList<String>(Arrays.asList(intent.getStringArrayExtra(EXTRA_FOR_PATHS)));
//
//            for (int i = 0; i < newPathList1.size(); i++) {
//                File f = new File(newPathList1.get(i));
//                if (!f.isDirectory()&&(!newPathList1.get(i).equals("..GoToRoot"))) {
//                    newPathList.add(newPathList1.get(i));
//                }
//            }
//            if((intent.getStringArrayExtra(EXTRA_FOR_FILES))!=null){
//                List<String> newFileList1 = new ArrayList<String>(Arrays.asList(intent.getStringArrayExtra(EXTRA_FOR_FILES)));
//
//                for (int i = 0; i < newFileList1.size(); i++) {
//                    if ((!newFileList1.get(i).equals("..GoToRoot"))) {
//                        newFileList.add(newFileList1.get(i));
//                    }
//                }
//            }
//            clickedFile = intent.getStringExtra(EXTRA_FOR_CLICKED_FILE);
//
//
//            if(clickedFile.equals("ADD")) {
//                PlayService.addPaths(newPathList);
//                if(PlayService.isShuffle()){
//                    setPath(PlayService.getShufflePath(), new ArrayList<String>());
//                }else {
//                    setPath(PlayService.getPath(), new ArrayList<String>());
//                }
//            }else{
//                path = new ArrayList<>();
//                setPath(newPathList, newFileList);
//                PlayService.setTrekNumber(path.indexOf(clickedFile));
//                PlayService.setPath(newPathList);
//                if(PlayService.getPlayer()!=null) {
//                    PlayService.setLastPlayedTime(0);
////                    PlayService.setPlayingFile(clickedFile);
//                    PlayService.startPlaying();
//                } else {
//                    Intent intent1 = new Intent(this, PlayService.class);
//                    startService(intent1);
//                    PlayService.setLastPlayedTime(0);
////                    PlayService.setPlayingFile(clickedFile);
//                    PlayService.startPlaying();
//                }
//
//
//            }
//        }else {
            if(PlayService.isShuffle()){
                setPath(PlayService.getShufflePath(), new ArrayList<String>());
            }else {
                setPath(PlayService.getPath(), new ArrayList<String>());
            }
//        }
        showViews();

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
                    if(PlayService.getPlayer()!=null) {
                        PlayService.startPlaying();
                    } else {
                        Intent intent1 = new Intent(getApplicationContext(), PlayService.class);
                        intent1.setAction("com.example.vov4ik.musicplayer.PlayService.play");
                        getApplicationContext().startService(intent1);
                    }
                }else{
                    PlayService.pausePlaying();
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
                    PlayService.nextSong();
                } else {
                    Intent intent1 = new Intent(getApplicationContext(), PlayService.class);
                    intent1.setAction("com.example.vov4ik.musicplayer.PlayService.next");
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
                    PlayService.previous();
                } else {
                    Intent intent1 = new Intent(getApplicationContext(), PlayService.class);
                    intent1.setAction("com.example.vov4ik.musicplayer.PlayService.prev");
                    getApplicationContext().startService(intent1);
                }
                progressWriter();

            }
        });
        final Button shuffle = (Button) findViewById(R.id.shuffle_player_activity);
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
                    path = oldPath;
                    musicFilesName = oldNames;
//                    setPath(PlayService.getPath(), new ArrayList<String>());
                } else {
                    PlayService.setShuffle(true);
                    musicFilesName = new ArrayList<String>();
                    path = new ArrayList<String>();
                    int[] array = PlayService.getResult();
                    for(int i = 0; i<oldPath.size(); i++){
                        path.add(oldPath.get(array[i]));
                        musicFilesName.add(oldNames.get(array[i]));
                    }
//                    setPath(PlayService.getShufflePath(), new ArrayList<String>());
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
                if (PlayService.getPlayer() != null) {
                    PlayService.setLastPlayedTime(progressChanged);
                    if (PlayService.isPlayingNow()) {
                        PlayService.startPlaying();
                    }
                } else {
                    Intent intent1 = new Intent(getApplicationContext(), PlayService.class);
                    getApplicationContext().startService(intent1);
                    PlayService.setLastPlayedTime(progressChanged);
                }
            }
        });


        final Button checkAllButton = (Button) findViewById(R.id.checkAll);
        assert checkAllButton != null;
        checkAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(allChecked){
                    allChecked = false;
                    removeItemList = new ArrayList<String>();
                    checkedNumbers = new ArrayList<String>();
                }
                else {
                    allChecked = true;
                    removeItemList = new ArrayList<String>();
                    removeItemList.addAll(path);

                    for (int i = 0; i<path.size(); i++){
                        checkedNumbers.add(Integer.toString(i));
                    }
                }
                showViews();
            }
        });

        Button remove = (Button) findViewById(R.id.removeButton);
        assert remove != null;
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkingTrigger = false;
//                List<String> newPath = new ArrayList<String>();
//                List<String> newNames = new ArrayList<String>();
                for(String s: removeItemList) {
                    musicFilesName.remove(path.indexOf(s));
                    path.remove(s);
                }
//                newPath = path;
//                newNames = musicFilesName;
//                path = new ArrayList<String>();
//                musicFilesName = new ArrayList<String>();
//                setPath(newPath, new ArrayList<String>(););
                showViews();
                PlayService.setPath(path);
                final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.playListLayoutInPLayerActivity);
                assert linearLayout != null;
                linearLayout.setVisibility(ViewPager.INVISIBLE);
                ScrollView pager = (ScrollView) findViewById(R.id.fullscreen_content);
                assert pager != null;
                pager.setVisibility(View.VISIBLE);
                removeItemList = new ArrayList<String>();
                checkedNumbers = new ArrayList<String>();
            }
        });
        Button addToPlayList = (Button) findViewById(R.id.addToPlaylist);
        assert addToPlayList != null;
        addToPlayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(removeItemList.size() == 0){
                    Toast.makeText(getApplicationContext(), "No selected songs!", Toast.LENGTH_SHORT).show();
                }else{
                    ScrollView pager = (ScrollView) findViewById(R.id.fullscreen_content);
                    assert pager != null;
                    pager.setVisibility(ViewPager.INVISIBLE);
                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.playListLayoutInPLayerActivity);
                    assert linearLayout != null;
                    linearLayout.setVisibility(LinearLayout.VISIBLE);
                    playlistList.add("+Add Playlist");
                    playlistList.addAll(DbConnector.getPlaylist(getApplicationContext()));
                    showPlaylistView();
                }
            }
        });
        asyncStop = false;
        progressWriter();
        async = new FetchTask().execute();

    }

    private void showPlaylistView(){
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.showPlaylistLayoutInPLayerActivity);
        assert linearLayout != null;
        linearLayout.removeAllViews();
        Drawable backgroundForAll;
        if(background){
            backgroundForAll = getResources().getDrawable(R.drawable.background_if_this_is_present);
        }else{
            backgroundForAll = null;
        }
        for (String s : playlistList) {
            TextView text = new TextView(linearLayout.getContext());
            text.setText(String.valueOf(s));
            text.setId((playlistList).indexOf(s));
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
            if(v.getId()==0 || playlistList.get(v.getId()).equals("No Playlists available")){
                if(playlistList.contains("No Playlists available")){
                    playlistList.remove("No Playlists available");
                }
                final LinearLayout inputLayout = (LinearLayout)findViewById(R.id.inputLayoutInPLayerActivity);
                assert inputLayout != null;
                inputLayout.setVisibility(View.VISIBLE);
                final EditText editText = (EditText)findViewById(R.id.addNewPlaylistInPLayerActivity);
                assert editText != null;
                Button addButton = (Button)findViewById(R.id.confirmInPLayerActivity);
                assert addButton != null;
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        if(PlayerActivity.playlistList.contains(editText.getText().toString())){
                            Toast.makeText(getApplicationContext(), "This name exist!!", Toast.LENGTH_SHORT).show();
                        }else {
                            PlayerActivity.playlistList.add(editText.getText().toString());

                        }
                        inputLayout.setVisibility(View.GONE);
                        showPlaylistView();
                    }
                });

            }else {
                DbConnector.setPlaylist(getApplicationContext(), playlistList.get(v.getId()), removeItemList);
                ScrollView pager = (ScrollView) findViewById(R.id.fullscreen_content);
                assert pager != null;
                pager.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(LinearLayout.INVISIBLE);
                LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.showPlaylistLayoutInPLayerActivity);
                assert linearLayout1 != null;
                linearLayout1.removeAllViews();
                playlistList = new ArrayList<>();
                removeItemList = new ArrayList<>();
                checkedNumbers = new ArrayList<String>();
                FrameLayout main = (FrameLayout)findViewById(R.id.menu_frame);
                assert main != null;
                main.setVisibility(FrameLayout.VISIBLE);
                LinearLayout remove = (LinearLayout)findViewById(R.id.remove_layout);
                assert remove != null;
                remove.setVisibility(LinearLayout.INVISIBLE);
                checkingTrigger = false;
                showViews();
            }
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
        String current =  cur/ 60000 + " : " + (((cur / 1000) % 60 >= 10) ? ((cur / 1000) % 60) : ("0" + (cur / 1000) % 60));
        String duration = dur / 60000 + " : " + (((dur / 1000) % 60 >= 10) ? ((dur / 1000) % 60) : ("0" + (dur / 1000) % 60));

        currentTime.setText(current);

        durationTime.setText(duration);

        song.setText(PlayService.trekName());

        seekBar.setMax(dur);
        seekBar.setProgress(cur);
        seekBar.setSecondaryProgress(dur);

    }

    public boolean onLongClick(int position) {
        checkingTrigger = true;
        removeItemList.add(path.get(position));
        checkedNumbers.add(Integer.toString(position));
        FrameLayout main = (FrameLayout)findViewById(R.id.menu_frame);
        assert main != null;
        main.setVisibility(FrameLayout.INVISIBLE);
        LinearLayout remove = (LinearLayout)findViewById(R.id.remove_layout);
        assert remove != null;
        remove.setVisibility(LinearLayout.VISIBLE);
        showViews();
        return true;
    }

    private void backgroundWriter(){

        FrameLayout main = (FrameLayout) findViewById(R.id.playerActivityMainFrame);
        Bitmap image = PlayService.getImage();
        assert main != null;
        if(image!=null){
            background = true;
            BitmapDrawable ob = new BitmapDrawable(getResources(), image);
            main.setBackground(ob);
        }else{
            background = false;
            main.setBackgroundColor(getResources().getColor(R.color.colorMainBackgroundForPlayerActivity));
        }
    }

    private class FetchTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Thread t = new Thread(){
                int i = 0, j = 0;
                @Override
                public void run() {
                    try {
                        while (!isInterrupted()&&!asyncStop&&playingNow!=null) {
                            Thread.sleep(500);
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
//                                    if(musicFilesName.size()<path.size()){
//                                        new FetchTaskForPathName().execute();
//                                    }
                                    if(PlayService.isPlayingNow()) {
                                        progressWriter();
                                        if (!playingNow.equals(PlayService.playingFile)) {
                                            backgroundWriter();
                                            j++;
                                            showViews();
                                        }
                                    }else{
                                        if (playingNow.equals(PlayService.playingFile)) {
//                                            refreshViews(playingNow);
                                            showViews();
                                        }
                                    }
                                    if(i==4){
                                        delayedHide(50);
                                        i=0;
                                    }
                                    buttonChanger();
                                    i++;
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

//    public boolean refreshViews(String previous) {
//        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layoutPlayer);
//        assert linearLayout != null;
//        if (PlayService.isPlayingNow()) {
//            playingNow = PlayService.playingFile;
//        } else {
//            playingNow = "non";
//        }
//        if (background != prevBackground) {
//            showViews();
//            return true;
//        }
//        Drawable backgroundForAll;
//        if(background){
//            backgroundForAll = getResources().getDrawable(R.drawable.background_if_this_is_present);
//        }else{
//            backgroundForAll = null;
//        }
//        try {
//            TextView text = (TextView) linearLayout.findViewById(path.indexOf(previous));
//            text.setPadding(30, 10, 20, 10);
//            text.setTextSize(16);
//            text.setBackground(null);
//            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) text.getLayoutParams();
//            mlp.setMargins(0, 15, 0, 15);
//            text.setBackground(backgroundForAll);
//        } catch (RuntimeException r) {
////            playingNow = "non";
//        }
//        if (PlayService.isPlayingNow()) {
//            try {
//                TextView text1 = (TextView) linearLayout.findViewById(path.indexOf(playingNow));
//                text1.setPadding(80, 10, 20, 10);
//                text1.setTextSize(18);
//                text1.setBackground(getResources().getDrawable(R.drawable.playing_background));
//                ViewGroup.MarginLayoutParams mlp1 = (ViewGroup.MarginLayoutParams) text1.getLayoutParams();
//                mlp1.setMargins(40, 15, 0, 15);
//            } catch (RuntimeException r) {
////                playingNow = "non";
//            }
//        }
//        if (checkingTrigger) {
//            for (String s : removeItemList) {
//                View text1 = linearLayout.findViewById(path.indexOf(s));
//                text1.setBackground(getResources().getDrawable(R.drawable.checked_view_background));
//                text1.setTag("checked");
//            }
//        }
//        return true;
//    }

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

    public void showViews(){
//        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layoutPlayer);
//        assert linearLayout != null;
//        linearLayout.removeAllViews();
        if(PlayService.isPlayingNow()) {
            playingNow = PlayService.playingFile;
        }else{
            playingNow = "non";
        }
//        Drawable backgroundForAll;
//        if(background){
//            backgroundForAll = getResources().getDrawable(R.drawable.background_if_this_is_present);
//        }else{
//            backgroundForAll = null;
//        }
        prevBackground = background;
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.player_activity_recycler_view);
        assert recyclerView != null;
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        RecyclerView.Adapter mAdapter = new RecyclerAdapterForPlayerActivity(this, musicFilesName, path, checkedNumbers, checkingTrigger, getApplicationContext(), background);
        recyclerView.setAdapter(mAdapter);
//        int i = 0;
//        for (String s : musicFilesName) {
//            TextView text = new TextView(linearLayout.getContext());
//            text.setText(s);
//            text.setId(i);
//            linearLayout.addView(text);
//            text.setOnClickListener(this);
//            text.setOnLongClickListener(this);
//
//            text.setPadding(30, 10, 20, 10);
//            text.setTextSize(16);
//            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) text.getLayoutParams();
//            mlp.setMargins(0, 15, 0, 15);
//            text.setBackground(backgroundForAll);
//            i++;
//        }
//        if(checkingTrigger){
//            for(String s:removeItemList) {
//                View text = linearLayout.findViewById(path.indexOf(s));
//                text.setBackground(getResources().getDrawable(R.drawable.checked_view_background));
//                text.setTag("checked");
//            }
//        }
////        Log.d("Test", playingNow+" "+path.indexOf(playingNow)+" "+linearLayout.getChildCount());
//        if (PlayService.isPlayingNow()){
//            try {
//                TextView text = (TextView) linearLayout.findViewById(path.indexOf(playingNow));
//                text.setPadding(80, 10, 20, 10);
//                text.setTextSize(18);
//                text.setBackground(getResources().getDrawable(R.drawable.playing_background));
//                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) text.getLayoutParams();
//                mlp.setMargins(40, 15, 0, 15);
//            }catch (RuntimeException r){
//                playingNow = "non";
//            }
//        }

    }
    public void onClick(int position) {
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.playListLayoutInPLayerActivity);
        assert linearLayout != null;
        if(linearLayout.getVisibility()==View.INVISIBLE) {
            if (checkingTrigger) {
//                if ((v.getTag() != null) && v.getTag().equals("checked")) {
                if(checkedNumbers.contains(Integer.toString(position))){
//                    v.setBackground(null);
//                    v.setTag(null);
                    removeItemList.remove(path.get(position));
                    checkedNumbers.remove(Integer.toString(position));
                } else {
//                    v.setBackground(getResources().getDrawable(R.drawable.checked_view_background));
                    removeItemList.add(path.get(position));
                    checkedNumbers.add(Integer.toString(position));
//                    v.setTag("checked");
//                    v.setBackground(getResources().getDrawable(R.drawable.checked_view_background));
                }
            } else {
                //clickedFile = path.get(v.getId());
                PlayService.setLastPlayedTime(0);
                //PlayService.setPlayingFile(clickedFile);
                PlayService.setTrekNumber(position);
                if(PlayService.getPlayer()!=null) {
                    PlayService.startPlaying();
                } else {
                    Intent intent1 = new Intent(this, PlayService.class);
                    intent1.setAction("com.example.vov4ik.musicplayer.PlayService.play");
                    intent1.putExtra("NUMBER", position);
                    startService(intent1);
                }
//                showViews();
            }
        }
    }

    @Override
    protected void onDestroy() {
        asyncStop = true;
        async.cancel(true);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
//        moveTaskToBack(true);
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.playListLayoutInPLayerActivity);
        assert linearLayout != null;
        if (linearLayout.getVisibility() == ViewPager.VISIBLE) {
            linearLayout.setVisibility(ViewPager.INVISIBLE);
            ScrollView pager = (ScrollView) findViewById(R.id.fullscreen_content);
            assert pager != null;
            pager.setVisibility(View.VISIBLE);
            LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.showPlaylistLayoutInPLayerActivity);
            assert linearLayout1 != null;
            linearLayout1.removeAllViews();
            playlistList = new ArrayList<>();
        }
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
        }else {
            finish();
            super.onBackPressed();
        }
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

}
