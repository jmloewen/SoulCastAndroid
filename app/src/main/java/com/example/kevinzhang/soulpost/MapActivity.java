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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
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

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Permission;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
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

import static com.amazonaws.mobileconnectors.s3.transferutility.TransferState.COMPLETED;
import static java.lang.Integer.parseInt;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

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

    //Variables for Audio Up / Audio Down.

    private Button mRecordButton;
    private AudioPipeline mPipeLine = new AudioPipeline();

    //The user's device
    private Device userDevice = null;

    private TransferUtility mTransferUtility;
    private Activity mActivity = this;
    private boolean mIsConnected = false;
    private ConnectivityManager gCm;

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://soulcast.ml")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        gCm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        mIsConnected = gCm.getActiveNetworkInfo() != null && gCm.getActiveNetworkInfo().isConnected();


        prefs = getSharedPreferences(SOULPREFS, Context.MODE_PRIVATE);
        editor = prefs.edit();
        setupFirebase();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mTransferUtility = Util.getTransferUtility(this);
        /*mAudioRecorder = new AudioRecorder();
        mAudioRecorder.setmAudioRecorderListener(new AudioRecorder.AudioRecorderListener() {
            @Override
            public void onRecordingFinished(File audioFile) {
                //TODO Upload to S3
                beginUpload(audioFile);
            }
        });*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            {
                checkLocationPermission();
           }

        }

        mRecordButton = (Button) findViewById(R.id.record_button);
        mRecordButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        // User pressed down on the button - if we're at an API that needs to check for perms, do so.
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            {
                                if ((ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.RECORD_AUDIO)
                                        == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(MapActivity.this,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                                    Log.d("TSTRCRD", "PRESTRTREC");
                                   //instantiate mPipeLine since all permissions granted
                                     mPipeLine.startRecording();
                                    Log.d("TSTRCRD", "POSTSTRTREC");
                                }
                            }
                        } else {
                            Log.d("TSTRCRD", "Test Start Record");
                          //  mPipeLine.startRecording();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        // User released the button
                        if ((ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.RECORD_AUDIO)
                                == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(MapActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                              mPipeLine.stopRecording();
                            Log.d("TSTRCRD", "Test Stop Record");
                        }
                        break;
                }
                return false;
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
        AppIndex.AppIndexApi.end(mClient, getIndexApiAction());
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient.disconnect();
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //user is on SDK > 23
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleAPIClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleAPIClient();
            mMap.setMyLocationEnabled(true);
        }
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

        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

        //if we can connect to the internet, do so & register.
        mIsConnected = gCm.getActiveNetworkInfo() != null && gCm.getActiveNetworkInfo().isConnected();

        if (mIsConnected && userDevice == null){
            userDevice = APIUserConnect.RegisterDevice(latLng, this);
            mRecordButton.setEnabled(true);
        }
        else if (userDevice == null){
            //tell the user they dont have internet available.
            Toast.makeText(getApplicationContext(), "Please enable Internet connectivity", Toast.LENGTH_LONG);
            mRecordButton.setEnabled(false);
        }
        else if (!mIsConnected && userDevice != null){
            //userdevice is not null and user is not connected.
            //Tell the user to connect to the internet.
        }
        else{
            //userdevice is not null and the user is connected to the internet - everything is good.
            mRecordButton.setEnabled(true);
        }


        //Toast.makeText(this, "CNXN Established", Toast.LENGTH_LONG).show();
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
        //Toast.makeText(getApplicationContext(), "OLC Called", Toast.LENGTH_SHORT).show();
        //this is done because we want to store the location for other purposes.
        mLastLocation = location;
        Log.d("oLC", "Location Changed");
        if (userDevice == null) {
            mIsConnected = gCm.getActiveNetworkInfo() != null && gCm.getActiveNetworkInfo().isConnected();
            //we should never arrive here - here only for debugging purposes right now.
            if (mIsConnected){
                LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                userDevice = APIUserConnect.RegisterDevice(latLng, this);
                mRecordButton.setEnabled(true);
            }else {
                Toast.makeText(getApplicationContext(), "Please enable Internet connectivity", Toast.LENGTH_LONG);
                mRecordButton.setEnabled(false);
            }
        } else {
            //update the device location
            userDevice.setLatitude((float)mLastLocation.getLatitude());
            userDevice.setLongitude((float)mLastLocation.getLongitude());
            userDevice.setRadius(DEFAULT_USER_RADIUS);

            //update the device record on the server
            APIUserConnect.UpdateDevice(userDevice, this);

            //move the map appropriately.
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        }
    }

    private void requestLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //Do we ever need to check this?  Is there a point where we need the location manager?
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

    private void checkLocationPermission() {
        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void checkPermission(String permissionType, int requestCode){
        if (ContextCompat.checkSelfPermission(this, permissionType) != PackageManager.PERMISSION_GRANTED){
            requestPermission(permissionType, requestCode);
        }
    }
    private void requestPermission(String permissionType, int requestCode){
        ActivityCompat.requestPermissions(this, new String[]{permissionType}, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
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
    }

    /**
     * This sets up the connection between the user and our server.
     */
    private void setupFirebase() {
        FirebaseRemoteConfig firebaseRemoteConfig;
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
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
        firebaseRemoteConfig.setConfigSettings(firebaseRemoteConfigSettings);
        firebaseRemoteConfig.setDefaults(defaultConfigMap);
    }

    void uploadSoulToServer(String fileName) {
        Toast.makeText(MapActivity.this, "In uploadToServer. S3Key is: " + fileName, Toast.LENGTH_SHORT).show();
        SoulpostAPI myAPI = retrofit.create(SoulpostAPI.class);

        try
        {
            Log.d("USTS", System.currentTimeMillis() + "");
            Toast.makeText(getApplicationContext(), System.currentTimeMillis() + "", Toast.LENGTH_LONG);
        }
        catch(Exception e)
        {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
        }


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
        mClient.connect();
        AppIndex.AppIndexApi.start(mClient, getIndexApiAction());

    }
}