package com.example.kevinzhang.soulpost;

import android.Manifest;

import android.content.Context;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.lang.Object;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.wearable.Asset;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.iid.FirebaseInstanceId;

import junit.framework.Assert;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.amazonaws.mobileconnectors.s3.transferutility.TransferState.COMPLETED;
import static java.lang.Integer.parseInt;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private View.OnClickListener mOnClickListener;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private final int AUDIO_AND_STORAGE_PERMISSION_REQUEST_CODE = 2;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationManager mLocationManager;
    private Location mLastLocation;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    private Button mRecordButton;
    private MediaRecorder mMediaRecorder;
    private AudioRecorder mAudioRecorder;
    private static final String SOULPREFS = "SoulcastPreferences";

    private Device userDevice = null;

    private TransferUtility mTransferUtility;
    Activity mActivity = this;

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://soulcast.ml")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //presistent store
        prefs = getSharedPreferences(SOULPREFS, Context.MODE_PRIVATE);
        editor = prefs.edit();

        //firebase
        setupFirebase();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //S3
        mTransferUtility = Util.getTransferUtility(this);

        setAudioRecorder();
        //MediaPlayer
//        mAudioRecorder = new AudioRecorder();
//        mAudioRecorder.setmAudioRecorderListener(new AudioRecorder.AudioRecorderListener() {
//            @Override
//            public void onRecordingFinished(File audioFile) {
//                //TODO Upload to S3
//                beginUpload(audioFile);
//            }
//        });

        permissionCheck();

        buttonSetup();
        //button setup
//        mRecordButton = (Button) findViewById(R.id.record_button);
//        mRecordButton.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                switch (motionEvent.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        // User pressed down on the button
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            {
//                                checkAudioAndStoragePermission();
//                                if ((ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.RECORD_AUDIO)
//                                        == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(MapActivity.this,
//                                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
//                                    Log.d("TSTRCRD", "PRESTRTREC");
//                                    mAudioRecorder.startRecording();
//                                    Log.d("TSTRCRD", "POSTSTRTREC");
//                                }
//                            }
//                        } else {
//                            Log.d("TSTRCRD", "Test Start Record");
//                            mAudioRecorder.startRecording();
//                        }
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        // User released the button
//                        if (mAudioRecorder.mHasAudioRecordingBeenStarted) {
//                            mAudioRecorder.stopRecording();
//                            Log.d("TSTRCRD", "Test Stop Record");
//
//                        }
//                        break;
//                }
//                return false;
//            }
//        });
//
        //build api client
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
//
//        mOnClickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                beginDownload("148814239037");
//            }
//        };
    }

    private void setAudioRecorder() {
        mAudioRecorder = new AudioRecorder();
        mAudioRecorder.setmAudioRecorderListener(new AudioRecorder.AudioRecorderListener() {
            @Override
            public void onRecordingFinished(File audioFile) {
                //TODO Upload to S3
                beginUpload(audioFile);
            }
        });
    }

    private void permissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            {
                checkLocationPermission();
            }
        }
    }

    private void buttonSetup() {
        //button setup
        mRecordButton = (Button) findViewById(R.id.record_button);
        mRecordButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // User pressed down on the button
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            {
                                checkAudioAndStoragePermission();
//                                if ((ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.RECORD_AUDIO)
//                                        == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(MapActivity.this,
//                                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
//                                    Log.d("TSTRCRD", "PRESTRTREC");
//                                    mAudioRecorder.startRecording();
//                                    Log.d("TSTRCRD", "POSTSTRTREC");
//                                }
                                mAudioRecorder.startRecording();
                            }
                        } else {
                            mAudioRecorder.startRecording();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mAudioRecorder.mHasAudioRecordingBeenStarted) {
                            mAudioRecorder.stopRecording();
                        }
                        break;
                }
                return false;
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        buildGoogleAPIClient();
        //mMap.setMyLocationEnabled(true);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            //user is on SDK > 23
//            if (ContextCompat.checkSelfPermission(this,
//                    Manifest.permission.ACCESS_FINE_LOCATION)
//                    == PackageManager.PERMISSION_GRANTED) {
//                buildGoogleAPIClient();
//                mMap.setMyLocationEnabled(true);
//            }
//        } else {
//            buildGoogleAPIClient();
//            mMap.setMyLocationEnabled(true);
//        }
    }

    protected synchronized void buildGoogleAPIClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLocationUpdates();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            userDevice = APIUserConnect.RegisterDevice(latLng, this);
        }

        Toast.makeText(this, "Connection Established", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onConnectionSuspended(int i) {
        //Toast.makeText(this, "CNXN Suspended", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Toast.makeText(this, "CNXN Fail", Toast.LENGTH_LONG).show();
    }

    /**
     * Currently, this is only being called on launch.  We need a locationlistener to listen for location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        //this is done because we want to store the location for other purposes.
        mLastLocation = location;
        if (userDevice == null) {
            //we should never arrive here - here only for debugging purposes right now.
            throw new AssertionError("userDevice cannot be null during onlocation changed");
        } else {
            //update the device location
            userDevice.latitude = (float) mLastLocation.getLatitude();
            userDevice.longitude = (float) mLastLocation.getLongitude();

            //update the device record on the server
            APIUserConnect.UpdateDevice(userDevice, this);

            //move the map appropriately.
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(20));
        }

        displayIncomingMessages();
    }

    private void requestLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    }

    private void beginDownload(final File audioFile){
        TransferObserver observer = mTransferUtility.download(Constants.BUCKET_NAME, audioFile.getName(), audioFile);
        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState newState) {
                //Enum status = newState.valueOf("Completed");
                switch (newState) {
                    case COMPLETED:
                        Toast.makeText(mActivity, "Download from S3 completed!", Toast.LENGTH_SHORT).show();
  //                      mAudioRecorder.
                }
                Log.v("transfer listener", "here");
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                String str = Long.toString(bytesCurrent);
                Log.v("transfer listener", str);
            }

            @Override
            public void onError(int id, Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void beginUpload(final File audioFile) {
        TransferObserver observer = mTransferUtility.upload(Constants.BUCKET_NAME, audioFile.getName(), audioFile);

        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState newState) {
                //Enum status = newState.valueOf("Completed");
                switch (newState) {
                    case COMPLETED:
                        Toast.makeText(mActivity, "Upload to S3 completed!", Toast.LENGTH_SHORT).show();
                        uploadSoulToServer(audioFile.getName());
                }
                Log.v("transfer listener", "here");
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                String str = Long.toString(bytesCurrent);
                Log.v("transfer listener", str);
            }

            @Override
            public void onError(int id, Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

    }

    private void checkAudioAndStoragePermission() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    AUDIO_AND_STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleAPIClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_LONG).show();
                }
            }
            // other cases to check for other permissions this app might request.
        }
    }

    private void displayIncomingMessages() {
        //a snackbar goes here, with a link to all messages generated since last update.
        Snackbar.make(findViewById(android.R.id.content), "New Message!", Snackbar.LENGTH_LONG)
                .setAction("OK", mOnClickListener)
                .setActionTextColor(Color.RED)
                .show();
    }

    /**
     * This sets up the connection between the user and our server.
     */
    private void setupFirebase() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        //local var
        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings =
                new FirebaseRemoteConfigSettings.Builder()
                        .setDeveloperModeEnabled(true)
                        .build();
        // Define default config values. Defaults are used when fetched config values are not
        // available. Eg: if an error occurred fetching values from the server.
        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put("friendly_msg_length", 10L);
        // Apply config settings and default values.
        mFirebaseRemoteConfig.setConfigSettings(firebaseRemoteConfigSettings);
        mFirebaseRemoteConfig.setDefaults(defaultConfigMap);
    }

    void uploadSoulToServer(String fileName) {
        Toast.makeText(MapActivity.this, "In uploadToServer. S3Key is: " + fileName, Toast.LENGTH_SHORT).show();
        SoulpostAPI myAPI = retrofit.create(SoulpostAPI.class);
        Soul mSoul = new Soul("Android", fileName, (int) System.currentTimeMillis() / 1000, mLastLocation.getLongitude(), mLastLocation.getLatitude(), 1.0, FirebaseInstanceId.getInstance().getToken());
        Call<Soul> call = myAPI.soulPost(mSoul);

        call.enqueue(new Callback<Soul>() {
            @Override
            public void onResponse(Call<Soul> call, Response<Soul> response) {
                Toast.makeText(MapActivity.this, " Soul uploaded to Soulcast server", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Soul> call, Throwable t) {
                Toast.makeText(MapActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Map Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }
}