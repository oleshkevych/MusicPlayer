package com.example.vov4ik.musicplayer.screens.search;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vov4ik.musicplayer.R;
import com.example.vov4ik.musicplayer.data.model.MusicFile;

import java.util.ArrayList;
import java.util.List;

import static com.example.vov4ik.musicplayer.screens.search.SearchActivity.ALBUM_TYPE;
import static com.example.vov4ik.musicplayer.screens.search.SearchActivity.ARTIST_TYPE;
import static com.example.vov4ik.musicplayer.screens.search.SearchActivity.SONG_TYPE;

/**
 * Created by vov4ik on 9/25/2016.
 */
public class RecyclerAdapterForSearchArtists extends RecyclerView.Adapter<RecyclerAdapterForSearchArtists.ViewHolder> {

    private List<String> mNamesList;
    private int mType;

    interface SearchRecyclerActions {

        void checkSectionVisibility(int type, boolean isVisible);

        void onClickSong(String title);

        void onClickAlbum(String album);

        void onClickArtist(String artist);
    }

    private SearchRecyclerActions mSearchRecyclerActions;

    public void setSearchRecyclerActions(SearchRecyclerActions searchRecyclerActions) {
        mSearchRecyclerActions = searchRecyclerActions;
    }

    RecyclerAdapterForSearchArtists() {
        mNamesList = new ArrayList<>();
    }

    void setValues(List<MusicFile> musicFiles, int type, String enteredText) {
        mType = type;
        switch (type) {
            case ARTIST_TYPE:
                for (MusicFile m : musicFiles) {
                    try {
                        if ((m.getAlbum() != null) && !mNamesList.contains(m.getArtist()) && (m.getArtist().toLowerCase().contains(enteredText.toLowerCase()))) {
                            mNamesList.add(m.getArtist());
                        }
                    } catch (NullPointerException n) {
                        Log.d("Error", n.getMessage());
                    }
                }
                break;
            case ALBUM_TYPE:
                for (MusicFile m : musicFiles) {
                    try {
                        if ((m.getAlbum() != null) && !mNamesList.contains(m.getAlbum()) && (m.getAlbum().toLowerCase().contains(enteredText.toLowerCase()))) {
                            mNamesList.add(m.getAlbum());
                        }
                    } catch (NullPointerException n) {
                        Log.d("Error", n.getMessage());
                    }
                }
                break;
            case SONG_TYPE:
                for (MusicFile m : musicFiles) {
                    try {
                        if (!mNamesList.contains(m.getTitle()) && (m.getTitle().toLowerCase().contains(enteredText.toLowerCase()))) {
                            mNamesList.add(m.getTitle());
                        }
                    } catch (NullPointerException n) {
                        Log.d("Error", n.getMessage());
                    }
                }
                break;
        }

        if (mNamesList.size() == 0) {
            mSearchRecyclerActions.checkSectionVisibility(type, false);
        } else {
            mSearchRecyclerActions.checkSectionVisibility(type, true);
        }
    }

    @Override
    public RecyclerAdapterForSearchArtists.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_text_view_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.textView.setText(mNamesList.get(position));

    }

    @Override
    public int getItemCount() {
        return mNamesList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View mView;
        TextView textView;


        ViewHolder(View v) {
            super(v);
            mView = v;
            textView = (TextView) v.findViewById(R.id.word_in_recycler_for_search);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (mType) {
                case ARTIST_TYPE:
                    mSearchRecyclerActions.onClickArtist(mNamesList.get(getPosition()));
                    break;
                case ALBUM_TYPE:
                    mSearchRecyclerActions.onClickAlbum(mNamesList.get(getPosition()));
                    break;
                case SONG_TYPE:
                    mSearchRecyclerActions.onClickSong(mNamesList.get(getPosition()));
                    break;
            }
        }
    }
}
