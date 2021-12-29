package com.emmahc.smartchair.BLEModule;

import androidx.annotation.IdRes;

/**
 * Created by dev.oni on 2018. 10. 22..
 * Copyright or OutSourcing Source
 * certificate dev.oni
 */

public interface BLEActionListener {
    void onENGModeStart();
    void onENGModeEnd(boolean b);
    void onUpdateChart(String chart, float[] x);
    void onTextUpdate(@IdRes int id, CharSequence text);
    void onTextColorUpdate(@IdRes int id, int color);
    void onBackgroundResourceUpdate(@IdRes int id, int drawableId);
}
