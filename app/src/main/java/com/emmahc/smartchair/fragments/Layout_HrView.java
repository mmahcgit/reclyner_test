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

public class Layout_HrView extends Fragment {

    public static Fragment Instance(int hrRate){
        Layout_HrView hrView = new Layout_HrView();
        Bundle b = new Bundle();
        b.putInt("DATA",hrRate);
        hrView.setArguments(b);
        return hrView;
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState ) {
        final View view = inflater.inflate( R.layout.layout_average_heart_rate, container, false );
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((TextView)view.findViewById(R.id.text_data)).setText(getArguments().getInt("DATA")+" Bpm");
    }
}