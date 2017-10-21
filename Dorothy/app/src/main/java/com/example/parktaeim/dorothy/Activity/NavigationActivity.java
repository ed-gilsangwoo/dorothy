package com.example.parktaeim.dorothy.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parktaeim.dorothy.APIUrl;
import com.example.parktaeim.dorothy.Model.DestinationResponseItem;
import com.example.parktaeim.dorothy.R;
import com.example.parktaeim.dorothy.RestAPI;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
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

public class NavigationActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {

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
    private boolean mTrackingMode = false;
    private TextToSpeech myTTS;
    private double realtimeLatitude;
    private double realtimeLongitude;
    private TextView distanceTextView;
    private TextView nextDistanceTextView;
    private int i=0;

    private TMapGpsManager tMapGps;
    final TMapData tmapData = new TMapData();


    ArrayList<DestinationResponseItem> geometryArrayList;
    ArrayList<DestinationResponseItem> propertiesArrayList;

    private void setNavigation() {

        distanceTextView = (TextView) findViewById(R.id.distanceTextView);
        nextDistanceTextView = (TextView) findViewById(R.id.nextDistanceTextView);

        for(i=0;i<geometryArrayList.size();){
            if(geometryArrayList.get(i).getGeometryType().equals("\"Point\"")){
                if(propertiesArrayList.get(i).getPointType().equals("\"S\"")){
                    myTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int i) {
                            myTTS.setLanguage(Locale.KOREAN);
                            myTTS.speak("경로 안내를 시작합니다. "+propertiesArrayList.get(i).getDescription().toString()+"하세요.", TextToSpeech.QUEUE_FLUSH, null);

                        }
                    });
                    i++;
//                    break;
                }else if(propertiesArrayList.get(i).getPointType().equals("\"N\"")){
                    myTTS.speak(geometryArrayList.get(i).getDescription(),TextToSpeech.QUEUE_FLUSH,null,null);
                    break;
                }else if(propertiesArrayList.get(i).getPointType().equals("\"E\"")){
                    break;
                }
            }else if(geometryArrayList.get(i).getGeometryType().equals("\"LineString\"")){
                String lineDesc = propertiesArrayList.get(i).getDescription();
                lineDesc = lineDesc.substring(1,lineDesc.length()-1);
                int index = lineDesc.indexOf(",");
                String distance = lineDesc.substring(index+2);

                String nextDistDesc = propertiesArrayList.get(i+2).getDescription();
                nextDistDesc = nextDistDesc.substring(1,nextDistDesc.length()-1);
                int index2 = nextDistDesc.indexOf(",");
                String nextDistance = nextDistDesc.substring(index2 + 2);

                distanceTextView.setText(distance);
                Log.d("distance",distance);
                nextDistanceTextView.setText(nextDistance);
                if(geometryArrayList.get(i+1).getCoordinates().get(0).equals(realtimeLongitude) && geometryArrayList.get(i).getCoordinates().get(1).equals(realtimeLatitude)){
                    i++;
                }
                break;
            }


        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        setLayout();
        setMap();
        getNaviData();
        setBottomLayout();

    }

    // 경로취소 종료 소리 피드백 하는 레이아웃 올렸다 내렸다
    private void setBottomLayout() {
        bottomDownLayout = (RelativeLayout) findViewById(R.id.bottomDownLayout);
        bottomUpLayout = (RelativeLayout) findViewById(R.id.bottomUpLayout);
        downArrowLayout = (RelativeLayout) findViewById(R.id.downArrowLayout);

        LinearLayout naviCancelLayout = (LinearLayout) findViewById(R.id.naviCancelLayout);
        LinearLayout finishLayout = (LinearLayout) findViewById(R.id.finishLayout);
        LinearLayout soundLayout = (LinearLayout) findViewById(R.id.soundLayout);
        LinearLayout feedbackLayout = (LinearLayout) findViewById(R.id.feedbackLayout);

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

        //경로취소
        naviCancelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(NavigationActivity.this)
                        .setTitle("경로 취소").setMessage("메인화면으로 이동하시겠습니까? ")
                        .setPositiveButton("메인화면으로", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                SearchDestActivity searchDestActivity = (SearchDestActivity) SearchDestActivity.searchDestActivity;
                                searchDestActivity.finish();       // SearchDestActivity finish
                                finish();   //NavigationActivity finish

                                Intent intent = new Intent(NavigationActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        })
                        .show();

            }

        });

        //종료
        finishLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(NavigationActivity.this,"onclick",Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(NavigationActivity.this)
                        .setTitle("종료").setMessage("도로시를 종료하시겠습니까?")
                        .setPositiveButton("종료하기", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                MainActivity mainActivity = (MainActivity) MainActivity.mainActivity;
                                mainActivity.finish();    //MainActivity finish

                                SearchDestActivity searchDestActivity = (SearchDestActivity) SearchDestActivity.searchDestActivity;
                                searchDestActivity.finish();       //SearchDestActivity finish

                                finish();

                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        })
                        .show();

            }
        });

        //소리
        soundLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //피드백
        feedbackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }


    @Override
    public void onLocationChange(Location location) {
        Log.d("loacationChange===","lat");
        if (mTrackingMode) {
            tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
            tMapView.setCenterPoint(location.getLongitude(), location.getLatitude());

            realtimeLatitude = location.getLatitude();
            realtimeLongitude = location.getLongitude();
            Log.d("naviActi current Loc =" + String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));

            getStraightDistance();

        }
    }

    // 직선 거리 계산
    private void getStraightDistance() {
        double startLon = realtimeLongitude;
        Log.d("startLon",String.valueOf(startLon));
        double startLat = realtimeLatitude;
        Log.d("startLat",String.valueOf(startLat));

        int idx = i;
        Log.d("before i",String.valueOf(i));
        Log.d("before idx",String.valueOf(idx));
        if(idx!=0) {
            if (idx % 2 == 1 || idx==1) {
                idx = idx - 1;
            }
        }
        Log.d("after idx",String.valueOf(idx));

        double endLon = Double.valueOf(geometryArrayList.get(idx+2).getCoordinates().get(0).toString());
        double endLat = Double.valueOf(geometryArrayList.get(idx+2).getCoordinates().get(1).toString());

        float[] result = new float[1];
        Location.distanceBetween(startLat,startLon,endLat,endLon,result);   // 거리 계산
        Log.d("dist res",String.valueOf(result[0]));

        distanceTextView = (TextView) findViewById(R.id.distanceTextView);
        distanceTextView.setText(String.valueOf((int)result[0])+" m");   // setText

    }


    private void getNaviData() {
        Intent intent = getIntent();
        double currentLatitude = intent.getDoubleExtra("currentLatitude", -1);     // 현재 위치 받아옴
        double currentLongitude = intent.getDoubleExtra("currentLongitude", -1);
        Log.d("setNavi location======" + String.valueOf(currentLatitude), String.valueOf(currentLongitude));


        Intent getNoor = getIntent();
        double noorLat = getNoor.getDoubleExtra("noorLat", -1);      //도착지 경도,위도
        double noorLon = getNoor.getDoubleExtra("noorLon", -1);


        // 경로 그리기
        final TMapPoint startPoint = new TMapPoint(currentLatitude, currentLongitude);   // 현재 위치
        final TMapPoint destPoint = new TMapPoint(noorLat, noorLon);  // 도착 위치

        tmapData.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, startPoint, destPoint, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine tMapPolyLine) {
                Log.d("path start======" + String.valueOf(startPoint.getLatitude()), String.valueOf(startPoint.getLongitude()));
                Log.d("path dest======" + String.valueOf(destPoint.getLatitude()), String.valueOf(destPoint.getLongitude()));
                tMapView.setLocationPoint(startPoint.getLongitude(), startPoint.getLatitude());
                tMapView.addTMapPath(tMapPolyLine);
                mTrackingMode = true;
                Log.d("path poly", "finish=========");

            }
        });


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

                Log.d("equals", geometryArrayList.get(0).getGeometryType());
                Log.d("equals", geometryArrayList.get(1).getGeometryType());
                Log.d("equals", geometryArrayList.get(2).getGeometryType());

                for (int i = 0; i < geometryArrayList.size(); i++) {
                    if (geometryArrayList.get(i).getGeometryType().equals("\"Point\"")) {
                        if (i == 0) {
                            getPropertiesStartArray(arrayList);
                        } else {
                            getPropertiesPointArray(arrayList, i);
                        }
                    } else if (geometryArrayList.get(i).getGeometryType().equals("\"LineString\"")) {
                        getPropertiesLineStringArray(arrayList, i);
                    }

                }
                Log.d("proper check for after", propertiesArrayList.get(0).getDescription());
                for (int j = 0; j < propertiesArrayList.size(); j++) {
                    Log.d("propertiesArraySize", String.valueOf(propertiesArrayList.size()));
                    System.out.println("aaaaa===" + propertiesArrayList.get(j).getDescription());
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
        Log.d("properStartDesc", description);
        String nextRoadName = jsonObject.getAsJsonPrimitive("nextRoadName").toString();
        int turnType = Integer.valueOf(jsonObject.getAsJsonPrimitive("turnType").toString());
        String pointType = jsonObject.getAsJsonPrimitive("pointType").toString();

        propertiesArrayList.add(new DestinationResponseItem(totalDistance, totalTime, totalFare, taxiFare, index, pointIndex, name, description, nextRoadName, turnType, pointType));


    }

    private void getPropertiesPointArray(ArrayList<DestinationResponseItem> arrayList, int i) {
        try {
            JsonObject jsonObject = arrayList.get(i).getProperties();
            int index = Integer.valueOf(jsonObject.getAsJsonPrimitive("index").toString());
            int pointIndex = Integer.valueOf(jsonObject.getAsJsonPrimitive("pointIndex").toString());
            String name = jsonObject.getAsJsonPrimitive("name").toString();
            String description = jsonObject.getAsJsonPrimitive("description").toString();
            String nextRoadname = jsonObject.getAsJsonPrimitive("nextRoadName").toString();
            int turnType = Integer.valueOf(jsonObject.getAsJsonPrimitive("turnType").toString());
            String pointType = jsonObject.getAsJsonPrimitive("pointType").toString();

            propertiesArrayList.add(new DestinationResponseItem(index, pointIndex, name, description, nextRoadname, turnType, pointType));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getPropertiesLineStringArray(ArrayList<DestinationResponseItem> arrayList, int i) {
        try {
            JsonObject jsonObject = arrayList.get(i).getProperties();
            int index = Integer.valueOf(jsonObject.getAsJsonPrimitive("index").toString());
            int lineIndex = Integer.valueOf(jsonObject.getAsJsonPrimitive("lineIndex").toString());
            String name = jsonObject.getAsJsonPrimitive("name").toString();
            String description = jsonObject.getAsJsonPrimitive("description").toString();
            int distance = Integer.valueOf(jsonObject.getAsJsonPrimitive("distance").toString());
            int time = Integer.valueOf(jsonObject.getAsJsonPrimitive("time").toString());
            int roadType = Integer.valueOf(jsonObject.getAsJsonPrimitive("roadType").toString());
            int facilityType = Integer.valueOf(jsonObject.getAsJsonPrimitive("facilityType").toString());

            propertiesArrayList.add(new DestinationResponseItem(index, lineIndex, name, description, distance, time, roadType, facilityType));
        } catch (Exception e) {
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
//        tMapView.setZoomLevel(15);   // 줌레벨
        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);

        tMapGps = new TMapGpsManager(NavigationActivity.this);
        tMapGps.setMinTime(1000);
        tMapGps.setMinDistance(1);
        tMapGps.setProvider(tMapGps.NETWORK_PROVIDER);  // 인터넷 이용 (실내일때 유용)
//        tMapGps.setProvider(tMapGps.GPS_PROVIDER);    // 현위치 gps 이용
        tMapGps.OpenGps();

        mTrackingMode = false;
        tMapView.setTrackingMode(mTrackingMode);

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
//                tMapView.removeTMapPath();
                tMapView.setZoomLevel(19);
                tMapView.setTrackingMode(mTrackingMode);   //트래킹모드
                tMapView.setSightVisible(true);

                setNavigation();
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
