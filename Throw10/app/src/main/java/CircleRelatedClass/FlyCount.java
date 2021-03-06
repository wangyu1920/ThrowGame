package CircleRelatedClass;

import android.content.SharedPreferences;
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
    private int num=100;
//    纳入计算的路径集合
    private PathManager pathManager=new PathManager();
    //    小球速度系数，决定整体运动快慢
    protected double vRate=0.3;
//    计算一定次数后将pathKnocked变为null用到的计数变量，只建议修改第一条
    private int reSetPathKnockedWhenNumIs=50;
    private long countNum=1000;
    private long readCountNum=1000;

//    PointInPath的缓存
    PathWithMode old;
    RectF bounds;
    Region region;

//    ________________________________________________

    protected void setParameter(SharedPreferences preferences) {
        if (preferences == null) {
            return;
        }
        r = preferences.getFloat("Circle.r", 50);
        fx = preferences.getInt("Circle.fx", 0);
        fy = preferences.getInt("Circle.fy", 0);
        m = preferences.getInt("Circle.m", 50);
        num = preferences.getInt("Circle.num", 100);
        vRate = preferences.getFloat("Circle.vRate", (float) 0.3);
        reSetPathKnockedWhenNumIs = preferences.getInt("Circle.period", 50);

    }

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
        return angle;
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
        if (path!=old) {
            path.close();
            old=path;
            bounds = new RectF();
            path.computeBounds(bounds, true);
            region = new Region();
            region.setPath(path, new Region((int) bounds.left, (int) bounds.top, (int) bounds.right, (int) bounds.bottom));
        }
        return region.contains(point.x, point.y);
    }
//  和measure()方法对应的用来判断小球撞击长方形path方向的方法
//    测的是点相对path中心的弧度
    private double getKnockDirection(PathWithMode pathWithMode,Point thePoint) {
        pathWithMode.close();
        RectF bounds = new RectF();
        pathWithMode.computeBounds(bounds, true);
        float left=Math.abs(thePoint.x - bounds.left);
        float top = Math.abs(thePoint.y - bounds.top);
        float right = Math.abs(thePoint.x - bounds.right);
        float bottom = Math.abs(thePoint.y - bounds.bottom);
        float dy=point.y-thePoint.y;
        float dx=point.x-thePoint.x;
        float min = Math.min(left, Math.min(top, Math.min(right, bottom)));
        if (min==left) {
            if (bottom - left < 6) {
                if (-dx > dy) {
                    return Math.PI;
                }
                return Math.PI/2;
            }
            if (top - left < 6) {
                if (-dx > -dy) {
                    return Math.PI;
                }
                return Math.PI*1.5;
            }
            return Math.PI;
        } else if (min==top) {
            if (left-top < 6) {
                if (-dx > -dy) {
                    return Math.PI;
                }
                return Math.PI*1.5;
            }
            if (right - top < 6) {
                if (dx > -dy) {
                    return 0;
                }
                return Math.PI*1.5;
            }
            return Math.PI*1.5;
        } else if (min==right) {
            if (top-right < 6) {
                if (dx > -dy) {
                    return 0;
                }
                return Math.PI*1.5;
            }
            if (bottom-right < 6) {
                if (dx > dy) {
                    return 0;
                }
                return Math.PI*1.5;
            }
            return 0;
        } else {
            if (right-bottom < 6) {
                if (dx > dy) {
                    return 0;
                }
                return Math.PI*1.5;
            }
            if (left-bottom < 6) {
                if (-dx > dy) {
                    return Math.PI;
                }
                return Math.PI/2;
            }
            return Math.PI*0.5;
        }

    }

//  如果小球标志点触碰了path,则根据其mode执行相应操作,并返回该path,没有则返回null
    public PathWithMode countOfPaths() {
        if (pathManager.getNum() == 0) {
            return null;
        }
        PathWithMode[] paths=pathManager.getPathNeedCount(point,r,50);
        if (paths == null) {
            return null;
        }
        if (countNum % readCountNum > reSetPathKnockedWhenNumIs) {
            pathKnocked=null;
        }
        for (PathWithMode path : paths) {
            if (!path.isCircle) {
                double radian;
                for (int i = 0; i < 4; i++) {
                    Point pointNeeded = new Point((int) (point.x + r*Math.cos(Math.PI*(0.5*i))),
                            (int) (point.y + r*Math.sin(Math.PI*(0.5*i))));
                    if (pointInPath(path, pointNeeded) &&
                            (path.doNotEquals(pathKnocked))) {
                        radian = getKnockDirection(path, pointNeeded);
                        switch (path.mode) {
                            case 1://原速率
                                reflect(radian-Math.PI/2, -1, 1);
                                break;
                            case 2://百分比
                                reflect(radian-Math.PI/2, path.rateY, path.rateX);
                                break;
                            case 3://骤停
                                vx0 = 0;
                                vy0 = 0;
                                isFly = false;
                                break;
                            case 4:// TODO: 2021/3/11

                        }
                        return path;
                    }
                }
                radian = measure(path.point)+Math.PI/2;
                for (int i = 0; i < num;i++) {
                    Point thePoint = new Point(point.x + (int) (r * Math.cos(radian+i*Math.PI/num)),
                            point.y + (int) (r * Math.sin(radian+Math.PI * i / num)));
                    if (pointInPath(path, thePoint) &&
                            (path.doNotEquals(pathKnocked))) {
                        radian = getKnockDirection(path,thePoint);
                        switch (path.mode) {
                            case 1://原速率
                                reflect(radian-Math.PI/2, -1, 1);
                                break;
                            case 2://百分比
                                reflect(radian-Math.PI/2, path.rateY, path.rateX);
                                break;
                            case 3://骤停
                                vx0 = 0;
                                vy0 = 0;
                                isFly = false;
                                break;
                            case 4:// TODO: 2021/3/11

                        }
                        return path;
                    }
                }
            } else {
                double radian = measure(path.point);
                Point thePoint = new Point(point.x + (int) (r * Math.cos(-radian)),
                        point.y + (int) (r * Math.sin(-radian)));
                if (pointInPath(path, thePoint) &&
                        (path.doNotEquals(pathKnocked))) {
                    switch (path.mode) {
                        case 1://原速率
                            reflect(radian-Math.PI/2, -1, 1);
                            break;
                        case 2://百分比
                            reflect(radian-Math.PI/2, path.rateY, path.rateX);
                            break;
                        case 3://骤停
                            vx0 = 0;
                            vy0 = 0;
                            isFly = false;
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
