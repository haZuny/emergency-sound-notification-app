package com.example.emergencysounddectector.SQLite;

import static com.example.emergencysounddectector.SQLite.Serializer.arrayToString;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class SQLiteHelper extends SQLiteOpenHelper {
    static String dbName = "detectedSound.db";
    String tableName = "DetectedSound";

    public SQLiteHelper(@Nullable Context context) {
        super(context, dbName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + tableName + " (id integer primary key autoincrement, category text, " + "percent_carhorn real, percent_dogbark real, percent_siren real, percent_none real, sound_buf text, datetime text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    // get insert query
    public String getInsertQuery(String category, float[] outputBuf, float[] soundBuf) throws IOException {
        float percent_carhorn = outputBuf[0];
        float percent_dogbark = outputBuf[1];
        float percent_siren = outputBuf[2];
        float percent_none = outputBuf[3];
        String serializedSound = arrayToString(soundBuf);
        String datetime = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.mm.dd hh:mm"));
        }

        return String.format("insert into %s(category, percent_carhorn, percent_dogbark, percent_siren, percent_none, sound_buf, datetime) values('%s', %f, %f, %f, %f, '%s', '%s')", tableName, category, percent_carhorn, percent_dogbark, percent_siren, percent_none, serializedSound, datetime);
    }

    // get select all query
    public String getSelectAllQuery() {
        return "select * from " + tableName;
    }

    // get delete item query
    public String getDeleteQuery(int id) {
        return String.format("delete from %s where id=%d", tableName, id);
    }
}
