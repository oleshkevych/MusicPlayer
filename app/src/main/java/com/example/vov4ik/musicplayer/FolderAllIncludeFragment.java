package com.example.vov4ik.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FolderAllIncludeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FolderAllIncludeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FolderAllIncludeFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private File[] files;
    private List<String> item = null;
    private List<String[]> path = null;
    private List<String[]> musicFiles = null;
    private String root;
    private  View rootView = null;
    private OnFragmentInteractionListener mListener;
    private boolean folderTrigger = false;
    private int numberOfFolder;
    final static String EXTRA_FOR_FILES = "extra for files";
    final static String EXTRA_FOR_PATHS = "extra for paths";


    public FolderAllIncludeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FolderAllIncludeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FolderAllIncludeFragment newInstance(String param1, String param2) {
        FolderAllIncludeFragment fragment = new FolderAllIncludeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_folder_all_include, container, false);
        root = Environment.getExternalStorageDirectory().getParentFile().getParent();
        item = DbConnector.getFoldersFromDb(getContext());
        path = DbConnector.getPathsFromDb(getContext());
        musicFiles = DbConnector.getFilesNamesFromDb(getContext());
        if(rootView!=null) {
            LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.layoutFolderAllInclude);
            for (String s : item) {
                TextView text = new TextView(linearLayout.getContext());
                text.setText(String.valueOf(s));
                text.setId((item).indexOf(s));
                ((LinearLayout) linearLayout).addView(text);
                text.setOnClickListener(this);
                text.setPadding(20, 10, 20, 10);
                text.setTextSize(16);
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) text.getLayoutParams();
                mlp.setMargins(0, 15, 0, 15);
            }
        }
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        if((!folderTrigger)) {
            numberOfFolder = v.getId();
            LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.layoutFolderAllInclude);
            linearLayout.removeAllViews();
            for (String s : musicFiles.get(v.getId())) {
                TextView text = new TextView(linearLayout.getContext());
                text.setText(String.valueOf(s));
                text.setId(Arrays.asList(musicFiles.get(v.getId())).indexOf(s));
                ((LinearLayout) linearLayout).addView(text);
                text.setOnClickListener(this);
                text.setPadding(20, 10, 20, 10);
                text.setTextSize(16);
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) text.getLayoutParams();
                mlp.setMargins(0, 15, 0, 15);
            }
            folderTrigger = true;
        }else if(v.getId()==0){
            LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.layoutFolderAllInclude);
            linearLayout.removeAllViews();
            for (String s : item) {
                TextView text = new TextView(linearLayout.getContext());
                text.setText(String.valueOf(s));
                text.setId((item).indexOf(s));
                ((LinearLayout) linearLayout).addView(text);
                text.setOnClickListener(this);
                text.setPadding(20, 10, 20, 10);
                text.setTextSize(16);
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) text.getLayoutParams();
                mlp.setMargins(0, 15, 0, 15);
            }
            folderTrigger = false;
        }else{
            Log.d("Test", "File name " + musicFiles.get(numberOfFolder)[v.getId()]);
            Log.d("Test", "Path " + path.get(numberOfFolder)[v.getId()]);
            Intent intent = new Intent(getActivity(), PlayerActivity.class);
            intent.putExtra(EXTRA_FOR_FILES, musicFiles.get(numberOfFolder));
            intent.putExtra(EXTRA_FOR_PATHS, path.get(numberOfFolder));
            startActivity(intent);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
