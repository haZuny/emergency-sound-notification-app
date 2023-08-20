package com.example.emergency_sound_detector;

import android.content.Context;
import android.media.AudioRecord;
import android.util.Log;
import android.widget.TextView;

public class Thread_RecordingThread extends Thread {
    // AudioRecordObj
    AudioRecord audioRecordingObj = null;
    // Custom View _ View Audio
    CustomView_ViewAudio customView_viewAudio = null;
    int CustomViewStep = 9;

    // Main Activity
    Context mainActivityContext;

    public Thread_RecordingThread(AudioRecord audioRecorderObj, CustomView_ViewAudio customVIew, Context context) {
        this.audioRecordingObj = audioRecorderObj;
        this.customView_viewAudio = customVIew;
        mainActivityContext = context;
    }

    @Override
    public void run() {
        Log.d("Action", "Start Threading");
        int state = 0;
        while (GlobalObj.get_isRecording()) {
            // Recording
            int ret = audioRecordingObj.read(GlobalObj.floatArr_recordingBuffer, 0, 100, AudioRecord.READ_NON_BLOCKING);
            if (ret > 0) {
                // 버퍼에 데이터 기록
                GlobalObj.deeplearningBuffer.validateDeepLearningBuffer(ret, GlobalObj.floatArr_recordingBuffer);
                // 0.2초에 한번씩 예측
                int recordingSeq = GlobalObj.deeplearningBuffer.seq;
                if (recordingSeq >= (int) GlobalObj.sampleRate * GlobalObj.deeplearningBuffer.detect_step) {
                    GlobalObj.deeplearningBuffer.seq = 0;
                    float predictVal = TFLite.predict();
                    GlobalObj.mainActivity.changeCarHornState(predictVal);
                }
                // 400번에 한번씩 커스텀뷰 갱신
                if (state == 0) {
                    // get aver value
                    float aver = 0;
                    for (float var : GlobalObj.floatArr_recordingBuffer) {
                        var = (var >= 0) ? var : -var;
                        aver += var;
                    }
                    aver /= 100;
                    // customView update
                    customView_viewAudio.validateDrawingBuffer(aver);
                    customView_viewAudio.invalidate();
                }
                state += 1;
                if (state > 4) {
                    state = 0;
                }

            }
        }
        Log.d("Action", "End Threading");
    }
}
