package com.example.vov4ik.musicplayer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vov4ik on 9/11/2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<String> musicFiles = new ArrayList<>();;
    private List<String> path = new ArrayList<>();
    private List<String> checked;
    private MusicListFragment ma;
    public boolean vis;
    public boolean folderTrigger;
    public boolean playlist;
    public Context context;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        public View mView;
        public TextView names;
        public TextView path;
        public CheckBox checkBox;



        public ViewHolder(View v) {
            super(v);
            mView = v;
            names = (TextView)v.findViewById(R.id.musicFileName);
            path = (TextView)v.findViewById(R.id.path);
            checkBox = (CheckBox)v.findViewById(R.id.checkBox);
            v.setOnClickListener(this);
            if(vis){
                checkBox.setVisibility(View.VISIBLE);
            } else {
                v.setOnLongClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            if (vis) {
                CheckBox c = (CheckBox) v.findViewById(R.id.checkBox);
                String pos = String.valueOf(getPosition());
                if (checked.contains(pos)) {
//                checked.remove(pos);
                    c.setChecked(false);
                } else {

                    c.setChecked(true);
                }
            }else {
                ma.onClick(getPosition());
            }
        }

        public void onClick(View v, boolean isChecked) {
            ma.onClick(getPosition());
        }
        @Override
        public boolean onLongClick(View v) {
            ma.onLongClick(getPosition());
            return true;
        }
    }

    public RecyclerViewAdapter(MusicListFragment ma, List<String> musicFiles, List<List<String>> paths, List<String> checked, boolean vis, boolean folderTrigger,
                               int folderNumber, boolean playlist, Context context, boolean isAllSongsFragment) {
        this.context = context;
        this.checked = checked;
        this.ma = ma;
        this.vis = vis;
        this.folderTrigger = folderTrigger;
        this.playlist = playlist;
        try {
            if (!playlist) {
                Log.d("Test", "1");
                if (!folderTrigger) {
                    Log.d("Test", "2");
                    this.musicFiles = musicFiles;
                    for (List<String> s : paths) {
                        try {
                            this.path.add((new File(s.get(0))).getParentFile().getPath());
                        } catch (IndexOutOfBoundsException i) {

//                        this.path.add((new File(s[0])).getParentFile().getPath());
                        }
                    }
                } else {
                    Log.d("Test", "3");
                    if(!isAllSongsFragment) {
                        Log.d("Test", "4");
                        this.path.add(0, "..goToRoot");
                        this.musicFiles.add(0, "..goToRoot");
                    }
                    for (int i = 0; i < paths.get(folderNumber).size(); i++) {
                        try {
                            this.path.add((new File(paths.get(folderNumber).get(i))).getParentFile().getPath());
                        }catch (NullPointerException e){

                            Log.d("NULL", paths.get(folderNumber).get(i));
                            Log.d("NULL", folderNumber+"ERROR");
                        }
                    }
                    this.musicFiles.addAll(musicFiles);
                }
            } else if (folderTrigger) {
                Log.d("Test", "5");
                this.path.add("");
                this.musicFiles.add(0, "..goToRoot");
                for (int i = 1; i < paths.get(0).size(); i++) {
                    this.path.add((new File(paths.get(0).get(i))).getParentFile().getPath());
                }
                this.musicFiles.addAll(musicFiles);
            }else{
                Log.d("Test", "6");
                this.musicFiles = musicFiles;
            }
//            Log.d("Test", musicFiles.size()+" "+this.musicFiles.size());
//
//            Log.d("Test", paths.get(folderNumber).size() + " = " + this.path.size());

        }catch (NullPointerException n){

            Log.d("Test", n.getMessage());
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text_view_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        try {
            holder.names.setText(musicFiles.get(position));
            if (!playlist) {
                holder.path.setText(path.get(position));
            } else if (folderTrigger) {
                holder.path.setText(path.get(position));
            }
            if(PlayService.isPlayingNow()&&path.get(position).equals(PlayService.playingFile)){
                holder.mView.setBackground(context.getResources().getDrawable(R.drawable.playing_background));
                holder.names.setTextColor(context.getResources().getColor(R.color.colorWhite));
                holder.path.setTextColor(context.getResources().getColor(R.color.colorWhite));
            }
            holder.checkBox.setOnCheckedChangeListener(null);
            holder.checkBox.setChecked(checked.contains(String.valueOf(position)));
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (vis) {
                        holder.onClick(buttonView, isChecked);
                    }
                }
            });
        }catch(IndexOutOfBoundsException i){
            Log.d("Test", i.getMessage());
        }


//        mAdapter. notifyItemInserted(Integer.parseInt(position));


    }

    @Override
    public int getItemCount() {
        try {
            return musicFiles.size();
        }catch (NullPointerException n){
            return 0;
        }
    }

}