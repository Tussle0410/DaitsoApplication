package com.example.a02;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class service_center_inquiry_Activity extends AppCompatActivity {
    private static String IP_ADDRESS = "192.168.0.3";   //내부 IP주소
    private static String TAG = "php";
    private String userID;
    private ImageButton back_button;
    private EditText comment,title;
    private TextView ID;
    private Button inquiry_input;
    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.service_center_inquiry_page);
        userID = getIntent().getExtras().getString("userID");
        inquiry_input = (Button) findViewById(R.id.service_center_inquiry_input_button);
        ID = (TextView) findViewById(R.id.service_center_inquiry_userID);
        ID.setText(userID);
        comment = (EditText)findViewById(R.id.service_center_inquiry_comment);
        title = (EditText)findViewById(R.id.service_center_inquiry_title);
        back_button = (ImageButton) findViewById(R.id.service_center_inquiry_back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        inquiry_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_inquiry new_inquiry = new new_inquiry();
                new_inquiry.execute("http://" + IP_ADDRESS + "/newInquiry.php", userID,title.getText().toString(),
                        comment.getText().toString());
                finish();
            }
        });
    }
    class new_inquiry extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(service_center_inquiry_Activity.this, "Please Wait", null, true, true);

        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String userID = (String) params[1];
            String title = (String) params[2];
            String comment = (String) params[3];
            String serverURL = (String) params[0];
            String postParameters = "title=" + title + "&comment=" + comment + "&userID=" + userID;
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
}
