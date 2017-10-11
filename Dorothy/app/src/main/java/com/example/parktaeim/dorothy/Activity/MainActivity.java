package com.example.parktaeim.dorothy.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.example.parktaeim.dorothy.R;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;


import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private boolean mTrackingMode = true;

    private double latitude;
    private double longitude;
    private EditText destEditText;
    private ImageView searchIcon;
    private String destination;
    private TMapView tMapView;
    private TMapGpsManager tMapGps;
    
    final static String APIKEY = "f6d6e268-7e09-331c-9753-e1a48087d569";

    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS
    };
    private static final String[] LOCATION_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setMap();
        setSearch();
    }


    private void setMap() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.map_view);
        tMapView = new TMapView(this);
        relativeLayout.addView(tMapView);
        tMapView.setSKPMapApiKey(APIKEY);

        tMapView.setCompassMode(true);    // 현재 보는 방향
        tMapView.setIconVisibility(true);   // 아이콘 표시
        tMapView.setZoomLevel(15);   // 줌레벨
        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);


        tMapGps = new TMapGpsManager(MainActivity.this);
        tMapGps.setMinTime(1000);
        tMapGps.setMinDistance(5);
//        tMapGps.setProvider(tMapGps.NETWORK_PROVIDER);  // 인터넷 이용 (실내일때 유용)
        tMapGps.setProvider(tMapGps.GPS_PROVIDER);    // 현위치 gps 이용
        tMapGps.OpenGps();

        tMapView.setTrackingMode(true);   //트래킹모드
        tMapView.setSightVisible(true);

    }

    @Override
    public void onLocationChange(Location location) {
        if (mTrackingMode) {
            tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }

    private void setSearch() {
        destEditText = (EditText) findViewById(R.id.destEditText);
        searchIcon = (ImageView) findViewById(R.id.searchIcon);

//        검색버튼 클릭시
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                destination = destEditText.getText().toString();
                Intent intent = new Intent(MainActivity.this, SearchDestActivity.class);
                intent.putExtra("destination", destination); //edittext에 입력된 값 넘기기
                startActivity(intent);
            }
        });

    }
}



