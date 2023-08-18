package com.example.emergency_sound_detector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;


public class CustomView_ViewAudio extends View {


    /**
     * Drawing buffer
     * 한 화면은 총 5초간의 오디오 정보를 표시합니다.
     * 1초에 50 sample rate로 그립니다.
     * buffer size = 50 * 5 = 250
     * 화면은 오른쪽부터 갱신됩니다.
     */
    private int bufSize = 250;
    ArrayList<Float> drawingBuffer = null;

    // Canvas info


    // Constructure
    public CustomView_ViewAudio(Context context, AttributeSet attrs) {
        super(context, attrs);
        // init buffer
        drawingBuffer = new ArrayList<Float>();
        for (int i = 0; i < bufSize; i++) {
            drawingBuffer.add((float) 0);
        }
        int a = getWidth();
    }

    // drawing buffer
    public void validateDrawingBuffer(float var) {
        drawingBuffer.remove(0);
        drawingBuffer.add(var);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width_size = getWidth();
        int height_size = getHeight();
        int y_mid = (int) height_size / 2;

        Paint paint = new Paint();

        // draw background
        paint.setColor(Color.LTGRAY);
        canvas.drawRect(0,0,width_size, height_size, paint);

        // Draw Graph
        paint.setColor(Color.BLACK);
        for (int i = 0; i < bufSize; i++) {
            float width = width_size/bufSize;
            float height = drawingBuffer.get(i) * height_size/2;
            canvas.drawRect(i*width_size/bufSize, y_mid - height, i*width_size/bufSize + width, y_mid + height, paint);
        }
    }
}
