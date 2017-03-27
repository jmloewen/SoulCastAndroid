package com.example.kevinzhang.soulpost;

import android.os.Build;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.view.MotionEvent;
import android.view.View;
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
    PermissionsHandler ph;

    public RecordButton(Context context, AttributeSet attrs, PermissionsHandler ph){
        super(context, attrs);

        //this.setLayoutParams()
        RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(60, 60);
        rlParams.setMargins(0, 0, 0, 24);
        rlParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rlParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rlParams.width = 200;
        rlParams.height = 200;
        this.setTag("record_button");
        this.setLayoutParams(rlParams);
        this.setBackgroundResource(R.drawable.record_button_states);

        this.ph = ph;
        StaticObjectReferences.mRecordButton = this;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // User pressed down on the button
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ph.checkAudioAndStoragePermission();
                    //StaticObjectReferences.mAudioPipeline.startRecording();
                } else {
                    StaticObjectReferences.mAudioPipeline.startRecording();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (StaticObjectReferences.mAudioPipeline.mHasAudioRecordingBeenStarted) {
                    StaticObjectReferences.mAudioPipeline.stopRecording();
                }
                break;
        }
        return true;
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


