package com.emmahc.smartchair.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.emmahc.smartchair.utils.Scale;

/**
 * Created by dev.oni on 2018. 10. 21..
 * Copyright or OutSourcing Source
 * certificate dev.oni
 */

public class DiastolicBloodPressureGrapic extends View {
    private int widthOfView,heightOfView;
    private int partWidth,partHeight;
    private int endLine;

    private int divideLineXsize = 10;//DP

    private Paint mPaint,linePaint,textPaint;
    private Rect textBounds = new Rect();


    private onDrawFinish listener;
    public DiastolicBloodPressureGrapic(Context context) {
        super(context);
        init();
    }

    public DiastolicBloodPressureGrapic(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DiastolicBloodPressureGrapic(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint = new Paint();

        linePaint.setColor(getResources().getColor(android.R.color.black));
        linePaint.setStrokeWidth(Scale.dp2px(getContext(),2));

        textPaint.setColor(getResources().getColor(android.R.color.black));
        textPaint.setTextSize(Scale.sp2px(getContext(),14));
        textPaint.getTextBounds("180 +",0,"180 +".length(),textBounds);
    }

    public void setListener(onDrawFinish listener) {
        this.listener = listener;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int padding = Scale.dp2px(getContext(),10);
        setPadding(padding,padding,padding,padding);

        widthOfView = measureWidth(widthMeasureSpec);
        heightOfView= measureHeight(heightMeasureSpec);
        int textWidth = widthOfView - getPaddingLeft() - getPaddingRight();
        int textHeight = heightOfView - getPaddingTop() - getPaddingBottom();

        Log.e("All Measure","width: "+widthOfView+", height: "+heightOfView+" textWidth: "+textWidth+", textHeight: "+textHeight);
        setMeasuredDimension(widthOfView, heightOfView);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        partWidth = widthOfView/8;
        partHeight= heightOfView/8;
        Log.e("onLayout","width: "+widthOfView+", height: "+heightOfView);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(0xff4dbfb4);//파
        drawHorizonLine(canvas,"  40",Scale.dp2px(getContext(),5),partWidth,partHeight*7,partHeight*7,true,partHeight*7);
        drawVerticalLine(canvas,"40",endLine,endLine,partHeight*7,partHeight*7,true);

        mPaint.setColor(0xff4dbfb4);//파
        drawHorizonLine(canvas,"  90",Scale.dp2px(getContext(),5),partWidth*3, partHeight*6,partHeight*7,true,partHeight*6);

        mPaint.setColor(0xff79ba66);//초
        drawHorizonLine(canvas," 120",Scale.dp2px(getContext(),5),partWidth*5, partHeight*4,partHeight*6,true,partHeight*4);
        drawVerticalLine(canvas,"60",partWidth*3,partWidth*5,partHeight*6,partHeight*7,false);

        mPaint.setColor(0xfff5a549);//주
        drawHorizonLine(canvas," 140",Scale.dp2px(getContext(),5),partWidth*6,partHeight*2, partHeight*4,false,0);
        drawVerticalLine(canvas,"80",partWidth*4,partWidth*5,partHeight*4,partHeight*7,false);

        mPaint.setColor(0xfff07d80);//분홍
        drawHorizonLine(canvas," 160",Scale.dp2px(getContext(),5),partWidth*7,partHeight,partHeight*2,false,0);
        drawVerticalLine(canvas,"90",partWidth*5,partWidth*6,partHeight*2,partHeight*7,false);

        mPaint.setColor(0xffeb5968);//빨
        drawHorizonLine(canvas,"180 +",Scale.dp2px(getContext(),5),partWidth*7,0,partHeight,false,0);
        drawVerticalLine(canvas,"100",partWidth*6,partWidth*7,partHeight,partHeight*7,false);

        drawVerticalLine(canvas,"120 +",partWidth*7,partWidth*7,0,partHeight*7,false);
        if(listener != null)
            listener.finishDrawing();
        //주석 달려있었음
        canvas.drawRect(partWidth*7, 0, widthOfView, partHeight*7, mPaint);
    }
    private void drawHorizonLine(Canvas c,String message,int startX,int endX,int startY,int endY,boolean isFinal,int drawPosition){
        int startLine;

        startX += Scale.dp2px(getContext(),5);
        c.drawText(message,startX,isFinal?drawPosition:startY+textBounds.height(),textPaint);

        startLine = textBounds.width()+startX+Scale.dp2px(getContext(),10);
        endLine = startLine+Scale.dp2px(getContext(),divideLineXsize);
        c.drawLine(startLine,startY,endLine,startY,linePaint);

        c.drawRect(endLine,startY,endX,endY,mPaint);
    }
    private void drawVerticalLine(Canvas c,String message,int startX,int endX,int startY,int endY,boolean noDrawing){
        c.drawRect(startX,startY,endX,endY,mPaint);
        c.drawText(message,startX,endY+textBounds.height(),textPaint);
        if(!noDrawing)
            c.drawLine(startX,endY,startX,endY-Scale.dp2px(getContext(),10),linePaint);
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
            if(specMode == MeasureSpec.AT_MOST) {
                // specSize가 최대값이므로
                // result와 specSize 중 최소값을 찾는다
                result = Math.min(result, specSize);
            }
        }
        // UNSPECIFIED 이면 0을 리턴
        return result;
    }

    //혈압치 받아와서 좌표(x,y) 어디에 찍어줄지 리턴값

    /**
     *
     * @param high  최대혈압
     * @param low   최저혈압
     * @return
     * [0] = 최저혈압 X position
     * [1] = 최대혈압 Y position
     */
    public float[] getPosition(int high,int low){
        float[] result = new float[2];
        if(low>120 || high>180){
            return null;
        }else{
            /**
             *        low = 최저혈압
             * divideSpot = 혈압치 구분 구간의 최대값 - 최소값 (100 ~ 120+ 라면, 120-100 = 20) --> 20개의 점 찍을 좌표를 계산한 값
             * partWidth  = 혈압치 구분 구간을 8구간으로 나눠놓은 값인데, 만약, 핸드폰 X축 총 길이가 1440이면, 1440/8의 값이다.(partHeight도 같음.)
             */
            float divideSpot;
            if(low>=100){
                low -= 100;
                divideSpot = (float)partWidth/20;
                result[0] += partWidth*6;
            }else if(low>=90){
                low -= 90;
                divideSpot = (float)partWidth/10;
                result[0] += partWidth*5;
            }else if(low>=80){
                low -= 80;
                divideSpot = (float)partWidth/10;
                result[0] += partWidth*4;
            }else if(low>=60){
                low -= 60;
                divideSpot = (float)partWidth/20;
                result[0] += partWidth*3;
            }else{
                low -= 40;
                divideSpot = (float)partWidth/20;
                result[0] += endLine;
            }

            result[0] += divideSpot*low;

            if(high>=160){
                high -= 160;
                divideSpot = (float)partHeight/20;
                result[1] += partHeight;
            }else if(high>=140){
                high -= 140;
                divideSpot = (float)partHeight/20;
                result[1] += partHeight*2;
            }else if(high>=120){
                high -= 120;
                divideSpot = (float)partHeight*2/20;
                result[1] += partHeight*4;
            }else if(high>=90){
                high -= 90;
                divideSpot = (float)partHeight*2/30;
                result[1] += partHeight*6;
            }else{
                high -= 40;
                divideSpot = (float)partHeight/50;
                result[1] += partHeight*7;
            }

            result[1] -= divideSpot*high;
            Log.e("position","X: "+result[0]+", Y: "+result[1]);
        }
        return result;
    }

    public float[] getDefaultPosition() {
        Log.e("endLine","endLine: "+endLine+", partHeight*7: "+partHeight*7);
        return new float[]{endLine,partHeight*7};
    }

    public interface onDrawFinish{
        void finishDrawing();
    }
}
