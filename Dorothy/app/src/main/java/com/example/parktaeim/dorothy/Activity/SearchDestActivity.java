package com.example.parktaeim.dorothy.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;

import com.example.parktaeim.dorothy.Adapter.SearchDestRecycleAdapter;
import com.example.parktaeim.dorothy.Model.SearchDestItem;
import com.example.parktaeim.dorothy.R;

import java.util.ArrayList;

/**
 * Created by parktaeim on 2017. 10. 1..
 */

public class SearchDestActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchDestRecycleAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchdest);

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList items = new ArrayList<>();
        //Setting item in RecyclerView
        items.add(new SearchDestItem("송강그린아파트","대전광역시 유성구 송강동",2.6));
        items.add(new SearchDestItem("송강그린아파트","대전광역시 유성구 송강동",2.6));
        items.add(new SearchDestItem("송강그린아파트","대전광역시 유성구 송강동",2.6));
        items.add(new SearchDestItem("송강그린아파트","대전광역시 유성구 송강동",2.6));
        items.add(new SearchDestItem("송강그린아파트","대전광역시 유성구 송강동",2.6));
        items.add(new SearchDestItem("송강그린아파트","대전광역시 유성구 송강동",2.6));

        adapter = new SearchDestRecycleAdapter(getApplicationContext(),items);
        recyclerView.setAdapter(adapter);
    }
}
