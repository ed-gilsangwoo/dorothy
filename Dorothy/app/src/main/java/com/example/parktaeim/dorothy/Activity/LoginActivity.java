package com.example.parktaeim.dorothy.Activity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.parktaeim.dorothy.APIUrl;
import com.example.parktaeim.dorothy.R;
import com.example.parktaeim.dorothy.RestAPI;

import java.util.concurrent.TimeUnit;

import jp.wasabeef.blurry.Blurry;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by parktaeim on 2017. 10. 1..
 */

public class LoginActivity extends AppCompatActivity {
    private ImageView backgroundImg;
    private Button loginBtn;
    private EditText idEditText;
    private EditText pwEditText;
    private String id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        idEditText = (EditText) findViewById(R.id.idEditText);
        pwEditText = (EditText) findViewById(R.id.pwEditText);

        backgroundImg = (ImageView) findViewById(R.id.img_blur_login);
        Glide.with(this).load(R.drawable.img_blur_login).into(backgroundImg);

        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = idEditText.getText().toString();
                String pw = pwEditText.getText().toString();
                System.out.println(id+", "+pw);

                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(100, TimeUnit.SECONDS)
                        .readTimeout(100, TimeUnit.SECONDS).build();

                Retrofit builder = new Retrofit.Builder()
                        .baseUrl(APIUrl.API_BASE_URL).client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                RestAPI restAPI = builder.create(RestAPI.class);
                Call<Void> call = restAPI.login(id,pw);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        System.out.println("hello~!~!~!");
                        Log.d("response-----------",String.valueOf(response.code()));

                        if(response.code() == 200){
                            Toast.makeText(LoginActivity.this, id + "님 환영합니다!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else if(response.code() == 201){
                            Toast.makeText(LoginActivity.this,"로그인 실패",Toast.LENGTH_SHORT).show();
                        }else if(response.code() == 400){
                            Toast.makeText(LoginActivity.this,"실패",Toast.LENGTH_SHORT).show();

                        }else if(response.code() == 500){
                            Toast.makeText(LoginActivity.this,"서버 실패",Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.d("오류랍니당~~~~",t.toString());
                    }
                });
            }
        });
    }
}
