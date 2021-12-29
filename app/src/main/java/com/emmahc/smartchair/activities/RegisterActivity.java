package com.emmahc.smartchair.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.emmahc.smartchair.R;

/**
 * Created by jinhyungwon_pc on 2018-02-12.
 */

public class RegisterActivity extends Activity {
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); //No title bar
        setTheme(android.R.style.Theme_NoTitleBar_Fullscreen);
        setContentView(R.layout.activity_register_not_use);
    }
}
