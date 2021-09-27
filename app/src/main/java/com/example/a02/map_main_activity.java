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

    private static String IP_ADDRESS = "";   //내부 IP주소
    private String JsonNo,JsonString,StoreViews="0",StoreRating="0.0",latitude,longitude,userID;
    private String check = "off";
    private String StoreNo="0";
    private int StoreResult;
    private GoogleMap mMap; //구글맵 변수 선언
    private Marker currentMarker = null;
    private static final String TAG = "googlemap";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 50000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 5000; // 0.5초
    private TextView map_width;

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;


    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, // 앱 실행 퍼미션
            Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소


    Location mCurrentLocatiion;
    LatLng currentPosition;

    // private FusedLocationProviderClient fusedLocationClient;  // 위치 서비스 클라이언트 인스턴스 (아직 getLastLocation() 추가 못함 나중에 할거임
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;
    private String category;


    private View mLayout; // Snackbar -> view  , Toast -> context 로 쓴다

    List<Marker> previous_marker = null; // PlacesListener 인터페이스 구현 시 필요한 변수


    Spinner spinner;
    SeekBar seekbar;
    private int meter=1000;
    private String meter_string="1000m";
    private int result_meter=1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // 액티비티꺼 불러오기
        setContentView(R.layout.map_main);

        // ArrayList 초기화와 버튼 클릭시 showPlaceInformation() 호출
        map_width = (TextView) findViewById(R.id.map_width);
        map_width.setText(meter_string);
        previous_marker = new ArrayList<Marker>();
        category = getIntent().getStringExtra("category");
        latitude = getIntent().getStringExtra("latitude");
        longitude = getIntent().getStringExtra("longitude");
        userID = getIntent().getStringExtra("userID");
        Log.d("카테고리",category);

        // 레이아웃 버튼 선언
        Button foundbutton = (Button)findViewById(R.id.map_found_button);
        Button width_button = (Button)findViewById(R.id.width_button);
        width_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result_meter = meter;
                Toast.makeText(getApplicationContext(),"반경이 변경되었습니다.",Toast.LENGTH_SHORT).show();
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
        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);  // 위치 서비스 클라이언트 인스턴스2222


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this); // getMapAsync 메소드 호출해서 구글맵 준비될 때 실행될 콜백 등록
    }

    //맵 사용 준비 완료시 호출됨 (초기 세팅임)
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");

        mMap = googleMap; // 공통변수를 mMap으로 선언해서 받아씀

        // 초기위치 지정했을 때 쓰는 메소드 (서울로 지정해놓음)
        setDefaultLocation();


        // 위치 접근 권한 갖고있는지 검사
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        //granted가 권한 허용이고 denied가 권한 거부상태다
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {


            startLocationUpdates(); // 위치 계속 업데이트


        }else { // 요청 허용한 적 없으면

            // 사용자가 퍼미션 거부 한 적 있으면
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                // 다음 메시지 출력
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        // 사용자에게 퍼미션 요청 (요청 결과는 onRequestPermissionResult에 수신)
                        ActivityCompat.requestPermissions( map_main_activity.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();


            } else {
                // 사용자가 퍼미션 거부 한 적 없으면 퍼미션 요청 바로 한다
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


        // intent씀 , 마커 눌렀을 때 정보창 뜨게 하기  (private GoogleMap mMap) 최근에 추가한 거
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
        // GpsTracker에서 받은 위도 경도 결과를 저장
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);

                currentPosition
                        = new LatLng(location.getLatitude(), location.getLongitude());


                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                        + " 경도:" + String.valueOf(location.getLongitude());

                Log.d(TAG, "onLocationResult : " + markerSnippet);


                //setCurrentLocation(location, markerTitle, markerSnippet);

                mCurrentLocatiion = location;
            }


        }

    };



    // 위치 정보 업데이트
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

                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
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




    // 좌표를 주소로 변환 (지오코딩)
    public String getCurrentAddress(LatLng latlng) {

        //gps를 주소로 변환해주는 거
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            // getFromLocation -> 좌표를 주소나 지명으로 변환하는 방법 . 반대는 getFromLocationName
            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }


    // 위치 접근 권한 상태 확인
    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    /* 위치 접근 권한 o, 지오코딩 서비스 o, 그 다음 수행
    화면 상에 마커 나타내고, 카메라 이동시키는 메소드
    */

    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {


        if (currentMarker != null) currentMarker.remove();


        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng); // 마커 표시될 위치
        markerOptions.title(markerTitle); // 마커에 표시될 이름(별명)
        markerOptions.snippet(markerSnippet); // 마커 누르면 간단한 설명
        markerOptions.draggable(true);


        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        mMap.moveCamera(cameraUpdate); // 카메라(화면) 지정한 위도, 경도로 움직임



    }


    public void setDefaultLocation() {  // 나중에 getLastLocation() 으로 바꿔야 함.....x


        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";


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


    // 권한 처리 메소드
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



    // 권한 요청 결과 리턴받는 메소드
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        // 요청한 퍼미션 개수만큼 수신됐으면
        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {


            boolean check_result = true;


            // 모든 퍼미션 허용했는지 체크
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if (check_result) {

                // 퍼미션 허용했으면 위치 업데이트 시작
                startLocationUpdates();
            } else {

                // 거부한 퍼미션이 있으면 이유를 설명해주고 앱 종료
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {


                    // 사용자가 거부만 선택한 경우 앱을 다시 실행해서 허용을 선택하면 앱 사용 가능
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();

                } else {

                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();
                }
            }

        }
    }


    // GPS 활성화 메소드
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(map_main_activity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
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

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음");


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
                //중복 마커 제거
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
        mMap.clear(); //지도 클리어 시킴

        if(previous_marker != null)
            previous_marker.clear(); // 지도상에 전에 실행한 마커가 있으면 지우고 시작한다
        if(category.equals("RESTAURANT")) {
            new NRPlaces.Builder()
                    .listener(map_main_activity.this)
                    .key("") // 여기는 기존 for android api키가 아니라 다른거 입력
                    .latlng(location.latitude, location.longitude) // 이게 현재위치 위도와 경도
                    .radius(result_meter) // 내 주변 반경 00미터 검색
                    .type(PlaceType.RESTAURANT) // 마음대로 바꾸기 가능
                    .language("ko", "KR") // 한국어로 나타내기
                    .build()
                    .execute(); // 실행
        }else if(category.equals("CAFE")) {
            new NRPlaces.Builder()
                    .listener(map_main_activity.this)
                    .key("") // 여기는 기존 for android api키가 아니라 다른거 입력
                    .latlng(location.latitude, location.longitude) // 이게 현재위치 위도와 경도
                    .radius(result_meter) // 내 주변 반경 00미터 검색
                    .type(PlaceType.CAFE) // 마음대로 바꾸기 가능
                    .language("ko", "KR") // 한국어로 나타내기
                    .build()
                    .execute(); // 실행
        }
        else if(category.equals("CHURCH")) {
            new NRPlaces.Builder()
                    .listener(map_main_activity.this)
                    .key("") // 여기는 기존 for android api키가 아니라 다른거 입력
                    .latlng(location.latitude, location.longitude) // 이게 현재위치 위도와 경도
                    .radius(result_meter) // 내 주변 반경 00미터 검색
                    .type(PlaceType.CHURCH) // 마음대로 바꾸기 가능
                    .language("ko", "KR") // 한국어로 나타내기
                    .build()
                    .execute(); // 실행
        }
        else if(category.equals("SUPERMARKET")) {
            new NRPlaces.Builder()
                    .listener(map_main_activity.this)
                    .key("") // 여기는 기존 for android api키가 아니라 다른거 입력
                    .latlng(location.latitude, location.longitude) // 이게 현재위치 위도와 경도
                    .radius(result_meter) // 내 주변 반경 00미터 검색
                    .type(PlaceType.STORE) // 마음대로 바꾸기 가능
                    .language("ko", "KR") // 한국어로 나타내기
                    .build()
                    .execute(); // 실행
        }
        else if(category.equals("BAKERY")) {
            new NRPlaces.Builder()
                    .listener(map_main_activity.this)
                    .key("") // 여기는 기존 for android api키가 아니라 다른거 입력
                    .latlng(location.latitude, location.longitude) // 이게 현재위치 위도와 경도
                    .radius(result_meter) // 내 주변 반경 00미터 검색
                    .type(PlaceType.BAKERY) // 마음대로 바꾸기 가능
                    .language("ko", "KR") // 한국어로 나타내기
                    .build()
                    .execute(); // 실행
        } else if(category.equals("CONVENIENCE_STORE")) {
            new NRPlaces.Builder()
                    .listener(map_main_activity.this)
                    .key("") // 여기는 기존 for android api키가 아니라 다른거 입력
                    .latlng(location.latitude, location.longitude) // 이게 현재위치 위도와 경도
                    .radius(result_meter) // 내 주변 반경 00미터 검색
                    .type(PlaceType.CONVENIENCE_STORE) // 마음대로 바꾸기 가능
                    .language("ko", "KR") // 한국어로 나타내기
                    .build()
                    .execute(); // 실행
        } else if(category.equals("HOSPITAL")) {
            new NRPlaces.Builder()
                    .listener(map_main_activity.this)
                    .key("") // 여기는 기존 for android api키가 아니라 다른거 입력
                    .latlng(location.latitude, location.longitude) // 이게 현재위치 위도와 경도
                    .radius(result_meter) // 내 주변 반경 00미터 검색
                    .type(PlaceType.HOSPITAL) // 마음대로 바꾸기 가능
                    .language("ko", "KR") // 한국어로 나타내기
                    .build()
                    .execute(); // 실행
        } else if(category.equals("ATM")){
            new NRPlaces.Builder()
                    .listener(map_main_activity.this)
                    .key("") // 여기는 기존 for android api키가 아니라 다른거 입력
                    .latlng(location.latitude, location.longitude) // 이게 현재위치 위도와 경도
                    .radius(result_meter) // 내 주변 반경 00미터 검색
                    .type(PlaceType.ATM) // 마음대로 바꾸기 가능
                    .language("ko", "KR") // 한국어로 나타내기
                    .build()
                    .execute(); // 실행
        }else if(category.equals("BOOK_STORE")){
            new NRPlaces.Builder()
                    .listener(map_main_activity.this)
                    .key("") // 여기는 기존 for android api키가 아니라 다른거 입력
                    .latlng(location.latitude, location.longitude) // 이게 현재위치 위도와 경도
                    .radius(result_meter) // 내 주변 반경 00미터 검색
                    .type(PlaceType.BOOK_STORE) // 마음대로 바꾸기 가능
                    .language("ko", "KR") // 한국어로 나타내기
                    .build()
                    .execute(); // 실행
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
