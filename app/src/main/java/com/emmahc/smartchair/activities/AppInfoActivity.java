package com.emmahc.smartchair.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.emmahc.smartchair.R;

public class AppInfoActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        try {
            ((TextView)findViewById(R.id.version)).setText(getPackageManager().getPackageInfo(getPackageName(),PackageManager.GET_ACTIVITIES).versionName.replaceAll("[a-zA-Z]|-", ""));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        findViewById(R.id.urlgo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.jakomo.co.kr"));
                startActivity(browserIntent);
            }
        });
    }
}
