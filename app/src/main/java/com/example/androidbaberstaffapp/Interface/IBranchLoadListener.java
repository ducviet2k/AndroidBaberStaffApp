package com.example.androidbaberstaffapp.Interface;

import com.example.androidbaberstaffapp.Model.Salon;

import java.util.List;

public interface IBranchLoadListener {
    void onBranchLoadSuccess(List<Salon> areaNameList);
    void onBranchLoadFailed(String message);
}
