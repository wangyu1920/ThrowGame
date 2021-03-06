package com.example.throw10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class View_Bound extends View {
    public View_Circle[] view_circles=new View_Circle[]{new View_Circle(500,500)};
//    __________________________________________________________
    public View_Bound(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
//   ————————————————————————————————————————————————————

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!view_circles[0].isExist) {
            view_circles[0] = new View_Circle(500, 500);
        }
        view_circles[0].DrawCircle(canvas);
        postInvalidateDelayed(20);
    }
//    ____________________________________________________

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        view_circles[0].moveByTouch(event);
        return true;
    }
}
