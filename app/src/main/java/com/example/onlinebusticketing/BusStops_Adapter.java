package com.example.onlinebusticketing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class BusStops_Adapter extends RecyclerView.Adapter<BusStops_Adapter.ViewHolder>{
    private ArrayList<String> dataList;
    int sourceNum, destinNum;

    public BusStops_Adapter(ArrayList<String> dataList, int sourceNum, int destinNum) {
        this.dataList = dataList;
        this.sourceNum = sourceNum;
        this.destinNum = destinNum;
    }

    @NonNull
    @Override
    public BusStops_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutResId = (viewType % 2 == 0) ? R.layout.item_bus_stop : R.layout.item_bus_stop_2;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        return new BusStops_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusStops_Adapter.ViewHolder holder, int position) {
        String item = dataList.get(position);
        holder.itemTextView.setText(item);
        if(position == sourceNum || position == destinNum){
            holder.itemTextView.setTextColor(ContextCompat.getColor(holder.itemTextView.getContext(), R.color.primaryColor));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == sourceNum){
            return sourceNum;
        }
        else if (position == destinNum){
            return  destinNum;
        }
        return position;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTextView = itemView.findViewById(R.id.busStop);
        }
    }
}
