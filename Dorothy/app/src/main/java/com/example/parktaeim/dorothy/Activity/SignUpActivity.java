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

public class SignUpActivity extends AppCompatActivity {
    private ViewGroup backLayoout;
    private Button signupBtn;
    private EditText idEditText;
    private EditText pwEditText;
    private EditText phoneEditText;
    private EditText nameEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        idEditText = (EditText) findViewById(R.id.idEditText);
        pwEditText = (EditText) findViewById(R.id.pwCheckEditText);
        phoneEditText = (EditText) findViewById(R.id.phoneEditText);
        nameEditText = (EditText) findViewById(R.id.nameEditText);

        backLayoout = (ViewGroup) findViewById(R.id.backLayout);
        backLayoout.post(new Runnable() {   // 백그라운드 이미지 블러 효과
            @Override
            public void run() {
                Blurry.with(SignUpActivity.this)
                        .radius(10)
                        .sampling(4)
                        .async()
                        .onto(backLayoout);
            }
        });

        signupBtn = (Button) findViewById(R.id.signUpBtn);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String id = idEditText.getText().toString();
                String pw = pwEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                String name = nameEditText.getText().toString();


                Retrofit builder = new Retrofit.Builder()
                        .baseUrl(APIUrl.API_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                RestAPI restAPI = builder.create(RestAPI.class);
                Call<Void> call = restAPI.signUp(id,pw,phone,name);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Log.d("response-----------",response.body().toString());
//                        Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
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
