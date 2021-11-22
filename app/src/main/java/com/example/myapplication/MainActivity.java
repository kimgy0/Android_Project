package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.dao.HttpUtil;
import com.example.myapplication.dto.json.JsonDto;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

//activity_main
public class MainActivity extends AppCompatActivity {

    private TextView regText;
    private TextView errorText;
    private EditText username;
    private EditText password;
    private Button button;

    JSONObject jsonObject = new JSONObject();
    private String json;
    private String server_url = "http://15.165.219.73:2000/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        errorText = (TextView) findViewById(R.id.nologin);


        button = (Button) findViewById(R.id.login);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("username", username.getText().toString());
                    jsonObject.put("password", password.getText().toString());
                    if(username.getText().toString().equals("") | password.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(), "빈칸이 존재합니다.", Toast.LENGTH_LONG).show();
                    }else{
                        json = jsonObject.toString();
                        try {
                            JsonDto jsonDto = new HttpUtil().execute(server_url, json, null).get();
                            if(jsonDto.getHttpCode() != HttpURLConnection.HTTP_OK){
                                errorText.setText("not matching id or password");
                            }else{
                                Intent intent = new Intent(getApplicationContext(), StudyGroupMain.class);
                                intent.putExtra("token",jsonDto.getToken());
                                startActivity(intent);
                            }
                        } catch (ExecutionException e) {
                            errorText.setText("not matching id or password");
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            errorText.setText("not matching id or password");
                            e.printStackTrace();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        regText = (TextView) findViewById(R.id.register);
        regText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MemberJoinActivity.class);
                startActivity(intent);
            }
        });
    }


}