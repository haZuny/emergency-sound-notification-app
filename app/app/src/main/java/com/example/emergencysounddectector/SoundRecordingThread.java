package com.example.emergencysounddectector;

import android.media.AudioRecord;

public class SoundRecordingThread {
    // Audio Record
    AudioRecord audioRecord;

    // Custom View
    CustomGraphView customGraphView;

    // Constructure
    public SoundRecordingThread(AudioRecord audioRecord){
        this.audioRecord = audioRecord;
    }

}
