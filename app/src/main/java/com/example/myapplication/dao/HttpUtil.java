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

public class HttpUtil extends AsyncTask<String, String, JsonDto> {
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

            Log.d("param = ",params[1]);
            byte[] outputInBytes = params[1].getBytes("UTF-8");
            OutputStream os = conn.getOutputStream();
            os.write( outputInBytes );
            os.close();

            int code = conn.getResponseCode();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
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
            JsonDto jsonInfo = new JsonDto(code, res);
            return jsonInfo;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
