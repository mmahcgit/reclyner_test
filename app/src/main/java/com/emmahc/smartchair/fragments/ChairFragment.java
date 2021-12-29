package com.emmahc.smartchair.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.emmahc.smartchair.BLEModule.BLEActionListener;
import com.emmahc.smartchair.GlobalDefines;
import com.emmahc.smartchair.R;
import com.emmahc.smartchair.activities.MainActivity;
import com.emmahc.smartchair.dto.API_Result_Dto;
import com.emmahc.smartchair.listener.OnFragmentBackPressListener;
import com.emmahc.smartchair.ui.onBackPressedListener;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;


import static com.emmahc.smartchair.activities.HomeActivity.mContextMainActivity;

/**
 * Created by dev.oni on 2018. 10. 19..
 * Copyright or OutSourcing Source
 * certificate dev.oni
 */

public class ChairFragment extends Fragment implements BLEActionListener {
    private LineChart mChartEcg,mChartPpg;
    private Button btn_eng_mode_end;

    private OnBackPressedCallback backCallback;

    public static boolean IsRunningChair = false;
    public static ChairFragment newInstance() {
        ChairFragment frg = new ChairFragment();
        return frg;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("ChairFragment","onResume");
        mContextMainActivity.getBinder().setListener(this);
        //mContext.unregisterReceiver(receiver);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate( R.layout.activity_control_chair, container, false );
        //chairFragment에서 뒤로가기
//        ((MainActivity)getActivity()).setFragmentBackPressListener(this);
        IsRunningChair = true;
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // Sets up Chart UI.
        mChartEcg = getView().findViewById(R.id.chart_ecg);
        mChartPpg = getView().findViewById(R.id.chart_ppg);
        mChartEcg.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        mChartPpg.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        btn_eng_mode_end = getView().findViewById(R.id.btn_eng_mode_end);
        //postDelayed() - 약간의 시간 딜레이
        //측정 중단 버튼 누르기 전까지는 1000millis마다 딜레이하며 반복
        //측정 중단 버튼 누르면 종료 프로토콜 보냄
        btn_eng_mode_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContextMainActivity.getBinder().getActtion().stopMeasuring();
    }
});
//        btn_eng_mode_end.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(!mContextMainActivity.getBinder().getActtion().isKey_is_measuring()){
//                    try {
//                        mContextMainActivity.getBinder().getActtion().measuring_end(false);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    //위의 measuring_end가 끝나고 프래그먼트 변경해줌
//                }else
//                    onENGModeEnd(false);
//            }
//        });
//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //측정 중단
//                btn_eng_mode_end.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if(!mContextMainActivity.getBinder().getActtion().isKey_is_measuring()){
//                            try {
//                                mContextMainActivity.getBinder().getActtion().measuring_end(false);
//                                Log.d("timetimetime","End?");
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            //위의 measuring_end가 끝나고 프래그먼트 변경해줌
//                            Log.d("timetimetime","isEnd?");
//                        }else
//                            onENGModeEnd(false);
//                    }
//                });
//            }
//        },1000);
    }


    // Functions.
    //차트 뷰의 형태 잡는 함수로 중간에 데이터 들어가는 부분 있음
    private void init_chart() {
        //ECG차트
        // Init for ChartEcg
        mChartEcg.getDescription().setEnabled(false);
        mChartEcg.setTouchEnabled(false);
        mChartEcg.setDragEnabled(false);
        mChartEcg.setScaleEnabled(false);
        mChartEcg.setDrawGridBackground(false);
        mChartEcg.setPinchZoom(false);
        mChartEcg.getLegend().setEnabled(false);

        mChartEcg.setBackgroundColor(Color.WHITE);
        mChartEcg.setAutoScaleMinMaxEnabled(true);

        //데이터
        LineData lineData_ecg = new LineData();

        lineData_ecg.setDrawValues(false);

        //데이터 넣는 부분
        mChartEcg.setData(lineData_ecg);
        mChartEcg.invalidate();

        mChartEcg.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        mChartEcg.getAxisLeft().setDrawLabels(false);
        mChartEcg.getAxisRight().setDrawLabels(false);
        mChartEcg.getXAxis().setDrawLabels(false);

        mChartEcg.getAxisLeft().setDrawGridLines(false);
        mChartEcg.getAxisRight().setDrawGridLines(false);
        mChartEcg.getXAxis().setDrawGridLines(false);


        //PPG차트
        // Init for ChartPpg
        mChartPpg.getDescription().setEnabled(false);
        mChartPpg.setTouchEnabled(false);
        mChartPpg.setDragEnabled(false);
        mChartPpg.setScaleEnabled(false);
        mChartPpg.setDrawGridBackground(false);
        mChartPpg.setPinchZoom(false);
        mChartPpg.getLegend().setEnabled(false);
        mChartPpg.setDrawGridBackground(false);
//        mChartPpg.setScaleX(2f);

        mChartPpg.setBackgroundColor(Color.WHITE);
        mChartPpg.setAutoScaleMinMaxEnabled(true);

        // 수정
        mChartPpg.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        mChartPpg.getAxisLeft().setDrawLabels(false);
        mChartPpg.getAxisRight().setDrawLabels(false);
        mChartPpg.getXAxis().setDrawLabels(false);

        mChartPpg.getAxisLeft().setDrawGridLines(false);
        mChartPpg.getAxisRight().setDrawGridLines(false);
        mChartPpg.getXAxis().setDrawGridLines(false);

        LineData lineData_ppg = new LineData();
        lineData_ppg.setDrawValues(false);

        //데이터 들어가는 부분
        mChartPpg.setData(lineData_ppg);
        mChartPpg.invalidate();
    }

    // 수정
    private void updateChartArray(String chart, float[] x) {
//        Log.w("isIn?","INININ");
        LineChart mChart = null;
        int color = Color.TRANSPARENT;
        switch (chart) {
            case "ECG" :
                mChart = mChartEcg;     color = Color.RED;
                try {
                    LineData data = mChart.getData();
                    ILineDataSet dataSet = data.getDataSetByIndex(0);
                    if (dataSet == null) {
                        dataSet = createSet(color);
                        data.addDataSet(dataSet);
                    }
                    for (int i = 0; i < GlobalDefines.Setting.CHART_UPDATE_PERIOD; i++) {

                        if(i % 2 == 0){
                            dataSet.addEntry(new Entry(dataSet.getEntryCount()+1, x[i]));
                        }
//                        Log.e("dataSet1", String.valueOf(x[i]));
                    }
                    data.notifyDataChanged();
                    mChart.notifyDataSetChanged();
           /*         *
                     * X축 보여지는 HeartBeat 개수 조절
                     * 4개정도 나옴.*/
                    mChart.setVisibleXRangeMaximum(GlobalDefines.Setting.ENG_MODE_CHART_NUMBER_OF_X * 1.5f);
                    mChart.moveViewToX(dataSet.getEntryCount());
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case "PPG" :
                mChart = mChartPpg;     color = Color.BLUE;
                try {
                    LineData data = mChart.getData();
                    ILineDataSet dataSet = data.getDataSetByIndex(0);
                    if (dataSet == null) {

                        dataSet = createSet(color);
                        data.addDataSet(dataSet);
                    }
                    for (int i = 0; i < GlobalDefines.Setting.CHART_UPDATE_PERIOD; i++) {
                        //Log.e("dataSetEntry2", String.valueOf(dataSet.getEntryCount()));
                        if(i % 2 != 0){
                            dataSet.addEntry(new Entry(dataSet.getEntryCount(), x[i]));
                        }
//                        Log.e("dataSet2", String.valueOf(x[i]));
                    }
                    data.notifyDataChanged();
                    mChart.notifyDataSetChanged();
                    /**
                     * X축 보여지는 HeartBeat 개수 조절
                     * 4개정도 나옴.
                     */
                    mChart.setVisibleXRangeMaximum(GlobalDefines.Setting.ENG_MODE_CHART_NUMBER_OF_X * 1.5f);

                    mChart.moveViewToX(dataSet.getEntryCount());
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        if (mChart == null) {
            Toast.makeText(getContext(), R.string.warning_undefined_chart_name, Toast.LENGTH_SHORT).show();
        }
    }
    private LineDataSet createSet(int color) {
        LineDataSet set = new LineDataSet(null, "DataSet");

        set.setDrawCircles(false);
        set.setColor(color);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setValueTextColor(Color.TRANSPARENT);

        return set;
    }


    //BLEAction
    @Override
    public void onENGModeStart() {
        init_chart();
        Log.d("init_chart","chartStart");
    }

    @Override
    public void onENGModeEnd(boolean b) {
        mContextMainActivity.findViewById(R.id.tabLayout).setVisibility(View.VISIBLE);
//        if(b){
//            mContextMainActivity.getBinder().setListener((BLEActionListener) mContextMainActivity.getFragment());
//            mContextMainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContent, new ResultFragment()).commitAllowingStateLoss();
//        }else{
//            mContextMainActivity.setFragmentBackPressListener(null);
//            mContextMainActivity.findViewById(R.id.fragmentContent).setClickable(false);
//            mContextMainActivity.getSupportFragmentManager().beginTransaction().remove(this).commitNowAllowingStateLoss();
//            mContextMainActivity.getBinder().setListener((BLEActionListener) mContextMainActivity.getFragment());
//            mContextMainActivity.getBinder().getTHprocess().onStart();
//        }

    }

    @Override
    public void onUpdateChart(String chart, float[] x) {
        updateChartArray(chart,x);
    }

    @Override
    public void onTextUpdate(int id, CharSequence text) {
        try {
            ((TextView) getView().findViewById(id)).setText(text);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onTextColorUpdate(int id, int color) {
    }


    @Override
    public void onBackgroundResourceUpdate(int id, int drawableId) {
        try {
            getView().findViewById(id).setBackgroundResource(drawableId);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
    @Override
    public void onStop() {
        IsRunningChair = false;
//        getFragmentManager().beginTransaction().replace(R.id.layout_control_chair_activity, new Tab5Fragment()).commitAllowingStateLoss();
//        mContextMainActivity.getBinder().getActtion().stopMeasuring();
//        mContextMainActivity.getBinder().getTHprocess().onStart();
        super.onStop();
    }

}
