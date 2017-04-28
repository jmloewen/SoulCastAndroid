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

    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private Device userDevice = null;

    private final float DEFAULT_USER_RADIUS = 1.0f;
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

    private AudioPipeline mAudioPipeline;

    private TransferUtility mTransferUtility;
    private Activity mActivity = this;
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

        String S3key = getIntent().getStringExtra("S3key");
        playNotificationMessage(S3key);
    }

    private void setPreferences() {
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

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        registerNewUserDevice();
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
        //TODO update device location
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failed, try again", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (userDevice == null) {
            throw new AssertionError("userDevice cannot be null during onLocationChanged");
        } else {
            updateDeviceLocation(mLastLocation);
            moveMaptoCurrentLocation(mLastLocation);
        }
    }

    private void moveMaptoCurrentLocation(Location mLastLocation) {
        LatLng currentLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
        //Set Marker
        if (currentLocationMarker == null){
            currentLocationMarker = mMap.addMarker(new MarkerOptions()
                    .position(currentLatLng)
                    .title("Current Location"));
        }else{
            currentLocationMarker.setPosition(currentLatLng);
        }
    }

    private void updateDeviceLocation(Location mLastLocation) {
        userDevice.setLatitude((float) mLastLocation.getLatitude());
        userDevice.setLongitude((float) mLastLocation.getLongitude());
        userDevice.setRadius(DEFAULT_USER_RADIUS);
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

    private void setupFirebase() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
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
        if(S3key == null) {
            Log.v("S3KeyNull","S3key is null");
            return;
        }
        receiveNotificationAudioFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),S3key);
        Log.v("S3Key is:",S3key);
        TransferObserver observer = mTransferUtility.download(Constants.BUCKET_NAME, receiveNotificationAudioFile.getName(), receiveNotificationAudioFile);
        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState newState) {
                switch (newState) {
                    case COMPLETED:
                        Toast.makeText(mActivity, "Download to S3 completed!", Toast.LENGTH_SHORT).show();
                        Log.v("DownloadStateComplete"," Download completed");
                        final MediaPlayer mMediaPlayer = new MediaPlayer();

                        try {
                            FileInputStream fd = openFile(receiveNotificationAudioFile);
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