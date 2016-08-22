package com.example.vov4ik.musicplayer;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by vov4ik on 8/14/2016.
 */
public abstract class MusicListFragment extends Fragment implements ISelectableFragment, View.OnClickListener, View.OnLongClickListener {

    final static String EXTRA_FOR_CLICKED_FILE = "extra for clicked file";
    final static String EXTRA_FOR_PATHS = "extra for paths";
    public boolean isCheckingTrigger() {
        return getMusicItemsList().isCheckingTrigger();
    }

    public List<String> getPreviousList(){
        return getMusicItemsList().getFolderName();
    }
    public List<String> getSelectedPaths()
    {
        return getMusicItemsList().getSelectedPaths();
    }

    public boolean isFolderTrigger() {
        return getMusicItemsList().isFolderTrigger();
    }

    public void setFolderTrigger(boolean folderTrigger) {
        getMusicItemsList().setFolderTrigger(folderTrigger);
    }

    public void unselectMusicItems()
    {
        List<String> list;
        getMusicItemsList().setCheckingTrigger(false);
        list = getMusicItemsList().getCheckedList();
        for(String s: list) {
            int i = Integer.parseInt(s);
            View v = getMusicItemsList().getLinearLayout().findViewById(i);
            v.setBackground(null);
            v.setTag(null);
        }
        getMusicItemsList().setCheckedList(new ArrayList<String>());
        getMusicItemsList().setNewSelectedPaths(new ArrayList<String>());
    }

    protected abstract MusicItemsList getMusicItemsList();

    public void show(List<String> list){
        //linearLayout = getMusicItemsList().getLinearLayout(); // TODO: Refactor this
        getMusicItemsList().getLinearLayout().removeAllViews();
        for (String s : list) {
            TextView text = new TextView(getMusicItemsList().getLinearLayout().getContext());
            text.setText(String.valueOf(s));
            text.setId((list).indexOf(s));
            ((LinearLayout) getMusicItemsList().getLinearLayout()).addView(text);
            text.setOnClickListener(this);
            text.setOnLongClickListener(this);
            text.setPadding(20, 10, 20, 10);
            text.setTextSize(16);
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) text.getLayoutParams();
            mlp.setMargins(0, 15, 0, 15);
        }
    }
    @Override
    public void onClick(View v) {
        if (!getMusicItemsList().isCheckingTrigger()) {
            if ((!getMusicItemsList().isFolderTrigger())) {
                getMusicItemsList().setNumberOfFolder(v.getId());
                show(Arrays.asList(getMusicItemsList().getMusicFiles().get(v.getId())));
                getMusicItemsList().setFolderTrigger(true);
            } else if (v.getId() == 0) {
                show(getMusicItemsList().getFolderName());
                getMusicItemsList().setFolderTrigger(false);
            } else {
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                intent.putExtra(EXTRA_FOR_CLICKED_FILE, getMusicItemsList().getPath().get(getMusicItemsList().getNumberOfFolder())[(v.getId())]);
                intent.putExtra(EXTRA_FOR_PATHS, getMusicItemsList().getPath().get(getMusicItemsList().getNumberOfFolder()));
                startActivity(intent);
                getMusicItemsList().setFolderTrigger(false);
                show(getMusicItemsList().getFolderName());
            }
        }else if (!((getMusicItemsList().isFolderTrigger()) && (v.getId() == 0))) {
            if ((v.getTag()!=null)&&(v.getTag().equals("checked"))){
                v.setBackground(null);
                v.setTag(null);
                List<String> l =  getMusicItemsList().getCheckedList();
                l.remove(String.valueOf(v.getId()));
                getMusicItemsList().setCheckedList(l);

                if ((!getMusicItemsList().isFolderTrigger())) {
                    for (int i = 1; i< getMusicItemsList().getPath().get(v.getId()).length; i++) {
                        getMusicItemsList().removeSelectedPaths(getMusicItemsList().getPath().get(v.getId())[i]);
                    }
                } else if (v.getId() != 0) {
                    getMusicItemsList().removeSelectedPaths(getMusicItemsList().getPath().get(getMusicItemsList().getNumberOfFolder())[v.getId()]);
                }

            } else {
                v.setBackground(getResources().getDrawable(R.drawable.checked_view_background));
                List<String> l =  getMusicItemsList().getCheckedList();
                l.add(String.valueOf(v.getId()));
                v.setTag("checked");
                getMusicItemsList().setCheckedList(l);

                if ((!getMusicItemsList().isFolderTrigger())) {
                    for (int i = 1; i< getMusicItemsList().getPath().get(v.getId()).length; i++) {
                        getMusicItemsList().setSelectedPaths(getMusicItemsList().getPath().get(v.getId())[i]);
                    }
                } else if (v.getId() != 0) {
                    getMusicItemsList().setSelectedPaths(getMusicItemsList().getPath().get(getMusicItemsList().getNumberOfFolder())[v.getId()]);
                }
            }
        }
    }


    @Override
    public boolean onLongClick(View v) {
//        Log.d("Test",(v.getId()==0)+" "+
        if(!((v.getId()==0)&&(getMusicItemsList().isFolderTrigger()))) {
            getMusicItemsList().setCheckingTrigger(true);
            v.setBackground(getResources().getDrawable(R.drawable.checked_view_background));
            v.setTag("checked");
            List<String> l =  getMusicItemsList().getCheckedList();
            l.add(String.valueOf(v.getId()));
            getMusicItemsList().setCheckedList(l);
            if ((!getMusicItemsList().isFolderTrigger())) {
                for (int i = 1; i< getMusicItemsList().getPath().get(v.getId()).length; i++) {
                    getMusicItemsList().setSelectedPaths(getMusicItemsList().getPath().get(v.getId())[i]);
                }
            } else if (v.getId() != 0) {
                getMusicItemsList().setSelectedPaths(getMusicItemsList().getPath().get(getMusicItemsList().getNumberOfFolder())[v.getId()]);
            }
        }
        return true;
    }
}
