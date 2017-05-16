package com.example.kevinzhang.soulpost;

import android.content.Context;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        buttonFragment.OnRecordButtonClickListener{

    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private Device userDevice = null;

    private int DEFAULT_LOCATION_REQUEST_INTERVAL = 1000;
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


    /**
     * onCreate is the basic setup function for MapActivity.  We will enter this activity assuming that a few things are already set up.
     * These things are Permissions, the Button Fragment, and the Audio Pipeline.
     * The first of these things is currently implemented, the latter two are not yet set up on MainActivity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //basic setup for the activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initializeTransferUtility();

        setupFirebase();
        setPreferences();
        setupMapFragment();
        setupAudioPipeline();

        playNotificationMessage(getIntent().getStringExtra(Constants.s3Key));
    }

    private void initializeTransferUtility() {
        mTransferUtility = Util.getTransferUtility(this);
    }

    /**
     * Grab existing Shared Preferences.
     */
    private void setPreferences() {
        prefs = getSharedPreferences(SOULPREFS, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    /**
     * Set up the Map Fragment Asynchronously within the application
     */
    private void setupMapFragment() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Set up the Audio Pipeline and the listener for file uploads.
     */
    private void setupAudioPipeline() {
        mAudioPipeline = new AudioPipeline();
        mAudioPipeline.setmAudioPipelineListener(new AudioPipeline.AudioPipelineListener() {
            @Override
            public void onRecordingFinished(File audioFile) {
                beginUpload(audioFile);
            }
        });
    }

    /**
     * If we're paused, stop location updates to conserve battery power.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }


    /**
     * Disconnect from Maps API if we close the application.
     */
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
     * When the map is ready, grab the pointer to it and allow the user to interact with it.
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

        mMap.setOnCameraMoveListener(this);

        buildGoogleAPIClient();
    }

    @Override
    public void onCameraMove() {
        Log.d("map", "current zoom: " + mMap.getCameraPosition().zoom);
        //TODO: float kilometersFromZoomLevel(float zoomLevel) {}
        // currentZoom = mMap.getCameraPosition().zoom
        // and when casting, refer to currentZoom..

    }

    /**
     * Builds the google api client, connects, and assigns it to a pointer.
     */
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

    /**
     * When we have successfully connected to the Maps API, start requesting location updates and register the user's device with our server.
     * @param bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLocationUpdates();

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        registerNewUserDevice();
    }

    /**
     * A funciton for registering new devices with the server.
     * Grab the user's location, register the device with the server, and assign this device to our local variable for device.
     */
    private void registerNewUserDevice() {
        try {
            userDevice = APIUserConnect.RegisterDevice(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), this);
            Toast.makeText(this, "Connection Established", Toast.LENGTH_LONG).show();
        } catch(NullPointerException e){
            e.printStackTrace();
            Toast.makeText(this, "Connection Failed in registerNewUserDevice", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * When the app is moved to the background and we lose API Connectivity, continue to update the user's location.
     * @param i
     */
    @Override
    public void onConnectionSuspended(int i) {
        //TODO update device location
    }

    /**
     * When the connection fails, tell the user.
     * @param connectionResult    A result of the attempted connection with the server.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failed, try again", Toast.LENGTH_LONG).show();
    }

    /**
     * Update the user's location when it changes Device-side.
     * @param location
     */
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

    /**
     * moves the map to the user's current location automatically.
     * @param mLastLocation The last known location of the user.
     */
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

    /**
     * Update the device's location with our server.
     * @param mLastLocation the last known location of the user, device-side.
     */
    private void updateDeviceLocation(Location mLastLocation) {
        userDevice.setLatitude((float) mLastLocation.getLatitude());
        userDevice.setLongitude((float) mLastLocation.getLongitude());
        userDevice.setRadius(DEFAULT_USER_RADIUS);
        APIUserConnect.UpdateDevice(userDevice, this);
    }

    /**
     * Set up the frequency of location requests and the priority of these requests
     */
    private void requestLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(DEFAULT_LOCATION_REQUEST_INTERVAL);
        mLocationRequest.setFastestInterval(DEFAULT_LOCATION_REQUEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    }

    /**
     * Begin an audio file upload to the Firebase server.
     * @param audioFile The file that we're uploading to our server.
     */
    private void beginUpload(final File audioFile) {
        mTransferUtility.upload(Constants.BUCKET_NAME, audioFile.getName(), audioFile)
        .setTransferListener(new TransferListener()
        {
            @Override
            public void onStateChanged(int id, TransferState newState)
            {
                //Enum status = newState.valueOf("Completed");
                switch (newState) {
                    case COMPLETED:
                        Toast.makeText(mActivity, "Upload to S3 completed!", Toast.LENGTH_SHORT).show();
                        APIUserConnect.echo(userDevice, audioFile.getName(), getApplicationContext());
                        APIUserConnect.createSoul(userDevice, audioFile.getName(), getApplicationContext());

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

    /**
     * Set up the firebase connection.
     */
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

    /**
     * Plays the message attached to the Android notification sent to the user.
     * This is a temporary function that will only exist until we have a proper message queue for our users.
     * @param s3Key The s3Key of the sent message, to be used to query the server for the audio file.
     */
    private void playNotificationMessage(String s3Key){
        //If the s3Key doesn't exist, we've accessed this function improperly, somehow.  Exit.
        if(s3Key == null) {
            Log.v("s3KeyNull","s3Key is null");
            return;
        }
        //Grab the audio file that we were sent, identified by the s3Key.
        receiveNotificationAudioFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), s3Key);
        TransferObserver observer = mTransferUtility.download(Constants.BUCKET_NAME, receiveNotificationAudioFile.getName(), receiveNotificationAudioFile);
        //create our transfer listener for this audio message.
        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState newState) {
                switch (newState) {
                    case COMPLETED:
                        Toast.makeText(mActivity, "Download to S3 completed!", Toast.LENGTH_SHORT).show();
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

    /**
     * Create a fileinputstream for the downloaded file
     * @param file
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private FileInputStream openFile(File file) throws FileNotFoundException, IOException {
        FileInputStream fos = new FileInputStream(file);
        // remember th 'fos' reference somewhere for later closing it
        return fos;
    }

    /**
     * An interface reference to the button fragment - if the button is pressed, start recording the message in the audiopipeline.
     */
    @Override
    public void onButtonPressed() {
        mAudioPipeline.startRecording();
    }

    /**
     * An interface reference to the button fragment - if the button is released, tsop recording the message in the audio pipeline.
     */
    @Override
    public void onButtonReleased() {
        if (mAudioPipeline.mHasAudioRecordingBeenStarted) {
            mAudioPipeline.stopRecording();
        }
    }
}