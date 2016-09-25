package com.example.vov4ik.musicplayer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
 * Created by vov4ik on 9/12/2016.
 */
public class RecyclerAdapterForPlayerActivity extends RecyclerView.Adapter<RecyclerAdapterForPlayerActivity.ViewHolder> {
    private List<String> musicFiles;
    private List<String> path = new ArrayList<>();
    private List<String> checked;
    private PlayerActivity ma;
    public boolean vis;
    public Context context;
    public boolean background;

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
    public RecyclerAdapterForPlayerActivity(PlayerActivity ma, List<String> musicFiles, List<String> path, List<String> checked, boolean vis,
                               Context context, boolean background) {
        this.musicFiles = musicFiles;
        this.checked = checked;
        this.ma = ma;
        this.vis = vis;
        this.context = context;
        this.background = background;
        for (String s : path) {
            this.path.add((new File(s)).getParentFile().getPath());
        }
    }
    //    public void updateList(List<String> checked) {
//        this.checked = checked;
//        notifyDataSetChanged();
//    }
    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerAdapterForPlayerActivity.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text_view_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.names.setText(musicFiles.get(position));
        holder.path.setText(path.get(position));
        holder.checkBox.setChecked(checked.contains(String.valueOf(position)));
        if(background){
            holder.mView.setBackground(context.getResources().getDrawable(R.drawable.background_if_this_is_present));
        }
        if(PlayService.isPlayingNow()&&position == PlayService.getTrekNumber()){
            holder.mView.setBackground(context.getResources().getDrawable(R.drawable.playing_background));
        }
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(checked.contains(String.valueOf(position)));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(vis) {
                    holder.onClick(buttonView, isChecked);
                }
            }
        });

//        mAdapter. notifyItemInserted(Integer.parseInt(position));


    }

    @Override
    public int getItemCount() {
        return musicFiles.size();
    }
}
