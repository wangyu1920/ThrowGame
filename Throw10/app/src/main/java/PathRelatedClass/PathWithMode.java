package PathRelatedClass;

import android.graphics.Path;
import android.graphics.Point;


public class PathWithMode extends Path {
    //    <碰撞模式>1:原速率反弹 2:百分比速率反弹 3:骤停 4:缓停
    public int mode=1;
    public float rateY= (float) -0.7;
    public float rateX= (float) 1;
    private final String name;
    public boolean isCircle;
//    是圆形则为圆心，否则为几何中心
    public Point point;
//    是圆形则是半径，否则是影响范围，PathManager类会依据这个查找Path
    public float r;
    public PathWithMode(String name, Point point, float r ,boolean b) {
        super();
        this.point=point;
        this.r=r;
        this.name=name;
        this.isCircle=b;
    }
    //    返回点与球心的距离
    public float distanceOfPoint(Point p) {
        float dy=point.y-p.y;
        float dx= point.x-p.x;
        return  (float) Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public String toString() {
        return  name;
    }

    public boolean equal(PathWithMode pathWithMode) {
        if (pathWithMode == null) {
            return false;
        }
        return this.name.equals(pathWithMode.name);
    }
    public String getName() {
        return name;
    }

}
