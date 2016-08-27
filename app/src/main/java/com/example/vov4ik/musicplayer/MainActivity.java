package com.example.vov4ik.musicplayer;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
    private static Boolean executeTrigger = false;
    private ViewPager viewPager;
    private PagerAdapter adapter;
    final static String EXTRA_FOR_CLICKED_FILE = "extra for clicked file";
    final static String EXTRA_FOR_PATHS = "extra for paths";



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
        if(PlayService.getPlayer()==null) {
            Intent intent2 = new Intent(this, NotificationClass.class);
            startService(intent2);
            Intent intent = new Intent(this, PlayService.class);
            startService(intent);
            Intent intent1 = new Intent(this, AutoAudioStopper.class);
            startService(intent1);
//            Intent intent3 = new Intent(this, PhoneCallReceiver.class);
//            startService(intent3);

            AudioManager am = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

            AutoAudioStopper.getInstance().setAudioManager(am);
            AutoAudioStopper.getInstance().setContext(this);
            ComponentName mReceiverComponent = new ComponentName(this,HeadphonesClickReceiver.class);
            am.registerMediaButtonEventReceiver(mReceiverComponent);

//            IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
//            filter.setPriority(1000);
//            HeadphonesClickReceiver r = new HeadphonesClickReceiver();
//            am.registerMediaButtonEventReceiver();
//            registerReceiver(r, filter);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String[] listOfMods = {"Album","Artist",  "Folder(All Content)", "Folder","Playlist"};
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        for (String s: listOfMods) {
            tabLayout.addTab(tabLayout.newTab().setText(s));
        }
        tabLayout.setTabGravity(TabLayout.MODE_FIXED);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabGravity(TabLayout.SCROLL_INDICATOR_RIGHT);

        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new PagerAdapter
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




        final Button playButton = (Button) findViewById(R.id.playButton);

        assert playButton != null;
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!PlayService.isPlayingNow()){
                    PlayService.startPlaying();
                }else{
                    PlayService.pausePlaying();
                }
                buttonChanger();
            }
        });
        Button nextButton = (Button) findViewById(R.id.nextButton);
        assert nextButton != null;
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayService.nextSong();
               buttonChanger();

            }
        });
        final ImageButton previousButton = (ImageButton) findViewById(R.id.previousButton);
        assert previousButton != null;
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayService.previous();
                buttonChanger();
            }
        });
        Button showPlaylist = (Button) findViewById(R.id.openPlayerList);
        assert showPlaylist != null;
        showPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                startActivity(intent);
            }
        });
        final Button shuffle = (Button) findViewById(R.id.shuffle);
        assert shuffle != null;
        if(PlayService.isShuffle()){
            shuffle.setBackground(getResources().getDrawable(R.drawable.shuffle_on));
        }else{
            shuffle.setBackground(getResources().getDrawable(R.drawable.shuffle_off));
        }
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PlayService.isShuffle()){
                    PlayService.setShuffle(false);
                    shuffle.setBackground(getResources().getDrawable(R.drawable.shuffle_off));
                }else{
                    PlayService.setShuffle(true);
                    shuffle.setBackground(getResources().getDrawable(R.drawable.shuffle_on));
                }
            }
        });
        LinearLayout layout = (LinearLayout)findViewById(R.id.playMenu);
        assert layout != null;
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test", v.getId()+"");
                Log.d("test", v.toString());
                v.setBackground(getResources().getDrawable(R.drawable.checked_view_background));
            }
        });


        if(!PlayService.isPlayingNow()) {
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            PlayService.playFile("START", getApplicationContext(), nm);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        buttonChanger();
    }

    private void buttonChanger(){
        if(PlayService.isPlayingNow()){
            /*AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, 0);
            manager.cancel(pendingIntent);
            pendingIntent.cancel();*/
            Button playButton = (Button) findViewById(R.id.playButton);
            playButton.setBackground(getResources().getDrawable(R.drawable.pause_png));
        }else{
           /* AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, 0);

            int interval = 900000;
            manager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), interval, pendingIntent);*/
            Button playButton = (Button) findViewById(R.id.playButton);
            playButton.setBackground(getResources().getDrawable(R.drawable.play_button_png));

        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }
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

    @Override
    public void onBackPressed() {
        ISelectableFragment ff = (ISelectableFragment)adapter.getItem(viewPager.getCurrentItem());
        if (ff.isCheckingTrigger()){
            ff.unselectMusicItems();
        }else if(ff.isFolderTrigger()) {
            ff.show(ff.getPreviousList());
            ff.setFolderTrigger(false);
        }else{
//            stopService(new Intent(this, PlayService.class));
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.refreshFilesButton) {
            if (!getExecuteTrigger()) {
                setExecuteTrigger(true);
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
        if (id == R.id.addSelected) {
            ISelectableFragment ff = (ISelectableFragment)adapter.getItem(viewPager.getCurrentItem());
            if (ff.getSelectedPaths().size()>0) {
                Intent intent = new Intent(this, PlayerActivity.class);
                intent.putExtra(EXTRA_FOR_PATHS, ff.getSelectedPaths().toArray(new String[ff.getSelectedPaths().size()]));
                intent.putExtra(EXTRA_FOR_CLICKED_FILE, "ADD");
                startActivity(intent);
                ff.unselectMusicItems();
            }else{
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                CharSequence text = "Make shore, that you have selected some thing! ";
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }

        if (id == R.id.closePlayer) {
            AlarmManager manager;
            PendingIntent pendingIntent;
            manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
            int interval = 180000;
            manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, System.currentTimeMillis(), interval, pendingIntent);
            PlayService.pausePlaying();
            Intent intent = new Intent(getApplicationContext(), PlayService.class);
            stopService(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

//    public boolean isMyServiceRunning(Class<?> serviceClass) {
//        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (serviceClass.getName().equals(service.service.getClassName())) {
//                return true;
//            }
//        }
//        return false;
//    }


    @Override
    protected void onDestroy() {
        AlarmManager manager;
        PendingIntent pendingIntent;
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


//      START NotificationClass.sendNotification();
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        int interval = 180000;
        manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, System.currentTimeMillis(), interval, pendingIntent);
        super.onDestroy();
    }
}
