package com.emmahc.smartchair;

import androidx.appcompat.app.AppCompatActivity;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.emmahc.smartchair.activities.HomeActivity;
import com.emmahc.smartchair.activities.MainActivity;
import com.emmahc.smartchair.common.Prefer;
import com.emmahc.smartchair.common.SharedPreferenceManager;
import com.emmahc.smartchair.dto.API_Result_Dto;
import com.emmahc.smartchair.fragments.Tab5Fragment;
import com.emmahc.smartchair.ui.BloodPressureCockPointView;
import com.emmahc.smartchair.ui.DiastolicBloodPressureGrapic;
import com.emmahc.smartchair.ui.tabView.BloodPressureArrow;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleDataSet;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static com.emmahc.smartchair.activities.MainActivity.mContextMainActivity;

public class APIResultActivity extends AppCompatActivity implements DiastolicBloodPressureGrapic.onDrawFinish {

    public ImageView image_stress_arrow;
    public ImageView image_stressbar;
    public LinearLayout stress_layout;
    public BloodPressureCockPointView pointerView;
    public DiastolicBloodPressureGrapic bloodPressureGrapic;
    double stress_score=0;
    public API_Result_Dto result;

    public ImageView arrow_imgaeView;

    public LineChart result_chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_p_i_result);
        SharedPreferenceManager.setData_to_SharedPrerence(this, "measure_start_check","false","Boolean");
        //Send_to_Server_RawData_Weight_kotlin에서 넘어온 값
        Intent intent = getIntent();
        //String형태로 넘어온 값을 API_Result_Dto형태로 변경
        String raw_result = intent.getStringExtra("response_result");
        raw_result = raw_result.substring(14).replace("(","{").replace(")","}");
        Gson gsonObj = new Gson();
        result = gsonObj.fromJson(raw_result, API_Result_Dto.class);

        //heartRate
        Double heartRate = intent.getDoubleExtra("heartRate",0);


        //평균 심박수
        TextView tv_hr_result = findViewById(R.id.tv_hr_result);
        tv_hr_result.setText(String.valueOf(result.getHeartRate()));
        //tv_vesselage_result
        TextView tv_vesselage_result = findViewById(R.id.tv_vesselage_result);
//        tv_vesselage_result.setText(String.valueOf(Integer.parseInt(String.valueOf(Math.round(heartRate)))));
        //스트레스 점수
        //BloodPressureCockPointView po
        image_stress_arrow = findViewById(R.id.image_stress_arrow);
        image_stressbar = findViewById(R.id.image_stressbar);
        pointerView = findViewById(R.id.pointer);
        bloodPressureGrapic = findViewById(R.id.grapic);
//        bloodPressureGrapic.setListener(this);

        stress_score = result.getStress();


        TextView stress_value = findViewById(R.id.stress_value);
        stress_value.setText(String.valueOf(stress_score));

        stress_layout = findViewById(R.id.stress_layout);

        image_stressbar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                image_stressbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                float stressbar_unit = (float)(image_stressbar.getWidth())/100.f;
                ObjectAnimator x = ObjectAnimator.ofFloat(stress_layout,"translationX",0,stressbar_unit*(float) stress_score);
                x.setDuration(1250);
                x.setInterpolator(Easing.getEasingFunctionFromOption(Easing.EasingOption.EaseInOutQuart));
                x.start();
            }
        });
        //heartRate - 심박수
        //systolic,diastolic - 혈압
        //혈압
        //setText할때 int형이면 에러 발생 가끔 함. -> 그래서 String으로 바꿈.
        TextView tv_bp_result =findViewById(R.id.tv_bp_result);
        tv_bp_result.setText(String.valueOf(result.getSystolic())+" "+String.valueOf(result.getDiastolic()));
        //몸무게
        TextView weight = findViewById(R.id.weight);
        weight.setText(String.valueOf(result.getWeight()));
//
//        arrow_imgaeView = findViewById(R.id.arrow_imgaeView);
//        BloodPressureArrow ba = new BloodPressureArrow(this);
//        ba.drawArrow(arrow_imgaeView, result.getSystolic(), result.getDiastolic());


    }

    public void initChart(){
        //차트 데이터
        //Sysolitic, Diastolic



    }

    //뒤로 가기
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(this, HomeActivity.class);
        String device_name = SharedPreferenceManager.getString_from_SharedPreference(this,"now_device_name");
        String device_address = SharedPreferenceManager.getString_from_SharedPreference(this,"now_device_address");
        Log.d("backpress","device_name is "+device_name);
        Log.d("backpress","device_address is "+device_address);
        Prefer.insertStringPref(this, Prefer.PREF_EXTRAS_DEVICE_NAME, device_name, device_name);
        Prefer.insertStringPref(this, Prefer.PREF_EXTRAS_DEVICE_ADDRESS,device_address, device_address);
        intent.putExtra(GlobalDefines.DeviceInformation.EXTRAS_DEVICE_NAME, device_name);
        intent.putExtra(GlobalDefines.DeviceInformation.EXTRAS_DEVICE_ADDRESS, device_address);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void finishDrawing() {
        pointerView.setPosition(bloodPressureGrapic.getPosition(Integer.parseInt(String.valueOf(result.getSystolic()))/10,Integer.parseInt(String.valueOf(result.getDiastolic()))/10),bloodPressureGrapic.getDefaultPosition());
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

}
