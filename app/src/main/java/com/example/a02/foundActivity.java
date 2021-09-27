package com.example.a02;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class foundActivity extends AppCompatActivity {
    private static String IP_ADDRESS = "";   //내부 IP주소
    private static String TAG = "php";
    private Button found_id_button, found_pw_button;
    private EditText found_id_name, found_pw_id,found_pw_birth;
    private RecyclerView.LayoutManager found_id_LayoutManager,found_pw_LayoutManager;
    private ArrayList<UserData> mArrayList;
    private ID_Found_Adapter ID_Adapter;
    private PW_Found_Adapter PW_Adapter;
    private RecyclerView found_id_listView,found_pw_listView;
    private String idJsonString,pwJsonString;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.id_pw_found_page);
        found_id_name = (EditText) findViewById(R.id.found_id_name);
        found_pw_birth = (EditText) findViewById(R.id.found_pw_birth);
        found_pw_id = (EditText) findViewById(R.id.found_pw_id);
        found_id_listView = (RecyclerView) findViewById(R.id.found_id_listView);
        found_pw_listView = (RecyclerView) findViewById(R.id.found_pw_listView);
        ImageButton found_backspace_button = (ImageButton) findViewById(R.id.found_backspace_button);
        mArrayList = new ArrayList<>();
        ID_Adapter = new ID_Found_Adapter(this,mArrayList);
        PW_Adapter = new PW_Found_Adapter(this,mArrayList);
        found_id_LayoutManager = new LinearLayoutManager(this);
        found_id_listView.setLayoutManager(found_id_LayoutManager);
        found_id_listView.setAdapter(ID_Adapter);
        found_pw_LayoutManager = new LinearLayoutManager(this);
        found_pw_listView.setLayoutManager(found_pw_LayoutManager);
        found_pw_listView.setAdapter(PW_Adapter);
        found_backspace_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
    });
        found_id_button = (Button) findViewById(R.id.found_id_button);
        found_id_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = found_id_name.getText().toString();
                if(userName.equals("")){
                    Toast.makeText(foundActivity.this, "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                }else {
                    mArrayList.clear();
                    GetID task = new GetID();
                    task.execute("http://" + IP_ADDRESS + "/FindID.php", userName);
                    found_id_name.setText("");
                }

            }
        });
        found_pw_button = (Button) findViewById(R.id.found_pw_button);
        found_pw_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = found_pw_id.getText().toString();
                String userBirth = found_pw_birth.getText().toString();
                if(userID.equals("") || userBirth.equals(""))
                {
                    Toast.makeText(foundActivity.this, "ID나 생년월일을 입력하세요", Toast.LENGTH_SHORT).show();
                }else{
                mArrayList.clear();
                GetPW task = new GetPW();
                task.execute("http://" + IP_ADDRESS + "/FindPW.php",userID,userBirth);
                found_pw_birth.setText("");
                found_pw_id.setText("");
                }
            }
        });
    }
    private class GetID extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(foundActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            if (result.equals("no")) {
                Toast.makeText(foundActivity.this, "ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
            else{
                idJsonString = result;
                showResult();
            }
            Log.d(TAG, "response - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword1 = (String) params[1];
            String serverURL = (String) params[0];
            String postParameters = "userName=" + searchKeyword1;


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


            try {
                JSONObject jsonObject = new JSONObject(idJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for(int i=0;i<jsonArray.length();i++){

                    JSONObject item = jsonArray.getJSONObject(i);
                    String userID = item.getString(TAG_ID);
                    Log.d(TAG,userID);
                    UserData userData = new UserData();

                    userData.setUserID(userID);

                    mArrayList.add(userData);
                    ID_Adapter.notifyDataSetChanged();
                }



            } catch (JSONException e) {
                Log.d(TAG, "showResult : ", e);
            }

    }}
    private class GetPW extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(foundActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            if (result.equals("no")) {
                Toast.makeText(foundActivity.this, "ID와 생년월일이 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
            else{
                pwJsonString = result;
                showResult();
            }
            Log.d(TAG, "response - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword1 = (String) params[1];
            String searchKeyword2 = (String) params[2];
            String serverURL = (String) params[0];
            String postParameters = "userID=" + searchKeyword1 + "&userBirth=" + searchKeyword2;


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
            String TAG_PW = "userPW";


            try {
                JSONObject jsonObject = new JSONObject(pwJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for(int i=0;i<jsonArray.length();i++){

                    JSONObject item = jsonArray.getJSONObject(i);
                    String userPW = item.getString(TAG_PW);
                    Log.d(TAG,userPW);
                    UserData userData = new UserData();
                    userData.setUserPW(userPW);
                    mArrayList.add(userData);
                    PW_Adapter.notifyDataSetChanged();
                }



            } catch (JSONException e) {
                Log.d(TAG, "showResult : ", e);
            }

        }}}


