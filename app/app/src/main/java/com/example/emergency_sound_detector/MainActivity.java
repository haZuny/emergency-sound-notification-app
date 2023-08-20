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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Tflite model init
        TFLite.initTfliteInterpreter(MainActivity.this, "car_horn.tflite");

        /**
         * Components
         */
        // buttons
        Button button_onoff = (Button) findViewById(R.id.button_onoff);
        Button button_playing = (Button) findViewById(R.id.button_play);
        // custom view
        CustomView_ViewAudio customView_viewAudio = (CustomView_ViewAudio) findViewById(R.id.custom_viewAudio);

        // 초기화
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
                audioRecordingThread = new Thread_RecordingThread(audioRecordObj, customView_viewAudio);
                audioRecordingThread.start();
            }
        });

        button_playing.setOnClickListener(v -> {
//            // play -> stop
//            if (isPlaying) {
//                isPlaying = false;
//                button_playing.setText("Play");
//            }
//            // stop -> play
//            else {
//                isPlaying = true;
//                button_playing.setText("Stop");
//            }
        });
    }
}

