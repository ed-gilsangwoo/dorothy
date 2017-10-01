package com.example.parktaeim.dorothy.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.parktaeim.dorothy.R;

import jp.wasabeef.blurry.Blurry;

/**
 * Created by parktaeim on 2017. 10. 1..
 */

public class LoginActivity extends AppCompatActivity {
    private ViewGroup backLayoout;
    private Button loginBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        backLayoout = (ViewGroup) findViewById(R.id.backLayout);
        backLayoout.post(new Runnable() {   // 백그라운드 이미지 블러 효과
            @Override
            public void run() {
                Blurry.with(LoginActivity.this)
                        .radius(10)
                        .sampling(4)
                        .async()
                        .onto(backLayoout);
            }
        });

        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
