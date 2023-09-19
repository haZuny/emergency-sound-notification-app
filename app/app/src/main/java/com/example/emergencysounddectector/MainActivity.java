package com.example.emergencysounddectector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;


public class MainActivity extends AppCompatActivity {

    // Components
    ImageButton btn_menuBtn;
    Button btn_startRecordingBtn;
    CustomGraphView customGraphView;

    // Audio Record
    private int audioRecordSource = MediaRecorder.AudioSource.MIC;
    private int audioRecordSampleRate = 22050;
    private int audioRecordChannelCount = AudioFormat.CHANNEL_IN_MONO;
    private int audioRecordFormat = AudioFormat.ENCODING_PCM_FLOAT;
    private int audioRecordBufSize = AudioRecord.getMinBufferSize(audioRecordSampleRate, audioRecordChannelCount, audioRecordFormat);
    // Audio Record Object
    public AudioRecord audioRecord = null;
    // Audio Record Thread
    public SoundRecordingThread audioRecordThread = null;
    // Audio Record State
    private boolean audioRecordingState = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init Components
        btn_menuBtn = findViewById(R.id.settingNoti_button_menu);
        btn_startRecordingBtn = findViewById(R.id.main_button_start);
        customGraphView = (CustomGraphView) findViewById(R.id.main_view_custom);

        // Menu button actions
        btn_menuBtn.setOnClickListener(v -> {
            final PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
            getMenuInflater().inflate(R.menu.menu_main, popupMenu.getMenu());
            // 메뉴 클릭 동작
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if (menuItem.getItemId() == R.id.menu_main_item1) {
                        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(intent);
                    } else if (menuItem.getItemId() == R.id.menu_main_item2) {
                        Intent intent = new Intent(MainActivity.this, HistoryIdxActivity.class);
                        startActivity(intent);
                    }
                    return false;
                }
            });
            popupMenu.show();
        });


        // Recording Start Button Actions
        btn_startRecordingBtn.setOnClickListener(v -> {
            // PermissionCheck
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 0x00000001);
                return;
            }
            // Stop -> Start
            if (audioRecordingState == false) {
                audioRecordingState = true;
                btn_startRecordingBtn.setText("Stop");
                audioRecord = new AudioRecord(audioRecordSource, audioRecordSampleRate, audioRecordChannelCount, audioRecordFormat, audioRecordBufSize);
                audioRecord.startRecording();

            }
            // Start -> Stop
            else{
                audioRecordingState = false;
                btn_startRecordingBtn.setText("Start");
                audioRecord.stop();
                audioRecord.release();
            }
        });
    }
}