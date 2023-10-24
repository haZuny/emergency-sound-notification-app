package com.example.emergencysounddectector;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;

public class SettingNotificationActivity extends AppCompatActivity {

    // Components
    SeekBar seekBar_soundSeekbar;
    SeekBar seekBar_vibrationSeekbar;
    Button button_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_notification);

        // init components
        seekBar_soundSeekbar = findViewById(R.id.settingNoti_seekbar_sound);
        seekBar_vibrationSeekbar = findViewById(R.id.settingNoti_seekbar_vib);
        button_save = findViewById(R.id.historyView_button_yes);

        button_save.setOnClickListener(v -> {
            
        });
    }
}