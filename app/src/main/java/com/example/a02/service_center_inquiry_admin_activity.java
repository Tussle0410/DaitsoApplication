package com.example.a02;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class service_center_inquiry_admin_activity extends AppCompatActivity {
    private static String IP_ADDRESS = "192.168.0.3";   //내부 IP주소
    private static String TAG = "php";
    private String inquiry_title,inquiry_comment,inquiry_No;
    private TextView title, comment;
    private ImageButton back_button;
    private Button write;
    private EditText answer;
    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.service_center_inquiry_answer_admin_page);
        back_button = (ImageButton)findViewById(R.id.service_center_answer_admin_back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        inquiry_title = getIntent().getExtras().getString("title");
        inquiry_comment = getIntent().getExtras().getString("comment");
        inquiry_No = getIntent().getExtras().getString("inquiry_no");
        title = (TextView) findViewById(R.id.service_center_inquiry_answer_admin_title);
        title.setMovementMethod(new ScrollingMovementMethod());
        comment = (TextView) findViewById(R.id.service_center_inquiry_answer_admin_comment);
        comment.setMovementMethod(new ScrollingMovementMethod());
        answer = (EditText) findViewById(R.id.service_center_inquiry_admin_answer_write);
        write = (Button) findViewById(R.id.service_center_answer_admin_write_button);
        title.setText(inquiry_title);
        comment.setText(inquiry_comment);
        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input_answer input_answer = new input_answer();
                input_answer.execute("http://" + IP_ADDRESS + "/newanswer.php", inquiry_No,answer.getText().toString());
                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        update_inquiryCheck update_inquiryCheck = new update_inquiryCheck();
                        update_inquiryCheck.execute("http://" + IP_ADDRESS + "/UpdateanswerCheck.php",inquiry_No);
                    }
                },300);
                Handler handler2 = new Handler();
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent user_page = new Intent(getApplicationContext(),service_center_inquiry_answer_activity.class);
                        user_page.putExtra("title",inquiry_title);
                        user_page.putExtra("comment",inquiry_comment);
                        user_page.putExtra("inquiry_no",inquiry_No);
                        startActivity(user_page);
                        finish();
                    }
                },300);
            }
        });
    }
    private class input_answer extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(service_center_inquiry_admin_activity.this,
                    "Please Wait", null, true, true);
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);
            if (result.equals("fail")) {
                Log.d("php", "답변 등록하기 오류 발생!");
            }
        }
        @Override
        protected String doInBackground(String... params) {

            String no = (String) params[1];
            String answer = (String) params[2];
            String serverURL = (String) params[0];
            String postParameters = "no=" + no + "&answer=" + answer;
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

    }
    private class update_inquiryCheck extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(service_center_inquiry_admin_activity.this,
                    "Please Wait", null, true, true);
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);
        }
        @Override
        protected String doInBackground(String... params) {
            String no = (String) params[1];
            String serverURL = (String) params[0];
            String postParameters = "no=" + no;
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

    }
}
