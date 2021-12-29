package com.emmahc.smartchair.dto;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

public class SharedData {
    private final String KEY = "USER_DATA";
    private int gender = 0;     //0일때 남자 1일때 여자
    private int weight = 0;
    private int height = 0;
    private int age = 0;

    //ppg
    private float [] ppg_data = new float[600];
    private int ppg_count;
    //ecg
    private float [] ecg_data = new float[600];
    private int ecg_count;


    private String hr, sbp, dbp, vesselAge, hdisease, meta, stress_score;

    public SharedData(Context ctx) {
        SharedData data = getDataNow(ctx);
        if(data!=null){
            gender = data.gender;
            weight = data.weight;
            height = data.height;
            age = data.age;
        }
    }

    public void resetData() {
        hr = sbp = dbp = vesselAge = hdisease = meta = stress_score = null;
        //ppg
        ppg_data = null;
        ppg_data = new float[600];
        ppg_count = 0;
        //ecg
        ecg_data = null;
        ecg_data = new float[600];
        ecg_count = 0;
    }

    public void setUserInfo(int age, int weight, int height, int gender){
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.gender = gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getGender() {
        return gender;
    }

    public int getWeight() {
        return weight;
    }

    public int getHeight() {
        return height;
    }

    public int getAge() {
        return age;
    }

    public float[] getPpg_data() {
        return ppg_data;
    }

    public int getPpg_count() {
        return ppg_count;
    }

    public String getHr() {
        return hr;
    }

    public String getSbp() {
        return sbp;
    }

    public String getDbp() {
        return dbp;
    }

    public String getVesselAge() {
        return vesselAge;
    }

    public String getHdisease() {
        return hdisease;
    }

    public String getMeta() {
        return meta;
    }

    public String getStress_score() {
        return stress_score;
    }

    public void setPpg_data(float[] ppg_data) {
        this.ppg_data = ppg_data;
    }

    public void setPpg_count(int ppg_count) {
        this.ppg_count = ppg_count;
    }

    public void setHr(String hr) {
        this.hr = hr;
    }

    public void setSbp(String sbp) {
        this.sbp = sbp;
    }

    public void setDbp(String dbp) {
        this.dbp = dbp;
    }

    public void setVesselAge(String vesselAge) {
        this.vesselAge = vesselAge;
    }

    public void setHdisease(String hdisease) {
        this.hdisease = hdisease;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public void setStress_score(String stress_score) {
        this.stress_score = stress_score;
    }

    public void setDataNow(Context ctx){
        SharedPreferences pref = ctx.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        pref.edit().putString(KEY,gson.toJson(this)).apply();
    }
    public SharedData getDataNow(Context ctx){
        SharedPreferences pref = ctx.getSharedPreferences(KEY,Context.MODE_PRIVATE);
        Gson gson = new Gson();
        Type type = new TypeToken<SharedData>(){}.getType();
        return gson.fromJson(pref.getString(KEY,null),type);
    }
    @Override
    public String toString() {
        return "SharedData{" +
                "gender=" + gender +
                ", weight=" + weight +
                ", height=" + height +
                ", age=" + age +
                ", ppg_data=" + Arrays.toString(ppg_data) +
                ", ppg_count=" + ppg_count +
                ", hr='" + hr + '\'' +
                ", sbp='" + sbp + '\'' +
                ", dbp='" + dbp + '\'' +
                ", vesselAge='" + vesselAge + '\'' +
                ", hdisease='" + hdisease + '\'' +
                ", meta='" + meta + '\'' +
                ", stress_score='" + stress_score + '\'' +
                '}';
    }
}
