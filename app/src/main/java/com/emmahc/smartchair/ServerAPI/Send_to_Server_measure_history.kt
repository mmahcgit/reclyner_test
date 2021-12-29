package com.emmahc.smartchair.ServerAPI

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.emmahc.smartchair.common.SharedPreferenceManager
import com.emmahc.smartchair.dto.History_API_Dto
import com.emmahc.smartchair.dto.SharedData
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Handler
import kotlin.collections.ArrayList

class Send_to_Server_measure_history(val context: Context) {
    private val API_STATUS_TAG = "History_API_STATUS"
    private val server_url = "http://183.99.48.93:8080/"
    private val client = OkHttpClient.Builder().build()
    private val retrofit = Retrofit.Builder()
            .baseUrl(server_url)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    private val api = retrofit.create(HistoryServerAPIInterface::class.java)
    //select * from data where (recorded_time >= :start or :start is null) and (recorded_time <= :end or :end is null)
    //레코드타임이 start이상이면서 레코드타임이 end이하
    fun request_History_Data(start:Long, end:Long, filterType:String){
        val call = api.get_History(start,end, filterType)
        Log.d(API_STATUS_TAG,"현재 시간 : "+ change_Time_to_Unix())
        //long L = System.currentTimeMillis()
        Log.d(API_STATUS_TAG,"현재 시간(unix) : "+ System.currentTimeMillis())
//        Log.d(API_STATUS_TAG,"현재 시간(unix)->현재 시간 : "+ change_Unix_to_time(System.currentTimeMillis().toString()))
        Log.d(API_STATUS_TAG,"현재 시간(unix)->현재 시간 : "+ change_Unix_to_time(System.currentTimeMillis().toString()))

        val callback = object :Callback<JsonArray>{
            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                Log.d(API_STATUS_TAG,"onFailure")
                Log.d(API_STATUS_TAG,t.toString())
            }
            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                if(response.isSuccessful){
                    Log.d(API_STATUS_TAG, "API 연결 성공 + 데이터 갖고 옴")
                    Log.d("result",response.body().toString())
                    SharedPreferenceManager.setData_to_SharedPrerence(context, "history_api",response.body().toString(),"String")
                    Log.d(API_STATUS_TAG,"History_api키에 결과값 저장")
                }else{
                    Log.d(API_STATUS_TAG,"API 연결은 성공했으나, 데이터는 못 갖고 옴")
                    Log.d(API_STATUS_TAG, response.toString())
                }
            }
        }
        call.enqueue(callback)

    }
    //unix변환 함수
    @SuppressLint("SimpleDateFormat")
    fun change_Unix_to_time(input:String): String? {
        val date = Date(input.toLong())
        val date_format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        date_format.timeZone = TimeZone.getTimeZone("GMT+9")
        return date_format.format(date)
    }
    @SuppressLint("SimpleDateFormat")
    fun change_Time_to_Unix(): Timestamp? {
        val data_format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val calendar = Calendar.getInstance()
        val date = data_format.format(calendar.time)
        return Timestamp.valueOf(date)
    }
    //import java.util.*;
    //
    //class Foo {
    //  public static void main(String args[]) {
    //
    //    Calendar c = Calendar.getInstance();
    //
    //    System.out.println(c.getTimeInMillis() / 1000);
    //
    //  }
    //}
}
