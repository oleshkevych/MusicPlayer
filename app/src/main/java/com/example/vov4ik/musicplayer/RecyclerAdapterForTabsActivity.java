package com.example.vov4ik.musicplayer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by vov4ik on 9/23/2016.
 */
public class RecyclerAdapterForTabsActivity extends RecyclerView.Adapter<RecyclerAdapterForTabsActivity.ViewHolder> {

    private List<TabConstructor> list;
    private TabCheckerActivity activity;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public View mView;
        public TextView name;
        public CheckBox checkBox;


        public ViewHolder(View v) {
            super(v);
            mView = v;
            name = (TextView) v.findViewById(R.id.tabsNames);
            checkBox = (CheckBox) v.findViewById(R.id.tabCheckBox);
            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            CheckBox c = (CheckBox) v.findViewById(R.id.tabCheckBox);
            if (c.isChecked()) {
                c.setChecked(false);
            } else {
                c.setChecked(true);
            }

        }
        public void onClick(View v, boolean isChecked) {
            activity.check(getPosition());
        }
    }
    public RecyclerAdapterForTabsActivity(TabCheckerActivity activity, List<TabConstructor> list) {
        this.list = list;
        this.activity = activity;
    }

    @Override
    public RecyclerAdapterForTabsActivity.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tab_recycler_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.name.setText(list.get(position).getName());
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(list.get(position).isVisibility());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                holder.onClick(buttonView, isChecked);
//                Log.d("Test", buttonView.getId()+"");
//                Log.d("Test", buttonView.getLayout().getLineCount()+"");


            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

