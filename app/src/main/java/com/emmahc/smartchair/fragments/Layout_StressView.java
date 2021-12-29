package com.emmahc.smartchair.fragments;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import com.emmahc.smartchair.R;
import com.github.mikephil.charting.animation.Easing;

/**
 * Created by dev.oni on 2018. 10. 29..
 * Copyright or OutSourcing Source
 * certificate dev.oni
 */

public class Layout_StressView extends Fragment {

    public static Fragment Instance(int stress){
        Layout_StressView pressureView = new Layout_StressView();
        Bundle b = new Bundle();
        b.putInt("DATA",stress);
        pressureView.setArguments(b);
        return pressureView;
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState ) {
        final View view = inflater.inflate( R.layout.layout_average_stress, container, false );
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ImageView image_stress_arrow = view.findViewById(R.id.image_stress_arrow);
//
//        view.findViewById(R.id.image_stressbar).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                view.findViewById(R.id.image_stressbar).getViewTreeObserver().removeOnGlobalLayoutListener(this);
//
//                float stressbar_unit = (float) (view.findViewById(R.id.image_stressbar).getWidth())/100.f;
//                ObjectAnimator x = ObjectAnimator.ofFloat(image_stress_arrow, "translationX",0, stressbar_unit*getArguments().getInt("DATA"));
//                x.setDuration(1250);
//                x.setInterpolator(Easing.getEasingFunctionFromOption(Easing.EasingOption.EaseInOutQuart));
//                x.start();
//            }
//        });
    }
}