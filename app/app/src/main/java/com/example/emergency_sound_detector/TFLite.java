package com.example.emergency_sound_detector;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.util.Log;

import com.example.emergency_sound_detector.JLibrosa.audio.JLibrosa;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

import org.tensorflow.lite.Interpreter;


public class TFLite {
    private static JLibrosa jlibrosa = new JLibrosa();
    private static Interpreter interpreter = null;

    private static float[][][][] input = new float[1][64][44][1];
    private static float[][] output = new float[1][1];


    public static float predict() {

        // MFCC
        float[][] mfcc = jlibrosa.generateMFCCFeatures(GlobalObj.deeplearningBuffer.getDlBuffer(), GlobalObj.sampleRate, 64);
        // input reshape (64, 44) -> (1, 64, 44, 1)
        for (int i = 0; i < mfcc.length; i++) {
            float[][] buf = new float[44][1];
            for (int j = 0; j < mfcc[0].length; j++) {
                buf[j][0] = mfcc[i][j];
            }
            input[0][i] = buf;
        }
        interpreter.run(input, output);
        Log.d("Predict", Arrays.toString(output[0]));

        return output[0][0];
    }

    // init tflite interpreter
    public static void initTfliteInterpreter(Activity activity, String modelPath) {
        interpreter = getTfliteInterpreter(activity, modelPath);
    }

    // Get Interpreter
    private static Interpreter getTfliteInterpreter(Activity activity, String modelPath) {
        try {
            return new Interpreter(loadModelFile(activity, modelPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 모델을 읽어오는 함수로, 텐서플로 라이트 홈페이지에 있다.
    // MappedByteBuffer 바이트 버퍼를 Interpreter 객체에 전달하면 모델 해석을 할 수 있다.
    private static MappedByteBuffer loadModelFile(Activity activity, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
}
