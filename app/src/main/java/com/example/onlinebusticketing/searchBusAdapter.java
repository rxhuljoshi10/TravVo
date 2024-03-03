package com.example.onlinebusticketing;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class searchBusAdapter extends RecyclerView.Adapter<searchBusAdapter.ViewHolder>{
    private ArrayList<String> dataList;
    private ArrayList<String> filteredData;
    DatabaseHelper databaseHelper;
    String source, destination;

    public searchBusAdapter(ArrayList<String> dataList, DatabaseHelper databaseHelper) {
        this.dataList = dataList;
        this.filteredData = new ArrayList<>(dataList);
        this.databaseHelper = databaseHelper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutResId = R.layout.item_search_bus_list;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = filteredData.get(position);
        String bus_number = item;
        try {
            item = item.substring(0, item.indexOf('-'));
        }
        catch (Exception e){}

        source = databaseHelper.getSource(bus_number, 1);
        destination = databaseHelper.getSource(bus_number, -1);

        holder.itemTextView.setText(item);
        holder.item_searchBus_Source.setText(source);
        holder.item_searchBus_Destination.setText(destination);

        int colorRes = (position % 2 == 0) ? com.google.android.material.R.color.design_default_color_background : R.color.grey;
        holder.item_searchBus.setBackgroundColor(ContextCompat.getColor(holder.itemTextView.getContext(), colorRes));

        holder.item_searchBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ListOfBusStops.class);
                intent.putExtra("Source","");
                intent.putExtra("Destination","");
                intent.putExtra("busNumber", bus_number);
                v.getContext().startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return filteredData.size();
    }

    public void filter(CharSequence charSequence) {
        filteredData.clear();

        if (TextUtils.isEmpty(charSequence)) {
            filteredData.addAll(dataList);
        } else {
            String query = charSequence.toString().toLowerCase().trim();
            for (String item : dataList) {
                if (item.toLowerCase().contains(query)) {
                    filteredData.add(item);
                }
            }
        }

        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemTextView, item_searchBus_Source, item_searchBus_Destination;
        LinearLayout item_searchBus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTextView = itemView.findViewById(R.id.item_searchBus_textview);
            item_searchBus = itemView.findViewById(R.id.item_searchBus);
            item_searchBus_Source = itemView.findViewById(R.id.item_searchBus_source);
            item_searchBus_Destination = itemView.findViewById(R.id.item_searchBus_destination);
        }
    }
}

