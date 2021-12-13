package com.loan555.kisdapplication2.JavaCode.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.loan555.kisdapplication2.JavaCode.DatabaseHelper;
import com.loan555.kisdapplication2.JavaCode.Model.History;
import com.loan555.kisdapplication2.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private static final String TAG = "KA.HistoryAdapter";
    Context context;
    List<History> historyList;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

    public HistoryAdapter(Context context, List<History> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.historyitems, parent, false);

        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        DatabaseHelper dh = DatabaseHelper.getInstance(context);
        try {
            Date date = format.parse(historyList.get(position).getThoiGianYeuCau());
            System.out.println(date);
            String str = dateFormat.format(date);
            holder.tvtime.setText(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.tvurl.setText(new StringBuilder("").append(historyList.get(position).getDiaChi()));
//        holder.tvtime.setText(new StringBuilder("").append(historyList.get(position).getThoiGianYeuCau()));
        holder.tvstatus.setText(new StringBuilder("").append(historyList.get(position).getTinhTrang()));
        holder.historyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, v);
                popup.inflate(R.menu.historytobl);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int menu = menuItem.getItemId();
                        boolean result = false;
                        switch (menu) {
                            case R.id.menu_addurl:
                                result = true;
                                break;
                        }
                        return result;
                    }
                });
                popup.show();
            }
        });
        holder.nameapp.setText(historyList.get(position).getTenapp());
        switch (historyList.get(position).getTenapp()) {
            case "Chrome":
                holder.iconapp.setImageResource(R.drawable.chrome);
                break;
            case "Messenger":
                holder.iconapp.setImageResource(R.drawable.messenger);
                break;
            case "Gmail":
                holder.iconapp.setImageResource(R.drawable.gmail);
                break;
            case "TikTok":
                holder.iconapp.setImageResource(R.drawable.tiktok);
                break;
            case "Instagram":
                holder.iconapp.setImageResource(R.drawable.instagram);
                break;
            case "YouTube":
                holder.iconapp.setImageResource(R.drawable.youtube);
                break;
            case "Zalo":
                holder.iconapp.setImageResource(R.drawable.zalo);
                break;
            case "Facebook":
                holder.iconapp.setImageResource(R.drawable.facebook);
                break;
            default:
                holder.iconapp.setImageResource(R.drawable.icondefault);
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout historyView;
        TextView tvurl;
        TextView tvtime;
        TextView tvstatus;
        ImageView iconapp;
        TextView nameapp;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            historyView = itemView.findViewById(R.id.historyview);
            tvurl = itemView.findViewById(R.id.tvurl);
            tvtime = itemView.findViewById(R.id.tvtime);
            tvstatus = itemView.findViewById(R.id.tvstatus);
            iconapp = itemView.findViewById(R.id.iconapp);
            nameapp = itemView.findViewById(R.id.nameapp);
        }
    }
}
