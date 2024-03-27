package com.example.onlinebusticketing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.ViewHolder> {
    private List<String> dataList;
    TextView sourceEntry,destinationEntry;
    private OnItemClickListener onItemClickListener;


    public HistoryListAdapter(OnItemClickListener listener, List<String> dataList, TextView sourceEntry, TextView destinationEntry) {
        onItemClickListener = listener;
        this.dataList = dataList;
        this.sourceEntry = sourceEntry;
        this.destinationEntry = destinationEntry;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = dataList.get(position);
        String[] historyParts = item.split(" - ");
        String source = "From "+ historyParts[0];
        String destination = "To "+ historyParts[1];

        int len= 19;
        if (source.length() > len) {
            source = source.substring(0, len) + "...";
        }
        if (destination.length() > len) {
            destination = destination.substring(0, len) + "...";
        }

        holder.sourceText.setText(source);
        holder.destinationText.setText(destination);

        holder.historyItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sourceEntry.setText(historyParts[0]);
                destinationEntry.setText(historyParts[1]);
                onItemClickListener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView historyItem;
        TextView sourceText, destinationText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            historyItem = itemView.findViewById(R.id.historyItem);
            sourceText = itemView.findViewById(R.id.sourceText);
            destinationText = itemView.findViewById(R.id.destinationText);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String item);
    }
}
