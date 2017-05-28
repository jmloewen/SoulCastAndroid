package com.camvy.kevinzhang.soulcast;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Jonathan on 5/14/2017.
 */



public class HistoryRecyclerAdapter extends RecyclerView.Adapter<HistoryRecyclerAdapter.HistoryHolder>{

    private ArrayList<Soul> mHistory;
    //item_image, item_date, item_description
    private ImageView item_image;
    private TextView item_date;
    private TextView item_description;


    /**
     * This is a class for individual rows in the history adapter.
     */
    public static class HistoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public HistoryHolder(View v){
            super(v);
            v.setOnClickListener(this);
        }

        public void onClick(View v){

        }
    }

    public HistoryRecyclerAdapter(ArrayList<Soul> souls){
        mHistory = souls;
    }

    @Override
    public HistoryRecyclerAdapter.HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item_row, parent, false);
        item_image = (ImageView)inflatedView.findViewById(R.id.item_image);
        item_date = (TextView)inflatedView.findViewById(R.id.item_date);
        item_description = (TextView) inflatedView.findViewById(R.id.item_description);

        //temporary for demo purposes.
        item_description.setText(mHistory.get(0).getSoulType());
        item_date.setText("" + mHistory.get(0).getEpoch());

        return new HistoryHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(HistoryRecyclerAdapter.HistoryHolder holder, int position) {

    }



    @Override
    public int getItemCount() {
        return mHistory.size();
    }
}


