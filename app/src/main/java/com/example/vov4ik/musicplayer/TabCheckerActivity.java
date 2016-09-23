package com.example.vov4ik.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class TabCheckerActivity extends AppCompatActivity {

    List<TabConstructor> tabConstructors = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_checker);
        tabConstructors = DbConnector.getAllTabs(getApplicationContext());
        List<String> l = DbConnector.getVisibleTabs(getApplicationContext());
        if((l.size() == 1)&&(l.get(0).equals(TabConstructor.getListOfTabs().get(3)))){
            tabConstructors.get(3).setVisibility(true);
        }
        Button confirm = (Button) findViewById(R.id.tab_confirm_button);
        assert confirm != null;
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbConnector.tabsFiller(getApplicationContext(), tabConstructors);
                startMainActivity();
                finish();
            }
        });
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.tab_recycler_view);
        assert recyclerView != null;
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        RecyclerAdapterForTabsActivity recyclerAdapterForTabsActivity = new RecyclerAdapterForTabsActivity(this, tabConstructors);
        recyclerView.setAdapter(recyclerAdapterForTabsActivity);
    }

    public void check(int position){
        boolean b = !tabConstructors.get(position).isVisibility();
        tabConstructors.get(position).setVisibility(b);
        Log.d("Test", tabConstructors.toString());

    }
    private void startMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
