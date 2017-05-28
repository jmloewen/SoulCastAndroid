package com.camvy.kevinzhang.soulcast;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * Created by Jonathan on 5/14/2017.
 */


public class HistoryActivity extends AppCompatActivity{
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private HistoryRecyclerAdapter mAdapter;
    private ArrayList<Soul> historySouls;
    private HistoryInterface historyInterface;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        historySouls = new ArrayList<Soul>();

        //ArrayList<Soul> dummySouls = new ArrayList<Soul>();
        //public Soul(String soulType, String s3Key, long epoch, Device userDevice) {
        //Soul dummySoul = new Soul("android", "dummyKey", 12312312, StaticObjectReferences.mUserDevice);
        //dummySouls.add(dummySoul);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        historyInterface = new HistoryInterface() {
            @Override
            public void onSuccess(Response<ArrayList<Soul>> listOfSouls) {

                for (int i = 0; i < listOfSouls.body().size(); i++){
                    historySouls.add(listOfSouls.body().get(i));
                }
                mAdapter.notifyDataSetChanged();
                Log.d("RetrievedSouls", "Retrieved list of souls");
                for (int i = 0; i < listOfSouls.body().size(); i++){
                    Log.d("SLS3:", listOfSouls.body().get(i).gets3Key());
                }
            }

            @Override
            public void onAPICallError(Response<ArrayList<Soul>> response) {
                Log.d("HistAPIERR", "No success in History call. Error: " + response.code() + ", error:" + response.errorBody().toString());
            }

            @Override
            public void onConnectionError(Throwable t) {
                Log.d("HistCNXNER", "Connection error in History. Error: " + t.getMessage());
            }
        };
        APIUserConnect.getHistory(historyInterface);
        mAdapter = new HistoryRecyclerAdapter(historySouls);
        mRecyclerView.setAdapter(mAdapter);
    }
}
