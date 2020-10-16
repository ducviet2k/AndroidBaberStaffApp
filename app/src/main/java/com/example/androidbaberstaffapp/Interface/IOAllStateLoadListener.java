package com.example.androidbaberstaffapp.Interface;

import android.view.View;

import com.example.androidbaberstaffapp.Model.City;

import java.util.List;

public interface IOAllStateLoadListener {
    void onAllStateLoadSuccess(List<City>cityList);
    void onAllStateLoadFailed(String message);
}
