package com.example.emergencysounddectector.SQLite;

import android.os.Build;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

public class Serializer {
    public static String arrayToString(float[] array) throws IOException {
        byte[] serializeMember;
        try(ByteArrayOutputStream baos= new ByteArrayOutputStream()){
            try(ObjectOutputStream oos= new ObjectOutputStream(baos)){
                oos.writeObject(array);
                // serializedMember -> 직렬화된 member 객체
                serializeMember = baos.toByteArray();
            }
        }
        //바이트 배열로 생성된 직렬화 데이터를 base64로 변환
        // base64: 8bit(64) -> ASCII String
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(serializeMember);
        }
        return "";
    }

    public static float[] stringToArray(String string) throws IOException {
        //직렬화 예제에서 생성된 base64 데이터
        String base64Member=string;
        byte[] serializedMember = new byte[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            serializedMember = Base64.getDecoder().decode(base64Member);
        }
        try(ByteArrayInputStream bais = new ByteArrayInputStream(serializedMember)){
            try(ObjectInputStream ois = new ObjectInputStream(bais)){
                //역직렬화된 member객체를 읽어온다.
                Object objectMember = ois.readObject();
                float[] array = (float[])objectMember;
                return array;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
