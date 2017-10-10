package com.example.parktaeim.dorothy.Activity;

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
import com.example.parktaeim.dorothy.R;
import com.kofigyan.stateprogressbar.StateProgressBar;

/**
 * Created by parktaeim on 2017. 10. 6..
 */

public class ReportActivity extends AppCompatActivity implements View.OnClickListener{
    private boolean btn1Pressed = false, btn2Pressed = false, btn3Pressed = false;
    private Button button1;
    private Button button2;
    private Button button3;
    private StateProgressBar progressBar;
    String[] descData = {"1단계","2단계","3단계"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        progressBar = (StateProgressBar) findViewById(R.id.progress_bar);

        progressBar.setStateDescriptionData(descData);

        button1.setOnClickListener((View.OnClickListener)this);
        button2.setOnClickListener((View.OnClickListener)this);
        button3.setOnClickListener((View.OnClickListener)this);


        progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("view----------",view.toString());
            }
        });


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button1:
                break;
            case R.id.button2:
                break;
            case R.id.button3:
                break;
        }
    }
}
