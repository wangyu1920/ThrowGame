package PathRelatedClass;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import CircleRelatedClass.Circle;

public class PathManager {
    HashMap<String, PathWithMode> pathHashMap = new HashMap<>();

    //    -----------------------------------------
    public int getNum() {
        return pathHashMap.size();
    }
//    增删Path
    public void addPath(PathWithMode pathWithMode) {
        if (pathHashMap.containsKey(pathWithMode.getName())) {
            return;
        }
        pathHashMap.put(pathWithMode.getName(), pathWithMode);
    }

    public void addPath() {

    }

    public void removePath(String name) {
        pathHashMap.remove(name);
    }

    public void removePath(boolean isCircle) {
    }

    public void removePath() {
        pathHashMap.clear();
    }

    public PathWithMode[] getAll() {
        return pathHashMap.values().toArray(new PathWithMode[0]);
    }

    //    返回与参数Point距离小于Path.r+r+范围d的Path集合，用作FlyCount的计算
    public PathWithMode[] getPathNeedCount(Point point,float r,float d) {
        Iterator<PathWithMode> valuesIterator=pathHashMap.values().iterator();
        PathWithMode[] paths=new PathWithMode[pathHashMap.size()];
        byte num=0;
        byte i=0;
        while (valuesIterator.hasNext()) {
            PathWithMode path = valuesIterator.next();
            if (path.distanceOfPoint(point)<r+path.r+d) {
                paths[num]=path;
                num++;
            }
        }
        if (num==0) {
            return null;
        }
        PathWithMode[] newPaths=new PathWithMode[num];
        while (i < num) {
            newPaths[i] = paths[i];
            i++;
        }
        return newPaths;
    }

    public PathWithMode getOne(String name) {
        return pathHashMap.get(name);
    }


    //    --------------------------------------
    public void draw(Canvas canvas) {
        Paint paint=new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.YELLOW);
        for (PathWithMode pathWithMode : pathHashMap.values()) {
            canvas.drawPath(pathWithMode, paint);
        }
    }
}
