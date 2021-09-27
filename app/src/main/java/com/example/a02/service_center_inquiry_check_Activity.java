package com.example.a02;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

public class service_center_inquiry_check_Activity extends AppCompatActivity {
    private static String IP_ADDRESS = "";   //내부 IP주소
    private static String TAG = "php";
    private String userID,JsonString;
    private TextView Text_userID;
    private ImageButton back_button;
    private RecyclerView inquiry_list;
    private RecyclerView.LayoutManager inquiry_list_manager;
    private inquiry_check_Adapter inquiry_check_adapter;
    private ArrayList<inquiryData> list;
    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.service_center_inquiry_check_page);
        userID = getIntent().getExtras().getString("userID");
        back_button = (ImageButton) findViewById(R.id.service_center_inquiry_check_back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Text_userID = (TextView) findViewById(R.id.service_center_inquiry_check_userID);
        Text_userID.setText(userID);
        inquiry_list = (RecyclerView) findViewById(R.id.service_center_inquiry_check_list);
        list = new ArrayList<>();
        inquiry_check_adapter = new inquiry_check_Adapter(this,list,userID);
        inquiry_list_manager = new LinearLayoutManager(this);
        inquiry_list.setAdapter(inquiry_check_adapter);
        inquiry_list.setLayoutManager(inquiry_list_manager);
        if(userID.equals("admin")){
            admin_take_inquiry admin_take_inquiry = new admin_take_inquiry();
            admin_take_inquiry.execute("http://" + IP_ADDRESS + "/admin_takeinquiry.php");

        }else {
            take_inquiry take_inquiry = new take_inquiry();
            take_inquiry.execute("http://" + IP_ADDRESS + "/takeInquiry.php", userID);
        }
    }
    private class take_inquiry extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(service_center_inquiry_check_Activity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);
            if (result.equals("fail")) {
                Toast.makeText(service_center_inquiry_check_Activity.this, "문의한 기록이 없습니다.", Toast.LENGTH_SHORT).show();
            } else {
                JsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String userID = (String) params[1];
            String serverURL = (String) params[0];
            String postParameters = "userID=" + userID;
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

        private void showResult() {

            String TAG_JSON = "inquiry";
            String TAG_Title = "inquiryTitle";
            String TAG_Comment = "inquiryComment";
            String TAG_Check = "answerCheck";
            String TAG_No = "inquiryNo";
            try {
                JSONObject jsonObject = new JSONObject(JsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);
                    String title = item.getString(TAG_Title);
                    String comment = item.getString(TAG_Comment);
                    String check = item.getString(TAG_Check);
                    String No = item.getString(TAG_No);
                    inquiryData inquiryData = new inquiryData();
                    inquiryData.setInquiry_title(title);
                    inquiryData.setInquiry_comment(comment);
                    inquiryData.setAnswerCheck(check);
                    inquiryData.setInquiry_no(No);
                    list.add(inquiryData);
                    inquiry_check_adapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                Log.d(TAG, "showResult : ", e);
            }

        }
    }
    private class admin_take_inquiry extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(service_center_inquiry_check_Activity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);
            if (result.equals("fail")) {
                Toast.makeText(service_center_inquiry_check_Activity.this, "문의한 기록이 없습니다.", Toast.LENGTH_SHORT).show();
            } else {
                JsonString = result;
                showResult();
            }
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

        private void showResult() {

            String TAG_JSON = "inquiry_admin";
            String TAG_Title = "inquiryTitle";
            String TAG_Comment = "inquiryComment";
            String TAG_Check = "answerCheck";
            String TAG_No = "inquiryNo";
            try {
                JSONObject jsonObject = new JSONObject(JsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);
                    String title = item.getString(TAG_Title);
                    String comment = item.getString(TAG_Comment);
                    String check = item.getString(TAG_Check);
                    String No = item.getString(TAG_No);
                    inquiryData inquiryData = new inquiryData();
                    inquiryData.setInquiry_title(title);
                    inquiryData.setInquiry_comment(comment);
                    inquiryData.setAnswerCheck(check);
                    inquiryData.setInquiry_no(No);
                    list.add(inquiryData);
                    inquiry_check_adapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                Log.d(TAG, "showResult : ", e);
            }

        }
    }
}
