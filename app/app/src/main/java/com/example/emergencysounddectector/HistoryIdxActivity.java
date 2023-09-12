package com.example.emergencysounddectector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;

public class HistoryIdxActivity extends AppCompatActivity {

    // 컴포넌트
    ListView listViet_historyIdx;

    // 리스트뷰 리스트
    ArrayList<History> histories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_idx);

        // (임시)
        histories = new ArrayList<>();
        histories.add(new History("Car horn", 75, "2023.09.11 10:12"));
        histories.add(new History("Dog bark", 60, "2023.09.11 10:12"));
        histories.add(new History("Dog bark", 99, "2023.09.11 10:12"));
        histories.add(new History("Siren", 87, "2023.09.11 10:12"));
        histories.add(new History("Siren", 73, "2023.09.11 10:12"));
        histories.add(new History("Siren", 75, "2023.09.11 10:12"));

        // 컴포넌트 초기화
        listViet_historyIdx = findViewById(R.id.historyIdx_listView);

        // 리스트뷰 연결
        HistoryIdxAdapter adapter = new HistoryIdxAdapter(this, histories);
        listViet_historyIdx.setAdapter(adapter);

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
        ArrayList<History> histories;

        public HistoryIdxAdapter(Context context, ArrayList<History> data) {
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
        public History getItem(int position) {
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

            // view 갱신
            text_title.setText(histories.get(position).getType() + " - " + histories.get(position).getPercent()+"%");
            text_datetime.setText(histories.get(position).getDatetime());

            // 타이틀 레이아웃 클릭
            laytou_title.setOnClickListener(v -> {
                Intent intent = new Intent(HistoryIdxActivity.this, HistoryViewActivity.class);
                startActivity(intent);
            });

            // 삭제버튼 클릭
            button_delete.setOnClickListener(v -> {
                histories.remove(position);
                this.notifyDataSetChanged();
            });

            return view;
        }
    }
}