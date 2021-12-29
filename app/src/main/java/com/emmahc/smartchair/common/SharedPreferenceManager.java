package com.emmahc.smartchair.common;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManager {
    public static final String PREFERENCES_NAME = "bebe_preference";
    //sharedPreference에 저장된 값 로드할 때 사용할 디폴트 값들
    private static final String DEFAULT_VALUE_STRING = "";
    private static final boolean DEFAULT_VALUE_BOOLEAN = false;
    private static final int DEFAULT_VALUE_INT = -1;
    private static final long DEFAULT_VALUE_LONG = -1L;
    private static final float DEFAULT_VALUE_FLOAT = -1F;

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    //SharedPreference에 데이터 저장
    public static void setData_to_SharedPrerence(Context context, String key, String value, String dataType){
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        switch (dataType){
            case "String":
                editor.putString(key, value);
                break;
            case "Boolean":
                editor.putBoolean(key, Boolean.parseBoolean(value));
                break;
            case "int":
                editor.putInt(key, Integer.parseInt(value));
                break;
            case "long":
                editor.putLong(key, Long.parseLong(value));
                break;
            case "float":
                editor.putFloat(key, Float.parseFloat(value));
                break;
        }
        editor.apply();
    }
    //String 값 로드
    public static String getString_from_SharedPreference(Context context, String key) {

        SharedPreferences prefs = getPreferences(context);

        return prefs.getString(key, DEFAULT_VALUE_STRING);

    }

    //boolean값 로드
    public static boolean getBoolean_from_SharedPreference(Context context, String key) {

        SharedPreferences prefs = getPreferences(context);

        return prefs.getBoolean(key, DEFAULT_VALUE_BOOLEAN);

    }


    //int값 로드
    public static int getInt_from_SharedPreference(Context context, String key) {

        SharedPreferences prefs = getPreferences(context);

        return prefs.getInt(key, DEFAULT_VALUE_INT);

    }



    //long 값 로드
    public static long getLong_from_SharedPreference(Context context, String key) {

        SharedPreferences prefs = getPreferences(context);

        return prefs.getLong(key, DEFAULT_VALUE_LONG);

    }


    //float값 로드
    public static float getFloat_from_SharedPreference(Context context, String key) {

        SharedPreferences prefs = getPreferences(context);

        return prefs.getFloat(key, DEFAULT_VALUE_FLOAT);

    }


    //키 값 삭제
    public static void removeKey_of_SharedPreference(Context context, String key) {

        SharedPreferences prefs = getPreferences(context);

        SharedPreferences.Editor edit = prefs.edit();

        edit.remove(key);

        edit.apply();

    }

    //모든 저장 데이터 삭제
    public static void clear_SharedPreference(Context context) {

        SharedPreferences prefs = getPreferences(context);

        SharedPreferences.Editor edit = prefs.edit();

        edit.clear();

        edit.apply();

    }
}