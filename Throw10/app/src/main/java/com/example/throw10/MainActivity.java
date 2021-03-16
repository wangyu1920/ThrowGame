package com.example.throw10;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Objects;
import java.util.Random;

import CircleRelatedClass.Circle;
import PathRelatedClass.PathWithMode;
import View.MainView;

public class MainActivity extends AppCompatActivity {
    private int deviceWidth;
    private int deviceHeight;

    MainView mainView;
    Button buttonIsFly;
    Button buttonAddCircle;
    Button buttonAddPath;
    Button buttonClear;
    int pathNum=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        deviceHeight=getApplicationContext().getResources().getDisplayMetrics().heightPixels;
        deviceWidth=getApplicationContext().getResources().getDisplayMetrics().widthPixels;
        Objects.requireNonNull(getSupportActionBar()).hide();
        buttonIsFly = findViewById(R.id.button);
        buttonAddCircle = findViewById(R.id.button1);
        buttonAddPath = findViewById(R.id.button2);
        buttonClear = findViewById(R.id.button3);
        mainView = findViewById(R.id.view);
        buttonIsFly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainView.isFly=!mainView.isFly;
            }
        });
        buttonAddCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num=0;
                while (num == 0) {
                    Circle circle=new Circle(500,500+(new Random().nextInt(100)));
                    circle.createPathManager(mainView.pathManager);
                    num=mainView.circleManager.addCircle(circle);
                }
                mainView.circleManager.deleteCircleIfNeeded();
            }
        });
        buttonAddPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                随机产生一个path的参数
                int x = new Random().nextInt(deviceWidth);
                int y= new Random().nextInt(deviceHeight);
                float r = new Random().nextInt(70)+30;
//                得到距离将生成的path最近的circle
                Circle circle = mainView.circleManager.getCircleNearliest(new Point(x, y), false);
//                判断二者是否重叠，重叠则再次生成新的path参数直到不重叠
                if (circle != null) {
                    while ((circle.r + r )> circle.distanceOfPoint(new Point(x, y))) {
                        x = new Random().nextInt(deviceWidth);
                        y= new Random().nextInt(deviceHeight);
                        r = new Random().nextInt(70)+30;
                        circle=mainView.circleManager.getCircleNearliest(new Point(x,y),false);
                    }
                }
//                利用path参数产生path
                PathWithMode path = new PathWithMode(String.valueOf(pathNum),
                        new Point(x, y),r,true);
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
                    path.r= (float) (path.r*1.5+10);
                    path.close();
                }
                mainView.pathManager.addPath(path);
                pathNum++;
//                将path传入所有circle
                Circle[] allCircles=mainView.circleManager.getCirclesAll();
                for (Circle allCircle : allCircles) {
                    if (allCircle != null) {
                        allCircle.createPathManager(
                                mainView.pathManager
                        );
                    }
                }
            }
        });
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainView.pathManager.removePath();
                mainView.circleManager.deleteCircle();
            }
        });
    }
}