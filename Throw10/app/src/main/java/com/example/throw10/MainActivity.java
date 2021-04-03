package com.example.throw10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.Toast;

import java.util.Objects;
import java.util.Random;

import CircleRelatedClass.Circle;
import PathRelatedClass.PathWithMode;
import View.CircleView;
import View.PathView;

public class MainActivity extends AppCompatActivity {
    private int deviceWidth;
    private int deviceHeight;
    public SharedPreferences preferences=null;
    CircleView circleView;
    PathView pathView;
    Button buttonIsFly;
    Button buttonAddCircle;
    Button buttonAddPath;
    Button buttonClear;
    int pathNum=0;
    long exitTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        deviceHeight=getApplicationContext().getResources().getDisplayMetrics().heightPixels;
        deviceWidth=getApplicationContext().getResources().getDisplayMetrics().widthPixels;
        Objects.requireNonNull(getSupportActionBar()).hide();
        preferences = getSharedPreferences("name",MODE_PRIVATE);
        buttonIsFly = findViewById(R.id.button);
        buttonAddCircle = findViewById(R.id.button1);
        buttonAddPath = findViewById(R.id.button2);
        buttonClear = findViewById(R.id.button3);
        circleView = findViewById(R.id.CircleView);
        pathView = findViewById(R.id.pathView);
        pathView.preferences=this.preferences;
        buttonIsFly.setOnClickListener(v -> circleView.isFly=!circleView.isFly);
        buttonAddCircle.setOnClickListener(v -> {
            int num=0;
            while (num == 0) {
                Circle circle=new Circle(
                        preferences.getInt("Circle.point.x",500),
                        preferences.getInt("Circle.point.y",500)
                                +(new Random().nextInt(100)),preferences);
                circle.createPathManager(pathView.pathManager);
                num= circleView.circleManager.addCircle(circle);
            }
            circleView.circleManager.deleteCircleIfNeeded();
        });
        buttonAddPath.setOnClickListener(v -> {
//                随机产生一个path的参数
            int x = new Random().nextInt(deviceWidth);
            int y= new Random().nextInt(deviceHeight);
            float r = new Random().nextInt(preferences.getInt("Math.r.max",90)-
                    preferences.getInt("Path.r.min",40))
                    +preferences.getInt("Path.r.min",40);
//                得到距离将生成的path最近的circle
            Circle circle = circleView.circleManager.getCircleNearliest(new Point(x, y), false);
//                判断二者是否重叠，重叠则再次生成新的path参数直到不重叠
            if (circle != null) {
                while ((circle.r + r )> circle.distanceOfPoint(new Point(x, y))) {
                    x = new Random().nextInt(deviceWidth);
                    y= new Random().nextInt(deviceHeight);
                    r = new Random().nextInt(preferences.getInt("Math.r.max",90)-
                            preferences.getInt("Path.r.min",40))
                            +preferences.getInt("Path.r.min",40);
                    circle= circleView.circleManager.getCircleNearliest(new Point(x,y),false);
                }
            }
//                利用path参数产生path
            PathWithMode path = new PathWithMode(String.valueOf(pathNum),
                    new Point(x, y),r,true,preferences);
            if (pathNum % 2 == 0) {
                //path画圆
                path.addCircle(x, y, r, Path.Direction.CCW);
            } else {
                path.isCircle=false;
                //path画正方形
                path.moveTo(x-r,y-r);
                path.lineTo(x + r, y - r);
                path.lineTo(x + r, y + r);
                path.lineTo(x - r, y + r);
                path.r+= (float) (path.r*1.42+20);
                path.close();
            }
            pathView.pathManager.addPath(path);
            pathView.invalidate();
            pathNum++;
//                将path传入所有circle
            Circle[] allCircles= circleView.circleManager.getCirclesAll();
            for (Circle allCircle : allCircles) {
                if (allCircle != null) {
                    allCircle.createPathManager(
                            pathView.pathManager
                    );
                }
            }
        });
        buttonClear.setOnClickListener(v -> {
            pathView.pathManager.removePath();
            pathView.invalidate();
            circleView.circleManager.deleteCircle();
        });
        findViewById(R.id.TextOfMain_lianxi).setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(MainActivity.this, CallActivity.class));
            startActivity(intent);
        });
        findViewById(R.id.TextOfMain_set).setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(MainActivity.this, SetActivity.class));
            startActivity(intent);
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        preferences = getSharedPreferences("name",MODE_PRIVATE);
        pathView.preferences=this.preferences;
        circleView.drawPeriod=getSharedPreferences("name", Context.MODE_PRIVATE)
                .getInt("CircleView.drawPeriod",16);
        pathView.pathManager.resetParameter(preferences);
        circleView.circleManager.resetParameter(preferences);
    }

}