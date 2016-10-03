package com.example.vov4ik.musicplayer;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vov4ik on 9/25/2016.
 */
public class RecyclerAdapterForSearchArtists extends RecyclerView.Adapter<RecyclerAdapterForSearchArtists.ViewHolder> {
    private List<String> list = new ArrayList<>();
    private SearchActivity activity;
    private String type;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public View mView;
        public TextView textView;




        public ViewHolder(View v) {
            super(v);
            mView = v;
            textView = (TextView)v.findViewById(R.id.word_in_recycler_for_search);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(type.equals("Artists")) {
                activity.clickOnArtist(list.get(getPosition()));
            }else if(type.equals("Albums")){
                activity.clickOnAlbum(list.get(getPosition()));
            }else if(type.equals("Songs")){
                activity.clickOnSong(list.get(getPosition()));
            }

        }

    }
    public RecyclerAdapterForSearchArtists(SearchActivity activity, List<MusicFile> musicFiles, String type, String enteredText) {
        if(type.equals("Artists")) {
            for (MusicFile m : musicFiles) {
                try {
                    if ((m.getAlbum()!=null)&&!list.contains(m.getArtist()) && (m.getArtist().toLowerCase().contains(enteredText.toLowerCase()))) {
                        this.list.add(m.getArtist());
                    }
                }catch(NullPointerException n){
                    Log.d("Error", n.getMessage());
                }
            }
        }else if(type.equals("Albums")){
            for (MusicFile m : musicFiles) {
                try {
                    if ((m.getAlbum()!=null)&&!list.contains(m.getAlbum()) && (m.getAlbum().toLowerCase().contains(enteredText.toLowerCase()))) {
                        this.list.add(m.getAlbum());
                    }
                }catch(NullPointerException n){
                    Log.d("Error", n.getMessage());
                }
            }
        }else if(type.equals("Songs")){
            for (MusicFile m : musicFiles) {
                try {
                    if (!list.contains(m.getTitle()) && (m.getTitle().toLowerCase().contains(enteredText.toLowerCase()))) {
                        this.list.add(m.getTitle());
                    }
                }catch(NullPointerException n){
                    Log.d("Error", n.getMessage());
                }
            }
        }
        if(list.size()==0){
            activity.checkVisibility(type, false);
        }else{
            activity.checkVisibility(type, true);
        }
        this.activity = activity;
        this.type = type;
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

        holder.textView.setText(list.get(position));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
