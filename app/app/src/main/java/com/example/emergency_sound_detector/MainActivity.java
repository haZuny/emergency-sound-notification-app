package com.example.emergency_sound_detector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    boolean running_state = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // 권한 요청.
        String[] permissions = {android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, permissions, 0);

        // components
        Button button_onoff = (Button) findViewById(R.id.button_onoff);

        // 초기화
        button_onoff.setText("Start");

        // 동작 정의
        button_onoff.setOnClickListener(v -> {
            Log.d("action", "onoff 버튼 누름");

            // Start->Stop
            if (running_state) {
                running_state = false;
                button_onoff.setText("Start");
            }
            // Stop->Start
            else {
                running_state = true;
                button_onoff.setText("Stop");
            }
        });
    }
}