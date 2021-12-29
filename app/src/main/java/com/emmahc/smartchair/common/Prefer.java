package com.emmahc.smartchair.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by kimsigyeong on 2018. 9. 9..
 */

public class Prefer {
    public static final String PREFERENCES_NAME = "reclyner_preference";
    final public static String PREF_EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    final public static String PREF_EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";


    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
    public static void insert_Device_info(Context context, String key, String value){
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public static String getDevice_info(Context context, String key){
        SharedPreferences prefs = getPreferences(context);
        return prefs.getString(key, "");
    }

    public static void insertStringPref(Context context, String prefName, String key, String value){
        SharedPreferences prefs = context.getSharedPreferences(prefName, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String selectStringPref(Context context, String prefName, String key){
        SharedPreferences prefs = context.getSharedPreferences(prefName, 0);
        String value = prefs.getString(key, "");
        return value;
    }
    public static void deleteDevice_pref(Context context,String prefName,String key){
        SharedPreferences prefs = context.getSharedPreferences(prefName, 0);

        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key); // will delete key key_name3
        editor.apply();

    }

}
