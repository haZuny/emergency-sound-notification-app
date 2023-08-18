package com.example.emergency_sound_detector;

import android.media.AudioRecord;
import android.util.Log;

public class RecordingThread extends Thread {
    AudioRecord audioRecordingObj = null;

    public RecordingThread(AudioRecord audioRecorderObj){this.audioRecordingObj = audioRecorderObj;}

    @Override
    public void run() {
        Log.d("Action", "Start Threading");
        while (GlobalObj.get_isRecording()) {
            int ret = audioRecordingObj.read(GlobalObj.floatArr_recordingBuffer, 0, 100, AudioRecord.READ_NON_BLOCKING);
            if (ret > 0) {
                // Todo
            }
        }
        Log.d("Action", "End Threading");
    }
}
