package com.example.emergencysounddectector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

public class SettingActivity extends AppCompatActivity {

    // Conponents
    LinearLayout layoutNoty;
    LinearLayout layoutDT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // 컴포넌트 초기화
        layoutNoty = findViewById(R.id.setting_layout_notiSet);
        layoutDT = findViewById(R.id.setting_layout_dectSet);

        // 알림 설정 동작 정의
        layoutNoty.setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, SettingNotificationActivity.class);
            startActivity(intent);
        });
        // 감지 설정 동작 정의
        layoutDT.setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, SettingDetectActivity.class);
            startActivity(intent);
        });
    }
}