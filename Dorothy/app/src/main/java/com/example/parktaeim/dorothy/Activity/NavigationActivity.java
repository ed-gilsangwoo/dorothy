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
import com.example.parktaeim.dorothy.Model.DestinationResponseItem;
import com.example.parktaeim.dorothy.R;
import com.example.parktaeim.dorothy.RestAPI;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.skp.Tmap.TMapView;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
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
    private RelativeLayout bottomDownLayout;
    private RelativeLayout bottomUpLayout;
    private RelativeLayout downArrowLayout;

    ArrayList<DestinationResponseItem> geometryArrayList;
    ArrayList<DestinationResponseItem> propertiesArrayList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        setLayout();
        setMap();
        setNavigation();
        setBottomLayout();

    }

    private void setBottomLayout() {
        bottomDownLayout = (RelativeLayout) findViewById(R.id.bottomDownLayout);
        bottomUpLayout = (RelativeLayout) findViewById(R.id.bottomUpLayout);
        downArrowLayout = (RelativeLayout) findViewById(R.id.downArrowLayout);

        bottomUpLayout.setVisibility(View.GONE);
        bottomDownLayout.setVisibility(View.VISIBLE);

        bottomDownLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomUpLayout.setVisibility(View.VISIBLE);
                bottomDownLayout.setVisibility(View.GONE);
            }
        });

        downArrowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomUpLayout.setVisibility(View.GONE);
                bottomDownLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setNavigation() {
        Intent intent = getIntent();
        double currentLatitude = intent.getDoubleExtra("currentLatitude", -1);
        double currentLongitude = intent.getDoubleExtra("currentLongitude", -1);
        Log.d("setNavi location======" + String.valueOf(currentLatitude), String.valueOf(currentLongitude));

        Intent getNoor = getIntent();
        double noorLat = getNoor.getDoubleExtra("noorLat", -1);
        double noorLon = getNoor.getDoubleExtra("noorLon", -1);
        HashMap<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("startX", currentLongitude);
        fieldMap.put("startY", currentLatitude);
        fieldMap.put("endX", noorLon);
        fieldMap.put("endY", noorLat);
        fieldMap.put("reqCoordType", "WGS84GEO");
        fieldMap.put("resCoordType", "WGS84GEO");

        Log.d("startX : " + String.valueOf(currentLatitude), "startY : " + String.valueOf(currentLongitude));
        Log.d("endX : " + String.valueOf(noorLat), "endY :" + String.valueOf(noorLon));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();

        Retrofit builder = new Retrofit.Builder()
                .baseUrl(APIUrl.TMAP_BASE_URL).client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RestAPI restAPI = builder.create(RestAPI.class);

        Call<JsonObject> call = restAPI.navigation("application/json", getString(R.string.tmap_app_key), fieldMap);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("Navigation response===", String.valueOf(response.code()));
                Log.d("navi response.body", String.valueOf(response.body()));

                JsonObject jsonObject = response.body();
                JsonArray jsonArray = jsonObject.getAsJsonArray("features");
                Log.d("navi array", jsonArray.toString());

                ArrayList<DestinationResponseItem> arrayList = new ArrayList<DestinationResponseItem>();
                geometryArrayList = new ArrayList<DestinationResponseItem>();
                propertiesArrayList = new ArrayList<DestinationResponseItem>();
                int len = jsonArray.size();
                Log.d("navi jsonArray length", String.valueOf(len));
                for (int i = 0; i < len; i++) {
                    JsonObject jsonObject1 = (JsonObject) jsonArray.get(i);
                    String type = jsonObject1.getAsJsonPrimitive("type").toString();
                    JsonObject geometryJsonObject = jsonObject1.getAsJsonObject("geometry");
                    JsonObject propertiesJsonObject = jsonObject1.getAsJsonObject("properties");

                    arrayList.add(new DestinationResponseItem(type, geometryJsonObject, propertiesJsonObject));
                    Log.d("navi in for", jsonObject1.toString());
                    Log.d("navi", arrayList.get(i).getProperties().toString());


                }

                getGeometryArray(arrayList);

                Log.d("equals",geometryArrayList.get(0).getGeometryType());
                Log.d("equals",geometryArrayList.get(1).getGeometryType());
                Log.d("equals",geometryArrayList.get(2).getGeometryType());

                for (int i = 0; i < geometryArrayList.size(); i++) {
                        if (geometryArrayList.get(i).getGeometryType().equals("\"Point\"")) {
                            if(i==0){
                                getPropertiesStartArray(arrayList);
                            }else {
                                getPropertiesPointArray(arrayList,i);
                            }
                        } else if (geometryArrayList.get(i).getGeometryType().equals("\"LineString\"")) {
                            getPropertiesLineStringArray(arrayList,i);
                        }

                }
    Log.d("proper check for after",propertiesArrayList.get(0).getDescription());
                for(int j=0;j<propertiesArrayList.size();j++){
                    Log.d("propertiesArraySize",String.valueOf(propertiesArrayList.size()));
                    System.out.println("aaaaa==="+propertiesArrayList.get(j).getDescription());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void getPropertiesStartArray(ArrayList<DestinationResponseItem> arrayList) {
        JsonObject jsonObject = arrayList.get(0).getProperties();
        int totalDistance = Integer.valueOf(jsonObject.getAsJsonPrimitive("totalDistance").toString());
        int totalTime = Integer.valueOf(jsonObject.getAsJsonPrimitive("totalTime").toString());
        int totalFare = Integer.valueOf(jsonObject.getAsJsonPrimitive("totalFare").toString());
        int taxiFare = Integer.valueOf(jsonObject.getAsJsonPrimitive("taxiFare").toString());
        int index = Integer.valueOf(jsonObject.getAsJsonPrimitive("index").toString());
        int pointIndex = Integer.valueOf(jsonObject.getAsJsonPrimitive("pointIndex").toString());
        String name = jsonObject.getAsJsonPrimitive("name").toString();
        String description = jsonObject.getAsJsonPrimitive("description").toString();
        Log.d("properStartDesc",description);
        String nextRoadName = jsonObject.getAsJsonPrimitive("nextRoadName").toString();
        int turnType = Integer.valueOf(jsonObject.getAsJsonPrimitive("turnType").toString());
        String pointType = jsonObject.getAsJsonPrimitive("pointType").toString();

        propertiesArrayList.add(new DestinationResponseItem(totalDistance,totalTime,totalFare,taxiFare,index,pointIndex,name,description,nextRoadName,turnType,pointType));


    }

    private void getPropertiesPointArray(ArrayList<DestinationResponseItem> arrayList,int i) {
            try{
                JsonObject jsonObject = arrayList.get(i).getProperties();
                int index = Integer.valueOf(jsonObject.getAsJsonPrimitive("index").toString());
                int pointIndex = Integer.valueOf(jsonObject.getAsJsonPrimitive("pointIndex").toString());
                String name = jsonObject.getAsJsonPrimitive("name").toString();
                String description = jsonObject.getAsJsonPrimitive("description").toString();
                String nextRoadname = jsonObject.getAsJsonPrimitive("nextRoadName").toString();
                int turnType = Integer.valueOf(jsonObject.getAsJsonPrimitive("turnType").toString());
                String pointType = jsonObject.getAsJsonPrimitive("pointType").toString();

                propertiesArrayList.add(new DestinationResponseItem(index, pointIndex, name, description, nextRoadname, turnType, pointType));
            }catch (Exception e){
                e.printStackTrace();
            }
    }

    private void getPropertiesLineStringArray(ArrayList<DestinationResponseItem> arrayList,int i) {
            try{
                JsonObject jsonObject = arrayList.get(i).getProperties();
                int index = Integer.valueOf(jsonObject.getAsJsonPrimitive("index").toString());
                int lineIndex = Integer.valueOf(jsonObject.getAsJsonPrimitive("lineIndex").toString());
                String name = jsonObject.getAsJsonPrimitive("name").toString();
                String description = jsonObject.getAsJsonPrimitive("description").toString();
                int distance = Integer.valueOf(jsonObject.getAsJsonPrimitive("distance").toString());
                int time = Integer.valueOf(jsonObject.getAsJsonPrimitive("time").toString());
                int roadType = Integer.valueOf(jsonObject.getAsJsonPrimitive("roadType").toString());
                int facilityType = Integer.valueOf(jsonObject.getAsJsonPrimitive("facilityType").toString());

                propertiesArrayList.add(new DestinationResponseItem(index,lineIndex,name,description,distance,time,roadType,facilityType));
            }catch (Exception e){
                e.printStackTrace();
            }
    }


    private void getGeometryArray(ArrayList<DestinationResponseItem> arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            JsonObject jsonObject = arrayList.get(i).getGeometry();
            String geometryType = jsonObject.getAsJsonPrimitive("type").toString();
            JsonArray coordinates = jsonObject.getAsJsonArray("coordinates");

            geometryArrayList.add(new DestinationResponseItem(geometryType, coordinates));
        }
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

    private void setLayout() {
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
