package com.example.a02;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

public class service_center_inquiry_answer_activity extends AppCompatActivity {
    private static String IP_ADDRESS = "192.168.0.3";   //내부 IP주소
    private static String TAG = "php";
    private String inquiry_title,inquiry_comment,inquiry_No,JsonString;
    private TextView title, comment,answer;
    private ImageButton back_button;
    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.service_center_inquiry_answer_page);
        back_button = (ImageButton)findViewById(R.id.service_center_answer_back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        inquiry_title = getIntent().getExtras().getString("title");
        inquiry_comment = getIntent().getExtras().getString("comment");
        inquiry_No = getIntent().getExtras().getString("inquiry_no");
        title = (TextView) findViewById(R.id.service_center_inquiry_answer_title);
        title.setMovementMethod(new ScrollingMovementMethod());
        comment = (TextView) findViewById(R.id.service_center_inquiry_answer_comment);
        comment.setMovementMethod(new ScrollingMovementMethod());
        answer = (TextView)findViewById(R.id.service_center_inquiry_answer);
        answer.setMovementMethod(new ScrollingMovementMethod());
        title.setText(inquiry_title);
        comment.setText(inquiry_comment);
        take_answer take_answer = new take_answer();
        take_answer.execute("http://" + IP_ADDRESS + "/takeanswer.php", inquiry_No);
    }
    private class take_answer extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(service_center_inquiry_answer_activity.this,
                    "Please Wait", null, true, true);
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);
            if (result.equals("fail")) {
                answer.setText("아직 답변이 되지 않았습니다.");
            } else {
                JsonString = result;
                showResult();
            }
        }
        @Override
        protected String doInBackground(String... params) {

            String searchKeyword1 = (String) params[1];
            String serverURL = (String) params[0];
            String postParameters = "no=" + searchKeyword1;
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

            String TAG_JSON = "answer";
            String TAG_Answer = "answer";
            try {
                JSONObject jsonObject = new JSONObject(JsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    answer.setText(item.getString(TAG_Answer));
                }
            } catch (JSONException e) {
                Log.d(TAG, "showResult : ", e);
            }

        }
    }

}
