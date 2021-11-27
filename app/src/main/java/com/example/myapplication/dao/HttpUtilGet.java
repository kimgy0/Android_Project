package com.example.myapplication.dao;

import android.os.AsyncTask;

import com.example.myapplication.dto.json.JsonDto;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtilGet extends AsyncTask<String, String, JsonDto> {
    @Override
    protected JsonDto doInBackground(String... params) {
        try {
            String url = params[0];
            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

            conn.setConnectTimeout(10000);
            conn.setReadTimeout(1000);
            conn.setConnectTimeout(1500);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("Authorization",params[1]);
            conn.connect();

            int code = conn.getResponseCode();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode() + conn.getRequestMethod());
            }


            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = br.readLine()) != null) {
                response.append(line);
                response.append(' ');
            }
            br.close();

            String res = response.toString();
            JsonDto jsonInfo ;


            if(res == null){
                jsonInfo = new JsonDto(code, null, conn.getHeaderField("Authorization"));
            }else{
                jsonInfo = new JsonDto(code, res, conn.getHeaderField("Authorization"));
            }

            return jsonInfo;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}