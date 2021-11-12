package com.example.a02;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ranking_activity extends AppCompatActivity {
    private static String IP_ADDRESS = "192.168.0.3";   //내부 IP주소
    private static String TAG = "php";
    private ImageButton back_button;
    private RecyclerView ranking_list;
    private ArrayList<UserData> user_list;
    private RecyclerView.LayoutManager layoutManager;
    private ranking_Adapter ranking_adapter;
    private String JsonString;
    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.ranking_page);
        back_button = (ImageButton) findViewById(R.id.ranking_back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ranking_list = (RecyclerView) findViewById(R.id.ranking_recyclerview);
        user_list = new ArrayList<>();
        ranking_adapter = new ranking_Adapter(this,user_list);
        layoutManager = new LinearLayoutManager(this);
        ranking_list.setAdapter(ranking_adapter);
        ranking_list.setLayoutManager(layoutManager);
        getRanking getRanking = new getRanking();
        getRanking.execute("http://" + IP_ADDRESS + "/takeranking.php");
    }
    private class getRanking extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ranking_activity.this,
                    "Please Wait", null, true, true);
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);
            JsonString = result;
            showResult();
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
        private void showResult(){

            String TAG_JSON="user";
            String TAG_ID = "userID";
            String TAG_Point = "userPoint";


            try {
                JSONObject jsonObject = new JSONObject(JsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for(int i=0;i<jsonArray.length();i++){

                    JSONObject item = jsonArray.getJSONObject(i);
                    String userID = item.getString(TAG_ID);
                    String point = item.getString(TAG_Point);
                    UserData userData = new UserData();
                    userData.setUserID(userID);
                    userData.setUserPoint(point);
                    userData.setUserNo(Integer.toString(i+1));
                    user_list.add(userData);
                    ranking_adapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                Log.d(TAG, "showResult : ", e);
            }

        }}
}
