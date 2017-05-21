package com.camvy.kevinzhang.soulcast;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jonathan on 4/30/2017.
 */

public class IncomingSoulsFragment extends Fragment {

    private OnIncomingSoulClickListener mCallback;
    private ListView mListView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> listOfSouls;
    public interface OnIncomingSoulClickListener {
        void onSoulPressed(CharSequence textFromListView);
        void addSoulToQueue(String s3Key);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            mCallback = (IncomingSoulsFragment.OnIncomingSoulClickListener) context;
        }catch(ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.incoming_souls_fragment, container, false);

        mListView = (ListView) mView.findViewById(R.id.incoming_listview);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCallback.onSoulPressed(((TextView)view).getText());

            }
        });
        listOfSouls = new ArrayList<String>();
        arrayAdapter  = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, listOfSouls);

        mListView.setAdapter(arrayAdapter);
        ((MapActivity)getActivity()).setFragmentRefreshListener(new MapActivity.FragmentRefreshListener(){
            public void addSoulToQueue(String s3Key){
                listOfSouls.add(s3Key);
                arrayAdapter.notifyDataSetChanged();
            }
        });
        return mView;
    }
}
