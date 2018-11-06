package com.example.tech.coinz;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.http.Url;

public class DownloadCompleteRunner {

    private static final String TAG = "DownloadCompleteRunner";

    static String result;

    public static void downloadComplete(String result){
        DownloadCompleteRunner.result = result;
        Log.d(TAG, result);
    }
}


