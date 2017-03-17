package com.example.vov4ik.musicplayer.screens.search;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vov4ik.musicplayer.service.PlayService;
import com.example.vov4ik.musicplayer.R;
import com.example.vov4ik.musicplayer.data.local.DbConnector;
import com.example.vov4ik.musicplayer.data.model.MusicFile;
import com.example.vov4ik.musicplayer.screens.main.MainActivity;
import com.example.vov4ik.musicplayer.screens.player.PlayerActivity;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    static final int ALBUM_TYPE = 1;
    static final int ARTIST_TYPE = 2;
    static final int SONG_TYPE = 3;

    private List<MusicFile> mMusicFiles;
    private String mEnteredText = "";
    private EditText mInputTextField;
    private boolean mDownloadTrigger = false;
    private boolean mIsAdding = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        new FetchTask().execute();
        mInputTextField = (EditText) findViewById(R.id.search_this_word);

        final TextView artistsTextView = (TextView) findViewById(R.id.artist_text_view);
        assert artistsTextView != null;
        artistsTextView.setText("Hide Artists");
        artistsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (findViewById(R.id.search_artist_layout).getVisibility() != View.GONE) {
                    findViewById(R.id.search_artist_layout).setVisibility(View.GONE);
                    artistsTextView.setText("Show Artists");
                } else {
                    artistsTextView.setText("Hide Artists");
                    findViewById(R.id.search_artist_layout).setVisibility(View.VISIBLE);
                }
            }
        });
        final TextView albumsTextView = (TextView) findViewById(R.id.album_text_view);
        assert albumsTextView != null;
        albumsTextView.setText("Hide Artists");
        albumsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (findViewById(R.id.search_album_layout).getVisibility() != View.GONE) {
                    findViewById(R.id.search_album_layout).setVisibility(View.GONE);
                    albumsTextView.setText("Show Artists");
                } else {
                    findViewById(R.id.search_album_layout).setVisibility(View.VISIBLE);
                    albumsTextView.setText("Hide Artists");
                }
            }
        });
        final TextView songsTextView = (TextView) findViewById(R.id.song_text_view);
        assert songsTextView != null;
        songsTextView.setText("Hide Artists");
        songsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (findViewById(R.id.search_song_layout).getVisibility() != View.GONE) {
                    findViewById(R.id.search_song_layout).setVisibility(View.GONE);
                    songsTextView.setText("Show Artists");
                } else {
                    findViewById(R.id.search_song_layout).setVisibility(View.VISIBLE);
                    songsTextView.setText("Hide Artists");
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


        assert mInputTextField != null;
        mInputTextField.setEnabled(false);
        mInputTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mEnteredText = s.toString();
                if (mDownloadTrigger) {
                    updateRecyclers();
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsAdding = !mIsAdding;
                Toast.makeText(getApplicationContext(), "Adding to current playlist = " + mIsAdding, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private class FetchTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            mMusicFiles = DbConnector.getMusicFiles(getApplicationContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mInputTextField.setEnabled(true);
            findViewById(R.id.wrapForOutput).setVisibility(View.VISIBLE);
            findViewById(R.id.progressbarSearching).setVisibility(View.GONE);
            mDownloadTrigger = true;
            updateRecyclers();
        }
    }

    private void updateRecyclers() {
        RecyclerView.LayoutManager artistLayoutManager = new LinearLayoutManager(getApplicationContext());
        RecyclerView artistRecycler = (RecyclerView) findViewById(R.id.search_activity_recycler_artist);
        assert artistRecycler != null;
        artistRecycler.setHasFixedSize(true);
        artistRecycler.setLayoutManager(artistLayoutManager);
        RecyclerAdapterForSearchArtists artistAdapter = new RecyclerAdapterForSearchArtists();
        artistAdapter.setSearchRecyclerActions(mSearchRecyclerActions);
        artistAdapter.setValues(mMusicFiles, ARTIST_TYPE, mEnteredText);
        artistRecycler.setAdapter(artistAdapter);

        RecyclerView.LayoutManager albumLayoutManager = new LinearLayoutManager(getApplicationContext());
        RecyclerView albumRecycler = (RecyclerView) findViewById(R.id.search_activity_recycler_album);
        assert albumRecycler != null;
        albumRecycler.setHasFixedSize(true);
        albumRecycler.setLayoutManager(albumLayoutManager);
        RecyclerAdapterForSearchArtists albumAdapter = new RecyclerAdapterForSearchArtists();
        albumAdapter.setSearchRecyclerActions(mSearchRecyclerActions);
        albumAdapter.setValues(mMusicFiles, ALBUM_TYPE, mEnteredText);
        albumRecycler.setAdapter(albumAdapter);

        RecyclerView.LayoutManager songLayoutManager = new LinearLayoutManager(getApplicationContext());
        RecyclerView songRecycler = (RecyclerView) findViewById(R.id.search_activity_recycler_song);
        assert songRecycler != null;
        songRecycler.setHasFixedSize(true);
        songRecycler.setLayoutManager(songLayoutManager);
        RecyclerAdapterForSearchArtists songAdapter = new RecyclerAdapterForSearchArtists();
        songAdapter.setSearchRecyclerActions(mSearchRecyclerActions);
        songAdapter.setValues(mMusicFiles, SONG_TYPE, mEnteredText);
        songRecycler.setAdapter(songAdapter);

    }

    private RecyclerAdapterForSearchArtists.SearchRecyclerActions mSearchRecyclerActions = new RecyclerAdapterForSearchArtists.SearchRecyclerActions() {
        @Override
        public void checkSectionVisibility(int type, boolean isVisible) {
            checkVisibility(type, isVisible);
        }

        @Override
        public void onClickSong(String title) {
            songSelected(title);
        }

        @Override
        public void onClickAlbum(String album) {
            albumSelected(album);
        }

        @Override
        public void onClickArtist(String artist) {
            artistSelected(artist);
        }
    };

    private void checkVisibility(int type, boolean isVisible) {
        switch (type) {
            case ARTIST_TYPE:
                findViewById(R.id.search_artist_layout).setVisibility(isVisible ? View.VISIBLE : View.GONE);
                break;
            case ALBUM_TYPE:
                findViewById(R.id.search_album_layout).setVisibility(isVisible ? View.VISIBLE : View.GONE);
                break;
            case SONG_TYPE:
                findViewById(R.id.search_song_layout).setVisibility(isVisible ? View.VISIBLE : View.GONE);
                break;
        }
    }

    private void artistSelected(String artist) {
        List<MusicFile> musicFiles = new ArrayList<>();
        for (MusicFile m : mMusicFiles) {
            if ((m.getArtist() != null) && m.getArtist().equals(artist)) {
                musicFiles.add(m);
            }
        }
        startPlaying(musicFiles);
    }

    private void albumSelected(String album) {
        List<MusicFile> musicFiles = new ArrayList<>();
        for (MusicFile m : mMusicFiles) {
            if ((m.getAlbum() != null) && m.getAlbum().equals(album)) {
                musicFiles.add(m);
            }
        }
        startPlaying(musicFiles);
    }

    private void songSelected(String song) {
        List<MusicFile> musicFiles = new ArrayList<>();
        for (MusicFile m : mMusicFiles) {
            if (m.getTitle().equals(song)) {
                musicFiles.add(m);
                break;
            }
        }
        startPlaying(musicFiles);
    }

    private void startPlaying(List<MusicFile> musicFiles) {
        if (mIsAdding) {
            addPathListAction(musicFiles);
        } else {
            addPathListAction(musicFiles);
            startFile(musicFiles.get(0));
            Intent intent = new Intent(this, PlayerActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (mEnteredText.length() > 0) {
            mInputTextField.setText("");
        } else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void startFile(MusicFile musicFile) {
        Intent intent = new Intent(PlayService.PLAY_FILE_ACTION);
        intent.putExtra(PlayService.FILE_EXTRA, musicFile);
        sendBroadcastIntent(intent);
    }

    private void addPathListAction(List<MusicFile> musicFiles) {
        Intent intent = new Intent(PlayService.ADD_LIST_MUSIC_FILES_ACTION);
        intent.putParcelableArrayListExtra(PlayService.LIST_EXTRA, (ArrayList<? extends Parcelable>) musicFiles);
        sendBroadcastIntent(intent);
    }


    private void sendBroadcastIntent(Intent intent) {
        LocalBroadcastManager.getInstance(SearchActivity.this).sendBroadcast(intent);
    }
}
