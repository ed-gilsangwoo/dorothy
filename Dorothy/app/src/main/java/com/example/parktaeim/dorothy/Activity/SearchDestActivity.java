package com.example.parktaeim.dorothy.Activity;

import android.content.Intent;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.example.parktaeim.dorothy.APIUrl;
import com.example.parktaeim.dorothy.Adapter.SearchDestRecycleAdapter;
import com.example.parktaeim.dorothy.Model.SearchDestItem;
import com.example.parktaeim.dorothy.R;
import com.example.parktaeim.dorothy.RestAPI;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okio.Utf8;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by parktaeim on 2017. 10. 1..
 */

public class SearchDestActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchDestRecycleAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private EditText destEditText;
    private ArrayList<SearchDestItem> items;
    private OkHttpClient client;
    private Retrofit builder;
    private RestAPI restAPI;

    private String destination;
    public Double lat = 0.0;
    public Double lon = 0.0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchdest);

        client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();

        builder = new Retrofit.Builder()
                .baseUrl(APIUrl.TMAP_BASE_URL).client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        restAPI = builder.create(RestAPI.class);
        setSearchView();

        setUpRecyclerView();

    }

    private void setSearchView() {

        //검색값 받아오기
        Intent intent = getIntent();
        destination = intent.getExtras().getString("destination");
        String address = intent.getExtras().getString("address");
        lat = intent.getExtras().getDouble("currentLatitude");
        lon = intent.getExtras().getDouble("currentLongitude");

        String encodedKeyword = null;

        try {
            encodedKeyword = URLEncoder.encode(destination, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Toast.makeText(getApplicationContext(), "URLENCODED SEARCHKEYWORD : " + encodedKeyword, Toast.LENGTH_SHORT).show();

        HashMap<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("count", 40);
        fieldMap.put("searchType", "all");
        fieldMap.put("searchKeyword", encodedKeyword);
        fieldMap.put("radius", 33);
        fieldMap.put("centerLon", lon == 0 ? 127.365678 : lon);
        fieldMap.put("centerLat", lat == 0 ? 36.336541 : lat);
        fieldMap.put("reqCoordType","WGS84GEO");
        Log.d("search lat"+lat.toString(),"lon"+lon.toString());
        fieldMap.put("searchtypCd", "R");

        Call<JsonObject> call = restAPI.search("application/json", getString(R.string.tmap_app_key), fieldMap);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("#!@#!@#", "setSearchView: " + call.request().url() + "\n" + call.request().header("Accept"));
                Log.d("response.code==",String.valueOf(response.code()));

                if (response.code() == 200) {
                    Log.d("response 200==========","success");
                    JsonObject searchPoiInfo = response.body().getAsJsonObject("searchPoiInfo");
                    JsonObject pois = searchPoiInfo.getAsJsonObject("pois");
                    JsonArray poi = pois.getAsJsonArray("poi");
                    Log.d("poi",poi.toString());
                    Iterator<JsonElement> iterator = poi.iterator();
                    while (iterator.hasNext()) {
                        JsonObject location = iterator.next().getAsJsonObject();
                        Double frontLat = location.get("frontLat").getAsDouble();
                        Double frontLon = location.get("frontLon").getAsDouble();
                        Double noorLat = location.get("noorLat").getAsDouble();
                        Double noorLon = location.get("noorLon").getAsDouble();
                        Double radius = location.get("radius").getAsDouble();
                        String name = location.get("name").getAsString();
                        String address = location.get("upperAddrName").getAsString() + " " + location.get("middleAddrName").getAsString()
                                + " " + location.get("lowerAddrName").getAsString() + " " + location.get("roadName").getAsString();

                        items.add(new SearchDestItem(name, address, radius, frontLat, frontLon, noorLat, noorLon));
                        Log.d("item add=====",name.toString());
                        adapter.notifyDataSetChanged();
                    }
                }
                if (response.code() == 204) {
                    Toast.makeText(getApplicationContext(), "No contents", Toast.LENGTH_SHORT).show();
                }else if(response.code() == 400){
                    Toast.makeText(getApplicationContext(), "400 ERROR",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Log.d("Error!!!!!!!!!!!",t.toString());
            }
        });

        destEditText = (EditText) findViewById(R.id.destEditText);
        destEditText.setText(destination);

    }

    private void setUpRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        items = new ArrayList<>();

        adapter = new SearchDestRecycleAdapter(getApplicationContext(), items);
        recyclerView.setAdapter(adapter);


    }
}
