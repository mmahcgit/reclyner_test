package com.emmahc.smartchair;

import android.app.Activity;
import android.widget.Toast;

/**
 * Exit application when back button is pressed twice in short time.
 */

public class BackButtonExitHandler {
    private long backButtonPressedTime = 0;
    private Toast toast;
    private Activity activity;

    public BackButtonExitHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backButtonPressedTime + 2000) {
            backButtonPressedTime = System.currentTimeMillis();
            toastGuide();
            return;
        } else {
            activity.finish();
            toast.cancel();
        }
    }

    public void toastGuide() {
        toast = Toast.makeText(activity, R.string.back_key_exit_guide, Toast.LENGTH_SHORT);
        toast.show();
    }
}
