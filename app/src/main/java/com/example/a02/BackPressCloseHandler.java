package com.example.a02;

import android.app.Activity;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class BackPressCloseHandler {
    private long backKeyPressTime=0;
    private Toast toast;
    private Activity activity;
    public BackPressCloseHandler(Activity context){
        this.activity = context;
    }
    public void onBackPressed(){
        if(System.currentTimeMillis()>backKeyPressTime+2000){
            backKeyPressTime=System.currentTimeMillis();
            showGuide();
            return;
        }
        if(System.currentTimeMillis()<=backKeyPressTime+2000){
            ActivityCompat.finishAffinity(activity);
            System.exit(0);
            toast.cancel();
        }
    }
    public void showGuide(){
        toast=Toast.makeText(activity,
                "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.",Toast.LENGTH_SHORT);
        toast.show();
    }
}
