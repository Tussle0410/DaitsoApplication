package com.example.a02.ui.top;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.a02.MainActivity;
import com.example.a02.R;
import com.example.a02.app_introduce_activity;
import com.example.a02.ranking_activity;
import com.example.a02.service_center_page_Activity;

public class TopFragment extends Fragment {
    private String userID;
    private Button service_center_button,logout_button,ranking_button,introduce_button;
    private View root;
    private TopViewModel topViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        topViewModel =
                ViewModelProviders.of(this).get(TopViewModel.class);
        root = inflater.inflate(R.layout.fragment_top, container, false);
        if(getArguments()!=null){
            userID = getArguments().getString("userID");
        }
        logout_button = (Button) root.findViewById(R.id.logout_button);
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        service_center_button = (Button) root.findViewById(R.id.service_center_intent_button);
        service_center_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent service_center_intent = new Intent(root.getContext(), service_center_page_Activity.class);
                service_center_intent.putExtra("userID",userID);
                startActivity(service_center_intent);
            }
        });
        ranking_button = (Button) root.findViewById(R.id.ranking_intent_button);
        ranking_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ranking_intent = new Intent(root.getContext(), ranking_activity.class);
                startActivity(ranking_intent);
            }
        });
        introduce_button = (Button) root.findViewById(R.id.app_introduce_button);
        introduce_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent introduce_intent = new Intent(root.getContext(), app_introduce_activity.class);
                startActivity(introduce_intent);
            }
        });
        return root;
    }

    void showDialog() {
        AlertDialog.Builder msgBuilder = new AlertDialog.Builder(root.getContext())
                .setTitle("로그 아웃")
                .setMessage("로그 아웃 하시겠습니까?")
                .setPositiveButton("로그 아웃", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent ( root.getContext(), MainActivity.class);
                        ActivityCompat.finishAffinity(getActivity());
                        startActivity (intent);
                        System.runFinalization();} })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialogInterface, int i) {
                        } });
        AlertDialog msgDlg = msgBuilder.create(); msgDlg.show(); }
}


