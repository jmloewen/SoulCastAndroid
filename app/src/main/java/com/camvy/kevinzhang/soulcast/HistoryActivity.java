package com.camvy.kevinzhang.soulcast;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonathan on 5/14/2017.
 */

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private HistoryRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        ArrayList<Soul> dummySouls = new ArrayList<Soul>();
        //public Soul(String soulType, String s3Key, long epoch, Device userDevice) {
        Soul dummySoul = new Soul("android", "dummyKey", 12312312, StaticObjectReferences.mUserDevice);
        dummySouls.add(dummySoul);
        mAdapter = new HistoryRecyclerAdapter(dummySouls);
        mRecyclerView.setAdapter(mAdapter);

        if (dummySouls.size() > 0){
            //displaySoulOnList
            mAdapter.notifyDataSetChanged();
        }
    }
}
