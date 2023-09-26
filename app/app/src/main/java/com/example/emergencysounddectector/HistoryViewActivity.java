package com.example.emergencysounddectector;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Arrays;

public class HistoryViewActivity extends AppCompatActivity {

    // Components
    TextView textView_category;
    TextView textView_datetime;
    TextView textView_percent_carHorn;
    TextView textView_percent_dogBark;
    TextView textView_percent_siren;
    TextView textView_percent_none;
    CustomGraphView customGraphView;

    // Intent
    Intent intent;

    // Detected Sound
    DetectedSound detectedSound;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_view);

        // Components Init
        textView_category = findViewById(R.id.historyView_text_type);
        textView_datetime = findViewById(R.id.historyView_text_datetime);
        textView_percent_carHorn = findViewById(R.id.historyView_text_percent_carHorn);
        textView_percent_dogBark = findViewById(R.id.historyView_text_percent_dogBark);
        textView_percent_siren = findViewById(R.id.historyView_text_percent_siren);
        textView_percent_none = findViewById(R.id.historyView_text_percent_none);
        customGraphView = findViewById(R.id.historyView_view_custom);

        // Get Intent
        intent = getIntent();
        detectedSound = (DetectedSound) intent.getExtras().getSerializable("sound");

        // Component update
        textView_category.setText(detectedSound.category);
        textView_datetime.setText(detectedSound.datetime);
        textView_percent_carHorn.setText(String.format("%.2f%%", detectedSound.percent_carhorn));
        textView_percent_dogBark.setText(String.format("%.2f%%", detectedSound.percent_dogbark));
        textView_percent_siren.setText(String.format("%.2f%%", detectedSound.percent_siren));
        textView_percent_none.setText(String.format("%.2f%%", detectedSound.percent_none));

        Log.d("확인", Float.toString(detectedSound.sound.length));
        customGraphView.invalidateSoundBuffer(detectedSound.sound);
        customGraphView.invalidate();

    }
}