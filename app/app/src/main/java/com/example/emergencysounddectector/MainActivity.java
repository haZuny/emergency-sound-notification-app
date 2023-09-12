package com.example.emergencysounddectector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.PopupMenu;


public class MainActivity extends AppCompatActivity {

    // Components
    ImageButton btn_menuBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 컴포넌트 초기화
        btn_menuBtn = findViewById(R.id.settingNoti_button_menu);

        // 메뉴 버튼 동작
        btn_menuBtn.setOnClickListener(v->{
            final PopupMenu popupMenu = new PopupMenu(getApplicationContext(),v);
            getMenuInflater().inflate(R.menu.menu_main,popupMenu.getMenu());
            // 메뉴 클릭 동작
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if (menuItem.getItemId() == R.id.menu_main_item1){
                        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(intent);
                    }else if (menuItem.getItemId() == R.id.menu_main_item2){
                        Intent intent = new Intent(MainActivity.this, HistoryIdxActivity.class);
                        startActivity(intent);
                    }
                    return false;
                }
            });
            popupMenu.show();
        });
    }
}