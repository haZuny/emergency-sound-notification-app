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

    public boolean running_state = false;
    boolean isPlaying = false;

    private int audioSource = MediaRecorder.AudioSource.MIC;
    private int sampleRate = 44100;
    private int channelCount = AudioFormat.CHANNEL_IN_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_FLOAT;
    private int bufSize = AudioRecord.getMinBufferSize(sampleRate, channelCount, audioFormat);

    public float[] buf = new float[bufSize];
    public AudioRecord audioRecorder = null;

    public Thread recordThread = null;


    int testsize = 0;


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
                while (running_state) {
                    int ret = audioRecorder.read(buf, 0, bufSize, AudioRecord.READ_NON_BLOCKING);
                    if (ret > 0) {
                        testsize += ret;
                        Log.d("버퍼", Integer.toString(testsize) + ", " + Integer.toString(ret) + ", " + Arrays.toString(buf));
                    }
//                    try {
//                        Thread.sleep(1);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
                }
                return;
            }
        });


        // 동작 정의
        button_onoff.setOnClickListener(v -> {
            Log.d("action", "onoff 버튼 누름");

            // Start->Stop
            if (running_state) {
                running_state = false;
                button_onoff.setText("Start");
                audioRecorder.stop();
                audioRecorder.release();
            }
            // Stop->Start
            else {
                running_state = true;
                button_onoff.setText("Stop");
                audioRecorder.startRecording();
                testsize = 0;
                recordThread.run();
            }
        });

        button_playing.setOnClickListener(v -> {
            // play -> stop
            if (isPlaying) {
                isPlaying = false;
                button_playing.setText("Play");
            }
            // stop -> play
            else {
                isPlaying = true;
                button_playing.setText("Stop");
            }
        });
    }
}

