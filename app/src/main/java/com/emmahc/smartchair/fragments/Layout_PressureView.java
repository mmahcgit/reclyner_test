package com.emmahc.smartchair.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emmahc.smartchair.R;

/**
 * Created by dev.oni on 2018. 10. 29..
 * Copyright or OutSourcing Source
 * certificate dev.oni
 */

public class Layout_PressureView extends Fragment {

    public static Fragment Instance(int SBP, int DBP){
        Layout_PressureView pressureView = new Layout_PressureView();
        Bundle b = new Bundle();
        b.putInt("SBP",SBP);
        b.putInt("DBP",DBP);
        pressureView.setArguments(b);
        return pressureView;
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState ) {
        final View view = inflater.inflate( R.layout.layout_average_blood_pressure, container, false );
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((TextView)view.findViewById(R.id.text_data1)).setText(getArguments().getInt("SBP")+" Bpm");
        ((TextView)view.findViewById(R.id.text_data2)).setText(getArguments().getInt("DBP")+" Bpm");
    }
}