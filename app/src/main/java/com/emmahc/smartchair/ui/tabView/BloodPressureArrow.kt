package com.emmahc.smartchair.ui.tabView

import android.content.Context
import android.graphics.*
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.view.View
import android.widget.ImageView
import com.emmahc.smartchair.R

class BloodPressureArrow(context: Context): View(context) {
    val paint = Paint()
    val path = Path()
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas!!.drawColor(WHITE)


    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(getDefaultSize(suggestedMinimumWidth, widthMeasureSpec), getDefaultSize(suggestedMinimumHeight, heightMeasureSpec))
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
    fun drawArrow(imageView: ImageView, systolic: Double, diastolic:Double){
        //하얀 도화지 생성
        val bitmap = Bitmap.createBitmap(1455, 948, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        imageView.setImageBitmap(bitmap)
        val paint = Paint()
        // 크레파스의 색 정하기
        paint.color = Color.BLACK
        // 크레파스의 굵기 정하기
        paint.strokeWidth = 2f
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        paint.color = BLACK
        path.moveTo(0f,-10f)
        path.lineTo(5f,0f)
        path.lineTo(-5f,0f)
        path.close()
        path.offset(10f,40f)
        path.offset(50f,100f)
        canvas.drawPath(path, paint)
//
//
//        //도화지에 선 그리기
//        canvas.drawLine(0f,-948f,systolic.toFloat(),diastolic.toFloat(),paint)
//        // 도화지에 좌표로 표시하기
//        canvas.drawPoint(systolic.toFloat(), diastolic.toFloat(), paint)
    }
    //// create and draw triangles
    //// use a Path object to store the 3 line segments
    //// use .offset to draw in many locations
    //// note: this triangle is not centered at 0,0
    //paint.setStyle(Paint.Style.STROKE);
    //paint.setStrokeWidth(2);
    //paint.setColor(Color.RED);
    //Path path = new Path();
    //path.moveTo(0, -10);
    //path.lineTo(5, 0);
    //path.lineTo(-5, 0);
    //path.close();
    //path.offset(10, 40);
    //canvas.drawPath(path, paint);
    //path.offset(50, 100);
    //canvas.drawPath(path, paint);
    //// offset is cumlative
    //// next draw displaces 50,100 from previous
    //path.offset(50, 100);
    //canvas.drawPath(path, paint);

}