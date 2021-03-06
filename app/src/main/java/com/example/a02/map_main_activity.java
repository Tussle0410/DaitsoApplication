package com.example.a02;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;



public class map_main_activity extends AppCompatActivity
        implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        PlacesListener {

    private static String IP_ADDRESS = "192.168.0.3";   //?????? IP??????
    private String JsonNo,JsonString,StoreViews="0",StoreRating="0.0",latitude,longitude,userID;
    private String check = "off";
    private String StoreNo="0";
    private int StoreResult;
    private GoogleMap mMap; //????????? ?????? ??????
    private Marker currentMarker = null;
    private static final String TAG = "googlemap";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 50000;  // 1???
    private static final int FASTEST_UPDATE_INTERVAL_MS = 5000; // 0.5???
    private TextView map_width;

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;


    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, // ??? ?????? ?????????
            Manifest.permission.ACCESS_COARSE_LOCATION};  // ?????? ?????????


    Location mCurrentLocatiion;
    LatLng currentPosition;

    // private FusedLocationProviderClient fusedLocationClient;  // ?????? ????????? ??????????????? ???????????? (?????? getLastLocation() ?????? ?????? ????????? ?????????
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;
    private String category;


    private View mLayout; // Snackbar -> view  , Toast -> context ??? ??????

    List<Marker> previous_marker = null; // PlacesListener ??????????????? ?????? ??? ????????? ??????


    Spinner spinner;
    SeekBar seekbar;
    private int meter=1000;
    private String meter_string="1000m";
    private int result_meter=1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // ??????????????? ????????????
        setContentView(R.layout.map_main);

        // ArrayList ???????????? ?????? ????????? showPlaceInformation() ??????
        map_width = (TextView) findViewById(R.id.map_width);
        map_width.setText(meter_string);
        previous_marker = new ArrayList<Marker>();
        category = getIntent().getStringExtra("category");
        latitude = getIntent().getStringExtra("latitude");
        longitude = getIntent().getStringExtra("longitude");
        userID = getIntent().getStringExtra("userID");
        Log.d("????????????",category);

        // ???????????? ?????? ??????
        Button foundbutton = (Button)findViewById(R.id.map_found_button);
        Button width_button = (Button)findViewById(R.id.width_button);
        width_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result_meter = meter;
                Toast.makeText(getApplicationContext(),"????????? ?????????????????????.",Toast.LENGTH_SHORT).show();
            }
        });
        foundbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showPlaceInformation(currentPosition);
            }
        });
        SeekBar progress = (SeekBar) findViewById(R.id.map_seekBar);
        progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(final SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
                meter = seekBar.getProgress();
                meter_string = Integer.toString(meter) + "m";
                map_width.setText(meter_string);
            }
        });


        mLayout = findViewById(R.id.map_layout_main);

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);


        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);  // ?????? ????????? ??????????????? ????????????2222


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this); // getMapAsync ????????? ???????????? ????????? ????????? ??? ????????? ?????? ??????
    }

    //??? ?????? ?????? ????????? ????????? (?????? ?????????)
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");
        mMap = googleMap; // ??????????????? mMap?????? ???????????? ?????????
        // ???????????? ???????????? ??? ?????? ????????? (????????? ???????????????)
        setDefaultLocation();
        // ?????? ?????? ?????? ??????????????? ??????
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        //granted??? ?????? ???????????? denied??? ?????? ???????????????
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {


            startLocationUpdates(); // ?????? ?????? ????????????


        }else { // ?????? ????????? ??? ?????????

            // ???????????? ????????? ?????? ??? ??? ?????????
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                // ?????? ????????? ??????
                Snackbar.make(mLayout, "??? ?????? ??????????????? ?????? ?????? ????????? ???????????????.",
                        Snackbar.LENGTH_INDEFINITE).setAction("??????", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        // ??????????????? ????????? ?????? (?????? ????????? onRequestPermissionResult??? ??????)
                        ActivityCompat.requestPermissions( map_main_activity.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();


            } else {
                // ???????????? ????????? ?????? ??? ??? ????????? ????????? ?????? ?????? ??????
                ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }


        // startLocationUpdates
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                Log.d( TAG, "onMapClick :");
            }
        });


        // intent??? , ?????? ????????? ??? ????????? ?????? ??????  (private GoogleMap mMap) ????????? ????????? ???
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener(){
            @Override
            public void onInfoWindowClick(Marker marker){

                String title = marker.getTitle();
                String address = marker.getSnippet();
                String[] region = address.split(" ");
                CheckStore checkStore = new CheckStore();
                checkStore.execute("http://" + IP_ADDRESS + "/CheckStore.php",title);
                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(check.equals("on")){
                        NewStoreNo newStoreNo = new NewStoreNo();
                        newStoreNo.execute("http://" + IP_ADDRESS + "/newStoreNo.php");
                        } }
                },200);
                Handler handler2 = new Handler();
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(check.equals("on")){
                        StoreResult = Integer.parseInt(StoreNo) + 1;
                        StoreNo = String.valueOf(StoreResult);}
                        StoreNo = StoreNo + "c";
                        StoreResult = Integer.parseInt(StoreViews) + 1;
                        StoreViews = String.valueOf(StoreResult);
                        Log.d("test : ",region[2]);
                        Intent intent = new Intent(getApplicationContext(),comActivity.class);
                        intent.putExtra("title",title);
                        intent.putExtra("address",address);
                        intent.putExtra("category",category);
                        intent.putExtra("check",check);
                        intent.putExtra("storeNo",StoreNo);
                        intent.putExtra("storeViews",StoreViews);
                        intent.putExtra("storeRating",StoreRating);
                        intent.putExtra("region",region[2]);
                        intent.putExtra("userID",userID);
                        startActivity(intent);
                        StoreViews="0";
                        check="off";
                        StoreNo="0";
                    }
                },400);

            }
        });
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        // GpsTracker?????? ?????? ?????? ?????? ????????? ??????
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);

                currentPosition
                        = new LatLng(location.getLatitude(), location.getLongitude());


                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "??????:" + String.valueOf(location.getLatitude())
                        + " ??????:" + String.valueOf(location.getLongitude());

                Log.d(TAG, "onLocationResult : " + markerSnippet);


                //setCurrentLocation(location, markerTitle, markerSnippet);

                mCurrentLocatiion = location;
            }


        }

    };



    // ?????? ?????? ????????????
    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);



            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED   ) {

                Log.d(TAG, "startLocationUpdates : ????????? ???????????? ??????");
                return;
            }


            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission())
                mMap.setMyLocationEnabled(true);

        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        if (checkPermission()) {

            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (mMap!=null)
                mMap.setMyLocationEnabled(true);

        }


    }


    @Override
    protected void onStop() {

        super.onStop();

        if (mFusedLocationClient != null) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }




    // ????????? ????????? ?????? (????????????)
    public String getCurrentAddress(LatLng latlng) {

        //gps??? ????????? ??????????????? ???
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            // getFromLocation -> ????????? ????????? ???????????? ???????????? ?????? . ????????? getFromLocationName
            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //???????????? ??????
            Toast.makeText(this, "???????????? ????????? ????????????", Toast.LENGTH_LONG).show();
            return "???????????? ????????? ????????????";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "????????? GPS ??????", Toast.LENGTH_LONG).show();
            return "????????? GPS ??????";

        }


        if (addresses == null || addresses.size() == 0) {
            return "?????? ?????????";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }


    // ?????? ?????? ?????? ?????? ??????
    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    /* ?????? ?????? ?????? o, ???????????? ????????? o, ??? ?????? ??????
    ?????? ?????? ?????? ????????????, ????????? ??????????????? ?????????
    */

    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {


        if (currentMarker != null) currentMarker.remove();


        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng); // ?????? ????????? ??????
        markerOptions.title(markerTitle); // ????????? ????????? ??????(??????)
        markerOptions.snippet(markerSnippet); // ?????? ????????? ????????? ??????
        markerOptions.draggable(true);


        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        mMap.moveCamera(cameraUpdate); // ?????????(??????) ????????? ??????, ????????? ?????????



    }


    public void setDefaultLocation() {  // ????????? getLastLocation() ?????? ????????? ???.....x


        //????????? ??????, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
        String markerTitle = "???????????? ????????? ??? ??????";
        String markerSnippet = "?????? ???????????? GPS ?????? ?????? ???????????????";


        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mMap.moveCamera(cameraUpdate);

    }


    // ?????? ?????? ?????????
    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            return true;
        }

        return false;

    }



    // ?????? ?????? ?????? ???????????? ?????????
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        // ????????? ????????? ???????????? ???????????????
        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {


            boolean check_result = true;


            // ?????? ????????? ??????????????? ??????
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if (check_result) {

                // ????????? ??????????????? ?????? ???????????? ??????
                startLocationUpdates();
            } else {

                // ????????? ???????????? ????????? ????????? ??????????????? ??? ??????
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {


                    // ???????????? ????????? ????????? ?????? ?????? ?????? ???????????? ????????? ???????????? ??? ?????? ??????
                    Snackbar.make(mLayout, "???????????? ?????????????????????. ?????? ?????? ???????????? ???????????? ??????????????????. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("??????", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();

                } else {

                    Snackbar.make(mLayout, "???????????? ?????????????????????. ??????(??? ??????)?????? ???????????? ???????????? ?????????. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("??????", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();
                }
            }

        }
    }


    // GPS ????????? ?????????
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(map_main_activity.this);
        builder.setTitle("?????? ????????? ????????????");
        builder.setMessage("?????? ???????????? ???????????? ?????? ???????????? ???????????????.\n"
                + "?????? ????????? ???????????????????");
        builder.setCancelable(true);
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //???????????? GPS ?????? ???????????? ??????
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d(TAG, "onActivityResult : GPS ????????? ?????????");


                        needRequest = true;

                        return;
                    }
                }

                break;
        }
    }
    @Override
    public void onPlacesFailure(PlacesException e) {

    }

    @Override
    public void onPlacesStart() {

    }

    @Override
    public void onPlacesSuccess(final List<Place> places) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (noman.googleplaces.Place place : places){

                    LatLng latLng
                            = new LatLng(place.getLatitude()
                            ,place.getLongitude());

                    String markerSnippet = getCurrentAddress(latLng);

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(place.getName());
                    markerOptions.snippet(markerSnippet);
                    Marker item = mMap.addMarker(markerOptions);
                    previous_marker.add(item);
                }
                //?????? ?????? ??????
                HashSet<Marker> hashSet = new HashSet<Marker>();
                hashSet.addAll(previous_marker);
                previous_marker.clear();
                previous_marker.addAll(hashSet);

            }
        });
    }

    @Override
    public void onPlacesFinished() {

    }


    public void showPlaceInformation(LatLng location)
    {
        mMap.clear(); //?????? ????????? ??????

        if(previous_marker != null)
            previous_marker.clear(); // ???????????? ?????? ????????? ????????? ????????? ????????? ????????????
        if(category.equals("RESTAURANT")) {
            new NRPlaces.Builder()
                    .listener(map_main_activity.this)
                    .key("AIzaSyAUl_zkM5ppfb0oKvw0Ls-cckf9ck9wLvc") // ????????? ?????? for android api?????? ????????? ????????? ??????
                    .latlng(location.latitude, location.longitude) // ?????? ???????????? ????????? ??????
                    .radius(result_meter) // ??? ?????? ?????? 00?????? ??????
                    .type(PlaceType.RESTAURANT) // ???????????? ????????? ??????
                    .language("ko", "KR") // ???????????? ????????????
                    .build()
                    .execute(); // ??????
        }else if(category.equals("CAFE")) {
            new NRPlaces.Builder()
                    .listener(map_main_activity.this)
                    .key("AIzaSyAUl_zkM5ppfb0oKvw0Ls-cckf9ck9wLvc") // ????????? ?????? for android api?????? ????????? ????????? ??????
                    .latlng(location.latitude, location.longitude) // ?????? ???????????? ????????? ??????
                    .radius(result_meter) // ??? ?????? ?????? 00?????? ??????
                    .type(PlaceType.CAFE) // ???????????? ????????? ??????
                    .language("ko", "KR") // ???????????? ????????????
                    .build()
                    .execute(); // ??????
        }
        else if(category.equals("CHURCH")) {
            new NRPlaces.Builder()
                    .listener(map_main_activity.this)
                    .key("AIzaSyAUl_zkM5ppfb0oKvw0Ls-cckf9ck9wLvc") // ????????? ?????? for android api?????? ????????? ????????? ??????
                    .latlng(location.latitude, location.longitude) // ?????? ???????????? ????????? ??????
                    .radius(result_meter) // ??? ?????? ?????? 00?????? ??????
                    .type(PlaceType.CHURCH) // ???????????? ????????? ??????
                    .language("ko", "KR") // ???????????? ????????????
                    .build()
                    .execute(); // ??????
        }
        else if(category.equals("SUPERMARKET")) {
            new NRPlaces.Builder()
                    .listener(map_main_activity.this)
                    .key("AIzaSyAUl_zkM5ppfb0oKvw0Ls-cckf9ck9wLvc") // ????????? ?????? for android api?????? ????????? ????????? ??????
                    .latlng(location.latitude, location.longitude) // ?????? ???????????? ????????? ??????
                    .radius(result_meter) // ??? ?????? ?????? 00?????? ??????
                    .type(PlaceType.STORE) // ???????????? ????????? ??????
                    .language("ko", "KR") // ???????????? ????????????
                    .build()
                    .execute(); // ??????
        }
        else if(category.equals("BAKERY")) {
            new NRPlaces.Builder()
                    .listener(map_main_activity.this)
                    .key("AIzaSyAUl_zkM5ppfb0oKvw0Ls-cckf9ck9wLvc") // ????????? ?????? for android api?????? ????????? ????????? ??????
                    .latlng(location.latitude, location.longitude) // ?????? ???????????? ????????? ??????
                    .radius(result_meter) // ??? ?????? ?????? 00?????? ??????
                    .type(PlaceType.BAKERY) // ???????????? ????????? ??????
                    .language("ko", "KR") // ???????????? ????????????
                    .build()
                    .execute(); // ??????
        } else if(category.equals("CONVENIENCE_STORE")) {
            new NRPlaces.Builder()
                    .listener(map_main_activity.this)
                    .key("AIzaSyAUl_zkM5ppfb0oKvw0Ls-cckf9ck9wLvc") // ????????? ?????? for android api?????? ????????? ????????? ??????
                    .latlng(location.latitude, location.longitude) // ?????? ???????????? ????????? ??????
                    .radius(result_meter) // ??? ?????? ?????? 00?????? ??????
                    .type(PlaceType.CONVENIENCE_STORE) // ???????????? ????????? ??????
                    .language("ko", "KR") // ???????????? ????????????
                    .build()
                    .execute(); // ??????
        } else if(category.equals("HOSPITAL")) {
            new NRPlaces.Builder()
                    .listener(map_main_activity.this)
                    .key("AIzaSyAUl_zkM5ppfb0oKvw0Ls-cckf9ck9wLvc") // ????????? ?????? for android api?????? ????????? ????????? ??????
                    .latlng(location.latitude, location.longitude) // ?????? ???????????? ????????? ??????
                    .radius(result_meter) // ??? ?????? ?????? 00?????? ??????
                    .type(PlaceType.HOSPITAL) // ???????????? ????????? ??????
                    .language("ko", "KR") // ???????????? ????????????
                    .build()
                    .execute(); // ??????
        } else if(category.equals("ATM")){
            new NRPlaces.Builder()
                    .listener(map_main_activity.this)
                    .key("AIzaSyAUl_zkM5ppfb0oKvw0Ls-cckf9ck9wLvc") // ????????? ?????? for android api?????? ????????? ????????? ??????
                    .latlng(location.latitude, location.longitude) // ?????? ???????????? ????????? ??????
                    .radius(result_meter) // ??? ?????? ?????? 00?????? ??????
                    .type(PlaceType.ATM) // ???????????? ????????? ??????
                    .language("ko", "KR") // ???????????? ????????????
                    .build()
                    .execute(); // ??????
        }else if(category.equals("BOOK_STORE")){
            new NRPlaces.Builder()
                    .listener(map_main_activity.this)
                    .key("AIzaSyAUl_zkM5ppfb0oKvw0Ls-cckf9ck9wLvc") // ????????? ?????? for android api?????? ????????? ????????? ??????
                    .latlng(location.latitude, location.longitude) // ?????? ???????????? ????????? ??????
                    .radius(result_meter) // ??? ?????? ?????? 00?????? ??????
                    .type(PlaceType.BOOK_STORE) // ???????????? ????????? ??????
                    .language("ko", "KR") // ???????????? ????????????
                    .build()
                    .execute(); // ??????
        }

        }
    private class CheckStore extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(map_main_activity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            Log.d("Check", "response - " + result);
            if(result.equals("fail")){
                check="on";
            }
            else{
                JsonString=result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = (String) params[0];
            String userID = (String) params[1];
            String postParameters = "title=" + userID;
            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d("Check", "response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }
        }
        private void showResult(){

            String TAG_JSON="checkStore";
            String TAG_Views = "storeViews";
            String TAG_Rating = "storeRating";
            String TAG_No = "storeNo";
            try {
                JSONObject jsonObject = new JSONObject(JsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
                JSONObject item = jsonArray.getJSONObject(0);
                StoreViews = item.getString(TAG_Views);
                StoreRating = item.getString(TAG_Rating);
                StoreNo = item.getString(TAG_No);
            } catch (JSONException e) {
                Log.d(TAG, "showResult : ", e);
            }}
    }
     private class NewStoreNo extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(map_main_activity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            Log.d("StoreNo", "response - " + result);
            if(result.equals("fail")){
                StoreNo="0";
            }else{
            JsonNo = result;
            showResult();}
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = (String) params[0];
            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d("StoreNo", "response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }
        }
         private void showResult(){

             String TAG_JSON="NewStoreNo";
             String TAG_No = "storeNo";
             try {
                 JSONObject jsonObject = new JSONObject(JsonNo);
                 JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
                 JSONObject item = jsonArray.getJSONObject(0);
                 StoreNo = item.getString(TAG_No);
             } catch (JSONException e) {
                 Log.d(TAG, "showResult : ", e);
             }}
    }
}
