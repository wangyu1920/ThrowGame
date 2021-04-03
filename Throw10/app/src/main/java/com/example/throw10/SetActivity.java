package com.example.throw10;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class SetActivity extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    EditText cc;
    EditText crc;
    EditText pc;
    EditText Cr;
    EditText Cm;
    EditText C_period;
    EditText C_countPeriod;
    EditText PR_min;
    EditText PR_max;
    EditText C_pointX;
    EditText C_pointY;
    EditText fx;
    EditText fy;
    EditText CvRate;
    EditText P_mode;
    EditText P_mode_X;
    EditText P_mode_Y;
    EditText drawPeriod;

    @SuppressLint("CommitPrefEdits")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        Objects.requireNonNull(getSupportActionBar()).hide();
        preferences = getSharedPreferences("name",MODE_PRIVATE);
        editor=preferences.edit();

        cc=findViewById(R.id.CircleColorS);
        crc=findViewById(R.id.CircleColorR);
        pc=findViewById(R.id.CircleColorG);

        cc.setText(getColorString(preferences.getInt("Circle.color",Color.parseColor("#ff005555"))));
        crc.setText(getColorString(preferences.getInt("Circle.RopeColor",Color.parseColor("#ff0000ff"))));
        pc.setText(getColorString(preferences.getInt("Path.color",Color.parseColor("#ffff0000"))));

        Cr = findViewById(R.id.Circle_r);
        Cr.setText(String.valueOf(preferences.getFloat("Circle.r",50)));
        Cm = findViewById(R.id.Circle_m);
        Cm.setText(String.valueOf(preferences.getInt("Circle.m",50)));
        C_period = findViewById(R.id.C_period);
        C_period.setText(String.valueOf(preferences.getInt("Circle.period",50)));
        C_countPeriod = findViewById(R.id.C_countPeriod);
        C_countPeriod.setText(String.valueOf(preferences.getInt("Circle.countPeriod",1)));
        fx = findViewById(R.id.fx);
        fy = findViewById(R.id.fy);
        fx.setText(String.valueOf(preferences.getInt("fx",0)));
        fy.setText(String.valueOf(preferences.getInt("fy",0)));
        PR_max = findViewById(R.id.Path_rma);
        PR_max.setText(String.valueOf(preferences.getInt("Path.r.max",90)));
        PR_min = findViewById(R.id.Path_rmi);
        PR_min.setText(String.valueOf(preferences.getInt("Path.r.min",40)));
        CvRate = findViewById(R.id.Circle_vRate);
        CvRate.setText(String.valueOf(preferences.getFloat("Circle.vRate", (float) 0.3)));
        P_mode = findViewById(R.id.Path_mode);
        P_mode.setText(String.valueOf(preferences.getInt("Path.mode",1)));
        P_mode_X = findViewById(R.id.Path_mode_RateX);
        P_mode_X.setText(String.valueOf(preferences.getFloat("Path.rateX", 1)));
        P_mode_Y = findViewById(R.id.Path_mode_RateY);
        P_mode_Y.setText(String.valueOf(preferences.getFloat("Path.rateY", (float) -0.7)));
        C_pointX = findViewById(R.id.Circle_pointX);
        C_pointY = findViewById(R.id.Circle_pointY);
        C_pointX.setText(String.valueOf(preferences.getInt("Circle.point.x",500)));
        C_pointY.setText(String.valueOf(preferences.getInt("Circle.point.y",500)));

        drawPeriod = findViewById(R.id.drawPeriod);
        drawPeriod.setText(String.valueOf(preferences.getInt("CircleView.drawPeriod",16)));

    }

    private String getColorString(int color) {
        String a=Integer.toHexString(Color.alpha(
                color
        ));
        String r=Integer.toHexString(Color.red(
                color
        ));
        String g=Integer.toHexString(Color.green(
                color
        ));
        String b=Integer.toHexString(Color.blue(
                color
        ));
        if (a.length()==1) {
            a = "0"+a;
        }
        if (g.length()==1) {
            g = "0"+g;
        }
        if (b.length()==1) {
            b = "0"+b;
        }
        if (r.length()==1) {
            r = "0"+r;
        }
        return "#" +a+r+g+b;
    }

    private static int toInt(View view) {
        return Integer.parseInt(String.valueOf(((EditText)view).getText()));
    }
    private static float toFloat(View view) {
        return Float.parseFloat(String.valueOf(((EditText)view).getText()));
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.out.println(cc.getText().toString());
        editor.putInt("Circle.color", Color.parseColor(cc.getText().toString()));
        editor.putInt("Circle.ropeColor", Color.parseColor(crc.getText().toString()));
        editor.putInt("Path.color", Color.parseColor(pc.getText().toString()));
        editor.putInt("Circle.m",toInt(Cm));
        editor.putInt("Circle.period",toInt(C_period));
        editor.putInt("Circle.countPeriod",toInt(C_countPeriod));
        editor.putInt("fx",toInt(fx));
        editor.putInt("fy",toInt(fy));
        editor.putInt("Path.mode",toInt(P_mode));
        editor.putInt("Path.r.max",toInt(PR_max));
        editor.putInt("Path.r.min",toInt(PR_min));
        editor.putFloat("Circle.r",toFloat(Cr));
        editor.putFloat("Path.rateX",toFloat(P_mode_X));
        editor.putFloat("Path.rateY",toFloat(P_mode_Y));
        editor.putFloat("Circle.vRate",toFloat(CvRate));
        editor.putInt("Circle.point.x",toInt(C_pointX));
        editor.putInt("Circle.point.y",toInt(C_pointY));
        editor.putInt("CircleView.drawPeriod",toInt(drawPeriod));

        editor.commit();
    }
}
