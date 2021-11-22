package com.example.myapplication;

import android.content.Intent;
import android.media.TimedText;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.dao.HttpUtil;
import com.example.myapplication.dao.HttpUtilNoReturn;
import com.example.myapplication.dto.json.JsonDto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import lombok.val;

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




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<Map<String,String>> list = new ArrayList<>();
        Map<String,String> map = new HashMap<>();
        JSONObject jsonObject = new JSONObject();

        setContentView(R.layout.activity_add_study_group);

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
                if (list.size() > 10 ){
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
                    timeText.setText(timeText.getText().toString()+"   "+Hour+":"+Minute);
                    map.put("localTime",Hour+":"+Minute);
                    list.add(map);
                }
            }
        });

        createButton = findViewById(R.id.addgroup);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "onClick method start", Toast.LENGTH_LONG).show();
                if(!(groupName.equals("") && comment.equals(""))){
                    try {
                        jsonObject.put("subject",groupName.getText());
                        jsonObject.put("comment",comment.getText());
                        if (!(list.size()<2 && list.size()>10)){
                            jsonObject.put("certifyNumber",list.size());
                            JSONArray jsonArray= new JSONArray(list);
                            jsonObject.put("localTimeList", jsonArray);

                            json = jsonObject.toString();
                            JsonDto jsonDto = new HttpUtil().execute(server_url, json,getIntent().getStringExtra("token")).get();
                            Toast.makeText(getApplicationContext(), json, Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(), jsonDto.getHttpCode() + jsonDto.getToken(), Toast.LENGTH_LONG).show();


                            if(jsonDto.getHttpCode() != HttpsURLConnection.HTTP_OK){
                                JSONObject parsing = new JSONObject(jsonDto.getJson());
                                String errorField = parsing.getString("errorField");
                                String errorMessages = parsing.getString("errorMessages");
                                textView.setText(errorMessages);
                            }else {


                                Toast.makeText(getApplicationContext(),"정상통과" , Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(),StudyGroupMain.class);
                                intent.putExtra("token",jsonDto.getToken());
                                startActivity(intent);


                            }
                        }else{
                            Toast.makeText(getApplicationContext(), "시간은 2개 이상 10개 이하로 해주세요.", Toast.LENGTH_LONG).show();
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
