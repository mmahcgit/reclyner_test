package com.emmahc.smartchair;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.DialogInterface;
import androidx.annotation.NonNull;


import java.util.ArrayList;

public class GlobalVariables extends Application {
    private ArrayList<Integer> RRIntervalEcg = new ArrayList<Integer>();
    private ArrayList<Integer> RRIntervalPpg = new ArrayList<Integer>();

    public ArrayList<Integer> getRRIntervalEcg() {
        return RRIntervalEcg;
    }

    public ArrayList<Integer> getRRIntervalPpg() {
        return RRIntervalPpg;
    }

    public void setRRIntervalEcg(ArrayList<Integer> RRIntervalEcg) {
        this.RRIntervalEcg = RRIntervalEcg;
    }

    public void setRRIntervalPpg(ArrayList<Integer> RRIntervalPpg) {
        this.RRIntervalPpg = RRIntervalPpg;
    }

    //ecg_ppg_total_Data
    private ArrayList<String> ecg_ppg_total_Data = new ArrayList<String>();
    //여기서 에러 발생가능성
    public void setEcg_ppg_total_Data(ArrayList<String> ecg_ppg_total_Data) {
        this.ecg_ppg_total_Data = ecg_ppg_total_Data;
    }

    private ArrayList<Integer> heartRate = new ArrayList<>();
    public void setHeartRate(ArrayList<Integer> heartRate){
        this.heartRate = heartRate;
    }


    private StringBuffer ecg_ppg_tData = new StringBuffer();
    public void setEcg_ppg_tData(StringBuffer ecg_ppg_tData){
        this.ecg_ppg_tData = ecg_ppg_tData;
    }

    private StringBuilder ecg_ppg_Data = new StringBuilder();
    public void setEcg_ppg_Data(StringBuilder ecg_ppg_Data){
        this.ecg_ppg_Data = ecg_ppg_Data;
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

}
