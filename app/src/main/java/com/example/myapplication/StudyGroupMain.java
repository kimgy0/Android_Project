package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.net.ssl.HttpsURLConnection;


//activity_study_group_main
public class StudyGroupMain extends AppCompatActivity {
    private LinearLayout myGroup;
    private LinearLayout myGoal;
    TextView view;
    private String server_url = "http://15.165.219.73:2000/api/user/printGroups";
    private Button createGroup;
    private Button joinGroup;
    JsonDto jsonDto;
    JSONObject parsing;
    private JSONObject jsonObject = new JSONObject();
    private JSONArray data;
    private int absent;
    private int tardy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_group_main);
        myGoal = findViewById(R.id.mygoal);
        myGroup = findViewById(R.id.groupList);
        absent = 0;
        tardy = 0;



        try {
            Toast.makeText(getApplicationContext(), server_url+getIntent().getStringExtra("token"), Toast.LENGTH_LONG).show();
            jsonDto = new HttpUtilGet().execute(server_url, getIntent().getStringExtra("token")).get();
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


                List<JSONObject> jsonItem = IntStream.range(0, data.length()).mapToObj(index -> {
                    try {
                        return (JSONObject) data.get(index);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } return null;
                }).collect(Collectors.toList());


                jsonItem.forEach( item -> {
                    try {

                        if(item.getBoolean("master")){
                            addTextView(item.getString("inviteKey") + " 방의 초대키     " +item.getString("groupName") + "  \uD83C\uDFA9", myGroup,item.getString("groupName"));
                        }else{
                            addTextView(item.getString("inviteKey") + " 방의 초대키      " +item.getString("groupName"), myGroup,item.getString("groupName"));
                        }

                        absent += item.getInt("absent");
                        tardy += item.getInt("tardy");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });

                addTextView("나의 지각 횟수는 " + tardy +"회 이며 \n"+" 그로 인한 결석 횟수는 " + absent + " 회 입니다.", myGoal, null);

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
                intent.putExtra("token",jsonDto.getToken());
                startActivity(intent);
            }
        });
        //그룹 참가
        joinGroup = findViewById(R.id.joinGroup);
        joinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), JoinStudyGroup.class);
                intent.putExtra("token",jsonDto.getToken());
                startActivity(intent);
            }
        });

    }

    public void addTextView(String a,LinearLayout l, String key){
        view = new TextView(this);
        view.setText(a);
        view.setTextColor(Color.BLACK);
        if(key != null){
            view.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), StudyGroup.class);
                    intent.putExtra("token",jsonDto.getToken());
                    intent.putExtra("key",key);
                    startActivity(intent);
                }
            });
        }

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        view.setLayoutParams(lp);
        view.setGravity(Gravity.CENTER);

        if(l != null){
            l.addView(view);
        }
    }
}
