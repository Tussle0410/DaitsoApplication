package com.example.a02;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class service_center_page_Activity extends AppCompatActivity {
    private ImageButton back_button;
    private Button service_inquiry_button, service_inquiry_check_button;
    private String userID;
    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.service_center_page);
        userID = getIntent().getExtras().getString("userID");
        back_button = (ImageButton) findViewById(R.id.service_center_back_button);
        service_inquiry_button = (Button) findViewById(R.id.service_center_inquiry_button);
        service_inquiry_check_button = (Button) findViewById(R.id.service_center_inquiry_check_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        service_inquiry_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent service_center_inquiry_intent = new Intent(getApplicationContext(),service_center_inquiry_Activity.class);
                service_center_inquiry_intent.putExtra("userID",userID);
                startActivity(service_center_inquiry_intent);
            }
        });
        service_inquiry_check_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent service_center_inquiry_check_intent = new Intent(getApplicationContext(),service_center_inquiry_check_Activity.class);
                service_center_inquiry_check_intent.putExtra("userID",userID);
                startActivity(service_center_inquiry_check_intent);
            }
        });
    }
}
