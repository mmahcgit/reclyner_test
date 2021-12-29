package com.emmahc.smartchair.ServerAPI

import android.util.Log
import java.lang.Exception
import java.lang.StringBuilder

class sumRawData {
    private val API_STATUS_TAG = "API_STATUS"
    fun sumRawData(data:ArrayList<StringBuilder>):String?{
        //속도 StringBuilder > StringBuffer > String
        val sumData:StringBuilder? = StringBuilder("Channel 1;Channel 2;Channl3 \n")
        try{
            Log.d(API_STATUS_TAG,"Start(sumRawData)")
                    for (element_Data in data){
                        if(sumData?.length!! < 150000){
                            sumData?.append(element_Data)

                        }
                    }

            Log.d(API_STATUS_TAG,"end(sumRawData)")
        }catch (e:Exception){
            Log.e(API_STATUS_TAG,"Error occured(sumRawData)")
        }
        return sumData.toString()
    }
    //      title = "KotlinApp"
    //      val progressDialog = ProgressDialog(this@MainActivity)
    //      progressDialog.setTitle("Kotlin Progress Bar")
    //      progressDialog.setMessage("Application is loading, please wait")
    //      progressDialog.show()
}