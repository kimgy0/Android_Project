package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.dao.HttpUtil;
import com.example.myapplication.dao.HttpUtilGet;
import com.example.myapplication.dto.json.JsonDto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;


//activity_study_group_main
public class StudyGroupMain extends AppCompatActivity {
    String server_url = "http://15.165.219.73:2000/api/user/printGroups";
    Button createGroup;
    Button joinGroup;
    JsonDto jsonDto;
    JSONObject jsonObject = new JSONObject();
    JSONObject parsing;
    JSONArray data;
    LinearLayout linearLayout;
    LinearLayout linearLayoutGoal;
    TextView groupName;
    TextView goal;
    int absent;
    int tardy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_group_main);
        try {
            jsonDto = new HttpUtilGet().execute(server_url, getIntent().getStringExtra("token")).get();
            Toast.makeText(getApplicationContext(), jsonDto.toString(), Toast.LENGTH_LONG).show();
            if ((int) jsonDto.getHttpCode() != HttpsURLConnection.HTTP_OK) {
                parsing = new JSONObject(jsonDto.getJson());
                String errorField = parsing.getString("errorField");
                String errorMessages = parsing.getString("errorMessages");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                Toast.makeText(getApplicationContext(), errorField + errorMessages, Toast.LENGTH_LONG).show();
                startActivity(intent);
            }else{
                parsing = new JSONObject(jsonDto.getJson());
                data = parsing.getJSONArray("data");
                Toast.makeText(getApplicationContext(), jsonDto.getJson(), Toast.LENGTH_LONG).show();
            }
        }catch(Exception e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }









        createGroup = (Button) findViewById(R.id.createGroup);
        //그룹 만들기
        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WriteGroupInfo.class);
                intent.putExtra("token",getIntent().getStringExtra("token"));
                startActivity(intent);
            }
        });
        //그룹 참가
        joinGroup = findViewById(R.id.joinGroup);
        joinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WriteGroupInfo.class);
                intent.putExtra("token",getIntent().getStringExtra("token"));
                startActivity(intent);
            }
        });
    }
}
