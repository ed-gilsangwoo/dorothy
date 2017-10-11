package com.example.parktaeim.dorothy.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.parktaeim.dorothy.R;


import jp.wasabeef.blurry.Blurry;

/**
 * Created by parktaeim on 2017. 10. 1..
 */

public class StartActivity extends AppCompatActivity {
    private ViewGroup backLayoout;
    private Button signupBtn;
    private TextView loginTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        backLayoout = (ViewGroup) findViewById(R.id.backLayout);
        backLayoout.post(new Runnable() {   // 백그라운드 이미지 블러 효과
            @Override
            public void run() {
                Blurry.with(StartActivity.this)
                        .radius(10)
                        .sampling(4)
                        .async()
                        .onto(backLayoout);
            }
        });


        //회원가입 화면으로 이동
        signupBtn = (Button) findViewById(R.id.signUpBtn);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signupIntent = new Intent(StartActivity.this,SignUpActivity.class);
                startActivity(signupIntent);
            }
        });

        //로그인 화면으로 이동
        loginTextView = (TextView) findViewById(R.id.loginTextView);
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(StartActivity.this,LoginActivity.class);
                startActivity(loginIntent);
            }
        });


    }






}
