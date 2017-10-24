package com.example.parktaeim.dorothy.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
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
    private static final String TAG = "NavigationActivity";

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
    private int i = 0;
    private String nextDistance;
    private RelativeLayout reportLayout;

    private TMapGpsManager tMapGps;
    final TMapData tmapData = new TMapData();


    private StateProgressBar strengthBar;

    private String[] descData = {"1단계", "2단계", "3단계"};

    private Button stateButtons[] = new Button[3];
    private int state = 1;

    private Button strengthButtons[] = new Button[3];
    private int strength = 1;

    private double lon = 0;
    private double lat = 0;

    private Button submitButton;

    private Retrofit builder;
    private OkHttpClient client;
    private RestAPI restAPI;


//    private NaverTTSTask mNaverTTSTask;

    ArrayList<DestinationResponseItem> geometryArrayList;
    ArrayList<DestinationResponseItem> propertiesArrayList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        setLayout();
        setMap();
        getNaviData();
        setBottomLayout();
        initStrengthBar();
        initStateButtons();
        initSubmitButton();
        initializeMarkers();
        markerUpdate();
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
                Toast.makeText(NavigationActivity.this, "onclick", Toast.LENGTH_SHORT).show();
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
        Log.d("loacationChange===", "lat");
        if (mTrackingMode) {
            tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
            tMapView.setCenterPoint(location.getLongitude(), location.getLatitude());

            realtimeLatitude = location.getLatitude();
            realtimeLongitude = location.getLongitude();
            Log.d("naviActi current Loc =" + String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));


            getStraightDistance();

        }

        if (startLayout.getVisibility() == View.VISIBLE) {
            setNavigation();
        }
    }


    // 직선 거리 계산
    private void getStraightDistance() {
        double startLon = realtimeLongitude;
        Log.d("startLon", String.valueOf(startLon));
        double startLat = realtimeLatitude;
        Log.d("startLat", String.valueOf(startLat));

        if (geometryArrayList == null || geometryArrayList.size() == 0) {
            Toast.makeText(NavigationActivity.this, "잠시만 기다려주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        int idx = i;
        Log.d("before i", String.valueOf(i));
        Log.d("before idx", String.valueOf(idx));
        if (idx != 0) {
            if (idx % 2 == 1 || idx == 1) {
                idx = idx - 1;
            }
        }
        Log.d("after idx", String.valueOf(idx));

        double endLon, endLat;
        if (i < geometryArrayList.size() - 2) {
            endLon = Double.valueOf(geometryArrayList.get(idx + 2).getCoordinates().get(0).toString());
            endLat = Double.valueOf(geometryArrayList.get(idx + 2).getCoordinates().get(1).toString());
        } else {
            endLon = Double.valueOf(geometryArrayList.get(idx).getCoordinates().get(0).toString());
            endLat = Double.valueOf(geometryArrayList.get(idx).getCoordinates().get(1).toString());
        }


        float[] result = new float[1];
        Location.distanceBetween(startLat, startLon, endLat, endLon, result);   // 거리 계산
        Log.d("dist res", String.valueOf(result[0]));

        distanceTextView = (TextView) findViewById(R.id.distanceTextView);
        distanceTextView.setText(String.valueOf((int) result[0]) + " m");   // setText

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

        TextView totalTimeTextView = (TextView) findViewById(R.id.totalTimeTextView);
        TextView totalDistTextView = (TextView) findViewById(R.id.totalDistTextView);
        TextView fareTextView = (TextView) findViewById(R.id.fareTextView);
        TextView taxiFareTextView = (TextView) findViewById(R.id.taxiFareTextView);

        double totalKmDist = (double) totalDistance;
        totalKmDist = totalDistance * 0.001;
        totalKmDist = Double.parseDouble(String.format("%.1f", totalKmDist));

        totalTimeTextView.setText(String.valueOf(totalTime / 60) + "분");
        totalDistTextView.setText(String.valueOf(totalKmDist + "km"));
        fareTextView.setText(String.valueOf(totalFare + "원"));
        taxiFareTextView.setText(String.valueOf(taxiFare + "원"));
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
        tMapView.setZoomLevel(12);   // 줌레벨
        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);

        tMapGps = new TMapGpsManager(NavigationActivity.this);
        tMapGps.setMinTime(1000);
//        tMapGps.setMinDistance(1);
        tMapGps.setProvider(tMapGps.NETWORK_PROVIDER);  // 인터넷 이용 (실내일때 유용)
//        tMapGps.setProvider(tMapGps.GPS_PROVIDER);    // 현위치 gps 이용
        tMapGps.OpenGps();

        mTrackingMode = false;
        tMapView.setTrackingMode(true);

        reportLayout = (RelativeLayout) findViewById(R.id.reportLayout);
        ImageView cancelBtn = (ImageView) findViewById(R.id.report_cancelBtn);

        reportLayout.setVisibility(View.GONE);

        tMapView.setOnLongClickListenerCallback(new TMapView.OnLongClickListenerCallback() {
            @Override
            public void onLongPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint) {

                reportLayout.setVisibility(View.VISIBLE);

                lat = tMapPoint.getLatitude();
                lon = tMapPoint.getLongitude();

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportLayout.setVisibility(View.GONE);
            }
        });


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
                Log.d("beforeLayout Gone=====", "startLayout Visible===-=====");

//                tMapView.removeTMapPath();
                tMapView.setZoomLevel(18);
                tMapView.setTrackingMode(mTrackingMode);   //트래킹모드
                tMapView.setSightVisible(true);

                haha();
            }
        });


        // Setting Text
        Intent intent = getIntent();
        destNameTextView.setText(intent.getStringExtra("destination"));
        addressTextView.setText(intent.getStringExtra("address"));
        bottomDestNameTextView.setText(intent.getStringExtra("destination"));
        bottomAddressTextView.setText(intent.getStringExtra("address"));

    }

    private void haha() {
//        if(startLayout.getVisibility() == View.VISIBLE){
//            setNavigation();
//        }
    }

    private void setNavigation() {

        distanceTextView = (TextView) findViewById(R.id.distanceTextView);
        nextDistanceTextView = (TextView) findViewById(R.id.nextDistanceTextView);

        int size = geometryArrayList.size();
        for (i = 0; i < size; ) {
            Log.d("proper", propertiesArrayList.get(i).getDescription().toString());

            if (geometryArrayList.get(i).getGeometryType().equals("\"Point\"")) {
                Log.d("proper", propertiesArrayList.get(i).getDescription().toString());

                if (propertiesArrayList.get(i).getPointType().equals("\"S\"")) {
                    Log.d("S proper", propertiesArrayList.get(i).getDescription().toString());

                    String mTextString = "경로 안내를 시작합니다. " + propertiesArrayList.get(i).getDescription().toString() + "하세요.";
//                    mNaverTTSTask = new NaverTTSTask();
//                    mNaverTTSTask.execute(mTextString);

                    i++;

                } else if (propertiesArrayList.get(i).getPointType().equals("\"N\"")) {
                    Toast.makeText(NavigationActivity.this, String.valueOf(i), Toast.LENGTH_SHORT).show();
                    myTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int i) {
                            myTTS.setLanguage(Locale.KOREAN);
                            myTTS.speak(propertiesArrayList.get(i).getDescription(), TextToSpeech.QUEUE_FLUSH, null);
                        }
                    });
                    i++;
                } else if (propertiesArrayList.get(i).getPointType().equals("\"E\"")) {
                    Toast.makeText(NavigationActivity.this, String.valueOf(i) + "목적지 도착", Toast.LENGTH_SHORT).show();

                    myTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int i) {
                            myTTS.setLanguage(Locale.KOREAN);
                            myTTS.speak("목적지에 도착하였습니다.", TextToSpeech.QUEUE_FLUSH, null);

                        }
                    });
                    break;

                }

            } else if (geometryArrayList.get(i).getGeometryType().equals("\"LineString\"")) {
//                if (i < geometryArrayList.size() - 2) {
                String lineDesc = propertiesArrayList.get(i).getDescription();
                lineDesc = lineDesc.substring(1, lineDesc.length() - 1);
                int index = lineDesc.indexOf(",");
                String distance = lineDesc.substring(index + 2);

                if (i < size - 2) {
                    String nextDistDesc = propertiesArrayList.get(i + 2).getDescription();
                    nextDistDesc = nextDistDesc.substring(1, nextDistDesc.length() - 1);
                    int index2 = nextDistDesc.indexOf(",");
                    nextDistance = nextDistDesc.substring(index2 + 2);
                } else {
                    distanceTextView.setText(nextDistance);
                    nextDistanceTextView.setVisibility(View.INVISIBLE);
                }

                distanceTextView.setText(distance);
                Log.d("distance", distance);
                nextDistanceTextView.setText(nextDistance);

                int pointIdx = i;
                Log.d("before i", String.valueOf(i));
                Log.d("before idx", String.valueOf(pointIdx));
                if (pointIdx % 2 == 1 || pointIdx == 1) {
                    pointIdx = pointIdx - 1;
                }
                Log.d("after idx", String.valueOf(pointIdx));

                float[] result = new float[2];

                Log.d("errorLat ==" + geometryArrayList.get(i + 1).getCoordinates().get(1).toString(), "errorLon ==" + geometryArrayList.get(i + 1).getCoordinates().get(0).toString());
                Log.d("realtimeLat ==" + realtimeLatitude, "realtimeLon ==" + realtimeLongitude);

                Location.distanceBetween(realtimeLatitude, realtimeLongitude, Double.valueOf(geometryArrayList.get(i + 1).getCoordinates().get(1).toString()), Double.valueOf(geometryArrayList.get(i + 1).getCoordinates().get(0).toString()), result);   // 거리 계산

                String cornerDist = String.valueOf(result[0]);

                Log.d("Corner Dist ==", String.valueOf(cornerDist));
                int corner = (int) Double.parseDouble(cornerDist);
                Log.d("Corner Dist Ing==", String.valueOf(corner));

//                    int corner = 0;
//                corner = 140;
                if (corner < 311980) {
                    i++;
                }

                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }

    }

//    private class NaverTTSTask extends AsyncTask<String[], Void, String> {
//
//        @Override
//        protected String doInBackground(String[]... strings) {
//            //여기서 서버에 요청
//            TTSApi.main(mTextString);
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//
//        }
//    }


    // 도로 신고
    private void initSubmitButton() {
        client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();

        builder = new Retrofit.Builder()
                .baseUrl(APIUrl.API_BASE_URL).client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        restAPI = builder.create(RestAPI.class);

        submitButton = (Button) findViewById(R.id.btn_report_submit);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final SweetAlertDialog dialog = new SweetAlertDialog(NavigationActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("주의!")
                        .setContentText("허위신고시 받는 불이익은 책임지지 않습니다.\n신고하시겠어요?")
                        .setConfirmText("예")
                        .setCancelText("아니오");

                dialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.cancel();
                    }
                });

                dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        report();
                        sweetAlertDialog.cancel();
                    }
                });

                dialog.show();
            }
        });
    }

    private void report() {
        HashMap<String, Object> fieldMap = new HashMap<>();

        fieldMap.put("id", "test01");
        fieldMap.put("lat", lat);
        fieldMap.put("lng", lon);
        fieldMap.put("kind", state);
        fieldMap.put("scope", strength);

        Call<Void> call = restAPI.report(fieldMap);
        Toast.makeText(getApplicationContext(), "CALLED", Toast.LENGTH_SHORT).show();
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(getApplicationContext(), "startResponse", Toast.LENGTH_SHORT).show();

                Log.d("Navigation", "onResponse: succeed" + response.code());
                if (response.code() == 200) {
                    new SweetAlertDialog(NavigationActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setContentText("신고가 접수되었습니다. 감사합니다.")
                            .setConfirmText("알겠습니다.").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.cancel();
                            reportLayout.setVisibility(View.GONE);
                        }
                    })
                            .show();
                }
                if (response.code() == 400) {
                    new SweetAlertDialog(NavigationActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setContentText("잘못된 요청입니다.")
                            .setConfirmText("알겠습니다.").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.cancel();
                        }
                    })
                            .show();
                }
                if (response.code() == 500) {
                    new SweetAlertDialog(NavigationActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setContentText("서버 오류입니다.")
                            .setConfirmText("알겠습니다.").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.cancel();
                        }
                    })
                            .show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
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
                strengthBar.animate();
                strengthBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
                Toast.makeText(getApplicationContext(), strength + "", Toast.LENGTH_SHORT).show();
            }
        });

        strengthButtons[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strength = 2;
                strengthBar.animate();
                strengthBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                Toast.makeText(getApplicationContext(), strength + "", Toast.LENGTH_SHORT).show();
            }
        });

        strengthButtons[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strength = 3;
                strengthBar.animate();
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

    private void initializeMarkers() {
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
                Log.d(TAG, "run: " + call.request().url());
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Log.d(TAG, "onResponse: " + response.code());
                        Log.d(TAG, "onResponse: " + call.request().url());

                        if (response.code() == 200) {
                            tMapView.removeAllMarkerItem();
                            int i = 0;
                            JsonArray res = response.body().get("showKey").getAsJsonArray();
                            Iterator<JsonElement> iterator = res.iterator();
                            while (iterator.hasNext()) {
                                JsonObject obj = iterator.next().getAsJsonObject();
                                Log.d(TAG, "onResponse: " + obj.toString());
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

    private void markerUpdate() {
//        new Timer().scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                Log.i("tag", "A Kiss every 5 seconds");
//            }
//        }, 0, 5000);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        Log.d(TAG, "run: " + call.request().url());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d(TAG, "onResponse: " + response.code());
                Log.d(TAG, "onResponse: " + call.request().url());

                if (response.code() == 200) {
                    tMapView.removeAllMarkerItem();
                    int i = 0;
                    JsonArray res = response.body().get("showKey").getAsJsonArray();
                    Iterator<JsonElement> iterator = res.iterator();
                    while (iterator.hasNext()) {
                        JsonObject obj = iterator.next().getAsJsonObject();
                        Log.d(TAG, "onResponse: " + obj.toString());
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
    }
}
