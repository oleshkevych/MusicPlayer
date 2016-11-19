package com.example.vov4ik.musicplayer;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class FolderAllIncludeFragment extends MusicListFragment {//implements View.OnClickListener, View.OnLongClickListener {

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
    private static List<String> selectedPaths = new ArrayList<>();
/*
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

    public static List<String> getSelectedPaths() {
        return selectedPaths;
    }

    public static void setSelectedPaths(String selectedPaths) {
        FolderAllIncludeFragment.selectedPaths.add(selectedPaths);
    }

    public static void setNewSelectedPaths(List<String> selectedPaths) {
        FolderAllIncludeFragment.selectedPaths = selectedPaths;
    }
    public static void removeSelectedPaths(String selectedPaths) {
        FolderAllIncludeFragment.selectedPaths.remove(selectedPaths);
    }
    */
    
    private static MusicItemsList musicItemsList;

    protected MusicItemsList getMusicItemsList()
    {
        return musicItemsList;
    }

    public FolderAllIncludeFragment()
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
        // Inflate the layout for this fragment
        getMusicItemsList().setRootView(inflater.inflate(R.layout.fragment_folder_all_include, container, false));
        getMusicItemsList().setRecyclerView((RecyclerView) getMusicItemsList().getRootView().findViewById(R.id.folder_all_include_recycler_view));
        getMusicItemsList().setCheckingTrigger(false);

        List<String> names = DbConnector.getMainFoldersFromDb(getContext());
        List<List<String>> m = DbConnector.getFileNamesForMainFolders(getContext());
        List<List<String>> p = DbConnector.getPathsForMainFolders(getContext());
        List<String> n = new ArrayList<>();
        List<List<String>> p1 = new ArrayList<>();
        List<List<String>> m1 = new ArrayList<>();

            for (int i = 0; i < names.size(); i++) {
                n.add(names.get(i));
            }
            Collections.sort(names);
        if (!(names.size() == 1 && names.get(0).contains("Error"))) {
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
/*
    @Override
    public void onClick(View v) {
        if (!getMusicItemsList().isCheckingTrigger()) {
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
                List<String> l = getMusicItemsList().getCheckedList();
                l.remove(String.valueOf(v.getId()));
                getMusicItemsList().setCheckedList(l);
                if ((!folderTrigger)) {
                    for (int i = 1; i< pathForMainFolders.get(v.getId()).length; i++) {
                        getMusicItemsList().removeSelectedPaths(pathForMainFolders.get(v.getId())[i]);
                    }
                } else if (v.getId() != 0) {
                    getMusicItemsList().removeSelectedPaths(pathForMainFolders.get(numberOfFolder)[v.getId()]);
                }
            } else {
                v.setBackground(getResources().getDrawable(R.drawable.checked_view_background));
                List<String> l = getMusicItemsList().getCheckedList();
                l.add(String.valueOf(v.getId()));
                v.setTag("checked");
                getMusicItemsList().setCheckedList(l);
                if ((!folderTrigger)) {
                    for (int i = 1; i< pathForMainFolders.get(v.getId()).length; i++) {
                        getMusicItemsList().setSelectedPaths(pathForMainFolders.get(v.getId())[i]);
                    }
                } else if (v.getId() != 0) {
                    getMusicItemsList().setSelectedPaths(pathForMainFolders.get(numberOfFolder)[v.getId()]);
                }


            }
        }
    }
    private void show(List<String> list){
        getMusicItemsList().setLinearLayout((LinearLayout) rootView.findViewById(R.id.layoutFolderAllInclude));
        linearLayout = getMusicItemsList().getLinearLayout(); // TODO: Refactor this
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
        if(!((v.getId()==0)&&(folderTrigger))) {
            getMusicItemsList().setCheckingTrigger(true);
            v.setBackground(getResources().getDrawable(R.drawable.checked_view_background));
            v.setTag("checked");
            List<String> l = getMusicItemsList().getCheckedList();
            l.add(String.valueOf(v.getId()));
            getMusicItemsList().setCheckedList(l);
            if ((!folderTrigger)) {
                for (int i = 1; i< pathForMainFolders.get(v.getId()).length; i++) {
                    getMusicItemsList().setSelectedPaths(pathForMainFolders.get(v.getId())[i]);
                }
            } else if (v.getId() != 0) {
                getMusicItemsList().setSelectedPaths(pathForMainFolders.get(numberOfFolder)[v.getId()]);
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
