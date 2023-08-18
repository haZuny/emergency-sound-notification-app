package com.example.emergency_sound_detector;

import android.util.Log;

import java.util.Arrays;

public class GlobalObj {
    // running state var
    static private boolean bool_isRecording = false;

    static public boolean get_isRecording() {
        return bool_isRecording;
    }

    static public void setBool_isRecording(boolean var) {
        bool_isRecording = var;
    }

    // recording buffer
    public static float[] floatArr_recordingBuffer = new float[200];
    public static DeepLearningBuffer deeplearningBuffer = new DeepLearningBuffer();

}

class DeepLearningBuffer {
    int seq = 0;
    public static float[] dlBuffer = new float[17640];

    public void validateDeepLearningBuffer(int len, float[] buf) {
        // 버퍼 당기기
        for (int i = len; i < 17640; i++) {
            dlBuffer[i - len] = dlBuffer[i];
        }
        // 데이터 삽입
        int lastIdx = 17640 - len - 1;
        for (int i = 0; i < len; i++) {
            dlBuffer[lastIdx + i] = buf[i];
        }

        seq += len;

        // 0.5초마다 검사
        // 44100 * 0.5 = 22050
        if (seq >= 22050) {
            // Todo
            seq = 0;
        }

        Log.d("Test", Arrays.toString(dlBuffer));
    }
}