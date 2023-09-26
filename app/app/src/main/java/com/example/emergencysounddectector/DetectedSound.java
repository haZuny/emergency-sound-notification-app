package com.example.emergencysounddectector;

import com.example.emergencysounddectector.SQLite.Serializer;

import java.io.Serializable;

public class DetectedSound implements Serializable {
    int id;
    String category;
    float percent_carhorn;
    float percent_dogbark;
    float percent_siren;
    float percent_none;
    float[] sound;
    String datetime;

    int bestPercent;

    public DetectedSound(int id, String category, float percent_carhorn, float percent_dogbark, float percent_siren, float percent_none, float[] soundBuf, String datetime){
        this.id = id;
        this.category = category;
        this.percent_carhorn = percent_carhorn;
        this.percent_dogbark = percent_dogbark;
        this.percent_siren = percent_siren;
        this.percent_none = percent_none;
        this.sound = soundBuf;
        this.datetime = datetime;

        switch (category){
            case "Car horn":
                bestPercent = (int)(percent_carhorn*100);
                break;
            case "Dog bark":
                bestPercent = (int)(percent_dogbark*100);
                break;
            case "Siren":
                bestPercent = (int)(percent_siren*100);
                break;
            case "None":
                bestPercent = (int)(percent_none*100);
                break;
            default:
                bestPercent = 0;
                break;
        }
    }
}
