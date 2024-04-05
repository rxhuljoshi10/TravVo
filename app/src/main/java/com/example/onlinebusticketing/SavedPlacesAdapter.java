package com.example.onlinebusticketing;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class SavedPlacesAdapter extends RecyclerView.Adapter<SavedPlacesAdapter.ViewHolder>{
    private List<Map.Entry<String, String>> dataList;
    private OnItemClickListener listener;
    private OnDelViewClickListener delViewClickListener;

    public SavedPlacesAdapter(OnItemClickListener listener,OnDelViewClickListener listener1, List<Map.Entry<String, String>> dataList) {
        this.listener = listener;
        delViewClickListener = listener1;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public SavedPlacesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutResId = R.layout.item_saved_places;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        return new SavedPlacesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedPlacesAdapter.ViewHolder holder, int position) {
        Map.Entry<String, String> item = dataList.get(position);

        holder.savedPlaceTitle.setText(item.getKey());
        if (item.getValue() != null){
            holder.savedPlaceAddress.setVisibility(View.VISIBLE);
            holder.savedPlaceAddress.setText(item.getValue());
        }

        if(position == 0){
            holder.savedPlacesIcon.setImageResource(R.drawable.icon_home);
        } else if (position == 1) {
            holder.savedPlacesIcon.setImageResource(R.drawable.ic_work);
        }

        holder.ItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(item, position);
            }
        });

        holder.menuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delViewClickListener.onDelViewClick(position);
            }
        });
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ItemView;
        TextView savedPlaceTitle, savedPlaceAddress;
        ImageView savedPlacesIcon, menuView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ItemView = itemView.findViewById(R.id.ItemView);
            savedPlaceTitle = itemView.findViewById(R.id.savedPlaceTitle);
            savedPlaceAddress = itemView.findViewById(R.id.savedPlaceAddress);
            savedPlacesIcon = itemView.findViewById(R.id.savedPlacesIcon);
            menuView = itemView.findViewById(R.id.delView);
        }
    }
    public interface OnItemClickListener {
        void onItemClick(Map.Entry<String, String> selectedItem, int position);
    }

    public interface OnDelViewClickListener {
        void onDelViewClick(int position);
    }
}
