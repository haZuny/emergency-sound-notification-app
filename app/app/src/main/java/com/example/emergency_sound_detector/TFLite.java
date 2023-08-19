package com.example.emergency_sound_detector;

import android.util.Log;

import com.example.emergency_sound_detector.JLibrosa.audio.JLibrosa;

import org.tensorflow.lite.Interpreter;


import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.Arrays;

import org.tensorflow.lite.support.common.FileUtil;

public class TFLite {
    private static JLibrosa jlibrosa = new JLibrosa();
    private static Interpreter interpreter = null;
    private static float[] output = new float[1];

    public static void initTFLiteInterpreter(){
        MappedByteBuffer tfliteModel;
        try {
            tfliteModel = FileUtil.loadMappedFile(activity, "my_model.tflite");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static float[] predict() {

        // MFCC
        float[][] mfcc = jlibrosa.generateMFCCFeatures(GlobalObj.deeplearningBuffer.getDlBuffer(), GlobalObj.sampleRate, 64);
        Log.d("Test", Integer.toString(mfcc[0].length));
        //interpreter.run(mfcc, output);
        Log.d("예측", Arrays.toString(output));


        return new float[5];
    }
}
