package com.example.a02.ui.home;

import android.widget.Spinner;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private Spinner spinner;
    private TextView tv_result;


    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("홈");
    }

    public LiveData<String> getText() {
        return mText;
    }
}