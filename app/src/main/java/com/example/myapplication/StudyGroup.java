package com.example.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.alarm.Alarm;
import com.example.myapplication.dao.HttpUtilGet;
import com.example.myapplication.dto.json.JsonDto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.net.ssl.HttpsURLConnection;

public class StudyGroup extends AppCompatActivity {

    static TextView view;
    TextView groupName;
    TextView comment;
    TextView absentAndTardy;
    TextView pictureAll;

    LinearLayout userList;


    Button alarm;
    Button noAlarm;
    Button delete;
    Button pictureShot;
    Button modify;
    JsonDto jsonDto;
    JSONObject parsing;
    JSONArray jsonArray;

    String key;
    int tardy;
    int hour;
    int minute;
    int absent;

    String server_url = "http://15.165.219.73:2000/api/user/printInGroup/";
    String server_delete_url = "http://15.165.219.73:2000/api/user/delete/";
    String server_alarm_url = "http://15.165.219.73:2000/api/user/alarm/";

    private Intent alarmIntent;
    private AlarmManager alarmManager;

    private Calendar calendar;
    int i = 0;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_group);

        userList = findViewById(R.id.in_groupMemberList);
        key = getIntent().getStringExtra("key");
        absentAndTardy = findViewById(R.id.in_groupAbsentCount);
        groupName = findViewById(R.id.in_groupName);
        comment = findViewById(R.id.in_groupComment);
        server_url += key;
        server_alarm_url+=key;

        alarm = findViewById(R.id.alarm);
        noAlarm = findViewById(R.id.noAlarm);



        if(alarmManager == null){
            noAlarm.setEnabled(false);
        }

        try {

            jsonDto = new HttpUtilGet().execute(server_url, getIntent().getStringExtra("token")).get();

            if ((int) jsonDto.getHttpCode() != HttpsURLConnection.HTTP_OK) {
                parsing = new JSONObject(jsonDto.getJson());
                String errorField = parsing.getString("errorField");
                String errorMessages = parsing.getString("errorMessages");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                Toast.makeText(getApplicationContext(), errorField + errorMessages, Toast.LENGTH_LONG).show();
                startActivity(intent);
            } else {

                parsing = new JSONObject(jsonDto.getJson());
                groupName.setText(parsing.getString("groupName"));
                comment.setText(parsing.getString("comment"));
                jsonArray = parsing.getJSONArray("users");

                List<JSONObject> collect = IntStream.range(0, jsonArray.length()).mapToObj(index -> {
                    try {
                        return (JSONObject) jsonArray.get(index);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).collect(Collectors.toList());

                collect.forEach(item -> {
                    try {
                        // checksum of absent and tardy for find user
                        if(item.getBoolean("checkMe") == true){
                            absent = item.getInt("absent");
                            tardy = item.getInt("tardy");
                            absentAndTardy.setText("나의 지각 횟수는" + tardy +"회 이며\n"+" 그로 인한 결석 횟수는" + absent + " 회 입니다.");
                        }
                        //user list
                        addTextView(item.getLong("userId")+"  "+item.getString("username"),userList,item.getBoolean("master"));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }



        alarm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    jsonDto = new HttpUtilGet().execute(server_alarm_url, getIntent().getStringExtra("token")).get();
                    if ((int) jsonDto.getHttpCode() != HttpsURLConnection.HTTP_OK) {
                        Toast.makeText(getApplicationContext(), "ㅆㅂ", Toast.LENGTH_LONG).show();
                        parsing = new JSONObject(jsonDto.getJson());
                        String errorField = parsing.getString("errorField");
                        String errorMessages = parsing.getString("errorMessages");
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                        Toast.makeText(getApplicationContext(), errorField + errorMessages, Toast.LENGTH_LONG).show();
                        startActivity(intent);
                    } else {


                        parsing = new JSONObject(jsonDto.getJson());
                        JSONArray data = parsing.getJSONArray("data");
                        List<JSONObject> collect = IntStream.range(0, data.length()).mapToObj(index -> {
                            try {
                                return (JSONObject) data.get(index);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }).collect(Collectors.toList());

                        collect.forEach(item -> {
                            try {
                                hour=item.getInt("hour");
                                minute=item.getInt("minute");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(System.currentTimeMillis());

                            if (calendar.before(Calendar.getInstance())) {
                                calendar.add(Calendar.DATE, 1);
                            }

                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE, minute);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);

                            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                            Intent AlarmIntent = new Intent(getApplicationContext(), Alarm.class);
                            PendingIntent pIntent = PendingIntent.getBroadcast(getApplicationContext(), i, AlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            i++;

                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pIntent);

                        });
                        Toast.makeText(getApplicationContext(),"알람 변경이 완료되었어요!",Toast.LENGTH_LONG).show();
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }


            }
        });



        noAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(int i = 0; i<10; i++){
                    alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent AlarmIntent = new Intent(getApplicationContext(), Alarm.class);
                    PendingIntent cancelIntent=PendingIntent.getBroadcast(getApplicationContext(), i, AlarmIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.cancel(cancelIntent);
                }
            }
        });

        delete = findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StudyGroup.this);
                builder.setTitle("정말로 탈퇴하실건가요?");;
                builder.setPositiveButton("탈퇴하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            JsonDto jsonDeleteDto = new HttpUtilGet().execute(server_delete_url + key, jsonDto.getToken()).get();
                            if(jsonDeleteDto.getHttpCode() == HttpsURLConnection.HTTP_OK){
                                Toast.makeText(getApplicationContext(),"탈퇴 되었습니다.",Toast.LENGTH_LONG);
                                Intent intent = new Intent(getApplicationContext(),StudyGroupMain.class);
                                intent.putExtra("token", jsonDto.getToken());
                                intent.putExtra("key", key);
                                startActivity(intent);
                            }else{
                                Toast.makeText(getApplicationContext(),"비정상적인 탈퇴 접근입니다.",Toast.LENGTH_LONG);
                                dialog.cancel();
                            }
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        finish();
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertD = builder.create();
                alertD.show();
            }
        });


        pictureShot = findViewById(R.id.goToPicture);
        pictureShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),PictureSend.class);
                intent.putExtra("token", jsonDto.getToken());
                intent.putExtra("key", key);
                startActivity(intent);
            }
        });
    }
    public void addTextView(String a, LinearLayout l,boolean b){
        view = new TextView(this);

        if(b){
            view.setText(a + "\uD83C\uDFF0");
        }else {
            view.setText(a);
        }
        view.setTextColor(Color.BLACK);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        view.setGravity(Gravity.CENTER);
        view.setLayoutParams(lp);

        l.addView(view);
    }
}
