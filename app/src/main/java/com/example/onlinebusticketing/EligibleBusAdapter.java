package com.example.onlinebusticketing;

import android.content.Intent;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class EligibleBusAdapter extends RecyclerView.Adapter<EligibleBusAdapter.ViewHolder> {
    private ArrayList<String> dataList;
    DatabaseHelper databaseHelper;
    String source, destination;

    public EligibleBusAdapter(ArrayList<String> dataList, String source, String destination, DatabaseHelper databaseHelper) {
        this.dataList = dataList;
        this.source = source;
        this.destination = destination;
        this.databaseHelper = databaseHelper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_eligible_bus, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = dataList.get(position);
        String bus_number = item;
        String stops = String.valueOf(databaseHelper.getTotalStops(bus_number, source, destination));
        item = item.substring(0, item.indexOf('-'));
        holder.eligibleBus.setText(item);
        holder.totalStops.setText(stops);
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
        TextView eligibleBus, totalStops;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eligibleBus = itemView.findViewById(R.id.eligibleBus);
            totalStops = itemView.findViewById(R.id.totalStops);
        }
    }
}
