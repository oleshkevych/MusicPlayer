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


public class FolderAllIncludeFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    private  View rootView;
    private boolean folderTrigger = false;
    private int numberOfFolder;
    final static String EXTRA_FOR_FILES = "extra for files";
    final static String EXTRA_FOR_PATHS = "extra for paths";
    private List<String> mainFolders = new ArrayList<>();
    private List<String[]> pathForMainFolders = new ArrayList<>();
    private List<String[]> musicFilesInMainFolders = new ArrayList<>();

    private static List<String> checkedList = new ArrayList<>();
    private static boolean checkingTrigger = false;
    private static LinearLayout linearLayout;

    public static boolean isCheckingTrigger() {
        return checkingTrigger;
    }

    public static void setCheckingTrigger(boolean checkingTrigger) {
        FolderAllIncludeFragment.checkingTrigger = checkingTrigger;
    }

    public static List<String> getCheckedList() {
        return checkedList;
    }

    public static void setCheckedList(List<String> checkedList) {
        FolderAllIncludeFragment.checkedList = checkedList;
    }

    public static LinearLayout getLinearLayout() {
        return linearLayout;
    }

    public static void setLinearLayout(LinearLayout linearLayout) {
        FolderAllIncludeFragment.linearLayout = linearLayout;
    }

    public FolderAllIncludeFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_folder_all_include, container, false);
        setCheckingTrigger(false);
        pathForMainFolders = DbConnector.getMainFoldersPathsFromDb(getContext());
        mainFolders = DbConnector.getMainFoldersFromDb(getContext());
        musicFilesInMainFolders = DbConnector.getMainFoldersNamesFromDb(getContext());
//        for(File f: parentFileForAll.listFiles()){
//            mainPath.add(f.getPath());
//        }
//
//        for(int i = 0; i<path.size(); i++){
//            String[] pathArray = path.get(i);
//
//            for(String s: pathArray){
//                setStringPath(s);
//                File file = new File(s);
//                if (file.isDirectory()){
//                    Log.d("Test", "DIRECTORY "+file.getName());
//                }else {
//                    getParent(s);
//                }
//            }
//        }
        if(rootView!=null) {
            show(mainFolders);
        }
        return rootView;
    }

//    private void getParent(String s){
//        File f = new File(s);
//        if(!mainPath.contains(f.getParentFile().getPath())) {
//            if (mainPath.contains(f.getParentFile().getParentFile().getPath())) {
//                if(f.isDirectory()) {
//                    if (!mainFolders.contains(f.getName())) {
//                        mainFolders.add(f.getName());
//                        pathForMainFolders.add(new String[]{"go to root",getStringPath()});
//                        musicFilesInMainFolders.add(new String[]{"go to root", new File(getStringPath()).getName()});
//                    } else {
//                        int number = mainFolders.indexOf(f.getName());
//                        String[] list = pathForMainFolders.get(number);
//                        List<String> listString = new ArrayList<>();
//                        listString.add(getStringPath());
//                        listString.addAll(0, Arrays.asList(list));
//                        pathForMainFolders.remove(number);
//                        String[] newArray = new String[listString.size()];
//                        pathForMainFolders.add(number, listString.toArray(newArray));
//                        list = musicFilesInMainFolders.get(number);
//                        listString = new ArrayList<>();
//                        listString.add(new File(getStringPath()).getName());
//                        listString.addAll(0, Arrays.asList(list));
//                        musicFilesInMainFolders.remove(number);
//                        newArray = new String[listString.size()];
//                        musicFilesInMainFolders.add(number, listString.toArray(newArray));
//                    }
//                }else{
//                    if (!mainFolders.contains("RootFiles")) {
//                        mainFolders.add("RootFiles");
//                        pathForMainFolders.add(new String[]{"go to root",getStringPath()});
//                        musicFilesInMainFolders.add(new String[]{"go to root", new File(getStringPath()).getName()});
//                    } else {
//
//                        int number = mainFolders.indexOf("RootFiles");
//                        String[] list = pathForMainFolders.get(number);
//                        List<String> listString = new ArrayList<>();
//                        listString.add(getStringPath());
//                        listString.addAll(0, Arrays.asList(list));
//                        pathForMainFolders.remove(number);
//                        String[] newArray = new String[listString.size()];
//                        pathForMainFolders.add(number, listString.toArray(newArray));
//                        list = musicFilesInMainFolders.get(number);
//                        listString = new ArrayList<>();
//                        listString.add(new File(getStringPath()).getName());
//                        listString.addAll(0, Arrays.asList(list));
//                        musicFilesInMainFolders.remove(number);
//                        newArray = new String[listString.size()];
//                        musicFilesInMainFolders.add(number, listString.toArray(newArray));
//                    }
//                }
//            } else {
//                getParent(f.getParentFile().getPath());
//            }
//        }
//    }
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
                show(Arrays.asList(musicFilesInMainFolders.get(v.getId())));
                folderTrigger = true;
            } else if (v.getId() == 0) {
                show(mainFolders);
                folderTrigger = false;
            } else {
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                intent.putExtra(EXTRA_FOR_FILES, musicFilesInMainFolders.get(numberOfFolder));
                intent.putExtra(EXTRA_FOR_PATHS, pathForMainFolders.get(numberOfFolder));
                startActivity(intent);
            }
        } else if (!((folderTrigger) && (v.getId() == 0))) {
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
    private void show(List<String> list){
        setLinearLayout((LinearLayout) rootView.findViewById(R.id.layoutFolderAllInclude));
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
        Log.d("Test", menuVisible + " FolderAllInclude");
        if((!menuVisible)&&(rootView!=null)){
            setCheckingTrigger(false);
            if(folderTrigger){
                show(Arrays.asList(musicFilesInMainFolders.get(numberOfFolder)));
            }else{
                show(mainFolders);
            }
        }
    }
}
