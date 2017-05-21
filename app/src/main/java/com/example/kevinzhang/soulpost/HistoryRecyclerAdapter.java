package com.example.kevinzhang.soulpost;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Jonathan on 5/14/2017.
 */



public class HistoryRecyclerAdapter extends RecyclerView.Adapter<HistoryRecyclerAdapter.HistoryHolder>{

    private ArrayList<Soul> mHistory;


    public static class HistoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public HistoryHolder(View v){
            super(v);
            v.setOnClickListener(this);
        }
        public void onClick(View v){
            Log.d("Recyclerview", "Clicked");
        }
    }

    public HistoryRecyclerAdapter(ArrayList<Soul> souls){
        mHistory = souls;
    }

    @Override
    public HistoryRecyclerAdapter.HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item_row, parent, false);
        return null;
    }

    @Override
    public void onBindViewHolder(HistoryRecyclerAdapter.HistoryHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mHistory.size();
    }
}


