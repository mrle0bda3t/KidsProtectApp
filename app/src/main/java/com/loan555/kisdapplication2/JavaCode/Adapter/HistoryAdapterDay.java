package com.loan555.kisdapplication2.JavaCode.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loan555.kisdapplication2.JavaCode.DatabaseHelper;
import com.loan555.kisdapplication2.JavaCode.Model.HistoryDay;
import com.loan555.kisdapplication2.R;

import java.util.ArrayList;

public class HistoryAdapterDay extends RecyclerView.Adapter<HistoryAdapterDay.HistoryViewHolder> {
    private static final String TAG = "KA.HistoryAdapterDay";
    Context context;
    ArrayList<HistoryDay> historyList;

    public HistoryAdapterDay(Context context, ArrayList<HistoryDay> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.layout_history_day, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        DatabaseHelper dh = DatabaseHelper.getInstance(context);
        holder.tvDay.setText(historyList.get(position).getDay());
        holder.listHistory.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        holder.listHistory.setAdapter(new HistoryAdapter(context, historyList.get(position).getList()));
        holder.historyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.clickItem();
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout historyView;
        TextView tvDay;
        RecyclerView listHistory;
        ImageView btnUpDown;
        Boolean isVisibility = false;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            historyView = itemView.findViewById(R.id.layoutItem);
            tvDay = itemView.findViewById(R.id.tvDay);
            listHistory = itemView.findViewById(R.id.listHistory);
            btnUpDown = itemView.findViewById(R.id.btnUpDown);
        }

        public void clickItem() {
            if (isVisibility) {
                listHistory.setVisibility(View.GONE);
                btnUpDown.setImageResource(R.drawable.ic_down);
                isVisibility = false;
            } else {
                listHistory.setVisibility(View.VISIBLE);
                btnUpDown.setImageResource(R.drawable.ic_up);
                isVisibility = true;
            }
        }
    }
}
