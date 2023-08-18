package com.example.emergency_sound_detector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import java.io.IOException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {


    /**
     * About Audio Recording
     */
    // recording parm
    private int audioSource = MediaRecorder.AudioSource.MIC;
    private int sampleRate = 44100;
    private int channelCount = AudioFormat.CHANNEL_IN_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_FLOAT;
    private int bufSize = AudioRecord.getMinBufferSize(sampleRate, channelCount, audioFormat);

    // recording obj
    public AudioRecord audioRecorder = null;
    // recording thread
    public Thread recordThread = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // components
        Button button_onoff = (Button) findViewById(R.id.button_onoff);
        Button button_playing = (Button) findViewById(R.id.button_play);

        // 초기화
        button_onoff.setText("Start");
        button_playing.setText("Play");


        // audio recorder
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        audioRecorder = new AudioRecord(audioSource, sampleRate, channelCount, audioFormat, bufSize);
        recordThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (GlobalObj.get_isRecording()) {
                    Log.d("asdf", "asdfasdfasdf");
                    int ret = audioRecorder.read(GlobalObj.floatArr_recordingBuffer, 0, 100, AudioRecord.READ_NON_BLOCKING);
                    if (ret > 0) {
//                        Log.d("버퍼", Integer.toString(testsize) + ", " + Integer.toString(ret) + ", " + Arrays.toString(buf));
                    }
                }
                return;
            }
        });


        // 동작 정의
        button_onoff.setOnClickListener(v -> {
            Log.d("action", "onoff 버튼 누름");

            // Start->Stop
            if (GlobalObj.get_isRecording()) {
                GlobalObj.setBool_isRecording(false);
                button_onoff.setText("Start");
                audioRecorder.stop();
                audioRecorder.release();
            }
            // Stop->Start
            else {
                GlobalObj.setBool_isRecording(true);
                button_onoff.setText("Stop");
                audioRecorder.startRecording();
                recordThread.start();
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

