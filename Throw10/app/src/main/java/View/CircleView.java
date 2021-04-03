package View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import CircleRelatedClass.Circle;
import CircleRelatedClass.CirclesManager;

public class CircleView extends View {
    public CirclesManager circleManager=new CirclesManager();
//    <绘制模式>1:全绘制 2:绘制动的 ----4:绘制不动的 8:绘制最新的
    public int drawMode=1;
//    <触摸模式>1:触摸一个最近的 2:触摸点范围内的 ----4:全触摸
    public int touchMode=1;
    public float distance=200;
//    <是否让球飞>
    public boolean isFly=false;

//    <绘制画面间隔>
    public int drawPeriod=getContext()
        .getSharedPreferences("name",Context.MODE_PRIVATE).getInt(
            "CircleView.drawPeriod",16);
//    ------------------------------------

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        int time=0;
        switch (drawMode) {
            case 1://画所有的小球
                time+=circleManager.Draw(canvas);
                break;
            case 2://画被触摸和在飞行的小球
                time+=circleManager.Draw(canvas, circleManager.getCirclesIsFly(true));
                time+=circleManager.Draw(canvas, circleManager.getCirclesIsTouch(true));
                break;
        }
        postInvalidateDelayed((drawPeriod-time)>0?(drawPeriod-time):1);
        super.onDraw(canvas);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (touchMode) {
            case 1://触摸最近的
                Circle circle=(circleManager.getCircleNearliest(
                        new Point((int)event.getX(),(int)event.getY()),true)
                );
                if (circle == null) {
                    break;
                }
                circleManager.moveByTouch(new Circle[]{circle}
                        ,event,isFly);
                break;
            case 2://触摸distance以内的
                circleManager.moveByTouch(circleManager.getCirclesInDistance(
                        new Point((int) event.getX(), (int) event.getY()
                        ), distance),event,isFly);
                break;
            case 4:// TODO: 2021/3/16
        }
        return true;
    }
}
