package com.example.emergency_sound_detector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    /**
     * About Audio Recording
     */
    // recording parm
    private int audioSource = MediaRecorder.AudioSource.MIC;
    private int sampleRate = GlobalObj.sampleRate;
    private int channelCount = AudioFormat.CHANNEL_IN_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_FLOAT;
    private int bufSize = AudioRecord.getMinBufferSize(sampleRate, channelCount, audioFormat);

    // recording obj
    public AudioRecord audioRecordObj = null;
    // recording thread
    public Thread_RecordingThread audioRecordingThread = null;

    /**
     * About layout
     */
    static TextView textView_isCarHorn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Global Obj context init
        GlobalObj.mainActivity = MainActivity.this;

        // Tflite model init
        TFLite.initTfliteInterpreter(MainActivity.this, "car_horn.tflite");

        /**
         * Components
         */
        // textView
        textView_isCarHorn = (TextView) findViewById(R.id.textView_isCarHorn);
        // buttons
        Button button_onoff = (Button) findViewById(R.id.button_onoff);
        Button button_playing = (Button) findViewById(R.id.button_play);
        // custom view
        CustomView_ViewAudio customView_viewAudio = (CustomView_ViewAudio) findViewById(R.id.custom_viewAudio);

        // 초기화
        textView_isCarHorn.setText("감지 안됨");
        button_onoff.setText("Start");
        button_playing.setText("Play");


        // audio permission check
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        // 동작 정의
        button_onoff.setOnClickListener(v -> {

            Log.d("ButtonClick", "Clicked Start/Stop Button");
            // Start->Stop
            if (GlobalObj.get_isRecording()) {
                Log.d("Action", "Start Recording");
                GlobalObj.setBool_isRecording(false);
                button_onoff.setText("Start");
                // release thread
                audioRecordingThread = null;
                // release audioRecord
                audioRecordObj.stop();
                audioRecordObj.release();
            }
            // Stop->Start
            else {
                Log.d("Action", "End Recording");
                GlobalObj.setBool_isRecording(true);
                button_onoff.setText("Stop");
                // start audioRecord
                audioRecordObj = new AudioRecord(audioSource, sampleRate, channelCount, audioFormat, bufSize);
                audioRecordObj.startRecording();
                // start threading
                audioRecordingThread = new Thread_RecordingThread(audioRecordObj, customView_viewAudio, this);
                audioRecordingThread.start();
            }
        });

        button_playing.setOnClickListener(v -> {
        });
    }

    // state 표시
    public void changeCarHornState(float predictVal){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (predictVal > 0.5) {
                    textView_isCarHorn.setText("경적 감지: " + Float.toString(predictVal));
                } else {
                    textView_isCarHorn.setText("감지 안됨: " + Float.toString(predictVal));
                }

            }
        });
    }
}

