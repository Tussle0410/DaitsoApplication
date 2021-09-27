package com.example.a02;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.a02.ui.dashboard.DashboardFragment;
import com.example.a02.ui.home.HomeFragment;
import com.example.a02.ui.notifications.NotificationsFragment;
import com.example.a02.ui.top.TopFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class main_layout_act extends AppCompatActivity {
    private GpsTracker gpsTracker;
    DashboardFragment dashboardFragment;
    HomeFragment homeFragment;
    NotificationsFragment notificationsFragment;
    TopFragment topFragment;
    BottomNavigationView bottomNavigationView;
    private String userID, userNo, userPW, userName, userBirth, userPoint, userSex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        gpsTracker = new GpsTracker(main_layout_act.this);
        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();
        String address = getCurrentAddress(latitude, longitude);
        String[] region = address.split(" ");

        dashboardFragment = new DashboardFragment();
        homeFragment = new HomeFragment();
        notificationsFragment = new NotificationsFragment();
        topFragment = new TopFragment();
        bottomNavigationView = findViewById(R.id.nav_view);
        Bundle extras = getIntent().getExtras();
        userID = extras.getString("ID");
        userNo = extras.getString("No");
        userPW= extras.getString("PW");
        userName = extras.getString("Name");
        userBirth = extras.getString("Birth");
        userPoint = extras.getString("Point");
        userSex = extras.getString("Sex");

        Bundle profile = new Bundle();
        profile.putString("userID",userID);
        profile.putString("userNo",userNo);
        profile.putString("userPW",userPW);
        profile.putString("userName",userName);
        profile.putString("userBirth",userBirth);
        profile.putString("userPoint",userPoint);
        profile.putString("userSex",userSex);
        notificationsFragment.setArguments(profile);

        Bundle home = new Bundle();
        home.putString("address",address);
        home.putString("latitude",Double.toString(latitude));
        home.putString("longitude",Double.toString(longitude));
        home.putString("userID",userID);
        homeFragment.setArguments(home);

        Bundle Dashboard = new Bundle();
        Dashboard.putString("region",region[2]);
        Dashboard.putString("userID",userID);
        dashboardFragment.setArguments(Dashboard);

        Bundle Top = new Bundle();
        Top.putString("userID",userID);
        topFragment.setArguments(Top);

        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,homeFragment).commitAllowingStateLoss();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.navigation_home:{
                        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,homeFragment).commitAllowingStateLoss();
                        return true;}
                    case R.id.navigation_top:{
                        getSupportFragmentManager().beginTransaction() .replace(R.id.nav_host_fragment,dashboardFragment).commitAllowingStateLoss();
                        return true; }
                    case R.id.navigation_profile:{
                        getSupportFragmentManager().beginTransaction() .replace(R.id.nav_host_fragment,notificationsFragment).commitAllowingStateLoss();
                        return true; }
                    case R.id.navigation_setting:{
                        getSupportFragmentManager().beginTransaction() .replace(R.id.nav_host_fragment,topFragment).commitAllowingStateLoss();
                        return true; }
                    default:
                        return false;
                }
            }
        });
    }
    public String getCurrentAddress( double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }
        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        }
        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";
    }
}


