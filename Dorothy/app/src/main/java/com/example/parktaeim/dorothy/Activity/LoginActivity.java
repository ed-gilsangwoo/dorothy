package com.example.parktaeim.dorothy.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.parktaeim.dorothy.APIUrl;
import com.example.parktaeim.dorothy.R;
import com.example.parktaeim.dorothy.RestAPI;

import jp.wasabeef.blurry.Blurry;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by parktaeim on 2017. 10. 1..
 */

public class LoginActivity extends AppCompatActivity {
    private ViewGroup backLayoout;
    private Button loginBtn;
    private EditText idEditText;
    private EditText pwEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        idEditText = (EditText) findViewById(R.id.idEditText);
        pwEditText = (EditText) findViewById(R.id.pwEditText);

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
                String id = idEditText.getText().toString();
                String pw = pwEditText.getText().toString();
                System.out.println(id+", "+pw);

                Retrofit builder = new Retrofit.Builder()
                        .baseUrl(APIUrl.API_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                RestAPI restAPI = builder.create(RestAPI.class);
                Call<Void> call = restAPI.login(id,pw);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        System.out.println("hello~!~!~!");
                        Log.d("response-----------",String.valueOf(response.code()));
//                        Intent intent = new Intent(LoginActivity.this,LoginActivity.class);
//                        startActivity(intent);
//                        finish();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });
            }
        });
    }
}
