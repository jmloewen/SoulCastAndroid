package com.example.kevinzhang.soulpost;

import android.support.v4.view.MarginLayoutParamsCompat;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Button;
import android.content.Context;
import android.util.AttributeSet;
import android.app.Activity;
import android.support.v7.widget.AppCompatButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Created by Jonathan on 3/19/2017.
 */

//this is a false error - it still compiles despite this.

/**
 * This is a class for holding Button functionality - to separate it from MapActivity.
 */
public class RecordButton extends Button {
    /**
     * @param context - the context of the activity the button sits in.
     */
    public RecordButton(Context context, AttributeSet attrs){
        super(context, attrs);
        /*
        this.setWidth(60);
        this.setHeight(60);
        //this.setLayoutParams()
        RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(60, 60);
        rlParams.setMargins(0, 0, 0, 24);
        rlParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rlParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        this.setTag("record_button");
        this.setLayoutParams(rlParams);

        this.setBackgroundResource(R.drawable.record_button_states);
        */
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Toast.makeText(getContext(), "ABCFDSSDFS", Toast.LENGTH_LONG).show();
        return super.onTouchEvent(event);
    }

    /*
    * <Button
        android:id="@+id/record_button"
        android:layout_width="60dp"
        android:layout_height="60dp"
        android:layout_alignParentBottom="true"
        android:layout_centerHorizontal="true"
        android:layout_marginBottom="24dp"
        android:background="@drawable/record_button_states"/>
    *
    * */

    //some function for animations
    //some function for click events
    //some function for setting up the audio pipeline

}
