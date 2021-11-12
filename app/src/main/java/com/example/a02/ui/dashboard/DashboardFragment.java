package com.example.a02.ui.dashboard;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.a02.Dashboard_Adapter;
import com.example.a02.R;
import com.example.a02.StoreData;
import com.example.a02.comActivity;
import com.example.a02.commentData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private static String IP_ADDRESS = "192.168.0.3";   //내부 IP주소
    private static String TAG = "php";
    private RecyclerView dashboard_list;
    private RecyclerView.LayoutManager dashboard_list_manager;
    private ArrayList<StoreData> storeData_list;
    private Dashboard_Adapter dashboard_adapter;
    private String region,JsonString,userID;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        if(getArguments()!=null){
            region  = getArguments().getString("region");
            userID = getArguments().getString("userID");
        }
        dashboard_list = (RecyclerView) root.findViewById(R.id.dashboard_recycleView);
        storeData_list = new ArrayList<>();
        dashboard_adapter = new Dashboard_Adapter(root.getContext(),storeData_list,userID);
        dashboard_list_manager = new LinearLayoutManager(root.getContext());
        dashboard_list.setLayoutManager(dashboard_list_manager);
        dashboard_list.setAdapter(dashboard_adapter);
        top_list top_list = new top_list();
        top_list.execute("http://" + IP_ADDRESS + "/topList.php", region);
        return root;
    }
    private class top_list extends AsyncTask<String, Void, String> {

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
            if (result.equals("no")) {
                Toast.makeText(root.getContext(), "근처 지역에 평가된 가게가 없습니다.", Toast.LENGTH_SHORT).show();
            } else {
                JsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword1 = (String) params[1];
            String serverURL = (String) params[0];
            String postParameters = "region=" + searchKeyword1;


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

            String TAG_JSON = "top_list";
            String TAG_Name = "storeName";
            String TAG_Address = "storeAddress";
            String TAG_Kinds = "storeKinds";
            String TAG_Rating = "storeRating";


            try {
                JSONObject jsonObject = new JSONObject(JsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);
                    String storeName = item.getString(TAG_Name);
                    String storeAddress = item.getString(TAG_Address);
                    String kinds = item.getString(TAG_Kinds);
                    String Rating = item.getString(TAG_Rating);
                    StoreData Data = new StoreData();
                    Data.setStoreName(storeName);
                    Data.setStoreAddress(storeAddress);
                    Data.setStoreRating(Rating);
                    Data.setStoreKinds(kinds);
                    storeData_list.add(Data);
                    dashboard_adapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                Log.d(TAG, "showResult : ", e);
            }

        }
    }

}
