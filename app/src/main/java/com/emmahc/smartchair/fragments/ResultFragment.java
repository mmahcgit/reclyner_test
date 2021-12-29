package com.emmahc.smartchair.fragments;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.emmahc.smartchair.dto.SharedData;
import com.emmahc.smartchair.activities.MainActivity;
import com.emmahc.smartchair.R;
import com.emmahc.smartchair.listener.OnFragmentBackPressListener;
import com.emmahc.smartchair.ui.BloodPressureCockPointView;
import com.emmahc.smartchair.ui.DiastolicBloodPressureGrapic;
import com.github.mikephil.charting.animation.Easing;

import static com.emmahc.smartchair.activities.MainActivity.mContextMainActivity;

/**
 * Created by dev.oni on 2018. 10. 19..
 * Copyright or OutSourcing Source
 * certificate dev.oni
 */

public class ResultFragment extends Fragment implements OnFragmentBackPressListener,DiastolicBloodPressureGrapic.onDrawFinish{

    private static final String TAG = "RESULT";


    public TextView tv_result_measure_hr;
    public TextView tv_result_measure_bp;
    public TextView tv_result_measure_vessel;
    public TextView tv_result_measure_hdisease;
    public TextView tv_result_measure_meta;
    public TextView tv_result_measure_stress;
    public ImageView image_stress_arrow;
    public ImageView image_stressbar;
    public BloodPressureCockPointView pointerView;
    public DiastolicBloodPressureGrapic bloodPressureGrapic;
    double stress_score=0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate( R.layout.activity_result_measure, container, false );

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //ResultFragment 로 Bind 되면 Tab레이아웃 보이게 변경
//        mContextMainActivity.findViewById(R.id.tabLayout).setVisibility(View.VISIBLE);
//        //Back Button 누를수있는 Fragment.
//        ((MainActivity)getActivity()).setFragmentBackPressListener(this);
//
//        tv_result_measure_hr = getView().findViewById(R.id.tv_hr_result);
//        tv_result_measure_bp = getView().findViewById(R.id.tv_bp_result);
//        tv_result_measure_vessel = getView().findViewById(R.id.tv_vesselage_result);
//        tv_result_measure_hdisease = getView().findViewById(R.id.tv_hdisease_result);
//        tv_result_measure_meta = getView().findViewById(R.id.tv_meta_result);
//        tv_result_measure_stress = getView().findViewById(R.id.tv_stress_score);
//        image_stress_arrow = getView().findViewById(R.id.image_stress_arrow);
//        image_stressbar=getView().findViewById(R.id.image_stressbar);
//        pointerView = getView().findViewById(R.id.pointer);
//        bloodPressureGrapic = getView().findViewById(R.id.grapic);
//        bloodPressureGrapic.setListener(this);
//
//
//        String string_hr = mContextMainActivity.getBinder().getHr_mean_final();
//        Log.d(TAG, "hr_mean2: "+ string_hr);
//        String string_stress = mContextMainActivity.getBinder().getStress_score_final();
//        Log.d(TAG, "stress_score_final: "+ string_stress);
//        if(string_stress != null)
//            stress_score = Double.parseDouble(string_stress);
//        tv_result_measure_hr.setText(string_hr);
//
//
////        ObjectAnimator x = ObjectAnimator.ofFloat(image_stress_arrow,
////                "translationX", defaultPosition[0]-radius, position[0]-radius);
////        x.setDuration(1250);
////        x.start();
//
//        SharedData data = mContextMainActivity.getBinder().getData();
//        tv_result_measure_bp.setText(String.format("%s/%s mmHg", data.getSbp(), data.getDbp()));
//        tv_result_measure_vessel.setText(String.format("%s 세", data.getVesselAge()));
//        tv_result_measure_hdisease.setText(data.getHdisease()+" %");
//        tv_result_measure_meta.setText(data.getMeta()+" %");
//
//        image_stressbar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                image_stressbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//
//                float stressbar_unit = (float) (image_stressbar.getWidth())/100.f;
//                ObjectAnimator x = ObjectAnimator.ofFloat(image_stress_arrow, "translationX",0, stressbar_unit*(float) stress_score);
//                x.setDuration(1250);
//                x.setInterpolator(Easing.getEasingFunctionFromOption(Easing.EasingOption.EaseInOutQuart));
//                x.start();
//            }
//        });
    }

    @Override
    public void onBack() {
        try{
            mContextMainActivity.findViewById(R.id.tabLayout).setVisibility(View.VISIBLE);
            mContextMainActivity.findViewById(R.id.fragmentContent).setClickable(false);
            getFragmentManager().beginTransaction().remove(this).commitNowAllowingStateLoss();
            mContextMainActivity.getBinder().getTHprocess().onStart();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            mContextMainActivity.setFragmentBackPressListener(null);
        }
    }

    @Override
    public void finishDrawing() {

//        SharedData data = mContextMainActivity.getBinder().getData();
//        pointerView.setPosition(bloodPressureGrapic.getPosition(Integer.parseInt(data.getSbp()),Integer.parseInt(data.getDbp())),bloodPressureGrapic.getDefaultPosition());

//        mContextMainActivity.getFbBinder().setOnSetData(false);
    }
}
