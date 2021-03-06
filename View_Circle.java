package com.example.throw10;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;


public class View_Circle extends FlyCount implements DeviceInform{
    private Point point=new Point(0,0);
    private float r=80;
    private int ColorOfCircle= Color.parseColor("#ff000000");
    private boolean isFly=false;
    public boolean isExist=true;
    Paint paint;
    Point point0;
//—————————————getter and setter—————————————————————————————————————————————

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public float getR() {
        return r;
    }

    public void setR(float r) {
        this.r = r;
    }

    public int getColorOfCircle() {
        return ColorOfCircle;
    }

    public void setColorOfCircle(int colorOfCircle) {
        ColorOfCircle = colorOfCircle;
    }

    public int getM() {
        return m;
    }

    public void setM(int m) {
        super.m = m;
    }

    public boolean isFly() {
        return isFly;
    }

    public void Fly(Point point0) {
        isFly = true;
        float[] floats=measure(point0);
        createFlyCount(floats[2], (float) (floats[1]+Math.PI),point0);
    }

//    ----------constructor------------------------------------

    public View_Circle(int x,int y) {
        this.point = new Point(x,y);
    }

    public View_Circle(Point point) {
        this.point = point;
    }
    public View_Circle(Point point, float r, int colorOfCircle, int m) {
        this.point = point;
        this.r = r;
        ColorOfCircle = colorOfCircle;
        this.m = m;
    }

    //    _________method______________________________________
    private float[] measure(Point point0) {
        float dy=point.y-point0.y;
        float dx= point.x-point0.x;
        float dl= (float) Math.sqrt(dx * dx + dy * dy);
        double sin= dy / dl;
        double cos= dx / dl;
        double angle;
        double radian;
        double angleOfSin = 180*Math.asin(sin)/Math.PI;
        double angleOfCos = 180*Math.acos(cos)/Math.PI;
        if (dy > 0 && dx < 0) {
            angle=angleOfCos;
        } else if (dy > 0 && dx > 0) {
            angle =angleOfSin;
        } else {
            angle=360-angleOfCos;
        }
        radian=angle*Math.PI/180;
        return new float[]{(float) angle, (float) radian,dl};
    }
    public void moveByFly() {
        if (!isFly()) {
            return;
        }
        point=count();
    }
    public void moveByTouch(MotionEvent event) {
        if (isFly()) {
            return;
        }
        if (point0 == null) {
            point0=point;
        }
        float x=event.getX();
        float y=event.getY();
        if (Math.abs(x - point.x) + Math.abs(y - point.y) > 100) {
            return;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                 point0=new Point(point.x,point.y);
            case MotionEvent.ACTION_MOVE:
                point.x = (int) x;
                point.y = (int) y;
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(x - point0.x) + Math.abs(y - point0.y) < 150) {
                    return;
                }
                Fly(point0);
        }
    }
    public void DrawCircle(Canvas canvas) {
        if (paint == null) {
            paint = new Paint();
            paint.setColor(getColorOfCircle());
        }
        moveByFly();
        if (point.y > device_height) {
            isFly=false;
            isExist=false;
        }
        canvas.drawCircle(point.x,point.y,r,paint);
    }

}
