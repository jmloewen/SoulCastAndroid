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

/**
 * This is the fragment that displays the button on the MapActivity.
 * Hopefully, we have re-use opportunities for it elsewhere!
 */

public class ButtonFragment extends Fragment {

    private OnButtonClickListener mCallback;
    private Button mButton;

    /**
     * A simple interface for button use in the MapActivity.
     */
    public interface OnButtonClickListener {
        void onButtonPressed();
        void onButtonReleased();
    }

    /**
     * Activates on attach to an Activity.
     * This setsup the callback to the activity that we are being attached to and throws an exception if it fails.
     * @param context   The activity this has been attached to
     */
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            mCallback = (OnButtonClickListener) context;
        }catch(ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    /**
     * Called on view creation.  Sets up the button for use with the activity that is running it.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.button_fragment, container, false);

        //Find the button, then create a listener for the button and assign this functionality to the calling activity.
        mButton = (Button) mView.findViewById(R.id.soul_button);
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

