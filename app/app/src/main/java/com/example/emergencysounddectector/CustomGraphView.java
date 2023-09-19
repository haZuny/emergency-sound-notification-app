package com.example.emergencysounddectector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Arrays;

public class CustomGraphView extends View {
    float[] soundBuffer = new float[50];


    public CustomGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    void invalidateSoundBuffer(float[] soundBuffer) {
        // 그래프를 양수로 전환, [50]크기로 압축(441마다 평균구하기)
        float aver = 0;
        for (int i = 1; i < 22050; i++) {
            aver += soundBuffer[i] >= 0 ? soundBuffer[i] : -soundBuffer[i];;
            if (i % 441 == 0){
                this.soundBuffer[i/441-1] = aver/441;
                aver = 0;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float viewWidth = getWidth();
        float viewHeight = getHeight();
        float x_term = viewWidth/50;
        float x_whiteTerm = x_term/4;
        float y_mid = viewHeight/2;

        // Background
        Paint paint = new Paint();
        paint.setColor(Color.argb(100,218,221,252));
        canvas.drawRect(0, 0, viewWidth, viewHeight, paint);

        // Graph
        paint.setColor(Color.argb(100, 100,163,155));
        for (int i = 0; i < 50; i++){
            // 0.5를 최대 크기로 정규화
            float nomalizeSound = soundBuffer[i] / (float)0.5;
            if (nomalizeSound>= 1){
                nomalizeSound = (float) 1;
            }
            float height = (int)(nomalizeSound * viewHeight);

            canvas.drawRect(x_term*i + x_whiteTerm, y_mid-(height/2), x_term*(i+1) - x_whiteTerm, y_mid+(height/2), paint);
        }

    }
}
