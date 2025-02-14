package com.example.onlinebusticketing;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;



public class InputActivityAdapter extends RecyclerView.Adapter<InputActivityAdapter.ViewHolder>{
    private ArrayList<String> dataList;
    private ArrayList<String> filteredData;
    private OnItemClickListener listener;
    private DatabaseHelper databaseHelper;
    int layoutResId;
    String travel;

    public InputActivityAdapter(OnItemClickListener listener, ArrayList<String> dataList, int layoutResId, Context context, String travel) {
        this.listener = listener;
        this.dataList = dataList;
        this.filteredData = new ArrayList<>(dataList);
        this.layoutResId = layoutResId;
        this.databaseHelper = new DatabaseHelper(context);
        this.travel = travel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        layoutResId = R.layout.item_stop_name_list;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = filteredData.get(position);
        holder.itemTextView.setText(item);
        if(travel.equals("Metro")){
            String stopName = dataList.get(position);
            String lineType = databaseHelper.getLineTypeForStop(stopName);
            Context context = holder.iconView.getContext();
            if (lineType.equalsIgnoreCase("Purple")) {
                holder.iconView.setColorFilter(ContextCompat.getColor(context, R.color.purple));

            } else if (lineType.equalsIgnoreCase("Aqua")) {
                holder.iconView.setColorFilter(ContextCompat.getColor(context, R.color.aqua));
            }
        }

        holder.item_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(item);
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
        TextView itemTextView;
        LinearLayout item_view;
        ImageView iconView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTextView = itemView.findViewById(R.id.item_stop_name);
            item_view = itemView.findViewById(R.id.item_stop_name_view);
            iconView = itemView.findViewById(R.id.iconView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String selectedItem);
    }
}

