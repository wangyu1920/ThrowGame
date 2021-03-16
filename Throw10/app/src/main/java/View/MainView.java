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
import PathRelatedClass.PathManager;
import PathRelatedClass.PathWithMode;

public class MainView extends View {
    public PathManager pathManager=new PathManager();
    public CirclesManager circleManager=new CirclesManager();
//    <绘制模式>1:全绘制 2:绘制动的 ----4:绘制不动的 8:绘制最新的
    public int drawMode=1;
//    <触摸模式>1:触摸一个最近的 2:触摸点范围内的 ----4:全触摸
    public int touchMode=1;
    public float distance=100;
//    <是否让球飞>
    public boolean isFly=false;
//    画布大小参数
    private int height=0;
    private int width=0;
//    ------------------------------------

    public MainView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        if (height == 0) {
            height=getHeight();
            width=getWidth();
        }
        if (pathManager.getOne("bound") == null) {
            PathWithMode pathWithMode=new PathWithMode("bound",
                    new Point(height+1,
                            width/2),
                    2000,false);
            pathWithMode.moveTo(-5,height);
            pathWithMode.lineTo(width+5,height);
            pathWithMode.lineTo(width+5,height+15);
            pathWithMode.lineTo(-5,height+15);
            pathWithMode.lineTo(-5,height);
            pathWithMode.close();
            pathManager.addPath(pathWithMode);
        }
        int time=0;
        pathManager.draw(canvas);
        switch (drawMode) {
            case 1://画所有的小球
                time+=circleManager.Draw(canvas);
                break;
            case 2://画被触摸和在飞行的小球
                time+=circleManager.Draw(canvas, circleManager.getCirclesIsFly(true));
                time+=circleManager.Draw(canvas, circleManager.getCirclesIsTouch(true));
                break;
        }
        postInvalidateDelayed((16-time)>0?(16-time):1);
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
