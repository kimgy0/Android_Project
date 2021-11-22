package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class JoinStudyGroup extends AppCompatActivity {


    EditText inviteKey;
    Button joinButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_study_group);
    }
}
