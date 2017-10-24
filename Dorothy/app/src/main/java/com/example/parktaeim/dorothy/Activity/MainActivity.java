package com.example.parktaeim.dorothy.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.example.parktaeim.dorothy.APIUrl;
import com.example.parktaeim.dorothy.R;
import com.example.parktaeim.dorothy.RestAPI;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private boolean mTrackingMode = true;

    private static final int INITIAL_REQUEST = 1337;
    private static final int CAMERA_REQUEST = INITIAL_REQUEST + 1;
    private static final int CONTACTS_REQUEST = INITIAL_REQUEST + 2;
    private static final int LOCATION_REQUEST = INITIAL_REQUEST + 3;

    private double currentLatitude;
    private double currentLongitude;
    private EditText destEditText;
    private ImageView searchIcon;
    private String destination;
    private TMapView tMapView;
    private TMapGpsManager tMapGps;

    LocationManager locationManager;
    String gpsProvider = LocationManager.GPS_PROVIDER;
    String networkProvider = LocationManager.NETWORK_PROVIDER;

    public static Activity mainActivity;

    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS
    };
    private static final String[] LOCATION_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final int REQUEST_CODE_LOCATION = 2;

    @Override
    public void onLocationChange(Location location) {
        Log.d("Start OnLocationChange", location.toString());
        if (mTrackingMode) {
            tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
            Log.d(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
            tMapView.setCenterPoint(location.getLongitude(), location.getLatitude());

            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();

//            tMapView.setLocationPoint(location.getLatitude(), location.getLongitude());
//            tMapView.setCenterPoint(location.getLatitude(),location.getLongitude());
//            tMapView.setMapPosition(location.getLatitude(),location.getLongitude());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("!@#!#@#!", "onCreate: " + ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION));
        Log.d("!@#!#@#!", "onCreate: " + ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET));

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, LOCATION_PERMS, REQUEST_CODE_LOCATION);
        } else {
            setSearch();
            setMap();

        }

        mainActivity = MainActivity.this;

        setIntervalMarkerUpdate();
    }


    private void setMap() {
        Log.d("!@#!@##!@", "setMap: ");

        LinearLayout relativeLayout = (LinearLayout) findViewById(R.id.map_view);
        tMapView = new TMapView(this);
        relativeLayout.addView(tMapView);
        tMapView.setSKPMapApiKey(getString(R.string.tmap_app_key));

        tMapView.setCompassMode(true);    // 현재 보는 방향
        tMapView.setIconVisibility(true);   // 아이콘 표시
        tMapView.setZoomLevel(15);   // 줌레벨
        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);

        tMapGps = new TMapGpsManager(MainActivity.this);
        tMapGps.setMinTime(100);
        tMapGps.setMinDistance(5);
        tMapGps.setProvider(tMapGps.NETWORK_PROVIDER);  // 인터넷 이용 (실내일때 유용)
//        tMapGps.setProvider(tMapGps.GPS_PROVIDER);    // 현위치 gps 이용
        tMapGps.OpenGps();


        tMapView.setTrackingMode(true);   //트래킹모드
        tMapView.setSightVisible(true);


//        try{
//            Log.d("setmap ====","location updates");
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, mListener);
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,100,1,mListener);
//
//        }catch (SecurityException e){
//            e.printStackTrace();
//        }

    }


    private void setSearch() {
        destEditText = (EditText) findViewById(R.id.destEditText);
        searchIcon = (ImageView) findViewById(R.id.searchIcon);

        destEditText.requestFocus();
//        검색버튼 클릭시
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLatitude == 0 || currentLongitude == 0) {
                    Toast.makeText(MainActivity.this, "잠시만 기다려주세요", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    destination = destEditText.getText().toString();
                    Log.d("getText MainActi===", destination);
                    Intent intent = new Intent(MainActivity.this, SearchDestActivity.class);
                    intent.putExtra("destination", destination); //edittext에 입력된 값 넘기기
                    intent.putExtra("currentLatitude", currentLatitude);
                    intent.putExtra("currentLongitude", currentLongitude);
                    Log.d("onClick Location======" + String.valueOf(currentLatitude), String.valueOf(currentLongitude));
                    startActivity(intent);
                    return;
                }

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

    private void setIntervalMarkerUpdate() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                final Bitmap mountain_damage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_location_mountain_damage_48dp_iloveimg_resized);
//        mountain_damage.setWidth(mountain_damage.getWidth() / 10);
//        mountain_damage.setHeight(mountain_damage.getHeight() / 10);

                final Bitmap road_damage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_location_road_damage_48dp_iloveimg_resized);
//        road_damage.setWidth(road_damage.getWidth() / 10);
//        road_damage.setHeight(road_damage.getHeight() / 10);

                final Bitmap water_damage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_location_water_damage_48dp_iloveimg_resized);
//        water_damage.setWidth(water_damage.getWidth() / 10);
//        water_damage.setHeight(water_damage.getHeight() / 10);

                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(100, TimeUnit.SECONDS)
                        .readTimeout(100, TimeUnit.SECONDS).build();

                Retrofit builder = new Retrofit.Builder()
                        .baseUrl(APIUrl.API_BASE_URL).client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                RestAPI restAPI = builder.create(RestAPI.class);

                Call<JsonObject> call = restAPI.getReportList();

                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                        if (response.code() == 200) {
                            tMapView.removeAllMarkerItem();
                            int i = 0;
                            JsonArray res = response.body().get("showKey").getAsJsonArray();
                            Iterator<JsonElement> iterator = res.iterator();
                            while (iterator.hasNext()) {
                                JsonObject obj = iterator.next().getAsJsonObject();

                                int type = obj.get("kind").getAsInt();
                                TMapMarkerItem markerItem = new TMapMarkerItem();
                                TMapPoint markerPoint = new TMapPoint(obj.get("lat").getAsDouble(), obj.get("lng").getAsDouble());
                                markerItem.setTMapPoint(markerPoint);
                                markerItem.setVisible(TMapMarkerItem.VISIBLE);
                                if (type == 1) markerItem.setIcon(road_damage);
                                if (type == 2) markerItem.setIcon(water_damage);
                                if (type == 3) markerItem.setIcon(mountain_damage);
                                tMapView.addMarkerItem("marker:" + i++, markerItem);

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
                Log.i("tag", "A Kiss every 5 seconds");
            }
        }, 0, 5000);
    }
}



