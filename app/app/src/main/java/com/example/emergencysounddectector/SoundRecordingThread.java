package com.example.emergencysounddectector;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioRecord;
import android.media.SoundPool;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.RequiresApi;

import com.example.emergencysounddectector.SQLite.SQLiteHelper;

import java.io.IOException;

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

    // Notify Sound
    SoundPool soundPool;
    int soundId;
    int streamId = 0;

    // SQLite
    SQLiteHelper sqLiteHelper;
    SQLiteDatabase sqLiteDatabase;

    // temp
    int lastState = 3;

    boolean runningState = true;

    // Constructure
    @RequiresApi(api = Build.VERSION_CODES.O)
    public SoundRecordingThread(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.audioRecord = mainActivity.audioRecord;
        this.customGraphView = mainActivity.customGraphView;
        this.vibrate = (Vibrator) mainActivity.getSystemService(Context.VIBRATOR_SERVICE);
        this.vibrationEffect = VibrationEffect.createOneShot(2147483647, 255);  // amplitude max: 255
        this.soundPool = mainActivity.soundPool;
        this.soundId = mainActivity.soundId;
        this.sqLiteHelper = mainActivity.sqliteHelper;
        this.sqLiteDatabase = mainActivity.sqLiteDatabase;
    }

    // Stop Run
    void stopRunning() {
        runningState = false;
        vibrate.cancel();
        soundPool.stop(streamId);
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
                    playNotiSound();
                }
                lastState = 0;
            } else if (predictOutputBuf[1] > 0.5) {
                if (lastState != 1) {
                    vibrate.vibrate(vibrationEffect);
                    playNotiSound();
                }
                lastState = 1;
            } else if (predictOutputBuf[2] > 0.5) {
                if (lastState != 2) {
                    vibrate.vibrate(vibrationEffect);
                    playNotiSound();
                    try {
                        sqLiteDatabase.execSQL(sqLiteHelper.getInsertQuery("Siren", predictOutputBuf, soundBuffer));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                lastState = 2;
            } else {
                vibrate.cancel();
                soundPool.stop(streamId);
                lastState = 3;
            }

            // CustomView 갱신
            customGraphView.invalidateSoundBuffer(soundOneSecBuffer);
            customGraphView.invalidate();
        }
    }


    // 음악 재생
    void playNotiSound(){
        soundPool.stop(streamId);
        streamId = soundPool.play(soundId, (float) 1, (float) 1, 0,-1, 1);
    }
}
