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

    private static final int INITIAL_REQUEST = 1337;
    private static final int CAMERA_REQUEST = INITIAL_REQUEST + 1;
    private static final int CONTACTS_REQUEST = INITIAL_REQUEST + 2;
    private static final int LOCATION_REQUEST = INITIAL_REQUEST + 3;

    private double latitude;
    private double longitude;
    private EditText destEditText;
    private ImageView searchIcon;
    private String destination;
    private TMapView tMapView;
    private TMapGpsManager tMapGps;

    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS
    };
    private static final String[] LOCATION_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final int REQUEST_CODE_LOCATION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("!@#!#@#!", "onCreate: " + ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION));
        Log.d("!@#!#@#!", "onCreate: " + ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, LOCATION_PERMS, REQUEST_CODE_LOCATION);
        } else {
            setMap();
            setSearch();
        }
    }


    private void setMap() {
        Log.d("!@#!@##!@", "setMap: ");

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.map_view);
        tMapView = new TMapView(this);
        relativeLayout.addView(tMapView);
        tMapView.setSKPMapApiKey(getString(R.string.tmap_app_key));

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
                intent.putExtra("lat", tMapGps.getLocation().getLatitude());
                intent.putExtra("lon", tMapGps.getLocation().getLongitude());
                startActivity(intent);
            }
        });

    }

    private boolean canAccessLocation() {
        return (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    private boolean hasPermission(String perm) {
        return (PackageManager.PERMISSION_GRANTED == checkSelfPermission(perm));
    }

    private boolean canAccessContacts() {
        return (hasPermission(Manifest.permission.READ_CONTACTS));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        setMap();
        setSearch();
    }
}



