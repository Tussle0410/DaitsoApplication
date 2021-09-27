package com.example.a02.ui.notifications;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.example.a02.R;
import com.example.a02.UserData;
import com.example.a02.foundActivity;
import com.example.a02.main_layout_act;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NotificationsFragment extends Fragment{
    private static String IP_ADDRESS = "";   //내부 IP주소
    private static String TAG = "php";
    private TextView Profile_No,Profile_ID,Profile_Name,Profile_Point,Profile_Birth,Profile_Sex,Profile_name;
    private String userID, userNo, userPW, userName, userBirth, userPoint, userSex,JsonString;
    private ImageView ranking;
    private NotificationsViewModel notificationsViewModel;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        root = inflater.inflate(R.layout.fragment_notifications, container, false);
        Profile_ID = (TextView)root.findViewById(R.id.profile_ID);
        Profile_Point = (TextView)root.findViewById(R.id.profile_Point);
        Profile_Sex = (TextView)root.findViewById(R.id.profile_Sex);
        Profile_Name = (TextView)root.findViewById(R.id.profile_Name);
        Profile_name = (TextView)root.findViewById(R.id.profile_name);
        Profile_Birth = (TextView)root.findViewById(R.id.profile_Birth);
        Profile_No = (TextView)root.findViewById(R.id.profile_No);
        ranking = (ImageView)root.findViewById(R.id.notification_ranking_image);
        if(getArguments() != null){
            userID = getArguments().getString("userID");
            userNo = getArguments().getString("userNo");
            userPW = getArguments().getString("userPW");
            userName = getArguments().getString("userName");
            userBirth = getArguments().getString("userBirth");
            userPoint = getArguments().getString("userPoint");
            userSex = getArguments().getString("userSex");
            Profile_ID.setText(userID);
            Profile_Point.setText(userPoint);
            Profile_Sex.setText(userSex);
            Profile_Name.setText(userName);
            Profile_name.setText(userName);
            Profile_Birth.setText(userBirth);
            Profile_No.setText(userNo);
        }
        checkRanking checkRanking = new checkRanking();
        checkRanking.execute("http://" + IP_ADDRESS + "/profile_ranking.php");
        return root;
    }
    private class checkRanking extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(root.getContext(),
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


            try {
                JSONObject jsonObject = new JSONObject(JsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for(int i=0;i<jsonArray.length();i++){

                    JSONObject item = jsonArray.getJSONObject(i);
                    String ID = item.getString(TAG_ID);
                    int ranking_no = i+1;
                    if(ID.equals(userID)){
                        if (ranking_no==1) {
                            ranking.setImageResource(R.drawable.profile_1st_img);
                        }else if(ranking_no==2) {
                            ranking.setImageResource(R.drawable.profile_2st_img);
                        }else if(ranking_no==3){
                            ranking.setImageResource(R.drawable.profile_3st_img);
                        }}
                }
            } catch (JSONException e) {
                Log.d(TAG, "showResult : ", e);
            }

        }}
}
