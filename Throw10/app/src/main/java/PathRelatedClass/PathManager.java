package PathRelatedClass;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.Hashtable;
import java.util.Iterator;

import CircleRelatedClass.Circle;

public class PathManager {
    Hashtable<String, PathWithMode> pathHashtable = new Hashtable<>();

    //    -----------------------------------------
    public int getNum() {
        return pathHashtable.size();
    }
    public void addPath(PathWithMode pathWithMode) {
        if (pathHashtable.containsKey(pathWithMode.getName())) {
            return;
        }
        pathHashtable.put(pathWithMode.getName(), pathWithMode);
    }

    public void removePath(String name) {
        pathHashtable.remove(name);
    }

    public void removePath() {
        pathHashtable.clear();
    }

    public PathWithMode[] getAll() {
        return pathHashtable.values().toArray(new PathWithMode[0]);
    }

    //    返回与参数Point距离小于Path.r+r+范围d的Path集合，用作FlyCount的计算
    public PathWithMode[] getPathNeedCount(Point point,float r,float d) {
        Iterator<PathWithMode> valuesIterator=pathHashtable.values().iterator();
        PathWithMode[] paths=new PathWithMode[pathHashtable.size()];
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
        return pathHashtable.get(name);
    }


    //    --------------------------------------
    public void draw(Canvas canvas) {
        Paint paint=new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.YELLOW);
        for (PathWithMode pathWithMode : pathHashtable.values()) {
            canvas.drawPath(pathWithMode, paint);
        }
    }
}
