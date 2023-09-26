package com.example.emergencysounddectector;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioRecord;
import android.media.SoundPool;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

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
    int cnt = 0;
    int tempSize = 5;
    int[] temp = new int[tempSize];
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
        
        // temp 초기화
        for (int i = 0; i < tempSize; i++){
            temp[i] = 3;
        }
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
            if (cnt < 22050){
                cnt += recordingResult;
                continue;
            }

            // 예측
            predictOutputBuf = soundClassifier.predict(soundOneSecBuffer, 22050);

            // 예측 결과 0.5 넘을 경우 탐색
            for (int i = 0; i < 4; i++){
                if (predictOutputBuf[i] > 0.5){
                    // temp에 기록
                    for (int j = 0; j < tempSize-1; j++){
                        temp[j] = temp[j+1];
                    }
                    temp[tempSize-1] = i;

                    // 모든 temp가 동일하면 갱신
                    boolean state = true;
                    for (int temp:temp){
                        if (temp != i){state = false;}
                    }
                    // 알림
                    if(state && i != 3 && i!=2 && lastState != i){
                        mainActivity.changeState(i);    // 상태 변경
                        vibrate.vibrate(vibrationEffect);   // 진동
                        playNotiSound();    // 소리
                        String category;    // Category 결정
                        switch (i){
                            case 0:
                                category = "Car horn";
                                break;
                            case 1:
                                category = "Dog bark";
                                break;
                            case 2:
                                category = "Siren";
                                break;
                            default:
                                category = "None";
                                break;
                        }
                        lastState = i;
                        // DB 기록
                        try {
                            sqLiteDatabase.execSQL(sqLiteHelper.getInsertQuery(category, predictOutputBuf, soundOneSecBuffer));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    // 알림 중지
                    else if(state && i == 3){
                        mainActivity.changeState(i);    // 상태변경
                        vibrate.cancel();   // 진동정지
                        soundPool.stop(streamId);   // 소리정지
                        lastState = 3;
                    }
                }
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
