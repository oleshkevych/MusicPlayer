package com.example.vov4ik.musicplayer;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class AlbumsFragment extends Fragment implements View.OnClickListener {

    final static String EXTRA_FOR_FILES = "extra for files";
    final static String EXTRA_FOR_PATHS = "extra for paths";
    private  View rootView;
    private List<String> albumsName = null;
    private List<String[]> path = null;
    private List<String[]> filesName = null;
    private boolean albumTrigger = false;
    private int numberOfAlbum;
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
        LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.layoutAlbum);
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

    @Override
    public void onClick(View v) {
        if((!albumTrigger)) {
            numberOfAlbum = v.getId();
            showFolders(Arrays.asList(filesName.get(v.getId())));
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

            albumTrigger = true;
        }else if(v.getId()==0){
            showFolders(albumsName);
            albumTrigger = false;
        }else{
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(path.get(numberOfAlbum)[v.getId()]);

            Log.d("Test", "Album" + mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
            Log.d("Test", "Artist" + mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
            Log.d("Test", "duration" + mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            Log.d("Test", "Title" + mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
            Log.d("Test", "Writer" + mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_WRITER));
//            Intent intent = new Intent(getActivity(), PlayerActivity.class);
//            intent.putExtra(EXTRA_FOR_FILES, filesName.get(numberOfAlbum));
//            intent.putExtra(EXTRA_FOR_PATHS, path.get(numberOfAlbum));
//            startActivity(intent);
        }
    }
}
