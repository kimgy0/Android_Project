package com.example.myapplication.dao;

import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;

import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class HttpUtilMultiPart extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... strings) {


        /*
        strings[0] = server_url;
        strings[1] = filepath
        strings[2] = groupId
        strings[3] = token
         */

        String lineEnd = "\r\n";
        String boundary = UUID.randomUUID().toString();
        String resp = null;

        File imageFile = new File(strings[1]);

        byte[] buffer;
        int maxBufferSize = 5 * 1024 * 1024;
        HttpURLConnection conn;
        String server_url = strings[0];


        try {
            URL connectUrl = new URL(server_url);
            FileInputStream fi = new FileInputStream(strings[1]);
            conn = (HttpURLConnection) connectUrl.openConnection();

            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("cache-control", "no-cache");
            conn.setRequestProperty("cache-length", "length");
            conn.setRequestProperty("Authorization",strings[3]);
            conn.connect();

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            //text 전송
            dos.writeBytes("\r\n--" + boundary + "\r\n");
            dos.writeBytes("Content-Disposition: form-data; name=\"groupId\"\r\n\r\n" + strings[2]);
            //Image 전송
            dos.writeBytes("--" + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"imageFile\";filename=\"" + imageFile.getAbsolutePath() + "\"" + lineEnd +"Content-Type: image/png");
            dos.writeBytes(lineEnd);


            int bytesAvailable = fi.available();
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            int bytesRead = fi.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fi.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fi.read(buffer, 0, bufferSize);
                dos.flush();
            }
            dos.writeBytes(lineEnd);
            dos.writeBytes("--" + boundary + "--" + lineEnd);
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return conn.getHeaderField("Authorization");
            } else {
                return null;
            }//실패
        } catch (MalformedURLException e) {
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }


        return null;
    }
}
