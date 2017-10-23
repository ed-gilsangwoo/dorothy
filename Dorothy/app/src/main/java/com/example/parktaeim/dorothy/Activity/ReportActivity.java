package com.example.parktaeim.dorothy.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.anton46.stepsview.StepsView;
import com.example.parktaeim.dorothy.APIUrl;
import com.example.parktaeim.dorothy.R;
import com.example.parktaeim.dorothy.RestAPI;
import com.kofigyan.stateprogressbar.StateProgressBar;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by parktaeim on 2017. 10. 6..
 */

public class ReportActivity extends AppCompatActivity {
    private static final String TAG = "ReportActivity";

    private StateProgressBar strengthBar;

    private String[] descData = {"1단계", "2단계", "3단계"};

    private Button stateButtons[] = new Button[3];
    private int state = 1;

    private Button strengthButtons[] = new Button[3];
    private int strength = 1;

    private double lon = 0;
    private double lat = 0;

    private Button submitButton;

    private Retrofit builder;
    private OkHttpClient client;
    private RestAPI restAPI;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        lat = getIntent().getDoubleExtra("lat", 0);
        lon = getIntent().getDoubleExtra("lon", 0);

        initStrengthBar();
        initStateButtons();
        initSubmitButton();
    }

    private void initSubmitButton() {
        client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();

        builder = new Retrofit.Builder()
                .baseUrl(APIUrl.API_BASE_URL).client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        restAPI = builder.create(RestAPI.class);

        submitButton = (Button) findViewById(R.id.btn_report_submit);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final SweetAlertDialog dialog = new SweetAlertDialog(ReportActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("주의!")
                        .setContentText("허위신고시 받는 불이익은 책임지지 않습니다.\n신고하시겠어요?")
                        .setConfirmText("예")
                        .setCancelText("아니오");

                dialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.cancel();
                    }
                });

                dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        report();
                        sweetAlertDialog.cancel();
                    }
                });

                dialog.show();
            }
        });
    }

    private void report() {
        HashMap<String, Object> fieldMap = new HashMap<>();

        fieldMap.put("id", "test01");
        fieldMap.put("lat", lat);
        fieldMap.put("lng", lon);
        fieldMap.put("kind", state);
        fieldMap.put("scope", strength);

        Call<Void> call = restAPI.report(fieldMap);
        Toast.makeText(getApplicationContext(), "CALLED", Toast.LENGTH_SHORT).show();
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                Log.d(TAG, "onResponse: succeed" + response.code());
                if (response.code() == 200) {
                    new SweetAlertDialog(ReportActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setContentText("신고가 접수되었습니다. 감사합니다.")
                            .setConfirmText("알겠습니다.").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.cancel();
                            finish();
                        }
                    })
                            .show();
                }
                if (response.code() == 400) {
                    new SweetAlertDialog(ReportActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setContentText("잘못된 요청입니다.")
                            .setConfirmText("알겠습니다.").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.cancel();
                        }
                    })
                            .show();
                }
                if (response.code() == 500) {
                    new SweetAlertDialog(ReportActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setContentText("서버 오류입니다.")
                            .setConfirmText("알겠습니다.").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.cancel();
                        }
                    })
                            .show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void initStrengthBar() {
        strengthBar = (StateProgressBar) findViewById(R.id.progress_bar);
        strengthBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
        strengthBar.setStateDescriptionData(descData);
        strengthBar.enableAnimationToCurrentState(true);

        strengthButtons[0] = (Button) findViewById(R.id.strength1);
        strengthButtons[1] = (Button) findViewById(R.id.strength2);
        strengthButtons[2] = (Button) findViewById(R.id.strength3);

        strengthButtons[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strength = 1;
                strengthBar.animate();
                strengthBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
                Toast.makeText(getApplicationContext(), strength + "", Toast.LENGTH_SHORT).show();
            }
        });

        strengthButtons[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strength = 2;
                strengthBar.animate();
                strengthBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                Toast.makeText(getApplicationContext(), strength + "", Toast.LENGTH_SHORT).show();
            }
        });

        strengthButtons[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strength = 3;
                strengthBar.animate();
                strengthBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                Toast.makeText(getApplicationContext(), strength + "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initStateButtons() {
        stateButtons[0] = (Button) findViewById(R.id.btn_roadDamage);
        stateButtons[1] = (Button) findViewById(R.id.btn_flood);
        stateButtons[2] = (Button) findViewById(R.id.btn_landslide);

        stateButtons[0].setBackgroundColor(getResources().getColor(R.color.colorPrimary, getTheme()));
        stateButtons[0].setTextColor(getResources().getColor(android.R.color.white, getTheme()));

        for (int i = 0; i < stateButtons.length; i++) {
            final int finalI = i;
            stateButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    state = finalI + 1;
                    for (int j = 0; j < 3; j++) {
                        if (j + 1 == state) {
                            stateButtons[j].setBackgroundColor(getResources().getColor(R.color.colorPrimary, getTheme()));
                            stateButtons[j].setTextColor(getResources().getColor(android.R.color.white, getTheme()));
                        } else {
                            stateButtons[j].setBackgroundColor(getResources().getColor(android.R.color.white, getTheme()));
                            stateButtons[j].setTextColor(getResources().getColor(R.color.gray, getTheme()));
                        }
                    }
                    Toast.makeText(getApplicationContext(), state + "", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
