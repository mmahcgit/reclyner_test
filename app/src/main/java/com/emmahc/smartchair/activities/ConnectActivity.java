package com.emmahc.smartchair.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.emmahc.smartchair.R;

//연결화면 출력 activity
public class ConnectActivity extends Activity{


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tab1);

        Button connectBtn = findViewById(R.id.btn_fragment1_start);
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConnectActivity.this, DeviceScanActivity.class);
                startActivity(intent);
                finish();
            }
        });





    }
}
