package com.example.a02;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class comActivity_top extends AppCompatActivity {
    private static String IP_ADDRESS = "192.168.0.3";   //내부 IP주소
    private static String TAG = "php";
    private String title = "", address = "", category = "", Rating = "",storeViews="",storeNo="",
            userID = "",stringRating, commentstring, ratingstringresult;
    private String JsonString, JsonRating,Jsonstore;
    private int ratinginteger=0;
    private float ratingresult=0;
    private ArrayList<commentData> marrayList;
    private Comment_Adapter comment_adapter;
    private RecyclerView.LayoutManager Comment_LayoutManager;
    private TextView store_title, store_address, store_Views, store_Rating,store_category;
    private ImageButton backspace_button;
    private RecyclerView community_comment;
    private RatingBar storeRating;
    private Button comment_input_button;
    private EditText comment_text;
    private boolean comment_rating_check = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_page);
        Bundle extras = getIntent().getExtras();
        title = extras.getString("title");
        userID = extras.getString("userID");
        category = extras.getString("category");
        store_category = (TextView) findViewById(R.id.community_store_category);
        storeRating = (RatingBar) findViewById(R.id.community_grade);
        storeRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                stringRating = Float.toString(rating);
                ratinginteger = (int) rating;
            }
        });
        comment_input_button = (Button) findViewById(R.id.community_comment_button);
        comment_text = (EditText) findViewById(R.id.community_comment);
        community_comment = (RecyclerView) findViewById(R.id.community_listView);
        marrayList = new ArrayList<>();
        comment_adapter = new Comment_Adapter(this, marrayList);
        Comment_LayoutManager = new LinearLayoutManager(this);
        community_comment.setLayoutManager(Comment_LayoutManager);
        community_comment.setAdapter(comment_adapter);
        backspace_button = (ImageButton) findViewById(R.id.community_back_button);
        backspace_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        store_title = (TextView) findViewById(R.id.community_store_title);
        store_address = (TextView) findViewById(R.id.community_store_address);
        store_Views = (TextView) findViewById(R.id.community_views);
        store_Rating = (TextView) findViewById(R.id.community_Rating_score);
        takeStore takeStore = new takeStore();
        takeStore.execute("http://" + IP_ADDRESS + "/takeStore.php", title);
        if (category.equals("RESTAURANT")) {
            store_category.setText("음식점");
        } else if (category.equals("CHURCH")) {
            store_category.setText("교회");
        } else if(category.equals("CAFE")){
            store_category.setText("카페");
        } else if(category.equals("SUPERMARKET")){
            store_category.setText("슈퍼마켓");
        } else if(category.equals("BAKERY")){
            store_category.setText("베이커리");
        } else if(category.equals("CONVENIENCE_STORE")){
            store_category.setText("편의점");
        }else if(category.equals("HOSPITAL")){
            store_category.setText("병원");
        }else if(category.equals("ATM")){
            store_category.setText("ATM");}
        else if(category.equals("BOOK_STORE")){
            store_category.setText("서점");}
        store_title.setText(title);
        Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                takeComment takeComment = new takeComment();
                takeComment.execute("http://" + IP_ADDRESS + "/takeComment.php", storeNo);
            }
        }, 200);
        Handler handler3 = new Handler();
        handler3.postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewIncrement viewIncrement = new ViewIncrement();
                viewIncrement.execute("http://" + IP_ADDRESS + "/incrementView.php", title);
            }
        }, 200);
        comment_input_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentstring = comment_text.getText().toString();
                newComment newComment = new newComment();
                newComment.execute("http://" + IP_ADDRESS + "/newComment.php", storeNo, stringRating,
                        commentstring, userID);
                Handler handler4 = new Handler();
                handler4.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(comment_rating_check){
                            takerating takerating = new takerating();
                            takerating.execute("http://" + IP_ADDRESS + "/outputrating.php", storeNo);}
                        comment_rating_check=true;
                    }
                }, 200);
                Handler handler5 = new Handler();
                handler5.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ratingstringresult = Float.toString(ratingresult);
                        Log.d(TAG,"Msg:"+ratingstringresult);
                        store_Rating.setText(ratingstringresult);
                        update_rating update_rating = new update_rating();
                        update_rating.execute("http://" + IP_ADDRESS + "/UpdateRating.php",title,ratingstringresult);
                    }
                }, 400);
                Handler handler6 = new Handler();
                handler6.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updatePoint updatePoint = new updatePoint();
                        updatePoint.execute("http://" + IP_ADDRESS + "/UpdatePoint.php",userID);
                    }
                },400);
            }
        });
    }


    private class newComment extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(comActivity_top.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "response - " + result);
            if (result.equals("fail")) {
                Toast.makeText(comActivity_top.this, "계정당 댓글을 하나만 작성할 수 있습니다.", Toast.LENGTH_SHORT).show();
                comment_rating_check=false;
            } else {
                commentData newcomment = new commentData();
                newcomment.setRating(ratinginteger);
                newcomment.setComment(comment_text.getText().toString());
                newcomment.setUser_ID(userID);
                marrayList.add(newcomment);
                comment_adapter.notifyDataSetChanged();
            }
            progressDialog.dismiss();

        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = (String) params[0];
            String searchKeyword1 = (String) params[1];
            String searchKeyword2 = (String) params[2];
            String searchKeyword3 = (String) params[3];
            String searchKeyword4 = (String) params[4];
            String postParameters = "title=" + searchKeyword1 + "&rating=" + searchKeyword2 + "&comment=" + searchKeyword3
                    + "&id=" + searchKeyword4;
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
    private class ViewIncrement extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(comActivity_top.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "response - " + result);
            progressDialog.dismiss();

        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = (String) params[0];
            String searchKeyword1 = (String) params[1];
            String postParameters = "title=" + searchKeyword1;
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

    private class takeComment extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(comActivity_top.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);
            if (result.equals("fail")) {
                Log.d(TAG, "not comment");
            } else {
                JsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword1 = (String) params[1];
            String serverURL = (String) params[0];
            String postParameters = "title=" + searchKeyword1;


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

            String TAG_JSON = "comment";
            String TAG_ID = "userID";
            String TAG_Comment = "storecomment";
            String TAG_Rating = "storeRating";


            try {
                JSONObject jsonObject = new JSONObject(JsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);
                    String userID = item.getString(TAG_ID);
                    String storeComment = item.getString(TAG_Comment);
                    int Rating = item.getInt(TAG_Rating);
                    commentData commentData = new commentData();
                    commentData.setUser_ID(userID);
                    commentData.setComment(storeComment);
                    commentData.setRating(Rating);
                    marrayList.add(commentData);
                    comment_adapter.notifyDataSetChanged();
                }


            } catch (JSONException e) {
                Log.d(TAG, "showResult : ", e);
            }

        }
    }

    private class takerating extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(comActivity_top.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);
            JsonRating = result;
            showResult();

        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword1 = (String) params[1];
            String serverURL = (String) params[0];
            String postParameters = "storeno=" + searchKeyword1;


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

            String TAG_JSON = "rating";
            String TAG_Rating = "storeRating";


            try {
                JSONObject jsonObject = new JSONObject(JsonRating);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    int Rating = item.getInt(TAG_Rating);
                    ratingresult += Rating;
                }

                ratingresult =ratingresult / jsonArray.length();
            } catch (JSONException e) {
                Log.d(TAG, "showResult : ", e);
            }

        }
    }

    private class update_rating extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(comActivity_top.this,
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

            String searchKeyword1 = (String) params[1];
            String searchKeyword2 = (String) params[2];
            String serverURL = (String) params[0];
            String postParameters = "title=" + searchKeyword1 + "&rating=" + searchKeyword2;


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
    private class updatePoint extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(comActivity_top.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "response - " + result);
            progressDialog.dismiss();

        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = (String) params[0];
            String searchKeyword1 = (String) params[1];
            String postParameters = "user=" + searchKeyword1;
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
    private class takeStore extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(comActivity_top.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);
            Jsonstore = result;
            showResult();
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword1 = (String) params[1];
            String serverURL = (String) params[0];
            String postParameters = "title=" + searchKeyword1;


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

            String TAG_JSON = "store";
            String TAG_No = "storeNo";
            String TAG_Views = "storeViews";
            String TAG_Rating = "storeRating";
            String TAG_Address = "storeAddress";
            String TAG_Kinds = "storeKinds";


            try {
                JSONObject jsonObject = new JSONObject(Jsonstore);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);
                    storeNo = item.getString(TAG_No)+"c";
                    storeViews = item.getString(TAG_Views);
                    Rating = item.getString(TAG_Rating);
                    category = item.getString(TAG_Kinds);
                    address = item.getString(TAG_Address);
                    store_address.setText(address);
                    store_Views.setText(storeViews);
                    store_Rating.setText(Rating);
                }


            } catch (JSONException e) {
                Log.d(TAG, "showResult : ", e);
            }

        }
    }
}
