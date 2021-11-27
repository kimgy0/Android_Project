package com.example.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.alarm.Alarm;
import com.example.myapplication.dao.HttpUtil;
import com.example.myapplication.dto.json.JsonDto;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class WriteGroupInfo extends AppCompatActivity {

    Button createButton;
    Button addTimeButton;
    TimePicker timePicker;
    TextView textView;
    TextView timeText;
    EditText groupName;
    EditText comment;


    private String json;
    private String server_url = "http://15.165.219.73:2000/api/user/createGroup";
    private String Hour;
    private String Minute;


    private AlarmManager alarmManager;


    private int alarmHour;
    private int alarmMinute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_study_group);

        List<Map<String,String>> list = new ArrayList<>();
        Map<String,String> map = new HashMap<>();
        List<Integer> alarmHourTemp = new ArrayList<>();
        List<Integer> alarmMinuteTemp = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();


        textView = findViewById(R.id.error);
        addTimeButton = findViewById(R.id.time_picker_add);
        timePicker = findViewById(R.id.time_picker);
        timeText = findViewById(R.id.timeText);

        groupName = findViewById(R.id.subject);
        comment = findViewById(R.id.comment);

        timePicker.setIs24HourView(true);
        addTimeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!(groupName.getText().toString().length()==0  | comment.getText().toString().length()==0)){
                    if (list.size() > 9 ){
                        Toast.makeText(getApplicationContext(), "열개 이상은 등록이 불가능합니다.", Toast.LENGTH_LONG).show();
                    }else{

                        if(timePicker.getHour() < 10){
                            Hour = 0+String.valueOf(timePicker.getHour());
                        }else{
                            Hour = String.valueOf(timePicker.getHour());
                        }
                        if(timePicker.getMinute() < 10){
                            Minute = 0+String.valueOf(timePicker.getMinute());
                        }else{
                            Minute = String.valueOf(timePicker.getMinute());
                        }

                        alarmHourTemp.add(timePicker.getHour());
                        alarmMinuteTemp.add(timePicker.getMinute());

                        timeText.setText(timeText.getText().toString()+"   "+Hour+":"+Minute);
                        map.put("localTime",Hour+":"+Minute);
                        list.add(map);
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "그룹의 이름과 그룹의 설명은 필수 항목 입니다.", Toast.LENGTH_LONG).show();
                }
            }
        });

        createButton = findViewById(R.id.addgroup);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(groupName.getText().toString().length()==0  | comment.getText().toString().length()==0)){
                    //Toast.makeText(getApplicationContext(), !(groupName.equals("") && comment.equals(""))+"1"+groupName.getText().toString()+"1"+comment.getText().toString()+"1", Toast.LENGTH_LONG).show();
                    try {
                        jsonObject.put("subject",groupName.getText());
                        jsonObject.put("comment",comment.getText());
                        if (list.size()<2 | list.size()>10){
                            Toast.makeText(getApplicationContext(), "시간은 2개 이상 10개 이하로 해주세요.", Toast.LENGTH_LONG).show();
                        }else{
                            jsonObject.put("certifyNumber",list.size());
                            JSONArray jsonArray= new JSONArray(list);
                            jsonObject.put("localTimeList", jsonArray);

                            json = jsonObject.toString();
                            JsonDto jsonDto = new HttpUtil().execute(server_url, json,getIntent().getStringExtra("token")).get();
                            Toast.makeText(getApplicationContext(), json, Toast.LENGTH_LONG).show();

                            Intent alarmIntent = new Intent(getApplicationContext(),Alarm.class);
                            PendingIntent pIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,alarmIntent, 0);


                            for(int i=0; i<map.size(); i++){
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    alarmHour = alarmHourTemp.get(i);
                                    alarmMinute = alarmMinuteTemp.get(i);
                                }
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.HOUR_OF_DAY, alarmHour);
                                calendar.set(Calendar.MINUTE, alarmMinute);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);
                                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),  AlarmManager.INTERVAL_DAY, pIntent);
                            }


                            if(jsonDto.getHttpCode() != HttpsURLConnection.HTTP_OK){
                                JSONObject parsing = new JSONObject(jsonDto.getJson());
                                String errorField = parsing.getString("errorField");
                                String errorMessages = parsing.getString("errorMessages");
                                textView.setText(errorMessages);
                            }else {

                                Intent intent = new Intent(getApplicationContext(),StudyGroupMain.class);
                                intent.putExtra("token",jsonDto.getToken());
                                startActivity(intent);
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "그룹의 이름과 그룹의 설명은 필수 항목 입니다.", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

}
