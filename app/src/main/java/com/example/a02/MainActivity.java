package com.example.a02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

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
import java.util.BitSet;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private static String IP_ADDRESS = "192.168.0.3";   //내부 IP주소
    private static String TAG = "php";
    private BackPressCloseHandler backPressCloseHandler;
    private Button login_button;
    private EditText login_input_id, login_input_pw;
    private String JsonString,JsonSaveString;
    private CheckBox login_SaveID_check;
    private GpsTracker gpsTracker;
    private boolean gpscheck = false,Save_ID_Check;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, // 앱 실행 퍼미션
            Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
            gpscheck=true;
        }else {
            checkRunTimePermission();
        }
        Context context = this;
        backPressCloseHandler = new BackPressCloseHandler(this);
        Save_ID_Check = PreferenceManager.getBoolean(context,"check");
        Button login_regButton = (Button) findViewById(R.id.login_reg_button);
        login_input_id = (EditText) findViewById(R.id.login_id);
        login_input_pw = (EditText) findViewById(R.id.login_pw);
        login_SaveID_check = (CheckBox) findViewById(R.id.login_SaveID_Check);
        if(Save_ID_Check){
            login_input_id.setText(PreferenceManager.getString(context,"ID"));
            login_SaveID_check.setChecked(true);
        }

        login_regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg_intent = new Intent(getApplicationContext(), regActivity.class);
                startActivity(reg_intent);
            }
        });
        Button login_foundButton = (Button) findViewById(R.id.login_found_button);
        login_foundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent found_intent = new Intent(getApplicationContext(), foundActivity.class);
                startActivity(found_intent);
            }
        });
        login_button = (Button) findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = login_input_id.getText().toString();
                String userPW = login_input_pw.getText().toString();
                if(userID.equals("")){
                    Toast.makeText(context, "아이디를 입력하세요.", Toast.LENGTH_SHORT).show();
                }else{
                    if(userPW.equals("")){
                        Toast.makeText(context, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                    }else{
                        if(login_SaveID_check.isChecked()){
                            PreferenceManager.setBoolean(context,"check",login_SaveID_check.isChecked());
                            PreferenceManager.setString(context,"ID",userID);
                        }else{
                            PreferenceManager.setBoolean(context,"check",login_SaveID_check.isChecked());
                        }
                        login task = new login();
                        task.execute("http://" + IP_ADDRESS + "/login.php", userID, userPW);
                        login_input_pw.setText("");
                    }
                }

            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {
        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            boolean check_result = true;
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }
            if (check_result) {

            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {
                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    void checkRunTimePermission(){
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {

                Toast.makeText(MainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }
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
            return "주소 미발견";
        }
        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        checkRunTimePermission();
                        return;
                    }
                }
                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    private class login extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            if (result.equals("fail")) {
                Toast.makeText(MainActivity.this, "아이디와 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            }else if(result.equals("check")){
                Toast.makeText(MainActivity.this, "비밀번호나 ID를 입력해주세요.", Toast.LENGTH_SHORT).show();
            }else {
                JsonString = result;
                showResult();

            }
            Log.d(TAG, "response - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword1 = (String) params[1];
            String searchKeyword2 = (String) params[2];
            String serverURL = (String) params[0];
            String postParameters = "userID=" + searchKeyword1 + "&userPW=" + searchKeyword2;


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
                Log.d(TAG, "response code - " + responseStatusCode);

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

            String TAG_JSON="user";
            String TAG_ID = "userID";
            String TAG_NO = "userNo";
            String TAG_PW = "userPW";
            String TAG_Sex = "userSex";
            String TAG_Birth = "userBirth";
            String TAG_Point = "userPoint";
            String TAG_Name = "userName";
            try {
                JSONObject jsonObject = new JSONObject(JsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                    JSONObject item = jsonArray.getJSONObject(0);
                    String userID = item.getString(TAG_ID);
                    String userNo = item.getString(TAG_NO);
                    String userPW = item.getString(TAG_PW);
                    String userName = item.getString(TAG_Name);
                    String userBirth = item.getString(TAG_Birth);
                    String userPoint = item.getString(TAG_Point);
                    String userSex = item.getString(TAG_Sex);
                    Log.d(TAG,userID);
                    Intent intent = new Intent(getApplicationContext(),main_layout_act.class);
                    intent.putExtra("ID",userID);
                    intent.putExtra("PW",userPW);
                    intent.putExtra("Name",userName);
                    intent.putExtra("Birth",userBirth);
                    intent.putExtra("Sex",userSex);
                    intent.putExtra("No",userNo);
                    intent.putExtra("Point",userPoint);
                    finish();
                    startActivity(intent);
            } catch (JSONException e) {
                Log.d(TAG, "showResult : ", e);
            }

        }}
    }

