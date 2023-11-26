package com.example.emergencysounddectector;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.emergencysounddectector.SQLite.SQLiteHelper;


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

    // Sound
    SoundPool soundPool;
    int soundId;

    // SQLite
    SQLiteHelper sqliteHelper;
    SQLiteDatabase sqLiteDatabase;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init Components
        text_carHornPercent = findViewById(R.id.historyView_text_percent_carHorn);
        text_dogBarkPercent = findViewById(R.id.historyView_text_percent_dogBark);
        text_sirenPercent = findViewById(R.id.historyView_text_percent_siren);
        text_nonePercent = findViewById(R.id.historyView_text_percent_none);
        text_predictState = findViewById(R.id.main_text_state);
        btn_menuBtn = findViewById(R.id.main_button_menu);
        btn_startRecordingBtn = findViewById(R.id.historyView_button_yes);
        customGraphView = findViewById(R.id.main_view_custom);

        // Sound init
        soundPool = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);
        soundId = soundPool.load(this, R.raw.notify, 1);

        // Deeplearning Model Init
        SoundClassifier.initTfliteInterpreter(this, "sound_detection.tflite");

        // SQLite Init
        sqliteHelper = new SQLiteHelper(this);
        sqLiteDatabase = sqliteHelper.getWritableDatabase();

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
                audioRecordThread = new SoundRecordingThread(this);
                audioRecordThread.start();
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
//                timer.cancel();
            }
        });
    }

    public void changeTypeState(String category){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text_predictState.setText(category);
            }
        });
    }

    public void changePercent(float[] outputBuf){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text_carHornPercent.setText(String.format("%.2f%%", outputBuf[0]));
                text_dogBarkPercent.setText(String.format("%.2f%%", outputBuf[1]));
                text_sirenPercent.setText(String.format("%.2f%%", outputBuf[2]));
                text_nonePercent.setText(String.format("%.2f%%", outputBuf[3]));
            }
        });
    }
}