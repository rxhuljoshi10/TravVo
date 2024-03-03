package com.example.onlinebusticketing;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class BusListAdapter extends RecyclerView.Adapter<BusListAdapter.ViewHolder> {
    private ArrayList<String> dataList;
    String source, destination;

    public BusListAdapter(ArrayList<String> dataList, String source, String destination) {
        this.dataList = dataList;
        this.source = source;
        this.destination = destination;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bus_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = dataList.get(position);
        String bus_number = item;
        item = item.substring(0, item.indexOf('-'));
        holder.itemTextView.setText(item);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ListOfBusStops.class);
                intent.putExtra("Source",source);
                intent.putExtra("Destination",destination);
                intent.putExtra("busNumber", bus_number);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTextView = itemView.findViewById(R.id.busItem);
        }
    }
}
