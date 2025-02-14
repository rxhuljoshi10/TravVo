package com.example.onlinebusticketing;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.ViewHolder>{
    private List<TicketData> dataList;
    private OnItemClickListener listener;
    public BookingHistoryAdapter(OnItemClickListener listener, List<TicketData> dataList) {
        this.listener = listener;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public BookingHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutResId = R.layout.item_booking_history;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        return new BookingHistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingHistoryAdapter.ViewHolder holder, int position) {
        TicketData item = dataList.get(position);
        String source = item.source;
        String destin = item.destination;
        int len= 25;
        if (source.length() > len) {
            source = source.substring(0, len) + "...";
        }
        if (destin.length() > len) {
            destin = destin.substring(0, len) + "...";
        }

        holder.bidView.setText("Booking ID : "+item.bookingId);
        holder.sourceView.setText(source);
        holder.destinationView.setText(destin);
        holder.dateView.setText(item.tDate+",");
        holder.timeView.setText(item.tTime);
        holder.statusView.setText(item.status);
        holder.travelView.setText(item.travel);
        if(item.travel.equals("Metro")){
            holder.imageView.setImageResource(R.drawable.ic_train_logo);
        }


        if(item.status.equals( "Cancelled" )){
            holder.statusView.setTextColor(Color.RED);
        }
        holder.ItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(item);
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
        TextView sourceView, destinationView, bidView, dateView, timeView, statusView, travelView;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ItemView = itemView.findViewById(R.id.ItemView);
            sourceView = itemView.findViewById(R.id.sourceView);
            destinationView = itemView.findViewById(R.id.destinationView);
            bidView = itemView.findViewById(R.id.bidView);
            dateView = itemView.findViewById(R.id.dateView);
            timeView = itemView.findViewById(R.id.timeView);
            statusView = itemView.findViewById(R.id.statusView);
            travelView = itemView.findViewById(R.id.travelView);
            imageView = itemView.findViewById(R.id.imageView7);
        }
    }
    public interface OnItemClickListener {
        void onItemClick(TicketData selectedItem);
    }
}
