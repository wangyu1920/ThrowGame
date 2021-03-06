package com.example.throw10;

import android.graphics.Point;
import android.util.Log;

public class FlyCount implements PhysicalParameter{
    private float velocity0;
    private float angleOfThrow;
    private int x0;
    private int y0;
    private long t0;
    private int fx=0;
    private int fy=0;
    public int m=50;
//    ________________________________________________


    public void createFlyCount(float velocity0, float angleOfThrow, Point point, int fx, int fy, int mm) {
        this.velocity0 = velocity0;
        this.angleOfThrow = angleOfThrow;
        this.x0 = point.x;
        this.y0 = point.y;
        this.fx = fx;
        this.fy = fy;
        this.m = mm;
    }

    public void createFlyCount(float velocity0, float angleOfThrow, Point point0) {
        this.velocity0 = velocity0;
        this.angleOfThrow = angleOfThrow;
        this.x0 = point0.x;
        this.y0 = point0.y;
        this.t0 = System.currentTimeMillis();
    }

    public Point count() {
        long t = System.currentTimeMillis();
        int x = (int) (x0 + velocity0 * Math.cos(angleOfThrow) * (t - t0) / 100
                + 0.5 * (ax+fx/m) * (t - t0) * (t - t0) /  10000);
        int y = (int) (y0 + velocity0 * Math.sin(angleOfThrow) * (t - t0)/100
                       +0.5 * (ay+fy/m) * (t - t0) * (t - t0)/10000 );
        return new Point(x,y);
    }
}
