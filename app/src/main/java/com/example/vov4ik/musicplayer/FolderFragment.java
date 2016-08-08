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

import java.util.Arrays;
import java.util.List;

public class FolderFragment extends Fragment implements View.OnClickListener {

    private List<String> folderName = null;
    private List<String[]> path = null;
    private List<String[]> musicFiles = null;
    private  View rootView;
    private boolean folderTrigger = false;
    private int numberOfFolder;
    final static String EXTRA_FOR_FILES = "extra for files";
    final static String EXTRA_FOR_PATHS = "extra for paths";
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
        if((!folderTrigger)) {
            numberOfFolder = v.getId();
            showFolders(Arrays.asList(musicFiles.get(v.getId())));
//            LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.layoutFolder);
//            linearLayout.removeAllViews();
//            for (String s : musicFiles.get(v.getId())) {
//                TextView text = new TextView(linearLayout.getContext());
//                text.setText(String.valueOf(s));
//                text.setId(Arrays.asList(musicFiles.get(v.getId())).indexOf(s));
//                ((LinearLayout) linearLayout).addView(text);
//                text.setOnClickListener(this);
//                text.setPadding(20, 10, 20, 10);
//                text.setTextSize(16);
//                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) text.getLayoutParams();
//                mlp.setMargins(0, 15, 0, 15);
//            }
            folderTrigger = true;
        }else if(v.getId()==0){
            showFolders(folderName);
            folderTrigger = false;
        }else{
            Intent intent = new Intent(getActivity(), PlayerActivity.class);
            intent.putExtra(EXTRA_FOR_FILES, musicFiles.get(numberOfFolder));
            intent.putExtra(EXTRA_FOR_PATHS, path.get(numberOfFolder));
            startActivity(intent);
        }
    }
    private void showFolders(List<String> list){
        LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.layoutFolder);
        linearLayout.removeAllViews();
        for (String s : list) {
            TextView text = new TextView(linearLayout.getContext());
            text.setText(String.valueOf(s));
            text.setId((list).indexOf(s));
            ((LinearLayout) linearLayout).addView(text);
            text.setOnClickListener(this);
            text.setPadding(20, 10, 20, 10);
            text.setTextSize(16);
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) text.getLayoutParams();
            mlp.setMargins(0, 15, 0, 15);
        }
    }
}
