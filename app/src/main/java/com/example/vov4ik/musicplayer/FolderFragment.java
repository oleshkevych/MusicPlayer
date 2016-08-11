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

public class FolderFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    private List<String> folderName = null;
    private List<String[]> path = null;
    private List<String[]> musicFiles = null;
    private  View rootView;
    private boolean folderTrigger = false;
    private int numberOfFolder;
    final static String EXTRA_FOR_FILES = "extra for files";
    final static String EXTRA_FOR_PATHS = "extra for paths";

    private static List<String> checkedList = new ArrayList<>();
    private static boolean checkingTrigger = false;
    private static LinearLayout linearLayout;

    public static boolean isCheckingTrigger() {
        return checkingTrigger;
    }

    public static void setCheckingTrigger(boolean checkingTrigger) {
        FolderFragment.checkingTrigger = checkingTrigger;
    }

    public static List<String> getCheckedList() {
        return checkedList;
    }

    public static void setCheckedList(List<String> checkedList) {
        FolderFragment.checkedList = checkedList;
    }

    public static LinearLayout getLinearLayout() {
        return linearLayout;
    }

    public static void setLinearLayout(LinearLayout linearLayout) {
        FolderFragment.linearLayout = linearLayout;
    }


    public FolderFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_folder, container, false);
        setCheckingTrigger(false);
        folderName = DbConnector.getFoldersFromDb(getContext());
        path = DbConnector.getPathsFromDb(getContext());
        musicFiles = DbConnector.getFilesNamesFromDb(getContext());
        if(rootView!=null) {
            showFolders(folderName);
        }
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onClick(View v) {
        if (!checkingTrigger) {
            if ((!folderTrigger)) {
                numberOfFolder = v.getId();
                showFolders(Arrays.asList(musicFiles.get(v.getId())));
                folderTrigger = true;
            } else if (v.getId() == 0) {
                showFolders(folderName);
                folderTrigger = false;
            } else {
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                intent.putExtra(EXTRA_FOR_FILES, musicFiles.get(numberOfFolder));
                intent.putExtra(EXTRA_FOR_PATHS, path.get(numberOfFolder));
                startActivity(intent);
            }
        }else if (!((folderTrigger) && (v.getId() == 0))) {
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
    private void showFolders(List<String> list){
        setLinearLayout((LinearLayout) rootView.findViewById(R.id.layoutFolder));
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
    public boolean onLongClick(View v) {
        if(!((v.getId()!=0)&&(folderTrigger))) {
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
            if(folderTrigger){
                showFolders(Arrays.asList(musicFiles.get(numberOfFolder)));
            }else{
                showFolders(folderName);
            }
        }
    }
}
