package com.example.emergencysounddectector;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    // Components
    TextView text_carHornPercent;
    TextView text_dogBarkPercent;
    TextView text_sirenPercent;
    TextView text_nonePercent;
    TextView text_predictState;
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

    // Timer (State Update)
    Timer timer;
    TimerTask timerTask_updatePercent;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init Components
        text_carHornPercent = findViewById(R.id.main_text_percent_carHorn);
        text_dogBarkPercent = findViewById(R.id.main_text_percent_dogBark);
        text_sirenPercent = findViewById(R.id.main_text_percent_siren);
        text_nonePercent = findViewById(R.id.main_text_percent_none);
        text_predictState = findViewById(R.id.main_text_state);
        btn_menuBtn = findViewById(R.id.main_button_menu);
        btn_startRecordingBtn = findViewById(R.id.main_button_start);
        customGraphView = findViewById(R.id.main_view_custom);


        // Model Init
        SoundClassifier.initTfliteInterpreter(this, "sound_detection.tflite");

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
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
                    return;
                }
            }
            // Stop -> Start
            if (audioRecordingState == false) {
                audioRecordingState = true;
                btn_startRecordingBtn.setText("Stop");
                // Generate AudioRecord
                audioRecord = new AudioRecord(audioRecordSource, audioRecordSampleRate, audioRecordChannelCount, audioRecordFormat, audioRecordBufSize);
                audioRecord.startRecording();
                // Generate AudioRecord Thread Obj
                audioRecordThread = new SoundRecordingThread(audioRecord, customGraphView, this);
                audioRecordThread.start();
                // percent update timer
                timer = new Timer();
                timerTask_updatePercent = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 퍼센트 업데이트
                                text_carHornPercent.setText(String.format("%.2f%%", audioRecordThread.predictOutputBuf[0]));
                                text_dogBarkPercent.setText(String.format("%.2f%%", audioRecordThread.predictOutputBuf[1]));
                                text_sirenPercent.setText(String.format("%.2f%%", audioRecordThread.predictOutputBuf[2]));
                                text_nonePercent.setText(String.format("%.2f%%", audioRecordThread.predictOutputBuf[3]));

                                // 상태 설정
                                if (audioRecordThread.predictOutputBuf[0] > 0.5){
                                    text_predictState.setText("Car horn");
                                }
                                else if(audioRecordThread.predictOutputBuf[1] > 0.5){
                                    text_predictState.setText("Dog bark");
                                }
                                else if(audioRecordThread.predictOutputBuf[2] > 0.5){
                                    text_predictState.setText("Siren");
                                }
                                else{
                                    text_predictState.setText("None");
                                }
                            }
                        });
                    }
                };
                timer.schedule(timerTask_updatePercent,0,100);
            }
            // Start -> Stop
            else{
                audioRecordingState = false;
                btn_startRecordingBtn.setText("Start");
                // stop thread
                audioRecordThread.stopRunning();
                // audioRecord Release
                audioRecord.stop();
                audioRecord.release();
                // Timer Release
                timer.cancel();
            }
        });
    }
}