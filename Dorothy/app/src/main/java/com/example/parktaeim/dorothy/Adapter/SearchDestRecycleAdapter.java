package com.example.parktaeim.dorothy.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.parktaeim.dorothy.Model.SearchDestItem;
import com.example.parktaeim.dorothy.R;

import java.util.ArrayList;

/**
 * Created by parktaeim on 2017. 10. 1..
 */

public class SearchDestRecycleAdapter extends RecyclerView.Adapter<SearchDestRecycleAdapter.ViewHolder> {
    Context context;
    private ArrayList<SearchDestItem> destItems;

    public SearchDestRecycleAdapter(Context context, ArrayList<SearchDestItem> destItems) {
        this.context = context;
        this.destItems = destItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_searchdest,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SearchDestItem searchDestItem = destItems.get(position);
        holder.destination.setText(searchDestItem.getDestination());
        holder.address.setText(searchDestItem.getAddress());
        holder.distance.setText(searchDestItem.getDistance().toString());

    }

    @Override
    public int getItemCount() {
        return destItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView destination;
        public TextView address;
        public TextView distance;

        public ViewHolder(View view){
            super(view);
            destination = (TextView) view.findViewById(R.id.destinationTextView);
            address = (TextView) view.findViewById(R.id.addressTextView);
            distance = (TextView) view.findViewById(R.id.distanceTextView);

        }
    }
}
