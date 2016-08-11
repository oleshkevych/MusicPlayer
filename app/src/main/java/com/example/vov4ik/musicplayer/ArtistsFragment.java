package com.example.vov4ik.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ArtistsFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    final static String EXTRA_FOR_FILES = "extra for files";
    final static String EXTRA_FOR_PATHS = "extra for paths";
    private View rootView;
    private List<String> artistsName = null;
    private List<String[]> path = null;
    private List<String[]> filesName = null;
    private boolean artistTrigger = false;
    private int numberOfArtist;

    private static List<String> checkedList = new ArrayList<>();
    private static boolean checkingTrigger = false;
    private static LinearLayout linearLayout;

    public static boolean isCheckingTrigger() {
        return checkingTrigger;
    }

    public static void setCheckingTrigger(boolean checkingTrigger) {
        ArtistsFragment.checkingTrigger = checkingTrigger;
    }

    public static List<String> getCheckedList() {
        return checkedList;
    }

    public static void setCheckedList(List<String> checkedList) {
        ArtistsFragment.checkedList = checkedList;
    }

    public static LinearLayout getLinearLayout() {
        return linearLayout;
    }

    public static void setLinearLayout(LinearLayout linearLayout) {
        ArtistsFragment.linearLayout = linearLayout;
    }

    public ArtistsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_artists, container, false);
        setCheckingTrigger(false);
        artistsName = DbConnector.getArtistFromDb(getContext());
        path = DbConnector.getArtistPathsFromDb(getContext());
        filesName = DbConnector.getArtistNamesFromDb(getContext());

        if(rootView!=null) {
            show(artistsName);
        }
        return rootView;

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    private void show(List<String> list){
        setLinearLayout((LinearLayout) rootView.findViewById(R.id.layoutArtist));
        linearLayout.removeAllViews();
        for (String s : list) {
            TextView text = new TextView(linearLayout.getContext());
            text.setText(String.valueOf(s));
            text.setId((list).indexOf(s));
            (linearLayout).addView(text);
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
            if ((!artistTrigger)) {
                numberOfArtist = v.getId();
                show(Arrays.asList(filesName.get(v.getId())));
                artistTrigger = true;
            } else if (v.getId() == 0) {
                show(artistsName);
                artistTrigger = false;
            } else {
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                intent.putExtra(EXTRA_FOR_FILES, filesName.get(numberOfArtist));
                intent.putExtra(EXTRA_FOR_PATHS, path.get(numberOfArtist));
                startActivity(intent);
            }
        } else if (!((artistTrigger) && (v.getId() == 0))) {
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
        if(!((v.getId()!=0)&&(artistTrigger))) {
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
        if((!menuVisible)&&(rootView!=null)){
            setCheckingTrigger(false);
            if(artistTrigger){
                show(Arrays.asList(filesName.get(numberOfArtist)));
            }else{
                show(artistsName);
            }
        }
    }
}
