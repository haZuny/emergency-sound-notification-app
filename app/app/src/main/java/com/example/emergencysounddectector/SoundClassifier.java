package com.example.emergencysounddectector;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;

import com.example.emergencysounddectector.JLibrosa.audio.JLibrosa;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class SoundClassifier {
    private static JLibrosa jlibrosa = new JLibrosa();
    private static Interpreter interpreter = null;

    private static float[][][][] input = new float[1][44][44][1];
    private static float[][] output = new float[1][4];

    // 예측
    public static float[] predict(float[] soundBuffer, int sampleRate) {
        // MFCC
        float[][] mfcc = jlibrosa.generateMFCCFeatures(soundBuffer, sampleRate, 44);
        // input reshape (64, 44) -> (1, 64, 44, 1)
        for (int i = 0; i < mfcc.length; i++) {
            float[][] buf = new float[44][1];
            for (int j = 0; j < mfcc[0].length; j++) {
                buf[j][0] = mfcc[i][j];
            }
            input[0][i] = buf;
        }
        interpreter.run(input, output);
        return output[0];
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
