package com.emmahc.smartchair.BLEModule;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class analyzingData {
    public static void recordingInputFile(String input_data) throws IOException {
        String file_name = "C:/Users/kramf/Desktop/data/input_data.txt";
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(file_name, true));
            Log.d("buffer created","buffer created");
            writer.write(input_data);
            writer.flush();
            writer.close();
            Log.d("writed","writed");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
