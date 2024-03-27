package com.example.onlinebusticketing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DropdownAdapter extends RecyclerView.Adapter<DropdownAdapter.ViewHolder> {

    private List<String> items;
    private LayoutInflater inflater;
    private OnItemClickListener onItemClickListener;

    public DropdownAdapter(Context context, List<String> items) {
        this.inflater = LayoutInflater.from(context);
        this.items = items;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = items.get(position);
        holder.bind(item);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(String item) {
            textViewItem.setText(item);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String item);
    }
}
