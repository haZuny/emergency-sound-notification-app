package com.example.emergencysounddectector;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpCommunication {
    URL url;
    HttpURLConnection connection;

    public HttpCommunication(String url) throws IOException {
        this.url = new URL(url);
        connection = (HttpURLConnection) this.url.openConnection();
        connection.setRequestMethod("POST");
        // json 형태로 전송
        connection.setRequestProperty("Content-Type","application/json");
        // 응답 형태 설정
        connection.setRequestProperty("Accept","application/json");
        // outputStream으로 POST 데이터 전송
        connection.setDoOutput(true);
    }

    String sendPostMethod(JSONObject jsonObj) throws IOException, JSONException {
        // connection 객체의 outputStream에 json 객체 전송
        BufferedWriter bufWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        bufWriter.write(jsonObj.toString());
        bufWriter.flush();
        bufWriter.close();

        // 응답 메시지 받기
        BufferedReader bufReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String returnMSG = bufReader.readLine();
        return returnMSG;
    }

    JSONObject getJsonObj(int id, String category, float percent_carHorn, float percent_dogBark, float percent_siren, float percent_none, String sound_buf, String dateTime) throws JSONException {
        JSONObject jsonObj = new JSONObject();

        jsonObj.put("id", Integer.toString(id));
        jsonObj.put("category", category);
        jsonObj.put("percent_carhorn", Float.toString(percent_carHorn));
        jsonObj.put("percent_dogbark", Float.toString(percent_dogBark));
        jsonObj.put("percent_siren", Float.toString(percent_siren));
        jsonObj.put("percent_none", Float.toString(percent_none));
        jsonObj.put("sound_buf", sound_buf);
        jsonObj.put("datetime", dateTime);

        return jsonObj;
    }
}
