package com.example.parktaeim.dorothy.Activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;


import com.example.parktaeim.dorothy.GPSInfo;
import com.example.parktaeim.dorothy.R;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private GPSInfo gps;
    private double latitude;
    private double longitude;
    private MapView mapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView = new MapView(this);

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

        setMapView();
    }

    private void setMapView (){
        gps = new GPSInfo(MainActivity.this);
        //현재 위도, 경도 가져오기
        if (gps.isGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            if (latitude == 0 || longitude == 0) {
            }
            String lat = Double.toString(latitude);
            String lon = Double.toString(longitude);

            System.out.println("위도 : " + lat + " 경도 : " + lon);
        } else {
            gps.showSettingsAlert();
        }

        //현재 위치를 중심으로 지도 보여주기
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude,longitude),true);

        //현재위치에 마커 찍기
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName("현재 위치");
        marker.setTag(0);
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(latitude,longitude));
        marker.setMarkerType(MapPOIItem.MarkerType.RedPin);
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); 
        mapView.addPOIItem(marker);
    }


}



