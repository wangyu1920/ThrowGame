package PathRelatedClass;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Point;

public class PathWithMode extends Path {
    //    <碰撞模式>1:原速率反弹 2:百分比速率反弹 3:骤停 4:缓停
    public int mode=1;
    public float rateY= (float) -0.7;
    public float rateX= (float) 1;
    private final String name;
    public boolean isCircle;
//    颜色
    public int color= Color.RED;
//    是圆形则为圆心，否则为几何中心
    public Point point;
//    是圆形则是半径，否则是影响范围，PathManager类会依据这个查找Path
    public float r;

    public void setParameter(SharedPreferences preferences) {
        color = preferences.getInt("Path.color",Color.YELLOW);
        mode = preferences.getInt("Path.mode", 1);
        rateX = preferences.getFloat("Path.rateX", 1);
        rateY = preferences.getFloat("Path.rateY", (float) -0.7);
    }
    public PathWithMode(String name, Point point, float r, boolean isCircle,
                        SharedPreferences preferences) {
        this(name, point, r, isCircle);
        if (preferences == null) {
            return;
        }
        setParameter(preferences);
    }
    public PathWithMode(String name, Point point, float r ,boolean isCircle) {
        super();
        this.point=point;
        this.r=r;
        this.name=name;
        this.isCircle=isCircle;
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

    public boolean doNotEquals(PathWithMode pathWithMode) {
        if (pathWithMode == null) {
            return true;
        }
        return !this.name.equals(pathWithMode.name);
    }
    public String getName() {
        return name;
    }

}
