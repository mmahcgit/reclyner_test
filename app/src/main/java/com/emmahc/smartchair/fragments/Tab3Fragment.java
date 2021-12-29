package com.emmahc.smartchair.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.emmahc.smartchair.R;
import com.emmahc.smartchair.activities.AppInfoActivity;
import com.emmahc.smartchair.activities.DeviceScanActivity;
import com.emmahc.smartchair.activities.HomeActivity;
import com.emmahc.smartchair.activities.UserInfoActivity;
import com.emmahc.smartchair.common.Prefer;
import com.emmahc.smartchair.common.SharedPreferenceManager;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;

import java.util.ArrayList;


public class Tab3Fragment extends Fragment {


    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState ) {
        final View view = inflater.inflate( R.layout.fragment_tab3, container, false );
        TextView connected_device_name = view.findViewById(R.id.connected_device_name);
        String prefName_device = SharedPreferenceManager.getString_from_SharedPreference(getContext(),"now_device_name");
        String prefName_address = SharedPreferenceManager.getString_from_SharedPreference(getContext(),"now_device_address");

        connected_device_name.setText("기기명 : "+prefName_device);
        TextView connected_device_address = view.findViewById(R.id.connected_device_address);
        connected_device_address.setText("기기주소 : "+prefName_address);



        //기기 정보 관련 레이아웃
        LinearLayout connected_device_info = view.findViewById(R.id.connected_device_info);



        //기기 정보 레이아웃이 눌렸을 때 Dialog뜨게 함
        connected_device_info.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new MaterialAlertDialogBuilder(getContext())
                        .setTitle("블루투스 설정")
                        .setMessage("다른 블루투스를 설정하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(getContext(), DeviceScanActivity.class);
                                intent.putExtra("reconnect",true);
                                String prefName_device = SharedPreferenceManager.getString_from_SharedPreference(getContext(),"now_device_name");
                                String prefName_address = SharedPreferenceManager.getString_from_SharedPreference(getContext(),"now_device_address");
                                Prefer.deleteDevice_pref(getContext(),Prefer.PREF_EXTRAS_DEVICE_NAME,prefName_device);
                                Prefer.deleteDevice_pref(getContext(),Prefer.PREF_EXTRAS_DEVICE_ADDRESS,prefName_address);
                                HomeActivity.mContextMainActivity.getBinder().unbindService();
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                builder.show();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getView().findViewById(R.id.user).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),UserInfoActivity.class));
            }
        });
        getView().findViewById(R.id.app).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),AppInfoActivity.class));
            }
        });
    }



}
