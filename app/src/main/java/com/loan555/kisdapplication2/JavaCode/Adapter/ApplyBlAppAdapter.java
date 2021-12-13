package com.loan555.kisdapplication2.JavaCode.Adapter;

import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.loan555.kisdapplication2.JavaCode.Model.BlacklistApp;
import com.loan555.kisdapplication2.JavaCode.SocketHandler;
import com.loan555.kisdapplication2.JavaCode.DatabaseHelper;
import com.loan555.kisdapplication2.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ApplyBlAppAdapter extends RecyclerView.Adapter<ApplyBlAppAdapter.ApplyblViewHolder> {
    private static final String TAG = "KA.ApplyBlAppAdapter";
    Context context;
    ArrayList<BlacklistApp> blackLists;
    String idkid;

    public ApplyBlAppAdapter(Context context, ArrayList<BlacklistApp> blackLists, String idkid) {
        this.context = context;
        this.blackLists = blackLists;
        this.idkid = idkid;
    }

    @NonNull
    @Override
    public ApplyBlAppAdapter.ApplyblViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.view_list_black_app, parent, false);
        return new ApplyBlAppAdapter.ApplyblViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplyBlAppAdapter.ApplyblViewHolder holder, int position) {
        DatabaseHelper dh = DatabaseHelper.getInstance(context);
        holder.txtName.setText(blackLists.get(position).getNameBl());
        holder.img.setImageResource(blackLists.get(position).getImg());
        if (blackLists.get(position).getActivate() == 1) {
            holder.btn_apply.setChecked(true);
        } else {
            holder.btn_apply.setChecked(false);
        }
        holder.applyitems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, holder.applyitems);
                popupMenu.inflate(R.menu.change_time_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.startTime: {
                                showDialogTimePicker(position, true, blackLists.get(position).getNameBl());
                                break;
                            }
                            case R.id.endTime: {
                                showDialogTimePicker(position, false, blackLists.get(position).getNameBl());
                                break;
                            }
                            case R.id.allDay: {
                                //TODO setAllDay in backend
                                blackLists.get(position).setTimeStart(blackLists.get(position).getTimeEnd());
                                dh.updateTimeNotActivate(idkid,blackLists.get(position).getNameBl(),"0:00","0:00");
                                notifyItemChanged(position);
                                break;
                            }
                        }
                        return true;
                    }
                });
                if (blackLists.get(position).getTimeStart().equals(blackLists.get(position).getTimeEnd())) {
                    popupMenu.getMenu().removeItem(R.id.allDay);
                }
                popupMenu.show();
            }
        });
        if (!blackLists.get(position).getTimeStart().equals(blackLists.get(position).getTimeEnd())) {
            holder.layoutTime.setVisibility(View.VISIBLE);
            holder.timeStart.setText(blackLists.get(position).getTimeStart());
            holder.timeEnd.setText(blackLists.get(position).getTimeEnd());
        } else holder.layoutTime.setVisibility(View.INVISIBLE);
        holder.btn_apply.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG,"Vào hàm onCheckedChanged");
                Toast.makeText(context, "check " + isChecked + blackLists.get(position).getNameBl(), Toast.LENGTH_SHORT).show();
                if (isChecked) {
                    int hourStart = 0;
                    int miStart = 0;
                    int hourEnd = 0;
                    int miEnd = 0;
                    String[] arrTimeStart;
                    String[] arrTimeEnd;
                    arrTimeStart = blackLists.get(position).getTimeStart().split(":");
                    arrTimeEnd = blackLists.get(position).getTimeEnd().split(":");
                    for (int i = 0; i < arrTimeStart.length; i++) {
                        if (i == 0) hourStart = Integer.parseInt(arrTimeStart[i]);
                        else miStart = Integer.parseInt(arrTimeStart[i]);
                    }
                    for (int i = 0; i < arrTimeEnd.length; i++) {
                        if (i == 0) hourEnd = Integer.parseInt(arrTimeEnd[i]);
                        else miEnd = Integer.parseInt(arrTimeEnd[i]);
                    }
                    int timeStart = hourStart*60+miStart;
                    int timeEnd = hourEnd*60+miEnd;
                    dh.updateActivateApp(1, idkid, blackLists.get(position).getNameBl());
                    String datasend = "";
                    datasend = "{\"tenApp\":\"" + blackLists.get(position).getNameBl() + "\",\"maTreEm\":\"" + idkid + "\",\"thoiGianBatDauTrongNgay\":" +timeStart+ ",\"thoiGianKetThucTrongNgay\":" + timeEnd + "}";
                    Log.d(TAG,"String chặn app : " +datasend);
                    JSONObject json = null;
                    try {
                        json = new JSONObject(datasend);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    SocketHandler.getSocket().emit("themAppChan", json);
                    Toast.makeText(context, "THEM AP CHAN THANH CONG", Toast.LENGTH_LONG).show();
                } else {
                    int hourStart = 0;
                    int miStart = 0;
                    int hourEnd = 0;
                    int miEnd = 0;
                    String[] arrTimeStart;
                    String[] arrTimeEnd;
                    arrTimeStart = blackLists.get(position).getTimeStart().split(":");
                    arrTimeEnd = blackLists.get(position).getTimeEnd().split(":");
                    for (int i = 0; i < arrTimeStart.length; i++) {
                        if (i == 0) hourStart = Integer.parseInt(arrTimeStart[i]);
                        else miStart = Integer.parseInt(arrTimeStart[i]);
                    }
                    for (int i = 0; i < arrTimeEnd.length; i++) {
                        if (i == 0) hourEnd = Integer.parseInt(arrTimeEnd[i]);
                        else miEnd = Integer.parseInt(arrTimeEnd[i]);
                    }
                    int timeStart = 0-hourStart*60-miStart;
                    int timeEnd = 0-hourEnd*60-miEnd;
                    dh.updateActivateApp(0, idkid, blackLists.get(position).getNameBl());
                    String datasend = "";
                    datasend = "{\"tenApp\":\"" + blackLists.get(position).getNameBl() + "\",\"maTreEm\":\"" + idkid + "\",\"thoiGianBatDauTrongMgay\":" +timeStart+ ",\"thoiGianKetThucTrongNgay\":" +timeEnd+ "}";
                    JSONObject json = null;
                    try {
                        json = new JSONObject(datasend);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG,"String bỏ chặn app" +datasend);
                    SocketHandler.getSocket().emit("themAppChan", json);
                    Toast.makeText(context, "BO THEM APP CHAN THANH CONG", Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    @Override
    public int getItemCount() {
        return blackLists.size();
    }

    public static class ApplyblViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout time;
        ConstraintLayout applyitems;
        Switch btn_apply;
        TextView txtName;
        ImageView img;
        TextView timeStart;
        TextView timeEnd;
        ConstraintLayout layoutTime;

        public ApplyblViewHolder(@NonNull View itemView) {
            super(itemView);
            applyitems = itemView.findViewById(R.id.layoutItemBlApp);
            btn_apply = itemView.findViewById(R.id.swApp);
            txtName = itemView.findViewById(R.id.tvNameApp);
            img = itemView.findViewById(R.id.imgApp);
            timeEnd = itemView.findViewById(R.id.timeStop);
            timeStart = itemView.findViewById(R.id.timeStart);
            layoutTime = itemView.findViewById(R.id.time);
            time = itemView.findViewById(R.id.time);
        }
    }

    private void showDialogTimePicker(int position, boolean isStart, String nameApp) {
        int hour = 0;
        int mi = 0;
        String[] arrTime;
        if (isStart) {
            arrTime = blackLists.get(position).getTimeStart().split(":");
        } else arrTime = blackLists.get(position).getTimeEnd().split(":");
        for (int i = 0; i < arrTime.length; i++) {
            if (i == 0) hour = Integer.parseInt(arrTime[i]);
            else mi = Integer.parseInt(arrTime[i]);
        }
// Thay đổi thời gian chặn
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                DatabaseHelper dh = DatabaseHelper.getInstance(context);
                String min = minute + "";
                if (minute == 0) min = "00";
                else if (minute < 10) min = "0" + minute;
                String str = hourOfDay + ":" + min;
                int hour = 0;
                int mi = 0;
                String[] arrTime;
                Log.d(TAG,"TimeStart : "+blackLists.get(position).getTimeStart()+" TimeEnd : "+blackLists.get(position).getTimeEnd());
                if (!isStart) {
                    arrTime = blackLists.get(position).getTimeStart().split(":");
                } else arrTime = blackLists.get(position).getTimeEnd().split(":");
                for (int i = 0; i < arrTime.length; i++) {
                    if (i == 0) hour = Integer.parseInt(arrTime[i]);
                    else mi = Integer.parseInt(arrTime[i]);
                }
                // Nếu thay đổi thời gian bắt đầu
                if (isStart) {
                    Log.i(TAG,"Đang thay đổi giờ bắt đầu");
                    if(hourOfDay>hour){
                        Log.d(TAG,"Giờ bắt đầu lúc này : "+String.valueOf(hourOfDay)+"Giờ kết thúc lúc này : "+String.valueOf(hour));
                        Toast.makeText(context, "Thời gian bắt đầu phải bé hơn thời gian kết thúc", Toast.LENGTH_LONG).show();
                    }
                    else if(hourOfDay==hour){
                        if(minute>=mi){
                            Log.d(TAG,"Phút bắt đầu lúc này : "+String.valueOf(minute)+"Phút kết thúc lúc này"+String.valueOf(mi));
                            Toast.makeText(context, "Thời gian bắt đầu phải bé hơn thời gian kết thúc", Toast.LENGTH_LONG).show();
                        }
                        else{
                            blackLists.get(position).setTimeStart(str);
                            dh.updateOneTimeApp(idkid,nameApp,str,0);
                            // Thay đổi thời gian bắt đầu và app đang được chặn sẵn
                            if(blackLists.get(position).getActivate()==1){
                                int hourEnd = 0;
                                int miEnd = 0;
                                String[] arrTimeEnd;
                                arrTimeEnd = blackLists.get(position).getTimeEnd().split(":");
                                for (int i = 0; i < arrTimeEnd.length; i++) {
                                    if (i == 0) hourEnd = Integer.parseInt(arrTimeEnd[i]);
                                    else miEnd = Integer.parseInt(arrTimeEnd[i]);
                                }
                                int timeEnd = hourEnd*60+miEnd;
                                int timeStart = hourOfDay*60+minute;
                                String datasend = "";
                                datasend = "{\"tenApp\":\"" + blackLists.get(position).getNameBl() + "\",\"maTreEm\":\"" + idkid + "\",\"thoiGianBatDauTrongNgay\":" +timeStart+ ",\"thoiGianKetThucTrongNgay\":" + timeEnd + "}";
                                Log.d(TAG, "String thêm app chặn : "+datasend);
                                JSONObject json = null;
                                try {
                                    json = new JSONObject(datasend);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                SocketHandler.getSocket().emit("themAppChan", json);
                                Toast.makeText(context, "THEM AP CHAN THANH CONG", Toast.LENGTH_LONG).show();
                            }
                            else{
                                int hourEnd = 0;
                                int miEnd = 0;
                                String[] arrTimeEnd;
                                arrTimeEnd = blackLists.get(position).getTimeEnd().split(":");
                                for (int i = 0; i < arrTimeEnd.length; i++) {
                                    if (i == 0) hourEnd = Integer.parseInt(arrTimeEnd[i]);
                                    else miEnd = Integer.parseInt(arrTimeEnd[i]);
                                }
                                int timeEnd = 0-hourEnd*60-miEnd;
                                int timeStart = 0-hourOfDay*60-minute;
                                String datasend = "";
                                datasend = "{\"tenApp\":\"" + blackLists.get(position).getNameBl() + "\",\"maTreEm\":\"" + idkid + "\",\"thoiGianBatDauTrongNgay\":" +timeStart+ ",\"thoiGianKetThucTrongNgay\":" + timeEnd + "}";
                                Log.d(TAG, "String bỏ chặn app : "+datasend);
                                JSONObject json = null;
                                try {
                                    json = new JSONObject(datasend);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                SocketHandler.getSocket().emit("themAppChan", json);
                                Toast.makeText(context, "BỎ AP CHAN THANH CONG", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    else{
                        blackLists.get(position).setTimeStart(str);
                        dh.updateOneTimeApp(idkid,nameApp,str,0);
                        if(blackLists.get(position).getActivate()==1){
                            int hourEnd = 0;
                            int miEnd = 0;
                            String[] arrTimeEnd;
                            arrTimeEnd = blackLists.get(position).getTimeEnd().split(":");
                            for (int i = 0; i < arrTimeEnd.length; i++) {
                                if (i == 0) hourEnd = Integer.parseInt(arrTimeEnd[i]);
                                else miEnd = Integer.parseInt(arrTimeEnd[i]);
                            }
                            int timeEnd = hourEnd*60+miEnd;
                            int timeStart = hourOfDay*60+minute;
                            String datasend = "";
                            datasend = "{\"tenApp\":\"" + blackLists.get(position).getNameBl() + "\",\"maTreEm\":\"" + idkid + "\",\"thoiGianBatDauTrongNgay\":" +timeStart+ ",\"thoiGianKetThucTrongNgay\":" + timeEnd + "}";
                            Log.d(TAG, "String thêm app chặn : "+datasend);
                            JSONObject json = null;
                            try {
                                json = new JSONObject(datasend);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            SocketHandler.getSocket().emit("themAppChan", json);
                            Toast.makeText(context, "THEM AP CHAN THANH CONG", Toast.LENGTH_LONG).show();
                        }
                        else{
                            int hourEnd = 0;
                            int miEnd = 0;
                            String[] arrTimeEnd;
                            arrTimeEnd = blackLists.get(position).getTimeEnd().split(":");
                            for (int i = 0; i < arrTimeEnd.length; i++) {
                                if (i == 0) hourEnd = Integer.parseInt(arrTimeEnd[i]);
                                else miEnd = Integer.parseInt(arrTimeEnd[i]);
                            }
                            int timeEnd = 0-hourEnd*60-miEnd;
                            int timeStart = 0-hourOfDay*60-minute;
                            String datasend = "";
                            datasend = "{\"tenApp\":\"" + blackLists.get(position).getNameBl() + "\",\"maTreEm\":\"" + idkid + "\",\"thoiGianBatDauTrongNgay\":" +timeStart+ ",\"thoiGianKetThucTrongNgay\":" + timeEnd + "}";
                            Log.d(TAG, "String bỏ chặn app : "+datasend);
                            JSONObject json = null;
                            try {
                                json = new JSONObject(datasend);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            SocketHandler.getSocket().emit("themAppChan", json);
                            Toast.makeText(context, "BỎ AP CHAN THANH CONG", Toast.LENGTH_LONG).show();
                        }
                    }
                    // Nếu thay đổi thời gian kết thúc
                } else {
                    if(hourOfDay<hour){
                        Toast.makeText(context, "Thời gian kết thúc phải lớn hơn thời gian bắt đầu", Toast.LENGTH_LONG).show();
                    }
                    else if(hourOfDay==hour){
                        if(minute<=mi){
                            Toast.makeText(context, "Thời gian kết thúc phải lớn hơn thời gian bắt đầu", Toast.LENGTH_LONG).show();
                        }
                        else{
                            blackLists.get(position).setTimeEnd(str);
                            dh.updateOneTimeApp(idkid,nameApp,str,1);
                            if(blackLists.get(position).getActivate()==1){
                                int hourStart = 0;
                                int miStart = 0;
                                String[] arrTimeStart;
                                arrTimeStart = blackLists.get(position).getTimeStart().split(":");
                                for (int i = 0; i < arrTimeStart.length; i++) {
                                    if (i == 0) hourStart = Integer.parseInt(arrTimeStart[i]);
                                    else miStart = Integer.parseInt(arrTimeStart[i]);
                                }
                                int timeEnd = hourOfDay*60+minute;
                                int timeStart = hourStart*60+miStart;
                                String datasend = "";
                                datasend = "{\"tenApp\":\"" + blackLists.get(position).getNameBl() + "\",\"maTreEm\":\"" + idkid + "\",\"thoiGianBatDauTrongNgay\":" +timeStart+ ",\"thoiGianKetThucTrongNgay\":" + timeEnd + "}";
                                Log.i(TAG,"String thêm app chặn : "+ datasend);
                                JSONObject json = null;
                                try {
                                    json = new JSONObject(datasend);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                SocketHandler.getSocket().emit("themAppChan", json);
                                Toast.makeText(context, "THEM AP CHAN THANH CONG", Toast.LENGTH_LONG).show();
                            }
                            else{
                                int hourStart = 0;
                                int miStart = 0;
                                String[] arrTimeStart;
                                arrTimeStart = blackLists.get(position).getTimeStart().split(":");
                                for (int i = 0; i < arrTimeStart.length; i++) {
                                    if (i == 0) hourStart = Integer.parseInt(arrTimeStart[i]);
                                    else miStart = Integer.parseInt(arrTimeStart[i]);
                                }
                                int timeEnd = 0-hourOfDay*60-minute;
                                int timeStart = 0-hourStart*60-miStart;
                                String datasend = "";
                                datasend = "{\"tenApp\":\"" + blackLists.get(position).getNameBl() + "\",\"maTreEm\":\"" + idkid + "\",\"thoiGianBatDauTrongNgay\":" +timeStart+ ",\"thoiGianKetThucTrongNgay\":" + timeEnd + "}";
                                Log.i(TAG,"String bỏ chặn app : "+ datasend);
                                JSONObject json = null;
                                try {
                                    json = new JSONObject(datasend);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                SocketHandler.getSocket().emit("themAppChan", json);
                                Toast.makeText(context, "BO APP CHAN THANH CONG", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    else{
                        blackLists.get(position).setTimeEnd(str);
                        dh.updateOneTimeApp(idkid,nameApp,str,1);
                        if(blackLists.get(position).getActivate()==1){
                            int hourStart = 0;
                            int miStart = 0;
                            String[] arrTimeStart;
                            arrTimeStart = blackLists.get(position).getTimeStart().split(":");
                            for (int i = 0; i < arrTimeStart.length; i++) {
                                if (i == 0) hourStart = Integer.parseInt(arrTimeStart[i]);
                                else miStart = Integer.parseInt(arrTimeStart[i]);
                            }
                            int timeEnd = hourOfDay*60+minute;
                            int timeStart = hourStart*60+miStart;
                            String datasend = "";
                            datasend = "{\"tenApp\":\"" + blackLists.get(position).getNameBl() + "\",\"maTreEm\":\"" + idkid + "\",\"thoiGianBatDauTrongNgay\":" +timeStart+ ",\"thoiGianKetThucTrongNgay\":" + timeEnd + "}";
                            Log.i(TAG,"String thêm app chặn : "+ datasend);
                            JSONObject json = null;
                            try {
                                json = new JSONObject(datasend);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            SocketHandler.getSocket().emit("themAppChan", json);
                            Toast.makeText(context, "THEM APP CHAN THANH CONG", Toast.LENGTH_LONG).show();
                        }
                        else{
                            int hourStart = 0;
                            int miStart = 0;
                            String[] arrTimeStart;
                            arrTimeStart = blackLists.get(position).getTimeStart().split(":");
                            for (int i = 0; i < arrTimeStart.length; i++) {
                                if (i == 0) hourStart = Integer.parseInt(arrTimeStart[i]);
                                else miStart = Integer.parseInt(arrTimeStart[i]);
                            }
                            int timeEnd = 0-hourOfDay*60-minute;
                            int timeStart = 0-hourStart*60-miStart;
                            String datasend = "";
                            datasend = "{\"tenApp\":\"" + blackLists.get(position).getNameBl() + "\",\"maTreEm\":\"" + idkid + "\",\"thoiGianBatDauTrongNgay\":" +timeStart+ ",\"thoiGianKetThucTrongNgay\":" + timeEnd + "}";
                            Log.i(TAG,"String bỏ chặn app : "+ datasend);
                            JSONObject json = null;
                            try {
                                json = new JSONObject(datasend);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            SocketHandler.getSocket().emit("themAppChan", json);
                            Toast.makeText(context, "BO AP CHAN THANH CONG", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                notifyItemChanged(position);
            }

        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, timeSetListener, hour, mi, true);
        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.show();
    }
}
