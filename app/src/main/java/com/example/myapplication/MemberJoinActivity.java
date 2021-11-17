package com.example.myapplication;

import android.content.Intent;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.dao.HttpUtil;
import com.example.myapplication.dto.json.JsonDto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import lombok.SneakyThrows;

public class MemberJoinActivity extends AppCompatActivity {

    private Button joinMember;
    private EditText username;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;

    private String json;
    private String server_url = "http://15.165.219.73:8080/api/all/join/";
    private HttpURLConnection conn;

    private OutputStream os;
    private InputStream is;
    private JSONObject jsonObject = new JSONObject();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_join);

        joinMember = (Button) findViewById(R.id.joinmember);
        joinMember.setOnClickListener(new View.OnClickListener() {
            @SneakyThrows
            @Override
            public void onClick(View v) {
                username = (EditText) findViewById(R.id.joinusername);
                email = (EditText) findViewById(R.id.joinemail);
                password = (EditText) findViewById(R.id.joinpassword);
                confirmPassword = (EditText) findViewById(R.id.joinconfirmpassword);

                conn = null;
                TextView confirm = (TextView) findViewById(R.id.test);

                if(username.getText().toString().equals("") | email.getText().toString().equals("") | password.getText().toString().equals("") | confirmPassword.getText().equals("")){
                    Toast.makeText(getApplicationContext(), "채워지지 않은 값이 존재합니다.", Toast.LENGTH_LONG).show();
                }else{
                    if (confirmPassword.getText().toString().equals(password.getText().toString()) & username.getText().toString().length() > 7 & username.getText().toString().length() < 20) {
                        try {
                            jsonObject.put("username", username.getText());
                            jsonObject.put("email", email.getText());
                            jsonObject.put("password", password.getText());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        json = jsonObject.toString();

                        JsonDto jsonDto = new HttpUtil().execute(server_url,json).get();

                        if(jsonDto.getHttpCode() != HttpsURLConnection.HTTP_OK){
                            JSONObject parsing = new JSONObject(jsonDto.getJson());
                            String errorField = parsing.getString("errorField");
                            String errorMessages = parsing.getString("errorMessages");
                            confirm.setText(errorField + " : " + errorMessages);
                        }else{
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    }
                }
            }
        });
    }
}


