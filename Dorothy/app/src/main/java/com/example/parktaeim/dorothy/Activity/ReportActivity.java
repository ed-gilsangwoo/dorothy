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

public class ReportActivity extends AppCompatActivity {

    private StateProgressBar strengthBar;

    private String[] descData = {"1단계", "2단계", "3단계"};

    private Button stateButtons[] = new Button[3];
    private int state = 1;

    private Button strengthButtons[] = new Button[3];
    private int strength = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        initStrengthBar();
        initStateButtons();
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
                strengthBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
                Toast.makeText(getApplicationContext(), strength + "", Toast.LENGTH_SHORT).show();
            }
        });

        strengthButtons[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strength = 2;
                strengthBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                Toast.makeText(getApplicationContext(), strength + "", Toast.LENGTH_SHORT).show();
            }
        });

        strengthButtons[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strength = 3;
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
