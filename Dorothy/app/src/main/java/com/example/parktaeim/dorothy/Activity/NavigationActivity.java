package com.example.parktaeim.dorothy.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.parktaeim.dorothy.APIUrl;
import com.example.parktaeim.dorothy.R;
import com.example.parktaeim.dorothy.RestAPI;
import com.google.gson.JsonObject;
import com.skp.Tmap.TMapView;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by parktaeim on 2017. 10. 12..
 */

public class NavigationActivity extends AppCompatActivity {

    private RelativeLayout beforeStartLayout;
    private RelativeLayout startLayout;
    private RelativeLayout startButton;
    private TMapView tMapView;
    private TextView bottomDestNameTextView;
    private TextView bottomAddressTextView;
    private TextView bottomTimeTextView;
    private TextView destNameTextView;
    private TextView addressTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        setLayout();
        setMap();
        setNavigation();

    }

    private void setNavigation() {
        Intent intent = getIntent();
        double lat = intent.getDoubleExtra("currentLatitude",-1);
        double lon = intent.getDoubleExtra("currentLongitude",-1);
        Log.d("setNavi location======"+String.valueOf(lat),String.valueOf(lon));

        HashMap<String, Object> fieldMap = new HashMap<>();
//        fieldMap.put("startX", );
//        fieldMap.put("startY", );
//        fieldMap.put("endX", );
//        fieldMap.put("endY", );

//        OkHttpClient client = new OkHttpClient.Builder()
//                .connectTimeout(100, TimeUnit.SECONDS)
//                .readTimeout(100, TimeUnit.SECONDS).build();
//
//        Retrofit builder = new Retrofit.Builder()
//                .baseUrl(APIUrl.TMAP_BASE_URL).client(client)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        RestAPI restAPI = builder.create(RestAPI.class);
//
//        Call<JsonObject> call = restAPI.navigation("application/json",getString(R.string.tmap_app_key),fieldMap);
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//
//            }
//        });
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
        bottomAddressTextView = (TextView) findViewById(R.id.addressTextViewBottom);
        bottomDestNameTextView = (TextView) findViewById(R.id.destNameTextViewBottom);
        bottomTimeTextView = (TextView) findViewById(R.id.timeTextViewBottom);
        destNameTextView = (TextView) findViewById(R.id.destNameTextView);
        addressTextView = (TextView) findViewById(R.id.addressTextView);

        // Setting Layout
        startLayout.setVisibility(View.GONE);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beforeStartLayout.setVisibility(View.GONE);
                startLayout.setVisibility(View.VISIBLE);
            }
        });

        // Setting Text
        Intent intent = getIntent();
        destNameTextView.setText(intent.getStringExtra("destination"));
        addressTextView.setText(intent.getStringExtra("address"));
        bottomDestNameTextView.setText(intent.getStringExtra("destination"));
        bottomAddressTextView.setText(intent.getStringExtra("address"));

    }
}
