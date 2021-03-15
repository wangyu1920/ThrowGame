package CircleRelatedClass;

import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Region;

import PathRelatedClass.PathManager;
import PathRelatedClass.PathWithMode;

public class FlyCount implements PhysicalParameter{
//    计算斜抛所用到的参数
    double vx0;
    double vy0;
    private int x0;
    private int y0;
    private long t0;
    protected boolean isFly=false;
    protected boolean isKnock=false;
    protected PathWithMode pathKnocked;
//    外力
    private int fx=0;
    private int fy=0;
//    小球质量
    public int m=50;
//    小球半径和坐标
    public float r=50;
    public Point point=new Point(0,0);
//    将圆形打破成点（标志点）的数量，决定了碰撞计算精度
    private int num=126;
//    纳入计算的路径集合
    private PathManager pathManager=new PathManager();
    //    小球速度系数，决定整体运动快慢
    protected double vRate=0.4;
//    计算一定次数后将pathKnocked变为null用到的计数变量
    private long countNum=1000;
    private long readCountNum=1000;

//    ________________________________________________

    public void setVRate(double vRate) {
        this.vRate = vRate;
    }
    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getFx() {
        return fx;
    }

    public void setFx(int fx) {
        this.fx = fx;
    }

    public int getFy() {
        return fy;
    }

    public void setFy(int fy) {
        this.fy = fy;
    }

    //__________________________________________________
    private Point[] getPointOfCircle() {
        Point[] points=new Point[num];
        for (byte i = 0; i < num;i++) {
            points[i]=new Point(point.x+(int)(r*Math.cos(2*Math.PI*i/360)),
                    point.y + (int) (r*Math.sin(2*Math.PI*i/num)));
        }
        return points;
    }
    //    测出小球相对指定点的弧度
    private double measure(Point point0) {
        float dy=point.y-point0.y;
        float dx=point.x-point0.x;
        float dl= (float) Math.sqrt(dx * dx + dy * dy);
        double sin= dy / dl;
        double cos= dx / dl;
        double angle;
        double angleOfSin = Math.asin(sin);
        double angleOfCos = Math.acos(cos);
        if (dy > 0 && dx < 0) {
            angle=angleOfCos;
        } else if (dy > 0 && dx > 0) {
            angle =angleOfSin;
        } else {
            angle=2*Math.PI-angleOfCos;
        }
        return angle-Math.PI/2;
    }

    private void reflect(double radian,float rateY,float rateX) {
        long t = System.currentTimeMillis();
        double vx= (vx0+(ax+((double)fx)/m) * (t - t0)/100);
        double vy= (vy0+(ay+((double)fy)/m) * (t - t0)/100);
        double vx1 = (vx * Math.cos(radian)+vy*Math.sin(radian))*rateX;
        double vy1 = (vx * -Math.sin(radian)+vy*Math.cos(radian))*rateY;
        vx0 = (vx1 * Math.cos(-radian)+vy1*Math.sin(-radian));
        vy0 = (vx1 * -Math.sin(-radian)+vy1*Math.cos(-radian));
        x0 = point.x;
        y0 = point.y;
        t0 = t;
    }
    private boolean pointInPath(PathWithMode path, Point point) {
        if (path.isCircle&&path.distanceOfPoint(this.point)<path.r+r) {
            return true;
        }
        path.close();
        RectF bounds = new RectF();
        path.computeBounds(bounds, true);
        Region region = new Region();
        region.setPath(path, new Region((int) bounds.left, (int) bounds.top, (int) bounds.right, (int) bounds.bottom));
        return region.contains(point.x, point.y);
    }

    private double getKnockDirection(PathWithMode pathWithMode,Point thePoint) {
        pathWithMode.close();
        RectF bounds = new RectF();
        pathWithMode.computeBounds(bounds, true);
        if (Math.abs(thePoint.x - bounds.left) < 6) {
            return Math.PI/2;
        } else if (Math.abs(thePoint.y - bounds.top) < 6) {
            return 0;
        } else if (Math.abs(thePoint.x - bounds.right) < 6) {
            return -Math.PI/2;
        } else {
            return Math.PI;
        }

    }


//  如果小球标志点触碰了path,则根据其mode执行相应操作,并返回该path,没有则返回null
    public PathWithMode countOfPaths() {
        if (pathManager.getNum() == 0) {
            return null;
        }
        PathWithMode[] paths=pathManager.getPathNeedCount(point,r,10);
        if (paths == null) {
            return null;
        }
        if (countNum % readCountNum > 500) {
            pathKnocked=null;
        }
        for (byte i = 0; i < num;i++) {
            Point thePoint=new Point(point.x+(int)(r*Math.cos(2*Math.PI*i/360)),
                    point.y + (int) (r*Math.sin(2*Math.PI*i/num)));
            for (PathWithMode path : paths) {
                if (pointInPath(path, thePoint)&&
                        (!path.equal(pathKnocked))) {
                    double radian;
                    if (path.isCircle) {
                        radian = measure(path.point);
                    } else {
                        radian = getKnockDirection(path,thePoint);
                    }
                    switch (path.mode) {
                        case 1://原速率
                            reflect(radian,-1,1);
                            break;
                        case 2://百分比
                            reflect(radian,path.rateY,path.rateX);
                            break;
                        case 3://骤停
                            vx0=0;
                            vy0=0;
                            isFly=false;
                            break;
                        case 4:// TODO: 2021/3/11

                    }
                    return path;
                }
            }
        }
        return null;
    }

//    -------------------------------------------------
    public void createFlyCount(float velocity0, float angleOfThrow, Point point, int fx, int fy, int mm) {
        this.x0 = point.x;
        this.y0 = point.y;
        this.fx = fx;
        this.fy = fy;
        this.m = mm;
        vx0= (velocity0*Math.cos(angleOfThrow));
        vy0= (velocity0*Math.sin(angleOfThrow));
    }

    public void createFlyCount(float velocity0, float angleOfThrow, Point point0) {
        this.x0 = point0.x;
        this.y0 = point0.y;
        this.t0 = System.currentTimeMillis();
        vx0= (velocity0*Math.cos(angleOfThrow));
        vy0= (velocity0*Math.sin(angleOfThrow));
    }

    public void createPathManager(PathManager pathManager) {
        this.pathManager=pathManager;
    }
    public Point count() {
        long t = System.currentTimeMillis();
        int x = (int) (x0 + vx0 * (t - t0) / 100*vRate
                + 0.5 * (ax+fx/m) * (t - t0) * (t - t0) /  10000*vRate);
        int y = (int) (y0 + vy0* (t - t0)/100*vRate
                       +0.5 * (ay+fy/m) * (t - t0) * (t - t0)/10000*vRate );
        if (pathManager.getNum()!=0) {
            PathWithMode newPathKnocked =countOfPaths();
            countNum++;
            if (newPathKnocked != null) {
                isKnock=true;
                pathKnocked=newPathKnocked;
                readCountNum=countNum;
            }
        }
        return new Point(x,y);
    }


}
