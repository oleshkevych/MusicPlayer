package com.example.vov4ik.musicplayer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private final String EXTRA_VISIBILITY_ARTIST = "visibility_artist";
    private final String EXTRA_VISIBILITY_ALBUM = "visibility_album";
    private final String EXTRA_VISIBILITY_SONG = "visibility_song";
    private final String EXTRA_ENTERED_TEXT = "entered_text";
    private List<MusicFile> musicFiles;
    private RecyclerView artistRecycler;
    private RecyclerView albumRecycler;
    private RecyclerView songRecycler;
    private ProgressBar progressBar;
    private LinearLayout wrapper;
    private LinearLayout artistLayout;
    private LinearLayout albumLayout;
    private LinearLayout songLayout;
    private String enteredText = "";
    private EditText inputTextField;
    private boolean downloadTrigger = false;
    private boolean addingTrigger = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        new FetchTask().execute();
        wrapper = (LinearLayout)findViewById(R.id.wrapForOutput);
        inputTextField = (EditText)findViewById(R.id.search_this_word);
        progressBar = (ProgressBar)findViewById(R.id.progressbarSearching);
        artistRecycler = (RecyclerView)findViewById(R.id.search_activity_recycler_artist);
        albumRecycler = (RecyclerView)findViewById(R.id.search_activity_recycler_album);
        songRecycler = (RecyclerView)findViewById(R.id.search_activity_recycler_song);
        artistLayout = (LinearLayout) findViewById(R.id.search_artist_layout);
        albumLayout = (LinearLayout) findViewById(R.id.search_album_layout);
        songLayout = (LinearLayout) findViewById(R.id.search_song_layout);
        if(savedInstanceState!=null){
            if(savedInstanceState.getBoolean(EXTRA_VISIBILITY_ALBUM)) {
                albumLayout.setVisibility(View.GONE);
            }
            if(savedInstanceState.getBoolean(EXTRA_VISIBILITY_ARTIST)) {
                artistLayout.setVisibility(View.GONE);
            }
            if(savedInstanceState.getBoolean(EXTRA_VISIBILITY_SONG)) {
                songLayout.setVisibility(View.GONE);
            }
            inputTextField.setText(savedInstanceState.getString(EXTRA_ENTERED_TEXT));
        }
        TextView artist = (TextView) findViewById(R.id.artist_text_view);
        assert artist != null;
        artist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout l = artistLayout;
                assert l != null;
                if(l.getVisibility()!=View.GONE){
                    l.setVisibility(View.GONE);
                }else{
                    l.setVisibility(View.VISIBLE);
                }
            }
        });
        TextView album = (TextView) findViewById(R.id.album_text_view);
        assert album != null;
        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout l = albumLayout;
                assert l != null;
                if (l.getVisibility() != View.GONE) {
                    l.setVisibility(View.GONE);
                } else {
                    l.setVisibility(View.VISIBLE);
                }
            }
        });
        final TextView song = (TextView) findViewById(R.id.song_text_view);
        assert song != null;
        song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout l = songLayout;
                assert l != null;
                if (l.getVisibility() != View.GONE) {
                    l.setVisibility(View.GONE);
                } else {
                    l.setVisibility(View.VISIBLE);
                }
            }
        });

        ImageView back = (ImageView) findViewById(R.id.back_icon);
        assert back != null;
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        assert inputTextField != null;
        inputTextField.setEnabled(false);
        inputTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                enteredText = s.toString();
                if (downloadTrigger) {
                    show();
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addingTrigger = !addingTrigger;
                Toast.makeText(getApplicationContext(), "Adding to current playlist = "+addingTrigger, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private class FetchTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            musicFiles = DbConnector.getMusicFilesForSearch(getApplicationContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            inputTextField.setEnabled(true);
            wrapper.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            downloadTrigger = true;
            show();
        }
    }
    private void show(){
//        if(artistLayout.getVisibility() == View.VISIBLE) {
            RecyclerView.LayoutManager artistLayoutManager = new LinearLayoutManager(getApplicationContext());
            assert artistRecycler != null;
            artistRecycler.setHasFixedSize(true);
            artistRecycler.setLayoutManager(artistLayoutManager);
            RecyclerView.Adapter artistAdapter = new RecyclerAdapterForSearchArtists(this, musicFiles, "Artists", enteredText);
            artistRecycler.setAdapter(artistAdapter);
//        }
//        if(albumLayout.getVisibility() == View.VISIBLE) {
            RecyclerView.LayoutManager albumLayoutManager = new LinearLayoutManager(getApplicationContext());
            assert albumRecycler != null;
            albumRecycler.setHasFixedSize(true);
            albumRecycler.setLayoutManager(albumLayoutManager);
            RecyclerView.Adapter albumAdapter = new RecyclerAdapterForSearchArtists(this, musicFiles, "Albums", enteredText);
            albumRecycler.setAdapter(albumAdapter);
//        }
//        if(songLayout.getVisibility() == View.VISIBLE) {
            RecyclerView.LayoutManager songLayoutManager = new LinearLayoutManager(getApplicationContext());
            assert songRecycler != null;
            songRecycler.setHasFixedSize(true);
            songRecycler.setLayoutManager(songLayoutManager);
            RecyclerView.Adapter songAdapter = new RecyclerAdapterForSearchArtists(this, musicFiles, "Songs", enteredText);
            songRecycler.setAdapter(songAdapter);
//        }

    }

    public void checkVisibility(String type, boolean visibility){
        if(type.equals("Artists")) {
            LinearLayout l = artistLayout;
            assert l != null;
            if (visibility) {
                l.setVisibility(View.VISIBLE);
            } else {
                l.setVisibility(View.GONE);
            }

        }else if(type.equals("Albums")){
            LinearLayout l = albumLayout;
            assert l != null;
            if (visibility) {
                l.setVisibility(View.VISIBLE);
            } else {
                l.setVisibility(View.GONE);
            }
        }else if(type.equals("Songs")){
            LinearLayout l = songLayout;
            assert l != null;
            if (visibility) {
                l.setVisibility(View.VISIBLE);
            } else {
                l.setVisibility(View.GONE);
            }

        }
    }

    public void clickOnArtist(String artist){
        List<String> path = new ArrayList<>();
        for(MusicFile m: musicFiles){
            if((m.getArtist()!=null)&&m.getArtist().equals(artist)){
                path.add(m.getPath());
            }
        }
       startPlaying(path);
    }
    public void clickOnAlbum(String album){
        List<String> path = new ArrayList<>();
        for(MusicFile m: musicFiles){
            if((m.getAlbum()!=null)&&m.getAlbum().equals(album)){
                path.add(m.getPath());
            }
        }
        startPlaying(path);
    }
    public void clickOnSong(String song){
        List<String> path = new ArrayList<>();
        for(MusicFile m: musicFiles){
            if(m.getTitle().equals(song)){
                path.add(m.getPath());
                break;
            }
        }
        startPlaying(path);
    }

    private void startPlaying(List<String> path){
        if(addingTrigger){
            PlayService.addPaths(path);
        }else {
            PlayService.setTrekNumber(0);
            PlayService.setPath(path);
            PlayService.setLastPlayedTime(0);
//        if(PlayService.getPlayer()==null) {
//            Intent intent1 = new Intent(this, PlayService.class);
//            startService(intent1);
//        }
//        PlayService.startPlaying();
            Intent intent1 = new Intent(getApplicationContext(), PlayService.class);
            intent1.setAction(PlayService.PLAY_ACTION);
            getApplicationContext().startService(intent1);
            Intent intent = new Intent(this, PlayerActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if(enteredText.length()>0){
            inputTextField.setText("");
        }else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(EXTRA_VISIBILITY_ALBUM, albumLayout.getVisibility() == View.GONE);
        savedInstanceState.putBoolean(EXTRA_VISIBILITY_ARTIST, artistLayout.getVisibility() == View.GONE);
        savedInstanceState.putBoolean(EXTRA_VISIBILITY_SONG, songLayout.getVisibility() == View.GONE);
        savedInstanceState.putString(EXTRA_ENTERED_TEXT, enteredText);

    }
}
