package CircleRelatedClass;


import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.Log;
import android.view.MotionEvent;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Objects;

import PathRelatedClass.PathWithMode;

@SuppressLint("LongLogTag")
public class CirclesManager{
    /*小球管理类,实际上是存放小球的Map
    * ====方法====
    * <增删小球>
    * int getNumberOfCircles()--返回小球数目
    * int addCircle(int x, int y)|(Point point, float r, int colorOfCircle, int m)--增加一个小球,返回其key,如果指定坐标有小球了则不添加并返回0
    * void deleteCircle()|(Circle circle)--删除指定小球，未指定则删除不在屏幕中的小球
    * <得到小球数组/小球>
    * Circle[] getCirclesContainPoint(Point point)
    * Circle[] getCirclesInPath(Path path)
    * Circle[] getCirclesInDistance(Point point, float distance)
    * Circle[] getCirclesIsTouch(boolean b)
    * Circle[] getCirclesIsFly(boolean b)
    * Circle getCircleNearliest(Point point)
    * Circle getCircleByPoint(Point point)
    * Circle getCircleNew()
    * Circle getCircleByKey(int key)
    * <触摸移动指定小球>
    * void moveByTouch(int key, MotionEvent event,boolean b)|(Circle[] circles, MotionEvent event, boolean b)
    * <(以指定颜色)绘制(指定/所有)小球>
    * int Draw(Canvas canvas)|(Canvas canvas,Circle[] circles)|(Canvas canvas,Circle circle,int color)
    * */
    private Integer numberOfCircles=0;
    private HashMap<String, Circle> circleHashMap = new HashMap<>();
    Iterator valuesIterator=circleHashMap.values().iterator();
    Iterator keyIterator=circleHashMap.keySet().iterator();

    //    -------getter----------------------------

    public int getNumberOfCircles() {
        return numberOfCircles;
    }

    public HashMap<String, Circle> getCircleHashMap() {
        return circleHashMap;
    }

    public void setCircleHashMap(HashMap<String, Circle> circleHashMap) {
        this.circleHashMap = circleHashMap;
    }

    //    -----------tools-------------------------------------------------
    private void refreshIterator() {
        valuesIterator=circleHashMap.values().iterator();
        keyIterator=circleHashMap.keySet().iterator();
    }
    private boolean pointInPath(Path path, Point point) {
        path.close();
        RectF bounds = new RectF();
        path.computeBounds(bounds, true);
        Region region = new Region();
        region.setPath(path, new Region((int) bounds.left, (int) bounds.top, (int) bounds.right, (int) bounds.bottom));
        return region.contains(point.x, point.y);
    }
//-----------------增/删小球----------------------------------------
    //  增加一个小球,返回其key,如果指定坐标有小球了则不添加并返回0
     public int addCircle(int x, int y) {
        if (getCircleByPoint(new Point(x, y)) != null) {
            return 0;
        }
        numberOfCircles++;
        circleHashMap.put(numberOfCircles.toString(),new Circle(x,y));
        refreshIterator();
        return numberOfCircles;
    }
    public int addCircle(int x, int y,SharedPreferences preferences) {
        if (getCircleByPoint(new Point(x, y)) != null) {
            return 0;
        }
        numberOfCircles++;
        circleHashMap.put(numberOfCircles.toString(),new Circle(x,y,preferences));
        refreshIterator();
        return numberOfCircles;
    }

    public int addCircle(Circle circle) {
        if (getCircleByPoint(circle.getPoint()) != null) {
            return 0;
        }
        numberOfCircles++;
        circleHashMap.put(numberOfCircles.toString(),circle);
        refreshIterator();
        return numberOfCircles;
    }


    public int addCircle(Point point, float r, int colorOfCircle, int m) {
        if (getCircleByPoint(point) != null) {
            return 0;
        }
        numberOfCircles++;
        circleHashMap.put(numberOfCircles.toString(),new Circle(point,r,colorOfCircle,m));
        refreshIterator();
        return numberOfCircles;
    }
//  删除指定小球，未指定则删除不在屏幕中的小球
    public void deleteCircle(Circle circle) {
        refreshIterator();
        String key;
        while (keyIterator.hasNext()) {
            key= (String) keyIterator.next();
            if (Objects.equals(circleHashMap.get(key), circle)) {
                circleHashMap.remove(key);
            }
        }
        refreshIterator();
    }
//    运用了递归来防止java.util.ConcurrentModificationException（删除元素时Iterator.hasNext()为True但.next()没东西了）
    public void deleteCircleIfNeeded() {
        refreshIterator();
        while (keyIterator.hasNext()) {
            String key= (String) keyIterator.next();
            Circle circle=(Circle) circleHashMap.get(key);
            if (circle!=null&&!circle.isExist()) {
                circle.threadOfCount.interrupt();
                circleHashMap.remove(key);
                deleteCircleIfNeeded();
            }
        }
        refreshIterator();
    }
    public void deleteCircle() {
        circleHashMap.clear();
        refreshIterator();
    }
//------------------找小球(多个)(一个)--------------------------------------

    //  返回所有小球
    public Circle[] getCirclesAll() {
        return circleHashMap.values().toArray(new Circle[0]);
    }

//  遍历，判断是否有小球包含这个点，返回这些小球的数组，没有返回null
    public Circle[] getCirclesContainPoint(Point point) {
        refreshIterator();
        Circle[] circles=new Circle[numberOfCircles+1];
        byte b=0;
        byte i=0;
        while (valuesIterator.hasNext()) {
            Circle circle = (Circle) valuesIterator.next();
            if (circle.pointInCircle(point)) {
                circles[b]=circle;
                ++b;
            }
        }
        if (b==0) {
            return null;
        }
        Circle[] newCircles=new Circle[b];
        while (i < b) {
            newCircles[i] = circles[i];
            i++;
        }
        return newCircles;
    }
    //  遍历，判断是否有小球在这个路径内，返回这些小球的数组，没有返回null
    public Circle[] getCirclesInPath(Path path) {
        refreshIterator();
        Circle[] circles=new Circle[numberOfCircles+1];
        byte b=0;
        byte i=0;
        while (valuesIterator.hasNext()) {
            Circle circle = (Circle) valuesIterator.next();
            if (pointInPath(path, circle.getPoint())) {
                circles[b]=circle;
                b++;
            }
        }
        if (b==0) {
            return null;
        }
        Circle[] newCircles=new Circle[b];
        while (i < b) {
            newCircles[i] = circles[i];
            i++;
        }
        return newCircles;
    }

    //  遍历，判断是否有小球在这个圆形范围内，返回这些小球的数组，没有返回null
    public Circle[] getCirclesInDistance(Point point, float distance) {
        Path path = new Path();
        path.addCircle(point.x,point.y,distance,Path.Direction.CCW);
        return getCirclesInPath(path);
    }
//    返回正在被触摸(true)/没有触摸(false)的小球数组，没有则null
    public Circle[] getCirclesIsTouch(boolean b) {
        refreshIterator();
        Circle[] circles=new Circle[numberOfCircles+1];
        byte num=0;
        byte i=0;
        while (valuesIterator.hasNext()) {
            Circle circle = (Circle) valuesIterator.next();
            if (circle.isTouch()==b) {
                circles[num]=circle;
                num++;
            }
        }
        if (num==0) {
            return null;
        }
        Circle[] newCircles=new Circle[num];
        while (i < num) {
            newCircles[i] = circles[i];
            i++;
        }
        return newCircles;
    }

    //    返回正在飞(true)/没有飞(false)的小球数组，没有则null
    public Circle[] getCirclesIsFly(boolean b) {
        refreshIterator();
        Circle[] circles=new Circle[numberOfCircles+1];
        byte num=0;
        byte i=0;
        while (valuesIterator.hasNext()) {
            Circle circle = (Circle) valuesIterator.next();
            if (circle.isFly()==b) {
                circles[num]=circle;
                num++;
            }
        }
        if (num==0) {
            return null;
        }
        Circle[] newCircles=new Circle[num];
        while (i < num) {
            newCircles[i] = circles[i];
            i++;
        }
        return newCircles;
    }
//  返回距离该点最近(b=true:且距离小于该球半径)的球；没有则返回null
    public Circle getCircleNearliest(Point point,boolean b) {
        refreshIterator();
        float minDistance=10000;
        float newDistance;
        Circle aim=null;
        while (valuesIterator.hasNext()) {
            Circle circle=((Circle)valuesIterator.next());
            newDistance=circle.distanceOfPoint(point);
            if ((circle.getR() >newDistance|!b) &&minDistance>newDistance) {
                minDistance=newDistance;
                aim=circle;
            }
        }
        return aim;
    }
    //  返回位于该点的球；没有则返回null
    public Circle getCircleByPoint(Point point) {
        refreshIterator();
        while (valuesIterator.hasNext()) {
            Circle aim=((Circle) valuesIterator.next());
            if (aim.getPoint() == point) {
                return aim;
            }
        }
        return null;
    }
//  返回最近一次生成的Circle
    public Circle getCircleNew() {
        return circleHashMap.get(numberOfCircles.toString());
    }
//    根据key返回小球
    public Circle getCircleByKey(int key) {
        return circleHashMap.get(String.valueOf(key));
    }

    //-------------移动小球--------------------------------------
    public void moveByTouch(int key, MotionEvent event,boolean b) {
        circleHashMap.get(String.valueOf(key)).moveByTouch(event,b);
    }
    public void moveByTouch(Circle[] circles, MotionEvent event, boolean b) {
        if (circles[0] == null) {
            Log.e("CirclesManager>moveByTouch>circles","null");
            return;
        }
        for (int i = circles.length-1; i >= 0; i--) {
            circles[i].moveByTouch(event,b);
        }
    }
// 刷新参数
    public void resetParameter(SharedPreferences preferences) {
        for (Circle circle : circleHashMap.values()) {
            circle.setParameter(preferences);
        }
    }
//    -------------画小球----------------------------------
    public int Draw(Canvas canvas) {
        refreshIterator();
        int time=0;
        while (valuesIterator.hasNext()) {
            time+=((Circle) valuesIterator.next()).DrawCircle(canvas);
        }
        return time;
    }
    public int Draw(Canvas canvas,Circle[] circles) {
        if (circles[0] == null) {
            return 0;
        }
        int time=0;
        for (int i = circles.length-1; i >= 0; i--) {
            time+=circles[i].DrawCircle(canvas);
        }
        return time;
    }
//    将小球以指定颜色绘制,无论小球本身是什么颜色
    public int Draw(Canvas canvas,Circle circle,int color) {
        if (circle == null) {
            return 0;
        }
        int time=0;
        int color0=circle.getColorOfCircle();
        circle.setColorOfCircle(color);
        circle.DrawCircle(canvas);
        circle.setColorOfCircle(color0);
        return time;
    }
}
