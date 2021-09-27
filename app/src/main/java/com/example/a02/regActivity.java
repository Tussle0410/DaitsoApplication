package com.example.a02;

import android.app.AlertDialog;
import android.app.AsyncNotedAppOp;
import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class regActivity extends AppCompatActivity {
    private static String IP_ADDRESS = "";   //내부 IP주소
    private static String TAG = "php";
    private boolean validate = false;
    private EditText reg_input_id, reg_input_pw, reg_input_pw_check, reg_input_name,
            reg_input_birth;
    private CheckBox reg_input_man, reg_input_girl,reg_input_person_check;
    private Button reg_insert_button, reg_id_check_button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_page);
        ImageButton reg_backspace_button = (ImageButton) findViewById(R.id.reg_backspace_button);
        reg_backspace_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        reg_input_id = (EditText) findViewById(R.id.reg_input_id);
        reg_input_pw = (EditText) findViewById(R.id.reg_input_pw);
        reg_input_pw_check = (EditText) findViewById(R.id.reg_input_pw_check);
        reg_input_name = (EditText) findViewById(R.id.reg_input_name);
        reg_input_birth = (EditText) findViewById(R.id.reg_input_birth);
        reg_input_man = (CheckBox) findViewById(R.id.reg_input_man);
        reg_input_girl = (CheckBox) findViewById(R.id.reg_input_girl);
        reg_insert_button = (Button) findViewById(R.id.reg_insert_button);
        reg_input_person_check = (CheckBox) findViewById(R.id.reg_input_personal_check);
        reg_insert_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(reg_input_person_check.isChecked()) {
                   if(reg_input_id.length()<1){
                       Toast.makeText(regActivity.this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                       return;
                   }
                   if(!validate){
                       Toast.makeText(regActivity.this, "아이디 중복확인을 해주세요.", Toast.LENGTH_SHORT).show();
                       return;
                   }
                    String userID = reg_input_id.getText().toString();
                    String userPW = reg_input_pw.getText().toString();
                    String pw_check = reg_input_pw_check.getText().toString();
                    if (!userPW.equals(pw_check)) {
                        Toast.makeText(regActivity.this, "비밀번호가 동일하지 않습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(reg_input_name.length()<1){
                        Toast.makeText(regActivity.this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String userName = reg_input_name.getText().toString();
                    if(reg_input_birth.length()<1){
                        Toast.makeText(regActivity.this, "생년월일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String userBirth = reg_input_birth.getText().toString();
                    String userSex = null;
                    if (reg_input_man.isChecked() && !(reg_input_girl.isChecked())) {
                        userSex = "남";
                    } else if (reg_input_girl.isChecked() && !(reg_input_man.isChecked())) {
                        userSex = "여";
                    } else {
                        Toast.makeText(regActivity.this, "성별을 선택해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    InsertData task = new InsertData();
                    task.execute("http://" + IP_ADDRESS + "/register.php", userID, userPW, userName,
                            userBirth, userSex);
                    reg_input_id.setText("");
                    reg_input_pw.setText("");
                    reg_input_pw_check.setText("");
                    reg_input_name.setText("");
                    reg_input_birth.setText("");

                    Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(regActivity.this, MainActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(regActivity.this, "개인정보 동의를 체크해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        reg_id_check_button = (Button) findViewById(R.id.reg_id_check_button);
        reg_id_check_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = reg_input_id.getText().toString();
                if (userID.equals("")) {
                    Toast.makeText(regActivity.this, "ID를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                checkData task = new checkData();
                task.execute("http://" + IP_ADDRESS + "/checkID.php", userID);
}});

    }


    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(regActivity.this, "Please Wait", null, true, true);

        }

        @Override
        protected String doInBackground(String... params) {

            String userID = (String) params[1];
            String userPW = (String) params[2];
            String userName = (String) params[3];
            String userBirth = (String) params[4];
            String userSex = (String) params[5];
            String serverURL = (String) params[0];
            String postParameters = "userID=" + userID + "&userPW=" + userPW + "&userName=" + userName
                    + "&userBirth=" + userBirth + "&userSex=" + userSex;
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
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();

            } catch (Exception e) {
                Log.d(TAG, "InsertData : Error ", e);
                return new String("Error: " + e.getMessage());
            }
        }
    }
    private class checkData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(regActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            if (result.equals("no")) {
                Toast.makeText(regActivity.this, "사용 불가능한 ID입니다.", Toast.LENGTH_SHORT).show();
            } 
            else{
                Toast.makeText(regActivity.this, "사용 가능한 ID입니다.", Toast.LENGTH_SHORT).show();
                validate=true;
            }
            Log.d(TAG, "response - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword1 = (String) params[1];
            String serverURL = (String) params[0];
            String postParameters = "userID=" + searchKeyword1;


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

    }}



