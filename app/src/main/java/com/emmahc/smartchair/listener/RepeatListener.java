package com.emmahc.smartchair.listener;

import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

//버튼 꾹 눌렀을 때를 처리할 리스너
public class RepeatListener implements View.OnTouchListener {
    private Handler handler = new Handler();
    //최초 인터벌
    private int initialInterval;
    //중간 인터벌
    private final int normalInterval;
    private final View.OnClickListener clickListener;
    //디버그용 에러코드 태그
    private final String errorCode =  "RL";
    //쓰레드 돌리기 위한 Runnable
    private Runnable handlerRunnable = new Runnable() {
        @Override
        public void run() {
            //
            handler.postDelayed(this, normalInterval);
            clickListener.onClick(downView);
        }
    };
    private View downView;
    public RepeatListener(int initialInterval, int normalInterval,
                          View.OnClickListener clickListener) {
        if (clickListener == null){
            Log.e(errorCode, "clickListener값 null (RepeatListener) ");
        }
        if (initialInterval < 0 || normalInterval < 0){
            Log.e(errorCode, "initialInterval || normalInterval < 0 (RepeatListener)");
        }
        this.initialInterval = initialInterval;
        this.normalInterval = normalInterval;
        this.clickListener = clickListener;
    }


    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handler.removeCallbacks(handlerRunnable);
                handler.postDelayed(handlerRunnable, initialInterval);
                downView = view;
                clickListener.onClick(view);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handler.removeCallbacks(handlerRunnable);
                downView = null;
                break;
        }
        return false;
    }

}
