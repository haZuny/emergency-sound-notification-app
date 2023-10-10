package com.example.emergencysounddectector;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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
    Button button_play;

    // Intent
    Intent intent;

    // Detected Sound
    DetectedSound detectedSound;

    // Audio Play
    AudioTrack audioTrack;

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
        button_play = findViewById(R.id.historyView_button_play);

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
        customGraphView.invalidateSoundBuffer(detectedSound.sound);
        customGraphView.invalidate();

        // Play btn action
        button_play.setOnClickListener(v -> {
            final int TEST_CONF = AudioFormat.CHANNEL_OUT_MONO;
            final int TEST_FORMAT = AudioFormat.ENCODING_PCM_FLOAT; // float: 32bit
            final int TEST_MODE = AudioTrack.MODE_STATIC;
            final int TEST_STREAM_TYPE = AudioManager.STREAM_MUSIC;
            audioTrack = new AudioTrack(TEST_STREAM_TYPE, 22050, TEST_CONF, TEST_FORMAT, 22050*4, TEST_MODE);
            audioTrack.write(detectedSound.sound, 0, 22050, AudioTrack.WRITE_BLOCKING);
            audioTrack.play();
        });

    }
}