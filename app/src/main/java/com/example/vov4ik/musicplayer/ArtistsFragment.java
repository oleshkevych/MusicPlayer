package com.example.vov4ik.musicplayer;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ArtistsFragment extends MusicListFragment {//implements View.OnClickListener, View.OnLongClickListener {

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
    private static List<String> selectedPaths = new ArrayList<>();
/*
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

    public static List<String> getSelectedPaths() {
        return selectedPaths;
    }

    public static void setSelectedPaths(String selectedPaths) {
        ArtistsFragment.selectedPaths.add(selectedPaths);
    }

    public static void setNewSelectedPaths(List<String> selectedPaths) {
        ArtistsFragment.selectedPaths = selectedPaths;
    }
    public static void removeSelectedPaths(String selectedPaths) {
        ArtistsFragment.selectedPaths.remove(selectedPaths);
    }
*/
private static MusicItemsList musicItemsList;

    protected MusicItemsList getMusicItemsList()
    {
        return musicItemsList;
    }

    public ArtistsFragment()
    {
        if (musicItemsList == null)
        {
            musicItemsList = new MusicItemsList();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getMusicItemsList().setRootView(inflater.inflate(R.layout.fragment_artists, container, false));
        getMusicItemsList().setRecyclerView((RecyclerView) getMusicItemsList().getRootView().findViewById(R.id.artist_recycler_view));
        getMusicItemsList().setCheckingTrigger(false);

        List<String> names = DbConnector.getArtistFromDb(getContext());
        List<List<String>> m = DbConnector.getFileNamesForArtists(getContext());
        List<List<String>> p = DbConnector.getPathsForArtists(getContext());
        List<String> n = new ArrayList<>();
        List<List<String>> p1 = new ArrayList<>();
        List<List<String>> m1 = new ArrayList<>();
        if (!(names.size() == 1 && names.get(0).contains("Error"))) {
            for (int i = 0; i < names.size(); i++) {
                n.add(names.get(i));
            }
            Collections.sort(names);
            Log.d("tetst", names.size() + " " + m.size() + " " + p.size() + " ");
            for (int i = 0; i < names.size(); i++) {
                int index = n.indexOf(names.get(i));
                p1.add(p.get(index));
                m1.add(m.get(index));
            }
        }

        getMusicItemsList().setFolderName(names);
        getMusicItemsList().setPath(p1);
        getMusicItemsList().setMusicFiles(m1);
        show(getMusicItemsList().getFolderName());
        return getMusicItemsList().getRootView();

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

/*
    private void show(List<String> list){
        getMusicItemsList().setLinearLayout((LinearLayout) rootView.findViewById(R.id.layoutArtist));
        linearLayout = getMusicItemsList().getLinearLayout(); // TODO: Refactor this
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
        if (!getMusicItemsList().isCheckingTrigger()) {
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
                List<String> l = getMusicItemsList().getCheckedList();
                l.remove(String.valueOf(v.getId()));
                getMusicItemsList().setCheckedList(l);

                if ((!artistTrigger)) {
                    for (int i = 1; i< path.get(v.getId()).length; i++) {
                        getMusicItemsList().removeSelectedPaths(path.get(v.getId())[i]);
                    }
                } else if (v.getId() != 0) {
                    getMusicItemsList().removeSelectedPaths(path.get(numberOfArtist)[v.getId()]);
                }

            } else {
                v.setBackground(getResources().getDrawable(R.drawable.checked_view_background));
                List<String> l = getMusicItemsList().getCheckedList();
                l.add(String.valueOf(v.getId()));
                v.setTag("checked");
                getMusicItemsList().setCheckedList(l);

                if ((!artistTrigger)) {
                    for (int i = 1; i< path.get(v.getId()).length; i++) {
                        getMusicItemsList().setSelectedPaths(path.get(v.getId())[i]);
                    }
                } else if (v.getId() != 0) {
                    getMusicItemsList().setSelectedPaths(path.get(numberOfArtist)[v.getId()]);
                }

            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if(!((v.getId()==0)&&(artistTrigger))) {
            getMusicItemsList().setCheckingTrigger(true);
            v.setBackground(getResources().getDrawable(R.drawable.checked_view_background));
            v.setTag("checked");
            List<String> l = getMusicItemsList().getCheckedList();
            l.add(String.valueOf(v.getId()));
            getMusicItemsList().setCheckedList(l);
            if ((!artistTrigger)) {
                for (int i = 1; i< path.get(v.getId()).length; i++) {
                    getMusicItemsList().setSelectedPaths(path.get(v.getId())[i]);
                }
            } else if (v.getId() != 0) {
                getMusicItemsList().setSelectedPaths(path.get(numberOfArtist)[v.getId()]);
            }
        }
        return true;
    }
*/
    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if((!menuVisible)&&(getMusicItemsList().getRootView()!=null)){
            getMusicItemsList().setCheckingTrigger(false);
            if(getMusicItemsList().isFolderTrigger()) {
                show((getMusicItemsList().getMusicFiles().get(getMusicItemsList().getNumberOfFolder())));
            }else {
                show(getMusicItemsList().getFolderName());
            }
        }
    }
}
