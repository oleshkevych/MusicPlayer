package com.example.vov4ik.musicplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ArtistsFragment extends Fragment implements View.OnClickListener{

    private File[] files;
    private List<String> item = null;
    private List<String> path = null;
    private String root;
    private  View rootView = null;

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
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_artists, container, false);

        LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.layoutArtist);
        for(String s: path) {
            TextView text = new TextView(linearLayout.getContext());
            text.setText(String.valueOf(s));
            text.setId((path).indexOf(s));
            linearLayout.addView(text);
            text.setOnClickListener(this);
            text.setPadding(20, 10, 20, 10);
            text.setTextSize(16);
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) text.getLayoutParams();
            mlp.setMargins(0, 15, 0, 15);
        }
        return rootView;

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("Test", "Starting fragment");
        boolean isSDPresent = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);

        if (isSDPresent) {
            File file[] = Environment.getExternalStorageDirectory().listFiles();
            if (file != null) {
                for (int i = 0; i < file.length; i++) {
                    Log.d("Test", "SSS " + file[i].getAbsolutePath());
                }
                files = file;
            }
        }
        root = Environment.getExternalStorageDirectory().getParentFile().getParent();

        getDir(root);
    }

    private void getDir(String dirPath){
        item = new ArrayList<String>();
        path = new ArrayList<String>();
        File f = new File(dirPath);
        File[] files = f.listFiles();

        if(!dirPath.equals(root))
        {
            item.add(root);
            path.add(root);
            item.add("../");
            path.add(f.getParent());
        }

        for(int i=0; i < files.length; i++)
        {
            File file = files[i];

            if(!file.isHidden() && file.canRead()){
                path.add(file.getPath());
                if(file.isDirectory()){
                    item.add(file.getName() + "/");
                }else{
                    item.add(file.getName());
                }
            }
        }
        if(rootView!=null) {
            LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.layoutArtist);
            for (String s : item) {
                TextView text = new TextView(linearLayout.getContext());
                text.setText(String.valueOf(s));
                text.setId((item).indexOf(s));
                linearLayout.addView(text);
                text.setOnClickListener(this);
                text.setPadding(20, 10, 20, 10);
                text.setTextSize(16);
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) text.getLayoutParams();
                mlp.setMargins(0, 15, 0, 15);
            }
        }
    }


    @Override
    public void onClick(View v) {
        Log.d("Test", "SSS " + path.get(v.getId()));
        File file = new File(path.get(v.getId()));

        if (file.isDirectory())
        {
            if(file.canRead()){
                LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.layoutArtist);
                linearLayout.removeAllViews();
                getDir(path.get(v.getId()));
            }else{
                new AlertDialog.Builder(getContext())
                        .setIcon(R.color.black_overlay)
                        .setTitle("[" + file.getName() + "] folder can't be read!")
                        .setPositiveButton("OK", null).show();
            }
        }else {
            new AlertDialog.Builder(getContext())
                    .setIcon(R.color.black_overlay)
                    .setTitle("[" + file.getName() + "]")
                    .setPositiveButton("OK", null).show();

        }


    }


}
