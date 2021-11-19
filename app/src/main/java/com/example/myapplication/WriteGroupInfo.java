package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class WriteGroupInfo extends AppCompatActivity {

    Button createButton;
    NumberPicker numberPicker;
    TimePicker timePicker;
    EditText groupName;
    EditText comment;

    JSONObject jsonObject = new JSONObject();
    private String json;

    private String server_url = "http://15.165.219.73:8080/api/user/createGroup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_study_group);

        timePicker = findViewById(R.id.timepicker);
        numberPicker = findViewById(R.id.numberpicker);
        numberPicker.setMaxValue(10);
        numberPicker.setMinValue(1);
        numberPicker.setValue(1);

        createButton = findViewById(R.id.addgroup);

        

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(groupName.equals("") & comment.equals(""))){
                    try {
                        jsonObject.put("subject",groupName.getText());
                        jsonObject.put("comment",comment.getText());
                        jsonObject.put("subject",groupName.getText());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "다음으로 넘어갈 수 없습니다. 빈 칸이 있는지 확인해주세요.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
