package com.example.emergencysounddectector;


import android.content.Context;
import android.media.AudioRecord;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.RequiresApi;

public class SoundRecordingThread extends Thread {
    // Audio Record
    AudioRecord audioRecord;
    float[] soundOneSecBuffer = new float[22050];
    float[] soundBuffer = new float[3000];

    // Classfier
    float[] predictOutputBuf = new float[4];

    // Custom View
    CustomGraphView customGraphView = null;

    // Deeplearning
    SoundClassifier soundClassifier = new SoundClassifier();

    // MainActivity
    MainActivity mainActivity;

    // Vibration
    Vibrator vibrate;
    VibrationEffect vibrationEffect;

    // temp
    int lastState = 3;

    boolean runningState = true;

    // Constructure
    @RequiresApi(api = Build.VERSION_CODES.O)
    public SoundRecordingThread(AudioRecord audioRecord, CustomGraphView customGraphView, MainActivity mainActivity) {
        this.audioRecord = audioRecord;
        this.customGraphView = customGraphView;
        this.mainActivity = mainActivity;
        this.vibrate = (Vibrator) mainActivity.getSystemService(Context.VIBRATOR_SERVICE);
        this.vibrationEffect = VibrationEffect.createOneShot(2147483647, 255);  // amplitude max: 255
    }

    // Stop Run
    void stopRunning() {
        runningState = false;
        vibrate.cancel();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {
        while (runningState) {
            // 0.1초 단위로 버퍼에 기록
            double recordingTerm = 0.1;
            int recordingResult = audioRecord.read(soundBuffer, 0, (int) (22050 * recordingTerm), AudioRecord.READ_NON_BLOCKING);
            for (int i = recordingResult; i < 22050; i++) {
                soundOneSecBuffer[i - recordingResult] = soundOneSecBuffer[i];
            }
            for (int i = 0; i < recordingResult; i++) {
                soundOneSecBuffer[22050 - recordingResult + i] = soundBuffer[i];
            }

            // 예측
            predictOutputBuf = soundClassifier.predict(soundOneSecBuffer, 22050);


            // 예측 진동
            if (predictOutputBuf[0] > 0.5) {
                if (lastState != 0) {
                    vibrate.vibrate(vibrationEffect);
                }
                lastState = 0;
            } else if (predictOutputBuf[1] > 0.5) {
                if (lastState != 1) {
                    vibrate.vibrate(vibrationEffect);
                }
                lastState = 1;
            } else if (predictOutputBuf[2] > 0.5) {
                if (lastState != 2) {
                    vibrate.vibrate(vibrationEffect);
                }
                lastState = 2;
            } else {
                vibrate.cancel();
                lastState = 3;
            }

            // CustomView 갱신
            customGraphView.invalidateSoundBuffer(soundOneSecBuffer);
            customGraphView.invalidate();
        }
    }

}
