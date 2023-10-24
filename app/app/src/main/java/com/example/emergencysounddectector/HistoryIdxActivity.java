package com.example.emergencysounddectector;

import static com.example.emergencysounddectector.SQLite.Serializer.stringToArray;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.emergencysounddectector.SQLite.SQLiteHelper;

import java.io.IOException;
import java.util.ArrayList;

public class HistoryIdxActivity extends AppCompatActivity {

    // 컴포넌트
    ListView listViet_historyIdx;

    // 리스트뷰 리스트
    ArrayList<DetectedSound> histories = new ArrayList<>();

    // SQLite
    SQLiteHelper sqliteHelper;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;

    HistoryIdxAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_idx);

        // 컴포넌트 초기화
        listViet_historyIdx = findViewById(R.id.historyIdx_listView);


        // SQLite Init
        sqliteHelper = new SQLiteHelper(this);
        sqLiteDatabase = sqliteHelper.getWritableDatabase();
        cursor = sqLiteDatabase.rawQuery(sqliteHelper.getSelectAllQuery(), null);

        // db정보 가져옴
        while(cursor.moveToNext()){
            Log.d("db", String.format("%s, %d, %s", cursor.getString(1), cursor.getInt(0), cursor.getString(7)));
            int id = cursor.getInt(0);
            String category = cursor.getString(1);
            float percent_carHorn = cursor.getFloat(2);
            float percent_dogBark = cursor.getFloat(3);
            float percent_siren = cursor.getFloat(4);
            float percent_none = cursor.getFloat(5);
            String soundString = cursor.getString(6);
            float[] soundBuf;
            try {
                soundBuf = stringToArray(soundString);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String datetime = cursor.getString(7);
            histories.add(0, new DetectedSound(id, category, percent_carHorn, percent_dogBark, percent_siren, percent_none, soundString, soundBuf, datetime));
        }
        
        // 리스트뷰 연결
        adapter = new HistoryIdxAdapter(this, histories);
        listViet_historyIdx.setAdapter(adapter);
    }

    // 액티비티 다시 실행될 때 어댑터 상태 갱신
    @Override
    protected void onResume() {

        super.onResume();

        histories.clear();
        cursor = sqLiteDatabase.rawQuery(sqliteHelper.getSelectAllQuery(), null);

        // db정보 가져옴
        while(cursor.moveToNext()){
            Log.d("db", String.format("%s, %d, %s", cursor.getString(1), cursor.getInt(0), cursor.getString(7)));
            int id = cursor.getInt(0);
            String category = cursor.getString(1);
            float percent_carHorn = cursor.getFloat(2);
            float percent_dogBark = cursor.getFloat(3);
            float percent_siren = cursor.getFloat(4);
            float percent_none = cursor.getFloat(5);
            String soundString = cursor.getString(6);
            float[] soundBuf;
            try {
                soundBuf = stringToArray(soundString);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String datetime = cursor.getString(7);
            histories.add(0, new DetectedSound(id, category, percent_carHorn, percent_dogBark, percent_siren, percent_none, soundString, soundBuf, datetime));
        }
        // 상태 갱신
        adapter.notifyDataSetChanged();
    }

    // 히스토리 클래스
    class History{
        String type;
        int percent;
        String datetime;

        public History(String type, int percent, String datetime){
            this.type = type;
            this.percent = percent;
            this.datetime = datetime;
        }

        String getType(){
            return type;
        }
        String getPercent(){
            return Integer.toString(percent);
        }
        String getDatetime(){
            return datetime;
        }
    }

    // 어댑터 클래스
    public class HistoryIdxAdapter extends BaseAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;
        ArrayList<DetectedSound> histories;

        public HistoryIdxAdapter(Context context, ArrayList<DetectedSound> data) {
            mContext = context;
            histories = data;
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return histories.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public DetectedSound getItem(int position) {
            return histories.get(position);
        }

        @Override
        public View getView(int position, View converView, ViewGroup parent) {
            View view = mLayoutInflater.inflate(R.layout.listitem_historyidx, null);

            // 컴포넌트
            LinearLayout laytou_title = view.findViewById(R.id.listitem_history_layout_title);
            TextView text_title = view.findViewById(R.id.listitem_history_text_title);
            TextView text_datetime = (TextView) view.findViewById(R.id.listitem_history_text_datetime);
            ImageButton button_delete = view.findViewById(R.id.listitem_history_button_delete);

            // Detected Sound 객체
            DetectedSound detectedSound = histories.get(position);

            // view 갱신
            text_title.setText(detectedSound.category + " - " + detectedSound.bestPercent+"%");
            text_datetime.setText(detectedSound.datetime);

            // 타이틀 레이아웃 클릭
            laytou_title.setOnClickListener(v -> {
                Intent intent = new Intent(HistoryIdxActivity.this, HistoryViewActivity.class);
                intent.putExtra("sound", detectedSound);
                startActivity(intent);
            });

            // 삭제버튼 클릭
            button_delete.setOnClickListener(v -> {
                sqLiteDatabase.execSQL(sqliteHelper.getDeleteQuery(detectedSound.id));
                histories.remove(position);
                this.notifyDataSetChanged();
            });

            return view;
        }

    }


}