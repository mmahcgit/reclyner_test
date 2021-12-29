package com.emmahc.smartchair.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.animation.Easing;

/**
 * Created by dev.oni on 2018. 10. 21..
 * Copyright or OutSourcing Source
 * certificate dev.oni
 */

public class BloodPressureCockPointView extends View {
    private float[] position,defaultPosition;

//    private float defaultPosition;

    private Paint positionPaint;
    public BloodPressureCockPointView(Context context) {
        super(context);
        init();
    }

    public BloodPressureCockPointView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BloodPressureCockPointView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        positionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        positionPaint.setStyle(Paint.Style.FILL);
        positionPaint.setColor(Color.BLACK);
    }
    private int measureWidth(int measureSpec) {
        return (measureText(measureSpec));
    }

    private int measureHeight(int measureSpec) {
        return (measureText(measureSpec));
    }
    private int measureText(int measureSpec) {
        int result = 0;
        // specMode는 현재뷰를 담고 있는 부모뷰의 스펙모드에 따른 현재 뷰의 스펙모드
        int specMode = MeasureSpec.getMode(measureSpec);
        // specSize는 현재뷰를 담고 있는 부모뷰가 줄 수 있는 공간의 최대크기
        int specSize = MeasureSpec.getSize(measureSpec);
        if(specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            // Measure the text: twice text size plus padding
            if(specMode == MeasureSpec.AT_MOST) {
                // specSize가 최대값이므로
                // result와 specSize 중 최소값을 찾는다
                result = Math.min(result, specSize);
            }
        }
        // UNSPECIFIED 이면 0을 리턴
        return result;
    }
    public void setPosition(float[] position,float[] defaultPosition) {
        this.position = position;
        this.defaultPosition = defaultPosition;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(position!=null) {
            int radius = Math.min(getWidth(),getHeight()/2);
            canvas.drawCircle(getWidth()/2,getHeight()/2 ,radius, positionPaint);
            Log.d("arrow","width : "+ getWidth()/2 + "height : "+getHeight()/2);
            moveDotAnimation(radius);
        }
    }

    /**
     * dot 움직이는 애니메이션
     * @param radius dot 반지름
     */
    private void moveDotAnimation(int radius){
        AnimatorSet animSetXY = new AnimatorSet();

        ObjectAnimator y = ObjectAnimator.ofFloat(this,
                "translationY",defaultPosition[1]-radius, position[1]-radius);

        ObjectAnimator x = ObjectAnimator.ofFloat(this,
                "translationX", defaultPosition[0]-radius, position[0]-radius);

        animSetXY.playTogether(x, y);
        animSetXY.setInterpolator(Easing.getEasingFunctionFromOption(Easing.EasingOption.EaseInOutQuart));
        animSetXY.setDuration(1250);
        animSetXY.start();
    }
}
