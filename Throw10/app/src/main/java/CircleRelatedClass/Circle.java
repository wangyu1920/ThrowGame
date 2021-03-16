package CircleRelatedClass;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Region;
import android.view.MotionEvent;
/*小球类
* 继承飞行计算类FlyCount，设备信息常量接口DeviceInform,物理参数接口PhysicalParameter
* 可修改的参数：坐标Point；半径r；颜色ColorOfCircle；质量m；额外恒力fx,fy；
* 小球状态参数：isFly()；isTouch()；isExist()；getPoint()
* 构造方法：第67行
* 其他方法：void moveByTouch(MotionEvent，Boolean):移动小球通过触摸,Boolean决定是否让小球飞行
*         int drawCircle(Canvas):如果小球存在于屏幕，绘制小球；返回绘制时间
*         void forceFly(float velocity0, float angleOfThrow, Point point0):强制小球从该位置飞出
*         boolean pointInCircle(Point):判断点是否在球内
*         float distanceOfPoint(Point):返回点与球心的距离
* */

public class Circle extends FlyCount{
    int device_width=0;
    int device_height=0;
    private int ColorOfCircle= Color.parseColor("#ff000000");
    private int ColorOfRope = Color.parseColor("#ff0000ff");
    private boolean isTouch=false;
    private boolean isExist=true;
    private boolean willFly=false;
    Paint paint;
    Point point0;
    Thread threadOfCount= new Thread(
            () -> {
                isFly = true;
                float[] floats=measure(point0);
                createFlyCount(floats[2], (float) (floats[1]+Math.PI),point0);
                while (true) {
                    if (isFly) {
                        if (point.y > device_height||point.x>device_width|point.x<-r) {
                            isFly=false;
                            isExist=false;
                            return;
                        }
                        point=count();

                    }
                    try {
                        wait(10);
                    } catch (Exception ignore) {

                    }
                }
            }
    );
//—————————————getter and setter—————————————————————————————————————————————


    public boolean isExist() {
        return isExist;
    }

    public boolean isTouch() {
        return isTouch;
    }

    public void setTouch(boolean touch) {
        isTouch = touch;
    }

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

    public void setColorOfRope(int colorOfRope) {
        ColorOfRope = colorOfRope;
    }
//    ----------constructor------------------------------------

//    2个构造方法，在指定位置产生一个小球；默认值：半径r=80；颜色=黑色；质量=50
    public Circle(int x, int y) {
        this.point = new Point(x,y);
    }

    public Circle(Point point, float r, int colorOfCircle, int m) {
        this.point = point;
        this.r = r;
        ColorOfCircle = colorOfCircle;
        this.m = m;
    }

    //    _________method______________________________________
//    测出小球的抛射角和初速度，以数组形式返回
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

    //    返回点与球心的距离
    public float distanceOfPoint(Point p) {
        float dy=point.y-p.y;
        float dx= point.x-p.x;
        return  (float) Math.sqrt(dx * dx + dy * dy);
    }

//    判断点是否在小球内
    public boolean pointInCircle(Point point) {
        RectF bounds = new RectF();
        Path pathOfCircle = new Path();
        pathOfCircle.addCircle((float)point.x, (float)point.y,r, Path.Direction.CW);
        pathOfCircle.computeBounds(bounds, true);
        Region region = new Region();
        region.setPath(pathOfCircle, new Region((int) bounds.left, (int) bounds.top, (int) bounds.right, (int) bounds.bottom));
        return region.contains(point.x, point.y);
    }

//    强制小球从指定位置起飞
    public void forceFly(float velocity0, float angleOfThrow, Point point0) {
        createFlyCount(velocity0, angleOfThrow, point0);
        isFly=true;
    }
//  让小球起飞,更新小球坐标;飞出边界则回收小球
    public void Fly(Point point0) {
        threadOfCount.start();
    }
//    根据事件来改变小球的坐标，b决定了是否发射小球
    public void moveByTouch(MotionEvent event,Boolean b) {
        if (!isTouch()&&event.getAction()!=MotionEvent.ACTION_DOWN) {
            return;//违背触摸的小球只能接受按下指令
        }
        if (isFly()) {//飞行的小球不会被触摸
            return;
        }
        if (point0 == null) {//防止空指针异常
            point0=point;
        }
        float x=event.getX();
        float y=event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (Math.abs(x - point.x) + Math.abs(y - point.y) > r*1.4) {
                    return;
                }
                 point0=new Point(point.x,point.y);
                 setTouch(true);
                 willFly=b;
            case MotionEvent.ACTION_MOVE:
                point.x = (int) x;
                point.y = (int) y;
                
                break;
            case MotionEvent.ACTION_UP:
                setTouch(false);
                if (Math.abs(x - point0.x) + Math.abs(y - point0.y) < r * 1.3&&b) {
                    point = new Point(point0.x, point0.y);
                    return;
                }
                if (b) {
                    Fly(point0);
                    willFly=false;
                } else {
                    point.x = (int) x;
                    point.y = (int) y;
                    
                }
        }
    }
    public float[] getPointUp(Point p) {
        float radian=measure(point0)[1];
        return new float[]{(float) (p.x+ Math.cos(radian+Math.PI/2) * r),
                (float) (p.y + Math.sin(radian+Math.PI/2) * r)};
    }
    public float[] getPointDown(Point p) {
        float radian=measure(point0)[1];
        return new float[]{(float) (p.x- Math.cos(radian+Math.PI/2) * r),
                (float) (p.y - Math.sin(radian+Math.PI/2) * r)};
    }

    //    如果小球存在于屏幕，绘制小球
    public int DrawCircle(Canvas canvas) {
        long t1=System.currentTimeMillis();
        if (device_height == 0) {
            device_height= (int) (canvas.getHeight()+150+r);
            device_width= (int) (canvas.getWidth()+r);
        }
        if (paint == null) {
            paint = new Paint();
            paint.setColor(getColorOfCircle());
        }
        if (isExist()) {
            canvas.drawCircle(point.x,point.y,r,paint);
        }else {
            try{threadOfCount.interrupt();}catch (Exception ignore){}
        }
        if (isTouch()&&willFly&&isExist()) {
            //        小球到固定点上的一条线
            float[] pointUp1 = getPointUp(point);
            float[] pointUp2 = getPointUp(point0);
//        小球到固定点的另一条线
            float[] pointDown1 = getPointDown(point);
            float[] pointDown2 = getPointDown(point0);
//        定义路径并画出绳子
            Paint paintRope = new Paint();
            paintRope.setStyle(Paint.Style.STROKE);
            paintRope.setStrokeWidth(5);
            paintRope.setColor(ColorOfRope);
            Path path = new Path();
            path.moveTo(pointDown2[0], pointDown2[1]);
            path.lineTo(pointDown1[0], pointDown1[1]);
            RectF rectF = new RectF(point.x - r, point.y - r,
                    point.x + r, point.y + r);
            path.addArc(rectF, measure(point0)[0] - 90, 180);
            path.moveTo(pointUp1[0], pointUp1[1]);
            path.lineTo(pointUp2[0], pointUp2[1]);
            canvas.drawPath(path, paintRope);

        }
        return (int) (System.currentTimeMillis()-t1);

    }

}
