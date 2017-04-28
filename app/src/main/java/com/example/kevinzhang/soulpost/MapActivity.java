package com.example.kevinzhang.soulpost;

import android.Manifest;

import android.app.Fragment;
import android.content.Context;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        buttonFragment.OnRecordButtonClickListener{

    //firebase
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    //permission request codes
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private final float DEFAULT_USER_RADIUS = 1.0f;

    //sharedpreferences
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private static final String SOULPREFS = "SoulcastPreferences";

    //Variables for location handling
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationManager mLocationManager;
    private Location mLastLocation;
    Marker currentLocationMarker;


    //Variables for Audio Up / Audio Down.
    private RecordButton tempRecordButton;
    private RecordButton mRecordButton;
    private AudioPipeline mAudioPipeline;

    //The user's device
    private Device userDevice = null;

    private TransferUtility mTransferUtility;
    private Activity mActivity = this;
    private boolean mIsConnected = false;
    private static File mAudioFile;
    private static File receiveNotificationAudioFile;

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://soulcast.ml")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mTransferUtility = Util.getTransferUtility(this);

        setPreferences();
        setupFirebase();
        setupMapFragment();
        setupAudioPipeline();

        Intent myIntent = getIntent();
        String S3key = myIntent.getStringExtra("S3key");
        playNotificationMessage(S3key);

    }

    private void setPreferences() {
        //presistent store
        prefs = getSharedPreferences(SOULPREFS, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    private void setupMapFragment() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void setupAudioPipeline() {
        mAudioPipeline = new AudioPipeline();
        mAudioPipeline.setmAudioPipelineListener(new AudioPipeline.AudioPipelineListener() {
            @Override
            public void onRecordingFinished(File audioFile) {
                beginUpload(audioFile);
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
//        AppIndex.AppIndexApi.end(mGoogleApiClient, getIndexApiAction());
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient.disconnect();
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
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
        buildGoogleAPIClient();
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
        if (fineLocationGranted()) {

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            //button enable here
            registerNewUserDevice();

        }else{
            requestFineLocationPermissions();
        }
    }

    private void registerNewUserDevice() {
        LatLng latLng;

        try {
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            userDevice = APIUserConnect.RegisterDevice(latLng, this);
            Toast.makeText(this, "Connection Established", Toast.LENGTH_LONG).show();
        } catch(NullPointerException e){
            e.printStackTrace();
        }
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
            throw new AssertionError("userDevice cannot be null during onlocation changed");
        } else {
            updateDeviceLocation(mLastLocation);
            moveMaptoCurrentLocation(mLastLocation);
        }
    }

    private void moveMaptoCurrentLocation(Location mLastLocation) {

        //move the map appropriately.
        LatLng currentLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));

        if (currentLocationMarker == null){
            currentLocationMarker = mMap.addMarker(new MarkerOptions()
                    .position(currentLatLng)
                    .title("Current Location"));
        }else{
            currentLocationMarker.setPosition(currentLatLng);
        }

    }

    private void updateDeviceLocation(Location mLastLocation) {
        //update the device location
        userDevice.setLatitude((float) mLastLocation.getLatitude());
        userDevice.setLongitude((float) mLastLocation.getLongitude());
        userDevice.setRadius(DEFAULT_USER_RADIUS);

        //update the device record on the server
        APIUserConnect.UpdateDevice(userDevice, this);
    }

    private void requestLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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
                        Toast.makeText(mActivity, "Soul Casted to Server", Toast.LENGTH_SHORT).show();
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
        if (!fineLocationGranted()) {
            requestFineLocationPermissions();
        }
    }

    private boolean fineLocationGranted() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    private void requestFineLocationPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission granted
                    if (fineLocationGranted()) {
                            buildGoogleAPIClient();
                    }
                } else {
                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_LONG).show();
                }
            }
            // other cases to check for other permissions this app might request.
        }
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
        FirebaseMessaging.getInstance().subscribeToTopic("friendly_engage");
    }

    //TODO: be refactored out
    void uploadSoulToServer(String fileName) {
        Toast.makeText(MapActivity.this, "In uploadToServer. S3Key is: " + fileName, Toast.LENGTH_SHORT).show();
        SoulpostAPI myAPI = retrofit.create(SoulpostAPI.class);

        Soul mSoul = new Soul("Android", fileName, System.currentTimeMillis(), userDevice);
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

    private void playNotificationMessage(String S3key){
        if(S3key == null)
        {
            Log.v("S3KeyNull","S3key is null");
            return;
        }

        receiveNotificationAudioFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),S3key);
        Log.v("KeyNotNull",S3key);
        TransferObserver observer = mTransferUtility.download(Constants.BUCKET_NAME, receiveNotificationAudioFile.getName(), receiveNotificationAudioFile);

        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState newState) {
                //Enum status = newState.valueOf("Completed");
                //Log.v("in OnStateChanged","In onstate changed");
                switch (newState) {
                    case COMPLETED:
                        Toast.makeText(mActivity, "Download to S3 completed!", Toast.LENGTH_SHORT).show();
                        Log.v("DownloadStateComplete"," Download completed");
                        final MediaPlayer mMediaPlayer = new MediaPlayer();

                        try {
                            FileInputStream fd = openFile(receiveNotificationAudioFile);
                            mMediaPlayer.setDataSource(fd.getFD());
//            Log.d(TAG, "Path: " + mAudioFile.getAbsolutePath());
                            mMediaPlayer.prepare();
                            mMediaPlayer.start();
                            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    mMediaPlayer.reset();
                                    Log.d("receiveNotificationDL", "Finished playing downloaded audio File");
                                }


                            });

                        } catch (IOException e) {

                            e.printStackTrace();
                            Log.d("receiveNotificationDL", e.getMessage());
                            Log.d("receiveNotificationDL", e.toString());
                        }
                }
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

    private FileInputStream openFile(File file) throws FileNotFoundException, IOException {
        FileInputStream fos = new FileInputStream(file);
        // remember th 'fos' reference somewhere for later closing it
        return fos;
    }

    @Override
    public void onButtonPressed() {
        mAudioPipeline.startRecording();
    }

    @Override
    public void onButtonReleased() {
        if (mAudioPipeline.mHasAudioRecordingBeenStarted) {
            mAudioPipeline.stopRecording();
        }
    }

}