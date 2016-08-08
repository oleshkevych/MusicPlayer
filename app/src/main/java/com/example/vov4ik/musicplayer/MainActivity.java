package com.example.vov4ik.musicplayer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity {
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private static Boolean executeTrigger = false;
//    private View mContentView;
//    private final Runnable mHidePart2Runnable = new Runnable() {
//        @SuppressLint("InlinedApi")
//        @Override
//        public void run() {
//            // Delayed removal of status and navigation bar
//
//            // Note that some of these constants are new as of API 16 (Jelly Bean)
//            // and API 19 (KitKat). It is safe to use them, as they are inlined
//            // at compile-time and do nothing on earlier devices.
//            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//        }
//    };
//    private final Runnable mHideRunnable = new Runnable() {
//        @Override
//        public void run() {
//            hide();
//        }
//    };


    public static Boolean getExecuteTrigger() {
        return executeTrigger;
    }

    public static void setExecuteTrigger(Boolean executeTrigger) {
        MainActivity.executeTrigger = executeTrigger;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        /*setExecuteTrigger(true);
        RefreshDb rDb = new RefreshDb();
        rDb.execute();*/

//        mContentView = findViewById(R.id.fullscreen_content);
//        backGround
//        try {
//            while (true) {
//                delayedHide(100);
//                Thread.sleep(5 * 1000);
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        String[] listOfMods = {"Folder", "Artist", "Album", "Folder(All Content)", "Playlist"};
//        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layoutForScrolling);
//        for(String s: listOfMods){
//            TextView text = new TextView(linearLayout.getContext());
//            text.setText(s);
//            text.setId(Arrays.asList(listOfMods).indexOf(listOfMods));
//            text.setTextSize(getResources().getDimension(R.dimen.bigText));
//            text.setPadding(100, 10, 100, 10);
//        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

    setSupportActionBar(toolbar);

    String[] listOfMods = {"Album","Artist",  "Folder(All Content)", "Folder","Playlist"};

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        for (String s: listOfMods){
        tabLayout.addTab(tabLayout.newTab().setText(s));
    }
        tabLayout.setTabGravity(TabLayout.MODE_FIXED);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabGravity(TabLayout.SCROLL_INDICATOR_RIGHT);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        Button refreshButton = (Button) findViewById(R.id.refreshFilesButton);
        assert refreshButton != null;
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //First variant
               /* while(getExecuteTrigger()){

                }*/
                if (!getExecuteTrigger()) {
                    RefreshDb rDb = new RefreshDb();
                    rDb.execute();
                }else{
                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_SHORT;
                    CharSequence text = "Updating is running!";
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
//        delayedHide(100);
    }

//    private void hide() {
//        // Hide UI first
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.hide();
//        }
//        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
//    }
//    private void delayedHide(int delayMillis) {
//        mHideHandler.removeCallbacks(mHideRunnable);
//        mHideHandler.postDelayed(mHideRunnable, delayMillis);
//    }
    private class RefreshDb extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            new DbConnector().fillerForDb(getApplicationContext());
            return null;
        }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        setExecuteTrigger(false);
    }
}
}
