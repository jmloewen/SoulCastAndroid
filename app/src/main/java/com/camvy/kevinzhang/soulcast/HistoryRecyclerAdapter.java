package com.camvy.kevinzhang.soulcast;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Jonathan on 5/14/2017.
 */



public class HistoryRecyclerAdapter extends RecyclerView.Adapter<HistoryRecyclerAdapter.HistoryHolder>{

    private ArrayList<Soul> mHistory;

    /**
     * This is a class for individual rows in the history adapter.
     */
    public static class HistoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView item_date;
        private TextView item_description;
        private String audioS3Key;
        private Context rowContext;

        public HistoryHolder(View view, Context rowContext){
            super(view);
            item_date = (TextView)view.findViewById(R.id.item_date);
            item_description = (TextView) view.findViewById(R.id.item_description);
            this.rowContext = rowContext;
            view.setOnClickListener(this);
        }
        //on click, we should play the mappropriate message.
        public void onClick(View v){
            playClickedMessage(audioS3Key);
        }

        private FileInputStream openFile(File file) throws FileNotFoundException, IOException {
            FileInputStream fos = new FileInputStream(file);
            // remember th 'fos' reference somewhere for later closing it
            return fos;
        }

        private void playClickedMessage(String s3Key){
            //If the s3Key doesn't exist, we've accessed this function improperly, somehow.  Exit.
            if(s3Key == null) {
                Log.v("s3KeyNull","s3Key is null");
                return;
            }

            //Grab the audio file that we were sent, identified by the s3Key.
            final File historySoul = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), s3Key);
            TransferUtility transferUtility = Util.getTransferUtility(rowContext);

            TransferObserver observer = transferUtility.download(Constants.BUCKET_NAME, historySoul.getName(), historySoul);
            //create our transfer listener for this audio message.
            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState newState) {
                    switch (newState) {
                        case COMPLETED:
//                        Toast.makeText(mActivity, "Download to S3 completed!", Toast.LENGTH_SHORT).show();
                            final MediaPlayer mMediaPlayer = new MediaPlayer();
                            try {
                                FileInputStream fd = openFile(historySoul);
                                mMediaPlayer.setDataSource(fd.getFD());
                                mMediaPlayer.prepare();
                                mMediaPlayer.start();
                                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        mMediaPlayer.reset();
                                        Log.d("receiveNotificationDL", "Finished playing downloaded audio File");
                                    }
                                });
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                    }
                }

                /**
                 * This conceivably exists to be able to show a progress bar for our file download.
                 * It is not being used.
                 * @param id
                 * @param bytesCurrent
                 * @param bytesTotal
                 */
                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    String str = Long.toString(bytesCurrent);
                    Log.v("transfer listener", str);
                }

                /**
                 * If there is an error in download, throw an error log into the android message queue.
                 * @param id
                 * @param e
                 */
                @Override
                public void onError(int id, Exception e) {
                    e.printStackTrace();
                    Log.v("PNMoE", "Error in downloading audio message.");
                }
            });
        }
    }

    public HistoryRecyclerAdapter(ArrayList<Soul> souls){
        mHistory = souls;
    }

    //This is the outer adapter, used to set up each individual row.
    @Override
    public HistoryRecyclerAdapter.HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item_row, parent, false);
        return new HistoryHolder(inflatedView, parent.getContext());
    }

    /**
     * This is where we populate on a row by row basis.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(HistoryRecyclerAdapter.HistoryHolder holder, int position) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Soul soul = mHistory.get(position);
        holder.item_description.setText(soul.getRadius() + "km away");
        holder.item_date.setText("Received on: " + sdf.format(new Date(soul.getEpoch())));
        holder.audioS3Key = soul.gets3Key();
    }

    @Override
    public int getItemCount() {
        return mHistory.size();
    }
}


