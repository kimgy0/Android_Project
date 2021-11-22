package com.example.myapplication.dao;


import android.os.AsyncTask;
import android.util.Log;

import com.example.myapplication.dto.json.JsonDto;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtilNoReturn extends AsyncTask<String, String, JsonDto> {
    @Override
    protected JsonDto doInBackground(String... params) {
        try {
            String url = params[0];
            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

            conn.setReadTimeout(1000);
            conn.setConnectTimeout(1500);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("Authorization",params[2]);


            Log.d("param = ",params[1]);
            byte[] outputInBytes = params[1].getBytes("UTF-8");
            OutputStream os = conn.getOutputStream();
            os.write( outputInBytes );
            os.close();

            int code = conn.getResponseCode();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }




            JsonDto jsonInfo ;

            if(conn.getHeaderField("Authorization")!=null){
                jsonInfo = new JsonDto(code, "NoReturn", conn.getHeaderField("Authorization"));
            }else{
                jsonInfo = new JsonDto(code, "NoReturn", null);
            }
            return jsonInfo;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
