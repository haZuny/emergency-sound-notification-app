package com.example.emergencysounddectector;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.emergencysounddectector.SQLite.SQLiteHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
    Button button_yes;
    Button button_no;

    // Intent
    Intent intent;

    // Detected Sound
    DetectedSound detectedSound;

    // Audio Play
    AudioTrack audioTrack;

    // HTTP connection
    HttpCommunication httpCommunication;

    SQLiteHelper sqliteHelper;
    SQLiteDatabase sqLiteDatabase;

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
        button_yes = findViewById(R.id.historyView_button_yes);
        button_no = findViewById(R.id.historyView_button_no);

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

        // SQLite
        this.sqliteHelper = new SQLiteHelper(this);
        this.sqLiteDatabase = sqliteHelper.getWritableDatabase();

        // Play btn action
        button_play.setOnClickListener(v -> {
            final int TEST_CONF = AudioFormat.CHANNEL_OUT_MONO;
            final int TEST_FORMAT = AudioFormat.ENCODING_PCM_FLOAT; // float: 32bit
            final int TEST_MODE = AudioTrack.MODE_STATIC;
            final int TEST_STREAM_TYPE = AudioManager.STREAM_MUSIC;
            audioTrack = new AudioTrack(TEST_STREAM_TYPE, 22050, TEST_CONF, TEST_FORMAT, 22050 * 4, TEST_MODE);
            audioTrack.write(detectedSound.sound, 0, 22050, AudioTrack.WRITE_BLOCKING);
            audioTrack.play();
        });

        // Yes btn action
        button_yes.setOnClickListener(v -> {
            // new Thread, 네트워크 통신은 main 쓰레드가 아닌 별도 쓰레드에서
            new Thread(() -> {
                // HTTP connection
                try {
                    httpCommunication = new HttpCommunication("http://220.69.208.121:4000/true-val/");
                    JSONObject jsonObj = httpCommunication.getJsonObj(detectedSound.id, detectedSound.category, detectedSound.percent_carhorn, detectedSound.percent_dogbark, detectedSound.percent_siren, detectedSound.percent_none, detectedSound.soundString, detectedSound.datetime);
                    String response = httpCommunication.sendPostMethod(jsonObj);
                    sqLiteDatabase.execSQL(sqliteHelper.getDeleteQuery(detectedSound.id));
                    finish();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        });

        // No btn action
        button_no.setOnClickListener(v -> {
            // new Thread, 네트워크 통신은 main 쓰레드가 아닌 별도 쓰레드에서
            new Thread(() -> {
                // HTTP connection
                try {
                    httpCommunication = new HttpCommunication("http://220.69.208.121:4000/false-val/");
                    JSONObject jsonObj = httpCommunication.getJsonObj(detectedSound.id, detectedSound.category, detectedSound.percent_carhorn, detectedSound.percent_dogbark, detectedSound.percent_siren, detectedSound.percent_none, detectedSound.soundString, detectedSound.datetime);
                    String response = httpCommunication.sendPostMethod(jsonObj);
                    sqLiteDatabase.execSQL(sqliteHelper.getDeleteQuery(detectedSound.id));
                    finish();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        });

    }
}