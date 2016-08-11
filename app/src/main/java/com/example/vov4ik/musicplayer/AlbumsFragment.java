package com.example.vov4ik.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlbumsFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    final static String EXTRA_FOR_FILES = "extra for files";
    final static String EXTRA_FOR_PATHS = "extra for paths";
    private  View rootView;
    private List<String> albumsName = null;
    private List<String[]> path = null;
    private List<String[]> filesName = null;
    private boolean albumTrigger = false;
    private int numberOfAlbum;

    private static List<String> checkedList = new ArrayList<>();
    private static boolean checkingTrigger = false;
    private static LinearLayout linearLayout;

    public static boolean isCheckingTrigger() {
        return checkingTrigger;
    }

    public static void setCheckingTrigger(boolean checkingTrigger) {
        AlbumsFragment.checkingTrigger = checkingTrigger;
    }

    public static List<String> getCheckedList() {
        return checkedList;
    }

    public static void setCheckedList(List<String> checkedList) {
        AlbumsFragment.checkedList = checkedList;
    }

    public static LinearLayout getLinearLayout() {
        return linearLayout;
    }

    public static void setLinearLayout(LinearLayout linearLayout) {
        AlbumsFragment.linearLayout = linearLayout;
    }

    public AlbumsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_albums, container, false);
        setCheckingTrigger(false);
        albumsName = DbConnector.getAlbumFromDb(getContext());
        path = DbConnector.getAlbumPathsFromDb(getContext());
        filesName = DbConnector.getAlbumNamesFromDb(getContext());

        if(rootView!=null) {
            showFolders(albumsName);
        }
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }
    private void showFolders(List<String> list){
        setLinearLayout((LinearLayout) rootView.findViewById(R.id.layoutAlbum));
        linearLayout.removeAllViews();
        for (String s : list) {
            TextView text = new TextView(linearLayout.getContext());
            text.setText(String.valueOf(s));
            text.setId((list).indexOf(s));
            ((LinearLayout) linearLayout).addView(text);
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
        if (!checkingTrigger) {
            if ((!albumTrigger)) {
                numberOfAlbum = v.getId();
                showFolders(Arrays.asList(filesName.get(v.getId())));
                albumTrigger = true;
            } else if (v.getId() == 0) {
                showFolders(albumsName);
                albumTrigger = false;
            } else {
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                intent.putExtra(EXTRA_FOR_FILES, filesName.get(numberOfAlbum));
                intent.putExtra(EXTRA_FOR_PATHS, path.get(numberOfAlbum));
                startActivity(intent);
            }
        } else if (!((albumTrigger) && (v.getId() == 0))) {
            if ((v.getTag()!=null)&&(v.getTag().equals("checked"))){
                v.setBackground(null);
                v.setTag(null);
                List<String> l = getCheckedList();
                l.remove(String.valueOf(v.getId()));
                setCheckedList(l);
            } else {
                v.setBackground(getResources().getDrawable(R.drawable.checked_view_background));
                List<String> l = getCheckedList();
                l.add(String.valueOf(v.getId()));
                v.setTag("checked");
                setCheckedList(l);
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if(!((v.getId()==0)&&(albumTrigger))) {
            setCheckingTrigger(true);
            v.setBackground(getResources().getDrawable(R.drawable.checked_view_background));
            v.setTag("checked");
            List<String> l = getCheckedList();
            l.add(String.valueOf(v.getId()));
            setCheckedList(l);
        }
        return true;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        Log.d("Test", menuVisible+" Album");
        if((!menuVisible)&&(rootView!=null)){
            setCheckingTrigger(false);
            if(albumTrigger){
                showFolders(Arrays.asList(filesName.get(numberOfAlbum)));
            }else{
                showFolders(albumsName);
            }
        }
    }
}
