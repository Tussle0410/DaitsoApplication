package com.example.a02.ui.top;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TopViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TopViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("TOP 리스트");
    }

    public LiveData<String> getText() {
        return mText;
    }
}

