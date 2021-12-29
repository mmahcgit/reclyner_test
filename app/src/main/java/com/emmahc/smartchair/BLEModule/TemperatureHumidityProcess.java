package com.emmahc.smartchair.BLEModule;

import android.os.CountDownTimer;
import android.util.Log;

public class TemperatureHumidityProcess {
    private final int MILLISINFUTURE = 1000 * 1000;//millisec to sec
    private final int LIVE_INTERVAL = 2 * 1000;//2 sec

    private BlueToothBinder binder;
    public TemperatureHumidityProcess(BlueToothBinder binder) {
        this.binder = binder;
    }

    private CountDownTimer countDownTimer = new CountDownTimer(MILLISINFUTURE, LIVE_INTERVAL) {
        public void onTick(long millisUntilFinished) {
            Log.e("T","IC");
            binder.getActtion().getTemperature();
        }

        public void onFinish() {
            countDownTimer.start();
        }
    };

    public void onStart(){
        countDownTimer.start();
    }
    public void onStop(){
        countDownTimer.cancel();
    }
}
