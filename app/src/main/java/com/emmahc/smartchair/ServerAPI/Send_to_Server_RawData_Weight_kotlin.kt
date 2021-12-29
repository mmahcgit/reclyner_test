package com.emmahc.smartchair.ServerAPI

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.emmahc.smartchair.APIResultActivity
import com.emmahc.smartchair.BLEModule.BlueToothBinder
import com.emmahc.smartchair.BluetoothLeService
import com.emmahc.smartchair.activities.MainActivity
import com.emmahc.smartchair.common.SharedPreferenceManager
import com.emmahc.smartchair.dto.API_Result_Dto
import com.emmahc.smartchair.dto.Raw_weightDto
import com.emmahc.smartchair.fragments.ResultFragment
import com.google.gson.JsonObject
import okhttp3.*
import org.json.JSONObject
import org.xml.sax.Parser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.security.Security
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.security.cert.CertificateException
import kotlin.jvm.Throws

//curl -d "{\"weight\":"60.5", \"rawData\":\"Channel1;Channel2;Channel3 32;54;157 33;55;157 36;56;157\"}" -H "Content-Type: application/json" -X POST http://183.99.48.93:8080/api/raw
//키 - weight, rawData(recordedTime은 잘못된 키값)
//rawData의 길이가 15000이상이여야함.
//post 형식이니까 쿼리스트링은 붙힐 필요 없음
//curl로 api테스트 해본 결과, 시크릿키는 없으므로 헤더에 뭐 붙힐 필요 없음.
class Send_to_Server_RawData_Weight_kotlin(val context: Context) {
    //Log에 사용할 변수들
    private val API_STATUS_TAG = "API_STATUS"
    private val Server_Url = "http://183.99.48.93:8080/"
    private val client = OkHttpClient.Builder()
        .connectTimeout(30,TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.MINUTES)
            .writeTimeout(30, TimeUnit.MINUTES)
        .build()
    //retrofit
    private val retrofit = Retrofit.Builder()
            .baseUrl(Server_Url)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    //api
    private val api = retrofit.create(ServerAPIInterface::class.java)

    //function
    //curl -d "{\"weight\":"60.5", \"rawData\":\"Channel1;Channel2;Channel3 32;54;157 33;55;157 36;56;157\"}" -H "Content-Type: application/json" -X POST http://183.99.48.93:8080/api/raw
    //이렇게 보내면 해결이 되는데.
    fun send_data_to_Server(weight:Double, rawData:String,calculatingDialog:ProgressDialog, heartRate:Double){
        val data_raw_Status = Raw_weightDto()
        data_raw_Status.weight = weight
        data_raw_Status.rawData = rawData
        val call = api.post(data_raw_Status)
        Log.d(API_STATUS_TAG,"시작")
        val result = API_Result_Dto()
        // Callback<Result> callback = new Callback<Result>() { //리스폰 시, 대응할 구현체
        call.enqueue(object : Callback<API_Result_Dto>{
            //연결 실패시
            //java.lang.IllegalArgumentException: Unexpected char 0x0a - > \n
            //java.net.UnknownServiceException: CLEARTEXT communication to 183.99.48.93 not permitted by network security policy
            // - > android 9.0부터는 https로 통신하게 강제함.
            //javax.net.ssl.SSLHandshakeException: Handshake failed - >
            override fun onFailure(call: Call<API_Result_Dto>, t: Throwable) {
                Log.e(API_STATUS_TAG, "API 연결 실패")
                Log.e(API_STATUS_TAG, "$t (in onFailure)")
            }
            //연결 성공시
            override fun onResponse(call: Call<API_Result_Dto>, response: retrofit2.Response<API_Result_Dto>) {
                Log.d(API_STATUS_TAG,"response.message() : ${response.message()}")
                Log.d(API_STATUS_TAG,"response.body() : ${response.body().toString()}")
                    if(response.isSuccessful){
                        Log.d(API_STATUS_TAG,"API 연결 성공")
                        Log.d(API_STATUS_TAG,"response_Message:${response.message()}")
                        Log.d(API_STATUS_TAG,"response_Result : ${response.body().toString()}")
                        //response로 온 값들은 API_Result_Dto에 담음
                        result.uniqueID = response.body()!!.uniqueID
                        result.recordedTime = response.body()!!.recordedTime
                        result.stress = response.body()!!.stress
                        result.heartRate = response.body()!!.heartRate
                        result.systolic = response.body()!!.systolic
                        result.diastolic = response.body()!!.diastolic
                        result.weight = response.body()!!.weight
                        Log.d(API_STATUS_TAG,"데이터 담음")
                        //APIResultActivity로 넘김
                        val intent = Intent(context, APIResultActivity::class.java)
                        intent.putExtra("response_result",result.toString())
                        intent.putExtra("heartRate",heartRate)
                        if(calculatingDialog.isShowing){
                            calculatingDialog.dismiss()
                        }
                        context.applicationContext.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK))
                        Log.d(API_STATUS_TAG,"APIResultActivity로 넘김")
                        return
                    }else{
                        //requestBody에 넣어줘야됨.
                        //no String-argument constructor/factory method to deserialize from String value
                        Log.d(API_STATUS_TAG,"API 연결은 성공했으나 response 못 받음")
                    }
            }
        })
        return
    }

}

//header에 데이터추가할 필요있을 때 사용할 인터셉터 클래스
private class HeaderInterceptor : Interceptor{
    private var clientKey = ""
    private val clientSecretKey = ""
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response = chain.run{
        proceed(
                request()
                        .newBuilder()
                        //request 던져보고 secretkey등 설정 필요하면 던짐
//                        .header()
//                        .addHeader()
                        .build()
        )
    }
}

//handShake failed error
//handShake error 발생 이유
//안드로이드는 https로 통신하게 돼 있는데, 서버 자체에 SSL이 없기 때문에
//강제로 http로 보내게 해야함.
//http로 보내게 하는 방법
//<application
//...
//android:usesCleartextTraffic="true">
//AndroidManifest의 내용에 위와 같은 태그를 붙여주면 됨

// no String-argument constructor/factory method to deserialize from String value ('Channel 1; Channel 2 36;56 36;56')

//Expected a string but was BEGIN_OBJECT at line 1 column 2 path $
//JSON 포맷이 맞지 않아서 발생하는 에러임. 
//결과 Json값이랑 포맷이 않맞아서 발생.
//curl로 찍어본 결과 온 값
//{"uniqueID":141,"recordedTime":1607315495766,"stress":12,"heartRate":85,"systolic":118,"diastolic":77,"weight":73.0}