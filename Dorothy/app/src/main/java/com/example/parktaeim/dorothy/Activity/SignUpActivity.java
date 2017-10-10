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
import android.widget.Toast;

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

public class SignUpActivity extends AppCompatActivity {
    private ViewGroup backLayoout;
    private Button signupBtn;
    private EditText idEditText;
    private EditText pwEditText;
    private EditText phoneEditText;
    private EditText nameEditText;
    private EditText pwCheckEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        idEditText = (EditText) findViewById(R.id.idEditText);
        pwEditText = (EditText) findViewById(R.id.pwEditText);
        phoneEditText = (EditText) findViewById(R.id.phoneEditText);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        pwCheckEditText = (EditText) findViewById(R.id.pwCheckEditText);

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
                String pwCheck = pwCheckEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                String name = nameEditText.getText().toString();

                System.out.println("name: " + name + "  id: " + id + "  pw: " + pw + "  pwCheck: " + pwCheck + "  Phone: " + phone);


                //edittext에 id나 pw,pwcheck가 비어있거나 성별이 선택되지 않았을때 토스트 띄워줌.
                if (name == null || name.length() == 0) {
                    Toast.makeText(SignUpActivity.this, "이름을 입력해주세요 !", Toast.LENGTH_SHORT).show();
                    return;
                } else if (id == null || id.length() == 0) {
                    Toast.makeText(SignUpActivity.this, "아이디를 입력해주세요!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (pw == null || pw.length() == 0) {
                    Toast.makeText(SignUpActivity.this, "비밀번호를 입력해주세요!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (pwCheck == null || pwCheck.length() == 0) {
                    Toast.makeText(SignUpActivity.this, "비밀번호를 한번 더 체크해주세요!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (phone == null || phone.length() == 0) {
                    Toast.makeText(SignUpActivity.this, "연락처를 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pw.equals(pwCheck)) {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(100, TimeUnit.SECONDS)
                            .readTimeout(100, TimeUnit.SECONDS).build();

                    Retrofit builder = new Retrofit.Builder()
                            .baseUrl(APIUrl.API_BASE_URL).client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RestAPI restAPI = builder.create(RestAPI.class);
                    Call<Void> call = restAPI.signUp(id, pw, phone, name);

                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Log.d("response.code", String.valueOf(response.code()));

                            if (response.code() == 200) {
                                Toast.makeText(SignUpActivity.this, "회원가입을 축하드립니다!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (response.code() == 201) {
                                Toast.makeText(SignUpActivity.this, "이미 사용중인 아이디입니다.", Toast.LENGTH_SHORT).show();
                            } else if (response.code() == 400) {
                                Toast.makeText(SignUpActivity.this, "실패", Toast.LENGTH_SHORT).show();

                            } else if (response.code() == 500) {
                                Toast.makeText(SignUpActivity.this, "서버 실패", Toast.LENGTH_SHORT).show();

                            }

                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.d("오류!!!!!!!!!!!!!", t.toString());
                        }
                    });
                } else {
                    Toast.makeText(SignUpActivity.this, "비밀번호와 비밀번호 체크 값이 다릅니다!", Toast.LENGTH_SHORT).show();
                    return;
                }


            }
        });
    }
}
