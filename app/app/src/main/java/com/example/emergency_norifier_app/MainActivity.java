package com.example.emergency_norifier_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {
    boolean running_state = false;


    // 16kHz Sampling rate
    private static final int RECORDER_SAMPLE_RATE = 16000;
    // 오디오 채널 MONO
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    // 마이크에서 음성 받아온다.
    int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    // 사용할 버퍼 사이즈
    int BUFFER_SIZE_RECORDING = AudioRecord.getMinBufferSize(RECORDER_SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);

    // Signals whether a recording is in progress (true) or not (false).
    // Boolean 타입 과 거의 비슷한 듯. 여러 쓰레드에 안전하다는 장점이 있는?
    private final AtomicBoolean recordingInProgress = new AtomicBoolean(false);
    // 음성을 녹음하는 객체. 음성을 디지털 데이터로 변환하는.
    private AudioRecord audioRecord = null;
    // 일반 스레드, 데이터를 계속 받아와서 파일에 저장하는
    private Thread recordingThread = null;a


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
                stopRecording();
            }
            // Stop->Start
            else {
                running_state = true;
                button_onoff.setText("Stop");
                startRecording();
            }
        });
    }

    /**
     * 녹음 시작 메소드
     */
    @SuppressLint("MissingPermission")  // 권한 에러 방지
    private void startRecording() {

        audioRecord = new AudioRecord(AUDIO_SOURCE, RECORDER_SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE_RECORDING);
        audioRecord.startRecording();

        recordingInProgress.set(true);

        recordingThread = new Thread(new MainActivity.RecordingRunnable(), "Recording Thread");
        recordingThread.start();
    }

    /**
     * 녹음 종료 메소드
     */
    private void stopRecording() {
        if (null == audioRecord) {
            return;
        }
        recordingInProgress.set(false);

        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;
        recordingThread = null;
    }


    private class RecordingRunnable implements Runnable {

        @Override
        public void run() {

            // 음성 데이터 잠시 담아둘 버퍼 생성.
            final ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE_RECORDING);

            // 녹음하는 동안 {} 안의 코드 실행.
            while (recordingInProgress.get()) {

                // audioRecord 객체에서 음성 데이터 가져옴.
                int result = audioRecord.read(buffer, BUFFER_SIZE_RECORDING);
                Log.d("sound recording", String.valueOf(buffer.array().length));
                if (result < 0) {
                    throw new RuntimeException("Reading of audio buffer failed: " +
                            getBufferReadFailureReason(result));
                }
            }
        }

        private String getBufferReadFailureReason(int errorCode) {
            switch (errorCode) {
                case AudioRecord.ERROR_INVALID_OPERATION:
                    return "ERROR_INVALID_OPERATION";
                case AudioRecord.ERROR_BAD_VALUE:
                    return "ERROR_BAD_VALUE";
                case AudioRecord.ERROR_DEAD_OBJECT:
                    return "ERROR_DEAD_OBJECT";
                case AudioRecord.ERROR:
                    return "ERROR";
                default:
                    return "Unknown (" + errorCode + ")";
            }
        }
    }
}