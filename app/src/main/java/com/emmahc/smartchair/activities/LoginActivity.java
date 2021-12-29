package com.emmahc.smartchair.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;

import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.emmahc.smartchair.BLEScan.BLE_ScanActivity;
import com.emmahc.smartchair.BackButtonExitHandler;
import com.emmahc.smartchair.R;




public class LoginActivity extends Activity {
    private BackButtonExitHandler backButtonExitHandler;
    private ProgressDialog progressDialog;

    private static String user_id = null;
    private static String user_pw = null;

    private FrameLayout login_loading_layout;
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); //No title bar
        setTheme(android.R.style.Theme_NoTitleBar_Fullscreen);
        setContentView(R.layout.activity_login);

        // Handler for back button exit
        backButtonExitHandler = new BackButtonExitHandler(this);

        // UI setting
        final EditText et_user_id = ( EditText )findViewById( R.id.et_user_id );
        final EditText et_user_pw = ( EditText )findViewById( R.id.et_user_pw );
//        Button btn_join = ( Button )findViewById( R.id.btn_join );
        Button btn_login = ( Button )findViewById( R.id.btn_login );
        Button btn_register = ( Button )findViewById( R.id.btn_register );

        login_loading_layout = findViewById(R.id.login_loading_layout);
        btn_login.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user_id = et_user_id.getText().toString().trim();
                user_pw = et_user_pw.getText().toString().trim();

                if (user_pw.length() < 6) {
                    Toast.makeText( LoginActivity.this, R.string.WarningLoginPwIsShort, Toast.LENGTH_SHORT ).show();
                    et_user_id.setText(null);
                    et_user_pw.setText(null);
                } else {
                    showLoadingDialog();

                    if (isEmailValid(et_user_id.getText())) {
                        Intent intent = new Intent(LoginActivity.this, BLE_ScanActivity.class);
                        startActivity(intent);

                    } else {
                        Toast.makeText(LoginActivity.this, R.string.WarningLoginIdNotEmailFormat, Toast.LENGTH_SHORT).show();
                        et_user_id.setText(null);
                        et_user_pw.setText(null);
//                        progressBar.setVisibility(View.INVISIBLE);
                        hideLoadingDialog();
                    }
                }
            }
        });

        btn_register.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);

                startActivity(intent);
            }
        });
    }

    private void showLoadingDialog() {

        //        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                login_loading_layout.setVisibility(View.VISIBLE);
//
//            }
//        },2000);
    }

    private void hideLoadingDialog() {
        if (progressDialog != null & progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        login_loading_layout.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        // Exit application when back button is pressed twice in short time.
        backButtonExitHandler.onBackPressed();
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
