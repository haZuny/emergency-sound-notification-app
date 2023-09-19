package com.example.emergencysounddectector;

import android.media.AudioRecord;
import android.util.Log;

import java.util.Arrays;

public class SoundRecordingThread extends Thread {
    // Audio Record
    AudioRecord audioRecord;
    float[] soundOneSecBuffer = new float[22050];
    float[] soundBuffer = new float[3000];

    // Custom View
    CustomGraphView customGraphView = null;

    boolean runningState = true;

    // Constructure
    public SoundRecordingThread(AudioRecord audioRecord, CustomGraphView customGraphView){
        this.audioRecord = audioRecord;
        this.customGraphView = customGraphView;
    }

    // Stop Run
    void stopRunning(){
        runningState = false;
    }

    @Override
    public void run(){
        while (runningState){
            // 0.1초 단위로 버퍼에 기록
            double recordingTerm = 0.1;
            int recordingResult = audioRecord.read(soundBuffer, 0, (int)(22050 * recordingTerm) , AudioRecord.READ_NON_BLOCKING);
            for (int i = recordingResult; i<22050; i++){
                soundOneSecBuffer[i-recordingResult] = soundOneSecBuffer[i];
            }
            for (int i = 0; i<recordingResult; i++){
                soundOneSecBuffer[22050-recordingResult+i] = soundBuffer[i];
            }

            // CustomView 갱신
            customGraphView.invalidateSoundBuffer(soundOneSecBuffer);
            customGraphView.invalidate();
        }
    }

}
