package com.example.parktaeim.dorothy.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.parktaeim.dorothy.R;
import com.skp.Tmap.TMapView;

/**
 * Created by parktaeim on 2017. 10. 12..
 */

public class NavigationActivity extends AppCompatActivity {

    private RelativeLayout beforeStartLayout;
    private RelativeLayout startLayout;
    private RelativeLayout startButton;
    private TMapView tMapView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        setLayout();
        setMap();

    }

    private void setMap() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.map_view);
        tMapView = new TMapView(this);
        relativeLayout.addView(tMapView);
        tMapView.setSKPMapApiKey(getString(R.string.tmap_app_key));

        tMapView.setCompassMode(true);    // 현재 보는 방향
        tMapView.setIconVisibility(true);   // 아이콘 표시
        tMapView.setZoomLevel(15);   // 줌레벨
        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);

    }

    private void setLayout(){
        beforeStartLayout = (RelativeLayout) findViewById(R.id.beforeStartNaviLayout);
        startLayout = (RelativeLayout) findViewById(R.id.startNaviLayout);
        startButton = (RelativeLayout) findViewById(R.id.startButton);

        startLayout.setVisibility(View.GONE);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beforeStartLayout.setVisibility(View.GONE);
                startLayout.setVisibility(View.VISIBLE);
            }
        });
    }
}
