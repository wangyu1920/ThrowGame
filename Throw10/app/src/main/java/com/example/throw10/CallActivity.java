package com.example.throw10;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class CallActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        Objects.requireNonNull(getSupportActionBar()).hide();
        Button button_dianhua = findViewById(R.id.ButtonOfMain_dianhua);
        Button button_duanxin = findViewById(R.id.ButtonOfMain_duanxin);
        @SuppressLint("NonConstantResourceId") View.OnClickListener listener= v -> {
            Intent intent = new Intent();
            switch (v.getId()) {
                case R.id.ButtonOfMain_dianhua:
                    intent.setAction(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:15615752516"));
                    startActivity(intent);
                    break;
                case R.id.ButtonOfMain_duanxin:
                    intent.setAction(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("sms:15615752516"));
                    intent.putExtra("sms_body","Hello");
                    startActivity(intent);
                    break;
            }
        };
        button_dianhua.setOnClickListener(listener);
        button_duanxin.setOnClickListener(listener);

    }
}
