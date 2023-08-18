package com.example.emergency_sound_detector;

public class GlobalObj {
    // running state var
    static private boolean bool_isRecording = false;
    static public boolean get_isRecording(){return bool_isRecording;}
    static public void setBool_isRecording(boolean var){bool_isRecording = var;}

    // recording buffer
    public static float[] floatArr_recordingBuffer = new float[200];


}
