package com.example.emergency_sound_detector;

import android.util.Log;

import com.example.emergency_sound_detector.JLibrosa.audio.JLibrosa;

import java.util.Arrays;

public class TFLite {
    private static JLibrosa jlibrosa = new JLibrosa();
    public static float[] predict(){

        // MFCC
        float[][] mfcc = jlibrosa.generateMFCCFeatures(GlobalObj.deeplearningBuffer.getDlBuffer(), GlobalObj.sampleRate, 64);
        Log.d("Test", Integer.toString(mfcc[0].length));

        return new float[5];
    }
}
