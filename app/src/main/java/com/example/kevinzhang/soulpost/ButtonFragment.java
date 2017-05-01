package com.example.kevinzhang.soulpost;

import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
/**
 * Created by Administrator on 2017-04-13.
 */

public class ButtonFragment extends Fragment {

    private OnRecordButtonClickListener mCallback;
    private Button mButton;
    public interface OnRecordButtonClickListener {
        void onButtonPressed();
        void onButtonReleased();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            mCallback = (OnRecordButtonClickListener) context;
        }catch(ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.button_fragment, container, false);

        mButton = (Button) mView.findViewById(R.id.record_button);

        mButton.setOnTouchListener(new View.OnTouchListener() {
                                             @Override
                                             public boolean onTouch(View v, MotionEvent event) {
                                                 if(event.getAction() == MotionEvent.ACTION_DOWN){
                                                     mCallback.onButtonPressed();
                                                 }else if(event.getAction() == MotionEvent.ACTION_UP){
                                                     mCallback.onButtonReleased();
                                                 }
                                                 return false;
                                             }
                                         }
        );
        return mView;
    }
}

