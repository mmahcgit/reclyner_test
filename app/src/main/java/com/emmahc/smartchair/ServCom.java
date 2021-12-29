package com.emmahc.smartchair;

import android.app.ProgressDialog;
import androidx.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.emmahc.smartchair.dto.SharedData;

import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public class ServCom {

    private String apiKey = "yQm1VrVOuunw4TBl4h1xOfp8pNEthVPIwnFu1xX2",
                accesskey = "PbDvaXxkTaHf19QGViU1",                     // access key id (from portal or sub account)
                secretKey = "HOAg4vr7bjzHr4OvMeAvw70Ae8nNKa6ctudDJuJy"; // access key id (from portal or sub account)

    static String [] servcom_result = null;

    private onResponseListener callbackListener;

    private boolean onSuccess;
    public ServCom(onResponseListener callbackListener, boolean b){
        this.callbackListener = callbackListener;
        this.onSuccess = b;
    }
//    public ServCom() {
//        servcom_result = new String[5];
//    }
    public String makeSignature(String current_timestamp, String ApiName) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String space = " ";                    // one space
        String newLine = "\n";                    // new line
        String method = "POST";                    // method
        String url = "/S-Vital/v1/" + ApiName;    // url (include query string)

        String timestamp = current_timestamp;            // current timestamp (epoch)

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(apiKey)
                .append(newLine)
                .append(accesskey)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
//        String encodeBase64String = Base64.encodeBase64String(rawHmac);
        String encodeBase64String = Base64.encodeToString(rawHmac, Base64.NO_WRAP);

        return encodeBase64String;
    }


    private interface ApiService {
        //베이스 Url
        String BASEURL = "https://ligiuebq0i.apigw.ntruss.com/S-Vital/v1/";
        @Headers({"Content-Type: application/json"})
        @POST("ppg_bp")
        Call<JsonObject> uploadPPG_stress(@Header("x-ncp-apigw-timestamp") String API_timestamp, @Header("x-ncp-apigw-api-key") String API_apikey, @Header("x-ncp-iam-access-key") String API_accesskey, @Header("x-ncp-apigw-signature-v1") String API_signature, @Body RequestBody body);
    }

    public String[] SendData(final ProgressDialog progress, final int fs, final SharedData sd, final float[] ppg_data_final){

        for(float d : ppg_data_final) {
            //Log.d(TAG, String.format("PPG_DATA : %f", d));
        }
        //Log.d(TAG, String.format("USER INFO: %d %d %d %d", sd.getGender(), sd.getWeight(), sd.getHeight(), sd.getAge()));
        Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(ApiService.BASEURL).build();
        ApiService apiService = retrofit.create(ApiService.class);

        MediaType mediaType = MediaType.parse("application/json");
        String data = "{\"fs\" :" + String.valueOf(fs) +
                      ",\"gender\" :" + String.valueOf(sd.getGender()) +
                      ",\"weight\" :" + String.valueOf(sd.getWeight()) +
                      ",\"height\" :" + String.valueOf(sd.getHeight()) +
                      ",\"age\" :" + String.valueOf(sd.getAge()) +
                      ",\"ppg\" : " + Arrays.toString(ppg_data_final) +"}";

        RequestBody body = RequestBody.create(mediaType, data);

        Long tsLong = System.currentTimeMillis();
        String timestamp = tsLong.toString();
        String signature = null;
        try {
            signature = makeSignature(timestamp, "ppg_bp");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        Call<JsonObject> call = apiService.uploadPPG_stress(timestamp, apiKey, accesskey, signature, body);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                progress.dismiss();

                if (response.isSuccessful()) {
                    JsonObject object = response.body();
                    Log.w("response",object.toString());
                    int sbp,dbp,vesselAge,hdisease,meta;

                    try{
                        sbp = (int)Double.parseDouble(object.get("sys").toString().replaceAll("\"", ""));
                        dbp = (int)Double.parseDouble(object.get("dia").toString().replaceAll("\"", ""));
                        vesselAge = (int)Double.parseDouble(object.get("vesselAge").toString().replaceAll("\"", ""));
                        hdisease = (int)Double.parseDouble(object.get("hdisease").toString().replaceAll("\"", ""));
                        meta = (int)Double.parseDouble(object.get("meta").toString().replaceAll("\"", ""));



                        /**
                         * {@link ResultMeasureActivity} 에서
                         * {@link com.emmahc.smartchair.fragments.ResultFragment} 로 DataSet 이전
                         */

                        sd.setSbp(String.valueOf(sbp));
                        sd.setDbp(String.valueOf(dbp));
                        sd.setVesselAge(String.valueOf(vesselAge));
                        sd.setHdisease(String.valueOf(hdisease));
                        sd.setMeta(String.valueOf(meta));

                        Log.w("SD",sd.toString());
                        callbackListener.onSuccess(onSuccess);
                    }catch (NullPointerException e){
                        Toast.makeText(progress.getContext(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        callbackListener.onSuccess(false);
                    }
                }else{
                    Log.e("response","데이터 오류");
                    callbackListener.onSuccess(false);
                }
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progress.dismiss();
                Log.e("response","데이터 불러오기 실패 :" + t.getMessage());
            }
        });
        return servcom_result;
    }
    public interface onResponseListener{
        void onSuccess(boolean onSuccess);
    }
}