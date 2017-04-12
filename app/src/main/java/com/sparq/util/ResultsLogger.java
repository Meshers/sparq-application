package com.sparq.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * Created by sarahcs on 4/12/2017.
 */


public class ResultsLogger {

    private File mResultsFile;
    private static final String FILE_NAME = "RESULTS_";

    public ResultsLogger(String quizName){
        mResultsFile = new File(Environment.getExternalStorageDirectory() + "/" + FILE_NAME + quizName
                + System.currentTimeMillis() + ".csv");
        try {
            mResultsFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            PrintWriter pw = new PrintWriter(mResultsFile);
            pw.println("User Name,Score");
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean writeResults(HashMap<Integer, Double> results) {

        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(mResultsFile, true));

            for (int userId : results.keySet()) {
                String line = userId + "," + results.get(userId);
                pw.println(line);
            }
            pw.close();
            return true;

        } catch (FileNotFoundException e) {
            Log.e("ResultLogger", "Failed writing results", e);
            return false;
        }
    }
}
