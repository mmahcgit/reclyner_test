package com.emmahc.smartchair.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.emmahc.smartchair.dto.AvgDTO;
import com.emmahc.smartchair.utils.Scale;

import java.util.ArrayList;

/**
 * Created by dev.oni on 2018. 10. 21..
 * Copyright or OutSourcing Source
 * certificate dev.oni
 */

public class GraphLayout extends View {
    private int widthOfView,heightOfView;
    private float partWidth,partHeight;

    private Paint linePaint,textPaint,textPaint2,dotPaint;
    private Rect textBounds,textBounds2;
    private Path path;

    private final int RADIUS = 45;

    private ArrayList<AvgDTO> pathArray;
    private int xLineCount,yLineCount;

    private onDrawFinish listener;
    private DateType dt;
    private DataType dataType;
    private float lastDataXposition;
    public GraphLayout(Context context) {
        super(context);
        init();
    }

    public GraphLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GraphLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void test(){
        pathArray = new ArrayList<>();
        dt = DateType.DAY;
        dataType=DataType.HR;
    }
    private void init(){
        test();

        path = new Path();

        textBounds = new Rect();
        textBounds2 = new Rect();

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint = new Paint();

        linePaint.setColor(0x11000000);
        linePaint.setStrokeWidth(Scale.dp2px(getContext(),1));

        textPaint.setColor(0x55000000);
        textPaint.setTextSize(Scale.sp2px(getContext(),15));
        textPaint.getTextBounds("01",0,2,textBounds);

        textPaint2.setColor(0x55000000);
        textPaint2.setTextSize(Scale.sp2px(getContext(),12));
        textPaint2.getTextBounds("2018년",0,5,textBounds2);

        dotPaint.setStrokeWidth(Scale.dp2px(getContext(),2));
        dotPaint.setStyle(Paint.Style.STROKE);
        dotPaint.setColor(0xff79ba66);

        //기본값
        if(xLineCount==0){
            if(dt== DateType.DAY)
                this.xLineCount = 31; //31일 기본
            else if(dt== DateType.WEEK)
                this.xLineCount = 48; //48주 기본
            else if(dt== DateType.MONTH)
                this.xLineCount = 12; //12개월 기본
            else if(dt== DateType.YEAR)
                this.xLineCount = 18; //2000 ~ 2018년 기본
        }
    }

    public void setListener(onDrawFinish listener) {
        this.listener = listener;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        widthOfView = measureWidth(widthMeasureSpec);
        heightOfView= measureHeight(heightMeasureSpec);

        Log.e("All Measure","width: "+widthOfView+", height: "+heightOfView);

        if(5>xLineCount) {
            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            setMeasuredDimension((int) (Scale.dp2px(getContext(), display.getWidth()/2.8f)*1.5f), heightOfView);
        }else
            setMeasuredDimension(Scale.dp2px(getContext(),45*xLineCount), heightOfView);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if(xLineCount>0) {
            partWidth = widthOfView / xLineCount;
            if(dataType==DataType.HR)
                partHeight = heightOfView / 140;
            else
                partHeight = heightOfView / 180;

            Log.e("onLayout", "width: " + widthOfView + ", height: " + heightOfView);
            Log.e("onLayout", "partWidth: " + partWidth + ", partHeight: " + partHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        path.reset();
        float endYPosition = 0f;
        if (dt == DateType.DAY)
            endYPosition = dayDraw(canvas);
        else if (dt == DateType.WEEK)
            endYPosition = weekDraw(canvas);
        else if (dt == DateType.MONTH)
            endYPosition = monthDraw(canvas);
        else if (dt == DateType.YEAR)
            endYPosition = yearDraw(canvas);
        //날짜 그린 후, X축(device) 선 쭉 긋기
        drawLine(canvas, 0, widthOfView, endYPosition, endYPosition);

        if(pathArray!=null) {

            //X축 그린 후, Y축 하나씩 선 긋기
            verticalLineDraw(canvas, xLineCount, endYPosition);

            drawText(canvas, "180", 0, endYPosition + textBounds.height());
            if(dataType==DataType.HR)
                drawText(canvas, "40", 0, heightOfView-textBounds.height());
            else
                drawText(canvas, "0", 0, heightOfView-textBounds.height());

            lastDataXposition=0f;
            if (pathArray != null && pathArray.size() > 0) {
                dotPaint.setColor(0xff79ba66);
                if(dataType==DataType.PRESSURE){
                    chartDraw(canvas, pathArray,1);
                    dotPaint.setColor(0xfff5a549);
                    chartDraw(canvas, pathArray,0);
                }else{
                    chartDraw(canvas, pathArray,-1);
                }
                if(listener!=null)
                    listener.finishDrawing(lastDataXposition-partWidth*4);
            }
        }else {
            String text = "데이터가 없습니다.";
            textPaint.getTextBounds(text, 0, text.length(), textBounds);
            drawText(canvas, text, getResources().getDisplayMetrics().widthPixels / 2 - textBounds.width() / 1.75f, heightOfView / 2);
        }
    }
    private void chartDraw(Canvas c,ArrayList<AvgDTO> arr,int hrType){
        path.reset();

        float nowX, nowY;
        float nextX, nextY;

        nowX = partWidth/2;
        nextX = nowX+partWidth;
        nowY = ofChartParse(arr,0,hrType);
        nextY = ofChartParse(arr,1,hrType);

        //마지막 데이터가 있는곳을 기준점으로 UI HorizonScroll X좌표가 변경됨.
        if(nextY>0)
            lastDataXposition = nowX;

        path.moveTo(nowX, heightOfView-partHeight*nowY);

        path.quadTo(nowX,heightOfView-partHeight*nowY,nextX,heightOfView-partHeight*nextY);

        drawText(c,ofTextParse(arr.get(0),hrType)+"",nowX-(float) textBounds.width(),(heightOfView-partHeight*nowY)-Scale.dp2px(getContext(),10)-textBounds.height());

        for(int i=2; i<arr.size(); i++){
            nowX = nextX;
            nowY = nextY;
            nextX = nowX+partWidth;
            nextY = ofChartParse(arr,i,hrType);

            //마지막 데이터가 있는곳을 기준점으로 UI HorizonScroll X좌표가 변경됨.
            if(nextY>0)
                lastDataXposition = nowX;

            path.quadTo(nowX,heightOfView-partHeight*nowY,nextX,heightOfView-partHeight*nextY);
            drawText(c,ofTextParse(arr.get(i-1),hrType)+"",nowX-(float) textBounds.width(),(heightOfView-partHeight*nowY)-Scale.dp2px(getContext(),10)-textBounds.height());
        }
        drawText(c,ofTextParse(arr.get(arr.size()-1),hrType)+"",nextX-(float) textBounds.width(),(heightOfView-partHeight*nextY)-Scale.dp2px(getContext(),10)-textBounds.height());

        c.drawPath(path, dotPaint);
    }
    private float ofChartParse(ArrayList<AvgDTO> arr,int i,int type){
        if(dataType==DataType.STRESS)
            return arr.get(i).getStress();
        else if(dataType==DataType.HR)
            return arr.get(i).getHr()-40;
        else{
            //type = 0 --> SBP
            //type = 1 --> DBP
            if(type==1)
                return (int) arr.get(i).getSBP();
            else
                return (int) arr.get(i).getDBP();
        }
    }

    private int ofTextParse(AvgDTO arr,int type){
        if(dataType==DataType.STRESS)
            return (int) arr.getStress();
        else if(dataType==DataType.HR)
            return (int) arr.getHr();
        else{
            //type = 0 --> SBP
            //type = 1 --> DBP
            if(type==1)
                return (int) arr.getSBP();
            else
                return (int) arr.getDBP();
        }
    }

    private float dayDraw(Canvas canvas){

        textPaint.getTextBounds("01",0,2,textBounds);
        drawText(canvas,"01",textBounds.width()/1.25f,0);

        for(int i =1; i<9; i++)
            drawText(canvas,"0"+(i+1),partWidth*i+textBounds.width()/1.25f,0);

        for(int i =9; i<xLineCount; i++)
            drawText(canvas,(i+1)+"",partWidth*i+textBounds.width()/1.25f,0);

        return textBounds.height()+Scale.dp2px(getContext(),10);
    }
    private float weekDraw(Canvas c){
        textPaint.getTextBounds("01",0,2,textBounds);
        textPaint2.getTextBounds("01월",0,3,textBounds2);
        for(int i=0; i<9; i++) {
            for (int j = 0; j < 4; j ++){
                drawText(c, "0" + (j + 1), partWidth * (i*4+j) + textBounds.width() / 1.25f, 0);
                drawText2(c,"0"+ (i+1)+"월",partWidth * (i*4+j) +textBounds2.width()/3f,textBounds2.height()+Scale.dp2px(getContext(),10));
            }
        }
        for(int i=9; i<xLineCount; i++) {
            for (int j = 0; j < 4; j ++){
                drawText(c, "0" + (j + 1), partWidth * (i*4+j) + textBounds.width() / 1.25f, 0);
                drawText2(c,(i+1)+"월",partWidth * (i*4+j) +textBounds2.width()/3f,textBounds2.height()+Scale.dp2px(getContext(),10));
            }
        }
    return textBounds.height()*2+Scale.dp2px(getContext(),20);
    }

    private float monthDraw(Canvas c){
        textPaint2.getTextBounds("01월",0,3,textBounds2);

        for(int i =0; i<9; i++)
            drawText2(c,"0"+(i+1)+"월",partWidth*i+textBounds2.width()/2f,0);
        for(int i =9; i<xLineCount; i++)
            drawText2(c,(i+1)+"월",partWidth*i+textBounds2.width()/2f,0);

        return textBounds2.height()+Scale.dp2px(getContext(),10);
    }

    private float yearDraw(Canvas c){
        textPaint2.getTextBounds("2018년",0,5,textBounds2);

        for(int i =0; i<xLineCount; i++)
            drawText2(c, pathArray.get(i).getEnumYear()+"", partWidth * i + Scale.dp2px(getContext(),2), 0);

        return textBounds2.height()+Scale.dp2px(getContext(),10);
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
        requestLayout();
        invalidate();
    }

    public void setDrawType(DateType dt){
        this.dt = dt;
    }
    public void setCountLine(int line){
        this.xLineCount = line;
        setMeasuredDimension(Scale.dp2px(getContext(),45*xLineCount), heightOfView);
    }
    private void verticalLineDraw(Canvas c,int xLineCount,float startY){
        for(int i=1; i<xLineCount; i++){
            drawLine(c,partWidth*i,partWidth*i,startY,heightOfView);
        }
    }
    private void drawText(Canvas c,String text, float startX,float startY){
        c.drawText(text,startX,startY+textBounds.height(),textPaint);
    }
    private void drawText2(Canvas c,String text, float startX,float startY){
        c.drawText(text,startX,startY+textBounds2.height(),textPaint2);
    }
    private void drawLine(Canvas c,float startX,float endX,float startY,float endY){
        c.drawLine(startX,startY,endX,endY,linePaint);
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

    public void setPathArray(ArrayList<AvgDTO> pathArray) {
        this.pathArray = pathArray;
        requestLayout();
        invalidate();
    }

    public interface onDrawFinish{
        void finishDrawing(float x);
    }
}
