package com.example.onlinebusticketing;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;



public class InputActivityAdapter extends RecyclerView.Adapter<InputActivityAdapter.ViewHolder>{
    private ArrayList<String> dataList;
    private ArrayList<String> filteredData;
    private OnItemClickListener listener;

    public InputActivityAdapter(OnItemClickListener listener, ArrayList<String> dataList) {
        this.listener = listener;
        this.dataList = dataList;
        this.filteredData = new ArrayList<>(dataList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutResId = R.layout.item_stop_name_list;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = filteredData.get(position);
        holder.itemTextView.setText(item);

        holder.item_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(item);

//                intent.putExtra("Source","");
//                intent.putExtra("Destination","");
//                intent.putExtra("busNumber", bus_number);
//                v.getContext().startActivity(intent);
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTextView = itemView.findViewById(R.id.item_stop_name);
            item_view = itemView.findViewById(R.id.item_stop_name_view);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String selectedItem);
    }
}

