package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.dao.HttpUtil;
import com.example.myapplication.dto.json.JsonDto;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class JoinStudyGroup extends AppCompatActivity {


    private EditText inviteKey;
    private Button joinButton;
    private TextView textView;
    private JSONObject jsonObject;
    private JSONObject parsing;
    private String server_url;
    private String json;
    private JsonDto jsonDto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_study_group);

        server_url = "http://15.165.219.73:2000/api/user/participateGroup";
        jsonObject = new JSONObject();
        textView = findViewById(R.id.goToAddGroup);
        joinButton = findViewById(R.id.inviteButton);
        inviteKey = findViewById(R.id.inviteKey);


        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inviteKey.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "키를 넣을 칸이 비어있어요.", Toast.LENGTH_LONG).show();
                }if(inviteKey.getText().toString().length()<36){
                    Toast.makeText(getApplicationContext(), "방의 공개키는 -, 숫자, 알파벳(소문자)를 포함한 36자리 여야만해요.", Toast.LENGTH_LONG).show();

                }else{
                    try {
                        jsonObject.put("key",inviteKey.getText().toString());
                        json = jsonObject.toString();
                        jsonDto = new HttpUtil().execute(server_url, json, getIntent().getStringExtra("token")).get();

                        if ((int) JoinStudyGroup.this.jsonDto.getHttpCode() != HttpsURLConnection.HTTP_OK) {
                            parsing = new JSONObject(JoinStudyGroup.this.jsonDto.getJson());
                            String errorField = parsing.getString("errorField");
                            String errorMessages = parsing.getString("errorMessages");
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            Toast.makeText(getApplicationContext(), errorField + errorMessages, Toast.LENGTH_LONG).show();
                            startActivity(intent);
                        }else {
                            Toast.makeText(getApplicationContext(), "정상적으로 참여되었어요.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(),StudyGroupMain.class);
                            intent.putExtra("token",jsonDto.getToken());
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),WriteGroupInfo.class);
                intent.putExtra("token",getIntent().getStringExtra("token"));
                startActivity(intent);
            }
        });


    }
}
